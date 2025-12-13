// Localização: apresentacao-frontend/src/main/react/src/components/ConsultaFormulario.tsx

import React from 'react';
import { useForm } from 'react-hook-form';
import { useConsultasApi, AgendamentoRequest } from '../api/useConsultasApi';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Card, CardHeader, CardTitle, CardContent } from './ui/card';
import { toast } from 'sonner';

// Mock do ID do usuário responsável pela ação. 
const MOCK_USUARIO_ID = 100; 

type FormValues = Omit<AgendamentoRequest, 'usuarioId'>;

/**
 * Componente de Formulário para Agendar/Criar uma nova consulta (Comando POST).
 * @param onConsultaAgendada Callback para ser chamado após o sucesso do agendamento.
 */
export const ConsultaFormulario: React.FC<{ onConsultaAgendada: () => void }> = ({ onConsultaAgendada }) => {
    const { agendarConsulta } = useConsultasApi();
    
    // Define uma data/hora inicial no formato local (YYYY-MM-DDTHH:MM)
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    const defaultDataHora = (new Date(now.getTime() - offset)).toISOString().slice(0, 16);
    
    const { 
        register, 
        handleSubmit, 
        reset, 
        formState: { isSubmitting, errors } 
    } = useForm<FormValues>({
        defaultValues: {
            dataHora: defaultDataHora,
            descricao: "Consulta de rotina",
            pacienteId: 1, 
            medicoId: 1,
        }
    });

    const onSubmit = async (data: FormValues) => {
        try {
            // Adiciona segundos e mapeia para o formato esperado pelo backend (LocalDateTime)
            const dataHoraIso = `${data.dataHora}:00`; 

            const requestData: AgendamentoRequest = {
                ...data,
                dataHora: dataHoraIso,
                usuarioId: MOCK_USUARIO_ID,
            };

            // Chamada POST
            await agendarConsulta(requestData);
            
            toast.success("Consulta agendada com sucesso!");
            reset();
            onConsultaAgendada(); // Recarrega a lista
        } catch (error) {
            console.error("Erro ao agendar consulta:", error);
            toast.error("Falha no agendamento.", { 
                description: "Verifique os dados, a disponibilidade e se o usuário/médico/paciente existem." 
            });
        }
    };

    return (
        <Card className="w-full">
            <CardHeader>
                <CardTitle>Agendar Nova Consulta</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    
                    {/* Data e Hora */}
                    <div className="grid gap-2">
                        <Label htmlFor="dataHora">Data e Hora</Label>
                        <Input 
                            id="dataHora" 
                            type="datetime-local" 
                            {...register("dataHora", { required: "Data e hora são obrigatórias." })} 
                            className={errors.dataHora ? "border-red-500" : ""}
                        />
                         {errors.dataHora && <p className="text-sm text-red-500">{errors.dataHora.message}</p>}
                    </div>
                    
                    {/* Descrição */}
                    <div className="grid gap-2">
                        <Label htmlFor="descricao">Descrição</Label>
                        <Input 
                            id="descricao" 
                            type="text" 
                            {...register("descricao")} 
                        />
                    </div>

                    {/* IDs Paciente e Médico */}
                    <div className="grid grid-cols-2 gap-4">
                        <div className="grid gap-2">
                            <Label htmlFor="pacienteId">ID do Paciente</Label>
                            <Input 
                                id="pacienteId" 
                                type="number" 
                                {...register("pacienteId", { 
                                    valueAsNumber: true, 
                                    required: "ID do Paciente é obrigatório." 
                                })} 
                                className={errors.pacienteId ? "border-red-500" : ""}
                            />
                            {errors.pacienteId && <p className="text-sm text-red-500">{errors.pacienteId.message}</p>}
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="medicoId">ID do Médico</Label>
                            <Input 
                                id="medicoId" 
                                type="number" 
                                {...register("medicoId", { 
                                    valueAsNumber: true, 
                                    required: "ID do Médico é obrigatório." 
                                })} 
                                className={errors.medicoId ? "border-red-500" : ""}
                            />
                            {errors.medicoId && <p className="text-sm text-red-500">{errors.medicoId.message}</p>}
                        </div>
                    </div>

                    <Button type="submit" className="w-full" disabled={isSubmitting}>
                        {isSubmitting ? "Agendando..." : "Confirmar Agendamento"}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
};