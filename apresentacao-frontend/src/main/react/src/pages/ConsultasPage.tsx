// Localização: apresentacao-frontend/src/main/react/src/pages/ConsultasPage.tsx

import React, { useState } from 'react';
import { ConsultaFormulario } from '../components/ConsultaFormulario';
import { ConsultasLista } from './ConsultasLista'; 
import { MedicalLayout } from '../components/MedicalLayout'; // Assume-se que este componente existe no seu projeto

/**
 * Página principal para Gerenciamento de Consultas.
 */
export const ConsultasPage: React.FC = () => {
    // Estado para forçar a atualização da lista após um agendamento bem-sucedido
    const [refreshToggle, setRefreshToggle] = useState(0);

    const handleAgendamentoSucesso = () => {
        // Incrementa o estado para disparar o useEffect na ConsultasLista
        setRefreshToggle(prev => prev + 1); 
    };

    return (
        <MedicalLayout>
            <h1 className="text-2xl font-bold mb-2">Gerenciamento de Consultas</h1>
            <p className="text-gray-600 mb-6">Agendamento e acompanhamento das consultas médicas.</p>
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                
                {/* Coluna de Comando (Criação) */}
                <div className="lg:col-span-1">
                    <ConsultaFormulario onConsultaAgendada={handleAgendamentoSucesso} />
                </div>

                {/* Coluna de Query (Listagem) */}
                <div className="lg:col-span-2">
                    <ConsultasLista refreshToggle={refreshToggle} />
                </div>
            </div>
        </MedicalLayout>
    );
};