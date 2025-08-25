package application.grid;

import application.grid.domain.model.PeriodToFetch;
import application.grid.service.ReportService;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceUnitTest {

    private final ReportService reportService = new ReportService(null, null, null);

    @Test
    void testBuildPeriodsLessThan6Months() {
        LocalDate mostRecent = LocalDate.of(2025, 7, 31);
        List<PeriodToFetch> periods = reportService.buildPeriods(mostRecent, 4);

        assertEquals(1, periods.size());
        assertEquals(LocalDate.of(2025, 4, 1), periods.get(0).getStarteDate());
        assertEquals(LocalDate.of(2025, 7, 31), periods.get(0).getEndDate());
    }

    @Test
    void testBuildPeriodsMoreThan6Months() {
        LocalDate mostRecent = LocalDate.of(2025, 7, 31);
        List<PeriodToFetch> periods = reportService.buildPeriods(mostRecent, 12);

        assertEquals(2, periods.size());
        assertEquals(LocalDate.of(2024, 8, 1), periods.get(1).getStarteDate());
        assertEquals(LocalDate.of(2025, 1, 31), periods.get(1).getEndDate());
    }

    @Test
    void testParseMonthString() {
        LocalDate date = reportService.parseMonthString("jul/25");
        assertEquals(LocalDate.of(2025, 7, 1), date);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                reportService.parseMonthString("xxx/25")
        );
        assertTrue(exception.getMessage().contains("Mês desconhecido"));
    }

}
