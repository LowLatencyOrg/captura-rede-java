package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

public class CapturaDao {

    private final JdbcTemplate jdbcTemplate;

    public CapturaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvarCaptura(Captura captura, LocalDateTime diaHora) {
        if (captura.getFkServidor() == null || captura.getFkComponente() == null || captura.getFkMetrica() == null || captura.getFkAlerta() == null || captura.getValor() == null) {

        } else {
            String sql = "INSERT INTO leitura (fk_servidor, fk_componente, fk_metrica, valor, fk_unidade_medida, fk_alerta, dia_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, captura.getFkServidor(), captura.getFkComponente(), captura.getFkMetrica(), captura.getValor(), captura.getFkUnidadeMedida(), captura.getFkAlerta(), diaHora);
        }
    }
}
