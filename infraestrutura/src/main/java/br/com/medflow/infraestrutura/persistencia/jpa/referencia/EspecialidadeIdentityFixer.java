package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ajusta a identidade/sequence da tabela especialidades ao iniciar a aplicação.
 * Útil quando a tabela foi povoada manualmente e a identidade do banco embutido
 * (H2) ficou dessincronizada, causando Unique/PK violations (23505) em inserts.
 */
@Component
public class EspecialidadeIdentityFixer {

    private static final Logger LOGGER = Logger.getLogger(EspecialidadeIdentityFixer.class.getName());

    private final JdbcTemplate jdbc;
    private final EspecialidadeJpaRepository repository;

    @Autowired
    public EspecialidadeIdentityFixer(JdbcTemplate jdbc, EspecialidadeJpaRepository repository) {
        this.jdbc = jdbc;
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            Integer maxId = repository.findMaxId();
            int next = (maxId == null) ? 1 : (maxId + 1);

            // H2 supports: ALTER TABLE <table> ALTER COLUMN <column> RESTART WITH <N>
            String sql = "ALTER TABLE especialidades ALTER COLUMN id RESTART WITH " + next;
            jdbc.execute(sql);
            LOGGER.log(Level.INFO, "Realinhado next id de especialidades para: " + next);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Não foi possível realinhar a sequência de especialidades: " + e.getMessage());
        }
    }
}
