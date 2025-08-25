package application.grid.client;

import application.grid.service.LoggerService;
import jakarta.validation.Valid;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CceeClient {

    @Value("${ccee.base-url}")
    private String baseUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final LoggerService loggerService;
    private static final int[] FIBONACCI_DELAYS = {1, 1, 2, 3, 5, 8};


    public InputStream importSpreadsheet(LocalDate startDate, LocalDate endDate) throws Exception {
        String url = baseUrl +
                "?p_p_id=br_org_ccee_pld_historico_PLDHistoricoPortlet_INSTANCE_lzsn" +
                "&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage" +
                "&_br_org_ccee_pld_historico_PLDHistoricoPortlet_INSTANCE_lzsn_inputInitialDate=" + startDate.format(FORMATTER) +
                "&_br_org_ccee_pld_historico_PLDHistoricoPortlet_INSTANCE_lzsn_tipoPreco=MENSAL" +
                "&_br_org_ccee_pld_historico_PLDHistoricoPortlet_INSTANCE_lzsn_inputFinalDate=" + endDate.format(FORMATTER);

        Exception lastException = null;

        for (int attempt = 0; attempt < FIBONACCI_DELAYS.length; attempt++) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    loggerService.info("CCEE_CLIENT", this.getClass(), "Download bem-sucedido na tentativa {}", attempt + 1);
                    return conn.getInputStream();
                } else {
                    throw new RuntimeException("Falha ao consultar CCEE. HTTP: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                lastException = e;
                int delaySeconds = FIBONACCI_DELAYS[attempt];
                loggerService.warn("CCEE_CLIENT", this.getClass(),
                        "Tentativa {} falhou. Aguardando {} segundos antes da próxima tentativa. Erro: {}",
                        attempt + 1, delaySeconds, e.getMessage());
                Thread.sleep(delaySeconds * 1000L);
            }
        }


        loggerService.error("CCEE_CLIENT", this.getClass(),
                "Todas as tentativas falharam ao consultar CCEE.", lastException);
        throw new RuntimeException("Todas as tentativas falharam ao consultar CCEE.", lastException);
    }
}