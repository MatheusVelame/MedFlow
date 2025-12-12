// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoAtualizacaoRequest.java

package br.com.medflow.aplicacao.administracao.medicos;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para atualização de médico.
 *
 * Campos IMUTÁVEIS (não podem ser atualizados):
 * - CRM (regra de negócio)
 * - Especialidade (deve usar endpoint específico se necessário)
 */
public class MedicoAtualizacaoRequest {

    private String nome;
    private String contato;
    private LocalDate dataNascimento;
    private List<DisponibilidadeRequest> disponibilidades;

    // Construtor vazio
    public MedicoAtualizacaoRequest() {}

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<DisponibilidadeRequest> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<DisponibilidadeRequest> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }

    /**
     * DTO para disponibilidade de horários.
     */
    public static class DisponibilidadeRequest {
        private String diaSemana;
        private String horaInicio; // "08:00"
        private String horaFim;    // "12:00"

        public DisponibilidadeRequest() {}

        public String getDiaSemana() {
            return diaSemana;
        }

        public void setDiaSemana(String diaSemana) {
            this.diaSemana = diaSemana;
        }

        public String getHoraInicio() {
            return horaInicio;
        }

        public void setHoraInicio(String horaInicio) {
            this.horaInicio = horaInicio;
        }

        public String getHoraFim() {
            return horaFim;
        }

        public void setHoraFim(String horaFim) {
            this.horaFim = horaFim;
        }
    }
}