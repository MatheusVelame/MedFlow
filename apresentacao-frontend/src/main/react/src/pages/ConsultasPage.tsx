// Localização: apresentacao-frontend/src/main/react/src/pages/ConsultasPage.tsx

import React, { useState } from 'react';
import { ConsultasLista } from './ConsultasLista'; 
// import { MedicalLayout } from '../components/MedicalLayout'; // <<--- REMOVIDO: Era a causa da duplicação do header.
import { AppointmentForm } from '../components/AppointmentForm'; // Importar o Formulário
import { Button } from '../components/ui/button'; // Novo import para o botão
import { Plus } from 'lucide-react'; // Novo import para ícone
import { toast } from 'sonner';


/**
 * Página principal para Gerenciamento de Consultas.
 */
export const ConsultasPage: React.FC = () => {
    // Estado para forçar a atualização da lista após um agendamento bem-sucedido
    const [refreshToggle, setRefreshToggle] = useState(0);
    const [isFormOpen, setIsFormOpen] = useState(false); // Estado para controlar o modal do form

    const handleAgendamentoSucesso = () => {
        // Incrementa o estado para disparar o useEffect na ConsultasLista
        setRefreshToggle(prev => prev + 1); 
        setIsFormOpen(false); // Fecha o modal após o sucesso
    };
    
    // Função de mock para salvar agendamento (conforme AppointmentForm.tsx)
    const handleSave = (data: any) => {
        console.log("Simulação de salvamento de agendamento:", data);
        // Aqui você chamaria a API real para agendar
        // Se o agendamento foi bem-sucedido, chame:
        toast.success("Agendamento salvo com sucesso (simulado).");
        handleAgendamentoSucesso();
    };


    return (
        <div className="space-y-6"> {/* Novo wrapper para o conteúdo, sem o header duplicado */}
            
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold">Gestão de Consultas</h1>
                <Button onClick={() => setIsFormOpen(true)}>
                    <Plus className="mr-2 h-4 w-4" />
                    Novo Agendamento
                </Button>
            </div>

            {/* Coluna de Query (Listagem) agora ocupa toda a largura disponível */}
            <div className="w-full">
                <ConsultasLista refreshToggle={refreshToggle} />
            </div>

            {/* Modal de Agendamento */}
            <AppointmentForm 
                open={isFormOpen}
                onOpenChange={setIsFormOpen}
                onSave={handleSave} // Passa a função de salvamento
            />
        </div>
    );
};