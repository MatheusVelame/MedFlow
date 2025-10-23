package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;

/**
 * Interface que simula a comunicação com o domínio Compartilhado/Administracao/Catalogo
 * Esta interface permanece aqui para desacoplamento do serviço.
 */
public interface VerificadorExternoServico { // Interface pública e acessível
    boolean pacienteEstaCadastrado(Long pacienteId);
    boolean medicoEstaCadastrado(Long medicoId);
    boolean medicoEstaAtivo(Long medicoId);
    boolean tipoExameEstaCadastrado(String tipoExame);
    boolean medicoEstaDisponivel(Long medicoId, LocalDateTime dataHora);
}
