package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoBanco {
    private final JdbcTemplate template;
    private final BasicDataSource basicDataSource;


    public ConexaoBanco() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/lowLatency?serverTimezone=America/Sao_Paulo");
        basicDataSource.setUsername("client_java");
        basicDataSource.setPassword("Sptech#2026");

        this.basicDataSource = basicDataSource;
        this.template = new JdbcTemplate(basicDataSource);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public JdbcTemplate getTemplate() {
        return template;
    }
}
