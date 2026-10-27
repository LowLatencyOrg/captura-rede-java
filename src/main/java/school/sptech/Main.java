package school.sptech;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Looca looca = new Looca();
        ConexaoBanco cnx = new ConexaoBanco();
        CapturaDao dao = new CapturaDao(cnx.getTemplate());

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

            Double mbRecebidos = recebidosAgora / (1024.0 * 1024.0);
            Double mbEnviados = enviadosAgora / (1024.0 * 1024.0);

            System.out.println(String.format("Recebido: %.2f MB", mbRecebidos));
            System.out.println(String.format("Enviado: %.2f MB", mbEnviados));
            System.out.println("-----");

            if (mbRecebidos > 50.0) {
                alertaRecebido = 3;
            } else if (mbRecebidos > 20.0) {
                alertaRecebido = 2;
            }

            if (mbEnviados > 50.0) {
                alertaEnviado = 3;
            } else if (mbEnviados > 20.0) {
                alertaEnviado = 2;
            }

            LocalDateTime diaHora = LocalDateTime.now();
            if (i % 2 == 0) {
                dao.salvarCaptura(new Captura(1, 4, 3, mbRecebidos, alertaRecebido, 3), diaHora);
                dao.salvarCaptura(new Captura(1, 4, 4, mbEnviados, alertaEnviado, 3), diaHora);
            } else {
                dao.salvarCaptura(new Captura(2, 4, 3, mbRecebidos, alertaRecebido, 3), diaHora);
                dao.salvarCaptura(new Captura(1, 4, 4, mbEnviados, alertaEnviado, 3), diaHora);
            }

            recebidoAntes = recebidoAtual;
            enviadoAntes = enviadoAtual;
            i++;
            Thread.sleep(5000);
        }
    }
}