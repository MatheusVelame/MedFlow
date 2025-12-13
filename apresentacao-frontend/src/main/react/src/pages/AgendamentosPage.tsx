// Localização: apresentacao-frontend/src/main/react/src/pages/AgendamentosPage.tsx

import React from 'react';
import { AppointmentForm } from '../components/AppointmentForm';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { useNavigate } from 'react-router-dom';
import { Calendar, TestTube } from 'lucide-react';
import { ExameFormWrapper } from '../components/ExameFormWrapper';
import { useAgendarExame } from '../hooks/useExames';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';

/**
 * Página unificada para criar novos agendamentos de consultas e exames.
 * Oferece interface com abas para escolher o tipo de agendamento.
 */
export const AgendamentosPage: React.FC = () => {
    const navigate = useNavigate();
    const agendar = useAgendarExame();
    const { user } = useAuth();

    const handleConsultaSuccess = () => {
        setTimeout(() => {
            navigate('/consultas');
        }, 2000);
    };

    const handleExameSuccess = async (data: any) => {
        try {
            // Formata data/hora para o backend (adiciona segundos se necessário)
            const dataHora = data.dataHora.length === 16 ? `${data.dataHora}:00` : data.dataHora;
            const responsavelId = Number(user?.id || 1);
            
            await agendar.mutateAsync({ 
                pacienteId: Number(data.pacienteId), 
                medicoId: Number(data.medicoId), 
                tipoExame: data.tipoExame, 
                dataHora, 
                responsavelId 
            });
            
            toast.success("Exame agendado com sucesso!", {
                description: "Redirecionando para a lista de exames...",
                duration: 3000,
            });
            
            setTimeout(() => {
                navigate('/exames');
            }, 2000);
        } catch (e: any) {
            toast.error("Erro ao agendar exame", {
                description: e?.message ?? "Ocorreu um erro ao agendar o exame.",
                duration: 5000,
            });
        }
    };

    return (
        <div className="container mx-auto p-6 max-w-5xl">
            <div className="mb-6 flex items-center gap-3">
                <Calendar className="h-8 w-8 text-primary" />
                <div>
                    <h1 className="text-3xl font-bold">Novo Agendamento</h1>
                    <p className="text-muted-foreground">
                        Escolha o tipo de agendamento e preencha os dados necessários
                    </p>
                </div>
            </div>

            <Tabs defaultValue="consulta" className="w-full">
                <TabsList className="grid w-full grid-cols-2 mb-6">
                    <TabsTrigger value="consulta" className="flex items-center gap-2">
                        <Calendar className="h-4 w-4" />
                        Consulta Médica
                    </TabsTrigger>
                    <TabsTrigger value="exame" className="flex items-center gap-2">
                        <TestTube className="h-4 w-4" />
                        Exame
                    </TabsTrigger>
                </TabsList>

                <TabsContent value="consulta">
                    <Card>
                        <CardHeader>
                            <CardTitle>Agendar Consulta</CardTitle>
                            <CardDescription>
                                Preencha os dados abaixo para agendar uma consulta médica
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <AppointmentForm onAgendamentoSuccess={handleConsultaSuccess} />
                        </CardContent>
                    </Card>

                    <div className="mt-4 text-sm text-muted-foreground">
                        <p>
                            <strong>Dica:</strong> Após agendar, você será redirecionado para a página de consultas 
                            onde poderá visualizar e gerenciar todos os agendamentos.
                        </p>
                    </div>
                </TabsContent>

                <TabsContent value="exame">
                    <Card>
                        <CardHeader>
                            <CardTitle>Agendar Exame</CardTitle>
                            <CardDescription>
                                Preencha os dados abaixo para solicitar um exame laboratorial ou de imagem
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <ExameFormWrapper onSave={handleExameSuccess} />
                        </CardContent>
                    </Card>

                    <div className="mt-4 text-sm text-muted-foreground">
                        <p>
                            <strong>Dica:</strong> Após agendar, você será redirecionado para a página de exames 
                            onde poderá acompanhar o status do seu agendamento.
                        </p>
                    </div>
                </TabsContent>
            </Tabs>
        </div>
    );
};

export default AgendamentosPage;
