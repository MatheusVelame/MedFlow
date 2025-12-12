package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * Entidade JPA para Disponibilidade de horários do médico.
 *
 * Representa os horários em que o médico está disponível para atendimento.
 * Exemplo: Segunda-feira das 08:00 às 12:00
 */
@Entity
@Table(name = "medico_disponibilidades")
public class DisponibilidadeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoJpa medico;

    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana; // Segunda, Terça, Quarta, etc.

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    // Construtor vazio para JPA
    public DisponibilidadeJpa() {}

    // Construtor completo
    public DisponibilidadeJpa(
            Long id,
            MedicoJpa medico,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFim) {
        this.id = id;
        this.medico = medico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicoJpa getMedico() {
        return medico;
    }

    public void setMedico(MedicoJpa medico) {
        this.medico = medico;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}
