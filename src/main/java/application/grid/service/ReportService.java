package application.grid.service;

import application.grid.client.CceeClient;
import application.grid.domain.Enum.Region;
import application.grid.domain.entity.MonthlyPrice;
import application.grid.domain.model.PeriodToFetch;
import application.grid.repository.MonthlyPriceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CceeClient cceeClient;
    private final MonthlyPriceRepository monthlyPriceRepository;
    private final  LoggerService loggerService;
    private static final Map<String, Integer> MONTH_MAP = Map.ofEntries(
            Map.entry("jan", 1),
            Map.entry("fev", 2),
            Map.entry("mar", 3),
            Map.entry("abr", 4),
            Map.entry("mai", 5),
            Map.entry("jun", 6),
            Map.entry("jul", 7),
            Map.entry("ago", 8),
            Map.entry("set", 9),
            Map.entry("out", 10),
            Map.entry("nov", 11),
            Map.entry("dez", 12)
    );

    public List<MonthlyPrice> getLast12Months() {
        LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
        return monthlyPriceRepository.findByDateAfterOrderByDateDesc(startDate);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void writeLast12MonthsCsv(PrintWriter writer) {
        List<MonthlyPrice> prices = getLast12Months();

        // montar CSV no formato MES;SUDESTE;SUL;NORDESTE;NORTE
        Map<LocalDate, Map<Region, BigDecimal>> csvMap = new TreeMap<>(Comparator.reverseOrder());

        for (MonthlyPrice price : prices) {
            csvMap.computeIfAbsent(price.getDate(), k -> new EnumMap<>(Region.class))
                    .put(price.getRegion(), price.getPrice());
        }

        // cabeçalho
        writer.println("MES;SUDESTE;SUL;NORDESTE;NORTE");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yyyy", new Locale("pt", "BR"));

        for (Map.Entry<LocalDate, Map<Region, BigDecimal>> entry : csvMap.entrySet()) {
            LocalDate date = entry.getKey();
            Map<Region, BigDecimal> regionMap = entry.getValue();

            String monthYear = capitalize(date.format(formatter));

            // garante que cada coluna seja separada corretamente
            String line = String.join(";",
                    monthYear,
                    regionMap.getOrDefault(Region.SUDESTE, BigDecimal.ZERO).toString().replace(".", ","),
                    regionMap.getOrDefault(Region.SUL, BigDecimal.ZERO).toString().replace(".", ","),
                    regionMap.getOrDefault(Region.NORDESTE, BigDecimal.ZERO).toString().replace(".", ","),
                    regionMap.getOrDefault(Region.NORTE, BigDecimal.ZERO).toString().replace(".", ",")
            );

            writer.println(line);
        }
    }

    @Transactional
    public void importReportMonthlyPrice() throws Exception {


        Optional<MonthlyPrice> latestRecord = monthlyPriceRepository.findTopByOrderByDateDesc();
        LocalDate latestRecordDate = latestRecord.map(MonthlyPrice::getDate)
                .orElse(LocalDate.now().minusMonths(12));

        LocalDate firstOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastClosedMonth = firstOfCurrentMonth.minusMonths(1);
        if (latestRecordDate.isBefore(lastClosedMonth)) {

            int monthsToFetch = (int) ChronoUnit.MONTHS.between(latestRecordDate.withDayOfMonth(1), lastClosedMonth.withDayOfMonth(1)) + 1;
            monthsToFetch = Math.min(monthsToFetch, 12);

            List<PeriodToFetch> periods = buildPeriods(lastClosedMonth, monthsToFetch);
            for (PeriodToFetch period : periods) {

                try (InputStream inputStream = cceeClient.importSpreadsheet(period.getStarteDate(), period.getEndDate());
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                    String headerLine = reader.readLine();
                    if (headerLine == null) {
                        throw new RuntimeException("Arquivo CSV vazio");
                    }

                    // Mapeia colunas do CSV para Region
                    Map<Region, Integer> regionColumnMap = buildRegionColumnMapFromCsv(headerLine.split(";"));
                    List<MonthlyPrice> monthlyPrices = new ArrayList<>();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            String[] cells = line.split(";");
                            String monthString = cells[0].trim().toLowerCase();
                            LocalDate date = parseMonthString(monthString);

                            for (Map.Entry<Region, Integer> entry : regionColumnMap.entrySet()) {
                                Region region = entry.getKey();
                                int colIndex = entry.getValue();
                                if (colIndex < cells.length) {
                                    String cellValue = cells[colIndex].replace(",", ".").trim();
                                    if (!cellValue.isEmpty()) {
                                        BigDecimal price = new BigDecimal(cellValue);
                                        monthlyPrices.add(new MonthlyPrice(date, region, price));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            loggerService.error("REPORT_FLOW", this.getClass(), "Erro ao processar linha CSV: {}", line, e);
                        }
                    }

                    monthlyPriceRepository.saveAll(monthlyPrices);
                    loggerService.info("REPORT_FLOW", this.getClass(), "Import finalizado | Total registros salvos: {}", monthlyPrices.size());

                } catch (Exception e) {
                    loggerService.error("REPORT_FLOW", this.getClass(), "Erro geral durante import do relatório CSV: {}", e.getMessage());
                    throw e;
                }
            }

        }
    }

    public List<PeriodToFetch> buildPeriods(LocalDate mostRecentDate, int monthsToFetch) {
        List<PeriodToFetch> periods = new ArrayList<>();

        if (monthsToFetch <= 6) {
            LocalDate startDate = mostRecentDate.minusMonths(monthsToFetch - 1).withDayOfMonth(1);
            LocalDate endDate = mostRecentDate.withDayOfMonth(mostRecentDate.lengthOfMonth());
            periods.add(new PeriodToFetch(startDate, endDate));
        } else {
            int remainingMonths = monthsToFetch;
            LocalDate endDate = mostRecentDate.withDayOfMonth(mostRecentDate.lengthOfMonth());

            while (remainingMonths > 0) {
                int chunkSize = Math.min(6, remainingMonths);

                LocalDate startDate = endDate.minusMonths(chunkSize - 1).withDayOfMonth(1);
                periods.add(new PeriodToFetch(startDate, endDate));

                remainingMonths -= chunkSize;
                endDate = startDate.minusDays(1);
            }
        }

        return periods;
    }


    public LocalDate parseMonthString(String monthString) {
        // separa mês/ano
        String[] parts = monthString.toLowerCase().split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de mês inválido: " + monthString);
        }
        String monthAbbr = parts[0];
        int month = MONTH_MAP.getOrDefault(monthAbbr, -1);
        if (month == -1) {
            throw new IllegalArgumentException("Mês desconhecido: " + monthAbbr);
        }
        int year = 2000 + Integer.parseInt(parts[1]); // '25' vira 2025
        return LocalDate.of(year, month, 1);
    }

    public Map<Region, Integer> buildRegionColumnMapFromCsv(String[] headers){
        Map<Region, Integer> map = new HashMap<>();
        IntStream.range(1, headers.length).forEach(i -> {
            String header = headers[i].trim().toUpperCase();
            switch (header) {
                case "SUDESTE" -> map.put(Region.SUDESTE, i);
                case "SUL" -> map.put(Region.SUL, i);
                case "NORDESTE" -> map.put(Region.NORDESTE, i);
                case "NORTE" -> map.put(Region.NORTE, i);
                default -> loggerService.warn(
                        "REPORT_FLOW",
                        this.getClass(),
                        "Coluna '{}' não corresponde a nenhuma Region conhecida, ignorando.",
                        header
                );
            }
        });
        return map;
    }

}
