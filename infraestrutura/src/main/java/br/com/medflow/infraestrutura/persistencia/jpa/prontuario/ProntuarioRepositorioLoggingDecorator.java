package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.Prontuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Decorator para adicionar logging às operações do repositório de Prontuário.
 */
public class ProntuarioRepositorioLoggingDecorator extends ProntuarioRepositorioDecorator {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioRepositorioLoggingDecorator.class);

    public ProntuarioRepositorioLoggingDecorator(ProntuarioRepositorioBase repositorio) {
        super(repositorio);
    }

    @Override
    public void salvar(Prontuario prontuario) {
        logger.info("Salvando prontuário: id={}, pacienteId={}", prontuario.getId(), prontuario.getPacienteId());
        try {
            repositorio.salvar(prontuario);
            logger.info("Prontuário salvo com sucesso: id={}", prontuario.getId());
        } catch (Exception e) {
            logger.error("Erro ao salvar prontuário: id={}", prontuario.getId(), e);
            throw e;
        }
    }

    @Override
    public Optional<Prontuario> obterPorId(String id) {
        logger.debug("Buscando prontuário por ID: {}", id);
        Optional<Prontuario> resultado = repositorio.obterPorId(id);
        if (resultado.isPresent()) {
            logger.debug("Prontuário encontrado: id={}", id);
        } else {
            logger.debug("Prontuário não encontrado: id={}", id);
        }
        return resultado;
    }

    @Override
    public List<Prontuario> buscarPorPaciente(String pacienteId) {
        logger.debug("Buscando prontuários por paciente: pacienteId={}", pacienteId);
        List<Prontuario> resultado = repositorio.buscarPorPaciente(pacienteId);
        logger.debug("Encontrados {} prontuários para o paciente: pacienteId={}", resultado.size(), pacienteId);
        return resultado;
    }

    @Override
    public List<Prontuario> listarTodos() {
        logger.debug("Listando todos os prontuários");
        List<Prontuario> resultado = repositorio.listarTodos();
        logger.debug("Total de prontuários encontrados: {}", resultado.size());
        return resultado;
    }
}
