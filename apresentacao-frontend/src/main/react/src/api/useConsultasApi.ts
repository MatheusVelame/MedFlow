// Localização: apresentacao-frontend/src/main/react/src/api/useConsultasApi.ts (Corrigido)

import { api } from './apiClient'; // CORRIGIDO: Agora importa 'api'
import { ConsultaResumo, ConsultaDetalhes, StatusConsulta } from './types'; 

// --- Tipos de Requisição ---

/**
 * Tipo para a requisição de Agendamento (POST /backend/consultas)
 */
export interface AgendamentoRequest {
    dataHora: string; 
    descricao: string;
    pacienteId: number;
    medicoId: number;
    usuarioId: number; 
}

/**
 * Tipo para a requisição de Mudança de Status (PUT /backend/consultas/{id}/status)
 */
export interface StatusUpdateRequest {
    novoStatus: StatusConsulta; 
    usuarioId: number; 
}

export const useConsultasApi = () => {
    
    const URL_BASE = '/backend/consultas'; 

    /**
     * [QUERY] Busca a lista resumida de todas as consultas.
     * GET /backend/consultas
     */
    const listarConsultas = async (): Promise<ConsultaResumo[]> => {
        // Uso corrigido de 'api'
        const response = await api.get<ConsultaResumo[]>(URL_BASE); 
        return response.data;
    };
    
    /**
     * [COMMAND] Agenda uma nova consulta.
     * POST /backend/consultas
     */
    const agendarConsulta = async (dados: AgendamentoRequest): Promise<void> => {
        // Uso corrigido de 'api'
        await api.post(URL_BASE, dados); 
    };

    /**
     * [COMMAND] Altera o status de uma consulta existente.
     * PUT /backend/consultas/{id}/status
     */
    const mudarStatus = async (id: number, dados: StatusUpdateRequest): Promise<void> => {
        // Uso corrigido de 'api'
        await api.put(`${URL_BASE}/${id}/status`, dados); 
    };

    /**
     * [QUERY] Obtém detalhes de uma consulta por ID.
     * GET /backend/consultas/{id}
     */
    const obterDetalhes = async (id: number): Promise<ConsultaDetalhes> => {
        // Uso corrigido de 'api'
        const response = await api.get<ConsultaDetalhes>(`${URL_BASE}/${id}`); 
        return response.data;
    };
    
    /**
     * [QUERY] Busca consultas apenas com status AGENDADA.
     * GET /backend/consultas/agendadas
     */
    const listarConsultasAgendadas = async (): Promise<ConsultaResumo[]> => {
        // Uso corrigido de 'api'
        const response = await api.get<ConsultaResumo[]>(`${URL_BASE}/agendadas`);
        return response.data;
    };

    return {
        listarConsultas,
        agendarConsulta,
        mudarStatus,
        obterDetalhes,
        listarConsultasAgendadas
    };
};