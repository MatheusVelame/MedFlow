// Localização: apresentacao-frontend/src/main/react/src/pages/ConsultasLista.tsx

import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useConsultasApi } from '../api/useConsultasApi';
import { ConsultaResumo, StatusConsulta, ConsultaDetalhes } from '../api/types'; // Importado ConsultaDetalhes
import { useAuth } from '../contexts/AuthContext';
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
import { formatDataHora } from '../lib/utils'; 

// Mapeamento de cores para os Status
const statusColors: Record<StatusConsulta, string> = {
    AGENDADA: "bg-blue-500 hover:bg-blue-600",
    EM_ANDAMENTO: "bg-blue-300 hover:bg-blue-600",
    REALIZADA: "bg-green-500 hover:bg-green-600",
    CANCELADA: "bg-red-500 hover:bg-red-600",
};

// NOVO TIPO: Armazena os detalhes carregados em cache
type DetalhesCache = Record<number, ConsultaDetalhes>;

/**
 * Componente para listar consultas e permitir a mudança de status.
 * Implementa Lazy Loading para detalhes (descrição e nomes completos).
 * @param refreshToggle Usado para forçar a recarga de dados após um agendamento.
 */
export const ConsultasLista: React.FC<{ refreshToggle: number }> = ({ refreshToggle }) => {
    // Adicionado 'obterDetalhes' ao destructuring
    const { listarConsultas, mudarStatus, obterDetalhes } = useConsultasApi(); 
    const { user } = useAuth();
    const [consultas, setConsultas] = useState<ConsultaResumo[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isUpdating, setIsUpdating] = useState<number | null>(null); 
    // Estado para armazenar os detalhes que já foram carregados
    const [detalhesCache, setDetalhesCache] = useState<DetalhesCache>({}); 
    // Estado para saber qual ID está sendo carregado no momento
    const [fetchingDetailId, setFetchingDetailId] = useState<number | null>(null);

    // Obter ID do usuário logado
    const usuarioId = useMemo(() => {
        return user?.id ? parseInt(user.id) : 1;
    }, [user]); 

    // Função para buscar a lista de consultas resumida
    const fetchConsultas = async () => {
        setIsLoading(true);
        try {
            const data = await listarConsultas(); 
            setConsultas(data);
            // Opcional: Manter o cache de detalhes ou limpá-lo ao recarregar a lista principal
            // setDetalhesCache({}); 
        } catch (error) {
            toast.error("Erro ao carregar consultas.");
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    // Função para buscar os detalhes de UMA consulta por ID
    const fetchDetalhes = useCallback(async (id: number) => {
        // Se já estiver no cache ou já estiver buscando, interrompe.
        if (detalhesCache[id] || fetchingDetailId === id) {
            return;
        }

        setFetchingDetailId(id);
        try {
            // QUERY: GET /backend/consultas/{id}
            const detalhes = await obterDetalhes(id); 
            
            // Armazena no cache
            setDetalhesCache(prev => ({
                ...prev,
                [id]: detalhes 
            }));

        } catch (error) {
            console.error(`Erro ao carregar detalhes da consulta ${id}:`, error);
            // Não exibe toast de erro para não ser muito intrusivo no hover
        } finally {
            setFetchingDetailId(null);
        }
    }, [detalhesCache, obterDetalhes, fetchingDetailId]); // Dependências do useCallback

    useEffect(() => {
        fetchConsultas();
    }, [refreshToggle]);

    // ... handleStatusChange (mantido inalterado)

    const handleStatusChange = async (consultaId: number, novoStatus: string) => {
        if (!['AGENDADA', 'EM_ANDAMENTO', 'REALIZADA', 'CANCELADA'].includes(novoStatus as StatusConsulta)) {
            toast.error("Status inválido.");
            return;
        }

        setIsUpdating(consultaId);
        try {
            const statusType = novoStatus as StatusConsulta;
            await mudarStatus(consultaId, { 
                novoStatus: statusType, 
                usuarioId: usuarioId 
            });
            
            toast.success(`Consulta #${consultaId} atualizada para ${novoStatus}.`);
            
            // Atualiza o estado local
            setConsultas(prev => prev.map(c => 
                c.id === consultaId ? { ...c, status: statusType } : c
            ));
            
            // Se o detalhe estiver em cache, atualiza o status dele também
            setDetalhesCache(prev => {
                if(prev[consultaId]) {
                    return {...prev, [consultaId]: {...prev[consultaId], status: statusType}}
                }
                return prev;
            })

        } catch (error) {
            console.error("Erro ao mudar status:", error);
            toast.error("Falha ao mudar status. Verifique as regras de negócio.");
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
                        consultas.map((consulta) => {
                            const detalhes = detalhesCache[consulta.id];
                            const isFetching = fetchingDetailId === consulta.id;
                            const isLoaded = !!detalhes;

                            return (
                                <TableRow 
                                    key={consulta.id}
                                    // Adicionado onMouseEnter para disparar o lazy loading
                                    onMouseEnter={() => fetchDetalhes(consulta.id)} 
                                    // Opcional: adicione onClick para garantir o carregamento em telas touch
                                    onClick={() => fetchDetalhes(consulta.id)} 
                                >
                                    <TableCell className="font-medium">{consulta.id}</TableCell>
                                    <TableCell>
                                        {formatDataHora(consulta.dataHora)}
                                    </TableCell>
                                    
                                    {/* COLUNA: DESCRIÇÃO - Exibe o detalhe se estiver carregado */}
                                    <TableCell className="text-sm max-w-xs truncate">
                                        {isFetching ? (
                                            <Loader2 className="h-4 w-4 animate-spin inline mr-2 text-gray-500" />
                                        ) : isLoaded ? (
                                            detalhes.descricao 
                                        ) : (
                                            consulta.descricao || '-' // Valor padrão do resumo
                                        )}
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
                                                <SelectItem value="AGENDADA">Agendada</SelectItem>
                                                <SelectItem value="EM_ANDAMENTO">Em Andamento</SelectItem>
                                                <SelectItem value="REALIZADA">Finalizar</SelectItem>
                                                <SelectItem value="CANCELADA">Cancelar</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </TableCell>
                                </TableRow>
                            );
                        })
                    )}
                </TableBody>
            </Table>
            </div>
        </div>
    );
};