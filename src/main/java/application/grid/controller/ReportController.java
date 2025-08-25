package application.grid.controller;


import application.grid.domain.dto.response.MonthlyPriceDTOResponse;
import application.grid.domain.entity.MonthlyPrice;
import application.grid.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/coletar-precos")
    public void importMontlhyPrice(HttpServletResponse response) {
        try {
            reportService.importReportMonthlyPrice();

            // prepara o response para CSV
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"monthly_prices.csv\"");

            try (PrintWriter writer = response.getWriter()) {
                reportService.writeLast12MonthsCsv(writer);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/precos-mensais")
    public List<MonthlyPriceDTOResponse> getLast12Months() {
        return reportService.getLast12Months().stream().map(monthlyPrice -> MonthlyPriceDTOResponse.fromEntity(monthlyPrice)).collect(Collectors.toList());
    }
}
