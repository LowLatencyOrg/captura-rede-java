package school.sptech;

public class Captura {

    private Integer fkServidor;
    private Integer fkComponente;
    private Integer fkMetrica;
    private Double valor;
    private Integer fkAlerta;
    private Integer fkUnidadeMedida;

    public Captura() {
    }

    public Captura(Integer fkServidor, Integer fkComponente, Integer fkMetrica, Double valor, Integer fkAlerta, Integer fkUnidadeMedida) {
        this.fkServidor = fkServidor;
        this.fkComponente = fkComponente;
        this.fkMetrica = fkMetrica;
        this.valor = valor;
        this.fkAlerta = fkAlerta;
        this.fkUnidadeMedida = fkUnidadeMedida;
    }

    public Integer getFkUnidadeMedida() {
        return fkUnidadeMedida;
    }

    public void setFkUnidadeMedida(Integer fkUnidadeMedida) {
        this.fkUnidadeMedida = fkUnidadeMedida;
    }

    public Integer getFkServidor() {
        return fkServidor;
    }

    public void setFkServidor(Integer fkServidor) {
        this.fkServidor = fkServidor;
    }

    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
    }

    public Integer getFkMetrica() {
        return fkMetrica;
    }

    public void setFkMetrica(Integer fkMetrica) {
        this.fkMetrica = fkMetrica;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getFkAlerta() {
        return fkAlerta;
    }

    public void setFkAlerta(Integer fkAlerta) {
        this.fkAlerta = fkAlerta;
    }
}
