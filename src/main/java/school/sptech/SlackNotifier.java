package school.sptech;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SlackNotifier {

    private static final long COOLDOWN_MS = 300_000L; // 5 minutos entre alertas iguais
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String webhookUrl;
    private final String canal;
    private final HttpClient httpClient;
    private final Map<String, Long> ultimoAlerta = new HashMap<>();

    public SlackNotifier(String canalPadrao) {
        this.webhookUrl = System.getenv("SLACK_WEBHOOK_URL");
        String canalEnv = System.getenv("SLACK_CHANNEL");
        this.canal = (canalEnv != null && !canalEnv.isBlank()) ? canalEnv : canalPadrao;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    private boolean podeNotificar(int fkServidor, int fkMetrica, int nivelAlerta) {
        String chave = fkServidor + "-" + fkMetrica + "-" + nivelAlerta;
        long agora = System.currentTimeMillis();
        long ultimo = ultimoAlerta.getOrDefault(chave, 0L);

        if (agora - ultimo >= COOLDOWN_MS) {
            ultimoAlerta.put(chave, agora);
            return true;
        }
        return false;
    }

    public void notificarAlerta(
            CapturaDao dao,
            int fkEmpresa,
            int fkServidor,
            int fkMetrica,
            double valorKb,
            int nivelAlerta,
            LocalDateTime diaHora
    ) {
        if (!podeNotificar(fkServidor, fkMetrica, nivelAlerta)) {
            return;
        }

        String metricaNome = (fkMetrica == 7) ? "Recebido (KB)" : "Enviado (KB)";
        String status = (nivelAlerta == 3) ? "CRITICO" : "ALERTA";

        // 1. Grava na tabela slack do MySQL (respeitando VARCHAR(45))
        String tituloDb = limitar(String.format("[%s] Rede - %s", status, metricaNome), 45);
        String conteudoDb = limitar(
                String.format(Locale.US, "Srv #%d: %.2f KB (%s)", fkServidor, valorKb, diaHora.format(FORMATO_HORA)),
                45
        );
        String canalDb = limitar(this.canal, 45);

        dao.salvarAlertaSlack(tituloDb, conteudoDb, canalDb, fkEmpresa);

        // 2. Envia a notificação HTTP para o Webhook do Slack
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[AVISO] SLACK_WEBHOOK_URL não configurada; alerta guardado apenas na base de dados.");
            return;
        }

        String mensagemSlack = String.format(
                Locale.US,
                "%s *[%s] Alerta lowLatency — Servidor #%d*\n*Componente:* Rede\n*Métrica:* %s\n*Leitura:* `%.2f KB`\n*Data/Hora:* %s",
                status,
                fkServidor,
                metricaNome,
                valorKb,
                diaHora.format(FORMATO_DATA_HORA)
        );

        String jsonPayload = "{\"text\":\"" + escaparJson(mensagemSlack) + "\"}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("[ERRO SLACK] HTTP " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            System.err.println("[ERRO SLACK] Falha ao enviar notificação: " + e.getMessage());
        }
    }

    private static String limitar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private static String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}