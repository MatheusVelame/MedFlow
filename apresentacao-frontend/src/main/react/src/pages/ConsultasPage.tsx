// Localização: apresentacao-frontend/src/main/react/src/pages/ConsultasPage.tsx

import React, { useState } from 'react';
// IMPORTAÇÃO CORRIGIDA: Não precisa mais do AppointmentForm aqui
import { ConsultasLista } from './ConsultasLista';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Separator } from '../components/ui/separator';
// IMPORTADO O NOVO COMPONENTE DE DIALOG
import { AgendamentoDialog } from '../components/AgendamentoDialog'; 

export const ConsultasPage: React.FC = () => {
    // Estado para forçar a recarga da lista quando o agendamento for concluído
    const [refreshToggle, setRefreshToggle] = useState(0); 

    const handleAgendamentoSuccess = () => {
        // Incrementa o estado para disparar o useEffect na ConsultasLista
        setRefreshToggle(prev => prev + 1); 
    };

    return (
        <div className="p-6 space-y-6">
            <h1 className="text-3xl font-bold">Gestão de Consultas</h1>

            {/* Nova seção para o botão de agendamento */}
            <div className="flex justify-end">
                {/* Usa o componente de Dialog para exibir o formulário */}
                <AgendamentoDialog onAgendamentoSuccess={handleAgendamentoSuccess} />
            </div>

            <Separator />
            
            {/* Coluna da Lista de Consultas */}
            <Card>
                <CardContent>
                    {/* Passa o toggle para que a lista recarregue ao agendar */}
                    <ConsultasLista refreshToggle={refreshToggle} />
                </CardContent>
            </Card>
        </div>
    );
};