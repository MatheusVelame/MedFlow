// Localização: apresentacao-frontend/src/main/react/src/pages/ConsultasLista.tsx

import React, { useState, useEffect } from 'react';
import { useConsultasApi } from '../api/useConsultasApi';
import { ConsultaResumo, StatusConsulta } from '../api/types'; 
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../components/ui/table';
import { Badge } from '../components/ui/badge';
import { 
    Select, 
    SelectContent, 
    SelectItem, 
    SelectTrigger, 
    SelectValue 
} from '../components/ui/select';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { formatDataHora } from '../lib/utils'; // Importa a função de formatação segura

// Mock do ID do usuário responsável pela ação de alteração de status
const MOCK_USUARIO_ID = 100; 

// Mapeamento de cores para os Status
const statusColors: Record<StatusConsulta, string> = {
    EM_ANDAMENTO: "bg-blue-500 hover:bg-blue-600",
    REALIZADA: "bg-green-500 hover:bg-green-600",
    CANCELADA: "bg-red-500 hover:bg-red-600",
};

/**
 * Componente para listar consultas e permitir a mudança de status.
 * @param refreshToggle Usado para forçar a recarga de dados após um agendamento.
 */
export const ConsultasLista: React.FC<{ refreshToggle: number }> = ({ refreshToggle }) => {
    const { listarConsultas, mudarStatus } = useConsultasApi();
    const [consultas, setConsultas] = useState<ConsultaResumo[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isUpdating, setIsUpdating] = useState<number | null>(null); 

    const fetchConsultas = async () => {
        setIsLoading(true);
        try {
            // QUERY: GET /backend/consultas
            const data = await listarConsultas(); 
            setConsultas(data);
        } catch (error) {
            toast.error("Erro ao carregar consultas.");
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    // Recarrega a lista quando o componente é montado ou quando refreshToggle muda
    useEffect(() => {
        fetchConsultas();
    }, [refreshToggle]);

    /**
     * Lida com a mudança de status da consulta (Comando PUT)
     */
    const handleStatusChange = async (consultaId: number, novoStatus: string) => {
        if (!['AGENDADA', 'REALIZADA', 'CANCELADA'].includes(novoStatus)) {
            toast.error("Status inválido.");
            return;
        }

        setIsUpdating(consultaId);
        try {
            const statusType = novoStatus as StatusConsulta;

            // COMMAND: PUT /backend/consultas/{id}/status
            await mudarStatus(consultaId, { 
                novoStatus: statusType, 
                usuarioId: MOCK_USUARIO_ID 
            });
            
            toast.success(`Consulta #${consultaId} atualizada para ${novoStatus}.`);
            
            // Atualiza o estado local para refletir a mudança imediatamente
            setConsultas(prev => prev.map(c => 
                c.id === consultaId ? { ...c, status: statusType } : c
            ));

        } catch (error) {
            console.error("Erro ao mudar status:", error);
            toast.error("Falha ao mudar status. Verifique as regras de negócio (ex: status inválido, consulta não encontrada).");
        } finally {
            setIsUpdating(null);
        }
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center h-40">
                <Loader2 className="mr-2 h-6 w-6 animate-spin text-blue-500" /> 
                <span className="text-gray-600">Carregando Consultas...</span>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <h2 className="text-2xl font-bold">Consultas Agendadas e Realizadas</h2>
            <div className="border rounded-lg overflow-x-auto">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead className="w-[50px]">ID</TableHead>
                        <TableHead className="w-[200px]">Data/Hora</TableHead>
                        <TableHead>Descrição</TableHead>
                        <TableHead className="w-[100px]">Pac. ID</TableHead>
                        <TableHead className="w-[100px]">Med. ID</TableHead>
                        <TableHead className="w-[120px]">Status Atual</TableHead>
                        <TableHead className="text-right w-[180px]">Mudar Status</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {consultas.length === 0 ? (
                         <TableRow>
                            <TableCell colSpan={7} className="text-center py-8 text-gray-500">
                                Nenhuma consulta encontrada. Agende uma nova!
                            </TableCell>
                        </TableRow>
                    ) : (
                        consultas.map((consulta) => (
                            <TableRow key={consulta.id}>
                                <TableCell className="font-medium">{consulta.id}</TableCell>
                                <TableCell>
                                    {/* Usa a função formatDataHora para lidar com strings de data inválidas/nulas */}
                                    {formatDataHora(consulta.dataHora)}
                                </TableCell>
                                <TableCell className="text-sm text-gray-500">
                                    {/* CORREÇÃO APLICADA: Tratamento para campos vazios ou nulos */}
                                    {consulta.descricao || '-'}
                                </TableCell>
                                <TableCell>
                                    {/* CORREÇÃO APLICADA: Tratamento para campos vazios ou nulos */}
                                    {consulta.pacienteId ?? '-'}
                                </TableCell>
                                <TableCell>
                                    {/* CORREÇÃO APLICADA: Tratamento para campos vazios ou nulos */}
                                    {consulta.medicoId ?? '-'}
                                </TableCell>
                                <TableCell>
                                    <Badge className={`${statusColors[consulta.status]}`}>{consulta.status}</Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <Select 
                                        onValueChange={(value) => handleStatusChange(consulta.id, value)}
                                        value={consulta.status}
                                        disabled={isUpdating === consulta.id}
                                    >
                                        <SelectTrigger className="w-[160px] ml-auto">
                                            <SelectValue placeholder="Mudar Status" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="AGENDADA">Agendar/Reagendar</SelectItem>
                                            <SelectItem value="REALIZADA">Finalizar</SelectItem>
                                            <SelectItem value="CANCELADA">Cancelar</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </TableCell>
                            </TableRow>
                        ))
                    )}
                </TableBody>
            </Table>
            </div>
        </div>
    );
};