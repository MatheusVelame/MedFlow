// Localização: src/components/AgendamentoDialog.tsx

import React, { useState } from 'react';
import { Button } from './ui/button';
import { 
    Dialog, 
    DialogContent, 
    DialogHeader, 
    DialogTitle, 
    DialogTrigger 
} from './ui/dialog';
import { AppointmentForm } from './AppointmentForm'; 
import { Plus } from 'lucide-react';

/**
 * Componente de Diálogo para Agendamento de Consultas.
 * Contém o botão de disparo e o formulário de agendamento.
 * @param onAgendamentoSuccess Callback para recarregar a lista na página pai.
 */
export const AgendamentoDialog: React.FC<{ onAgendamentoSuccess: () => void }> = ({ onAgendamentoSuccess }) => {
    const [open, setOpen] = useState(false);

    const handleSuccess = () => {
        setOpen(false); // Fecha o modal após o sucesso
        onAgendamentoSuccess(); // Dispara a recarga da lista
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button className="flex items-center gap-2 bg-green-600 hover:bg-green-700">
                    <Plus className="h-4 w-4" />
                    Cadastrar Nova Consulta
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                    <DialogTitle>Novo Agendamento</DialogTitle>
                </DialogHeader>
                {/* O formulário agora recebe o callback para fechar o modal após o sucesso */}
                <AppointmentForm onAgendamentoSuccess={handleSuccess} />
            </DialogContent>
        </Dialog>
    );
};