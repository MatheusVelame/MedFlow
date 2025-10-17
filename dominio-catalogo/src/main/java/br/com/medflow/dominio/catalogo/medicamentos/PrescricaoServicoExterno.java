package br.com.medflow.dominio.catalogo.medicamentos;

// Simula a consulta de vínculo com prescrições ativas
class PrescricaoServicoExterno {
    public boolean possuiPrescricaoAtiva(String medicamentoId) {
        // ID especial para o cenário de falha por vínculo ativo
        return medicamentoId.equals("MED_DIPIRONA_ATIVO"); 
    }
}