package br.com.medflow.apresentacao.referencia.especialidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeFormulario(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String nome,

    @Size(max = 255, message = "Description must have at most 255 characters")
    String descricao
) {}