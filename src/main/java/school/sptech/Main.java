package school.sptech;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Looca looca = new Looca();

        long recebidoAntes = 0;
        long enviadoAntes = 0;

        while (true) {
            long recebido = 0;
            long enviado = 0;

            for (RedeInterface rede : looca.getRede().getGrupoDeInterfaces().getInterfaces()) {
                recebido += rede.getBytesRecebidos();
                enviado += rede.getBytesEnviados();
            }

            if (recebidoAntes != 0) {
                System.out.println("Recebido: " + (recebido - recebidoAntes) / 1024 + " KB");
                System.out.println("Enviado: " + (enviado - enviadoAntes) / 1024 + " KB");
                System.out.println("-----");
            }

            recebidoAntes = recebido;
            enviadoAntes = enviado;

            Thread.sleep(5000);
        }
    }
}