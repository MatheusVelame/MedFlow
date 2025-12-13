// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoCadastroRequest.java

package br.com.medflow.aplicacao.administracao.medicos;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para cadastro de novo médico.
 */
public class MedicoCadastroRequest {

    private String nome;
    private String contato;
    private String crmNumero;
    private String crmUf;
    private Integer especialidadeId;
    private LocalDate dataNascimento;
    private List<DisponibilidadeRequest> disponibilidades;

    // Construtor vazio
    public MedicoCadastroRequest() {}

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

    public String getCrmNumero() {
        return crmNumero;
    }

    public void setCrmNumero(String crmNumero) {
        this.crmNumero = crmNumero;
    }

    public String getCrmUf() {
        return crmUf;
    }

    public void setCrmUf(String crmUf) {
        this.crmUf = crmUf;
    }

    public Integer getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Integer especialidadeId) {
        this.especialidadeId = especialidadeId;
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