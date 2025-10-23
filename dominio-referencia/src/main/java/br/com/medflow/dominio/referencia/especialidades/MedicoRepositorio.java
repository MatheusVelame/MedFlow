package br.com.medflow.dominio.referencia.especialidades;

public interface MedicoRepositorio {

    /**
     * Conta quantos médicos ativos estão vinculados a uma determinada especialidade.
     * @param nomeEspecialidade O nome da especialidade a ser verificada.
     * @return O número de médicos ativos vinculados.
     */
    int contarMedicosAtivosVinculados(String nomeEspecialidade);
}
