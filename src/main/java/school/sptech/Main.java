package school.sptech;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Looca looca = new Looca();
        ConexaoBanco cnx = new ConexaoBanco();
        CapturaDao dao = new CapturaDao(cnx.getTemplate());
        SlackNotifier slackNotifier = new SlackNotifier("#nome-do-seu-canal");

        int i = 1;
        long recebidoAntes = 0;
        long enviadoAntes = 0;

        while (true) {
            int alertaRecebido = 1;
            int alertaEnviado = 1;
            long recebidoAtual = 0;
            long enviadoAtual = 0;

            for (RedeInterface rede : looca.getRede().getGrupoDeInterfaces().getInterfaces()) {
                recebidoAtual += rede.getBytesRecebidos();
                enviadoAtual += rede.getBytesEnviados();
            }

            if (recebidoAntes == 0) {
                recebidoAntes = recebidoAtual;
                enviadoAntes = enviadoAtual;
                Thread.sleep(5000);
                continue;
            }

            long recebidosAgora = recebidoAtual - recebidoAntes;
            long enviadosAgora = enviadoAtual - enviadoAntes;

            Double kbRecebidos = recebidosAgora / (1024.0);
            Double kbEnviados = enviadosAgora / (1024.0);

            System.out.println(String.format("Recebido: %.2f KB", kbRecebidos));
            System.out.println(String.format("Enviado: %.2f KB", kbEnviados));
            System.out.println("-----");

            if (kbRecebidos > (50.0 * 1024.0)) {
                alertaRecebido = 3;
            } else if (kbRecebidos > (20.0 * 1024.0)) {
                alertaRecebido = 2;
            }

            if (kbEnviados > (50.0 * 1024.0)) {
                alertaEnviado = 3;
            } else if (kbEnviados > (20.0 * 1024.0)) {
                alertaEnviado = 2;
            }

            LocalDateTime diaHora = LocalDateTime.now();
            int servidorRecebido = (i % 2 == 0) ? 1 : 2;
            int servidorEnviado = 1;

            dao.salvarCaptura(new Captura(servidorRecebido, 4, 7, kbRecebidos, alertaRecebido, 3), diaHora);
            dao.salvarCaptura(new Captura(servidorEnviado, 4, 6, kbEnviados, alertaEnviado, 3), diaHora);

            if (alertaRecebido == 2 || alertaRecebido == 3) {
                slackNotifier.notificarAlerta(dao, 1, servidorRecebido, 7, kbRecebidos, alertaRecebido, diaHora);
            }

            if (alertaEnviado == 2 || alertaEnviado == 3) {
                slackNotifier.notificarAlerta(dao, 1, servidorEnviado, 6, kbEnviados, alertaEnviado, diaHora);
            }

            recebidoAntes = recebidoAtual;
            enviadoAntes = enviadoAtual;
            i++;
            Thread.sleep(5000);
        }
    }
}