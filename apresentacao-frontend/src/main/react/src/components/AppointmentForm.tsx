// Localização: apresentacao-frontend/src/main/react/src/components/AppointmentForm.tsx

import React, { useState, useMemo, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { format } from "date-fns";
import { Calendar as CalendarIcon, Loader2 } from "lucide-react";
import { toast } from "sonner";

import { useConsultasApi, AgendamentoRequest } from "../api/useConsultasApi";
import { useListarPacientes } from "../api/usePacientesApi";
import { useListarFuncionarios } from "../api/useFuncionariosApi";
import { useAuth } from "../contexts/AuthContext";
import { 
    Form, 
    FormControl, 
    FormField, 
    FormItem, 
    FormLabel, 
    FormMessage 
} from "./ui/form";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { Popover, PopoverContent, PopoverTrigger } from "./ui/popover";
import { Calendar } from "./ui/calendar";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select";
import { cn } from "../lib/utils";
import { Textarea } from "./ui/textarea";

// --- Definição do Schema de Validação (Zod) ---
const AgendamentoSchema = z.object({
    dataHora: z.date({
        required_error: "A data e hora são obrigatórias.",
    }).refine(date => {
        // Permite 60s de tolerância para testes
        const now = new Date();
        now.setSeconds(now.getSeconds() - 60);
        return date.getTime() >= now.getTime();
    }, {
        message: "O agendamento deve ser para o futuro ou presente.",
    }),
    descricao: z.string()
        .min(1, { message: "A descrição é obrigatória." })
        .max(500, { message: "A descrição não pode ter mais de 500 caracteres." })
        .trim(),
    pacienteId: z.coerce.number()
        .int({ message: "ID do paciente inválido." })
        .positive({ message: "Selecione um paciente." })
        .refine(val => val > 0, { 
            message: "Por favor, selecione um paciente da lista." 
        }),
    medicoId: z.coerce.number()
        .int({ message: "ID do médico inválido." })
        .positive({ message: "Selecione um médico." })
        .refine(val => val > 0, { 
            message: "Por favor, selecione um médico da lista." 
        }),
    usuarioId: z.coerce.number()
        .int()
        .positive({ message: "ID do usuário responsável é obrigatório." }),
    hora: z.string()
        .regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, {
            message: "Hora inválida. Use o formato HH:MM (ex: 10:30)."
        })
        .refine(val => {
            // Valida se a hora não é no passado quando combinada com a data
            const [hours, minutes] = val.split(':').map(Number);
            return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
        }, {
            message: "Hora inválida. Deve estar entre 00:00 e 23:59."
        }),
});

type AgendamentoFormValues = z.infer<typeof AgendamentoSchema>;

/**
 * Componente de Formulário para Agendamento de Consultas.
 * @param onAgendamentoSuccess Callback executado após o sucesso do agendamento (usado para fechar o modal e recarregar a lista).
 */
export const AppointmentForm: React.FC<{ onAgendamentoSuccess: () => void }> = ({ onAgendamentoSuccess }) => {
    const { agendarConsulta } = useConsultasApi();
    const { user } = useAuth();
    const { data: pacientes = [], isLoading: isLoadingPacientes } = useListarPacientes();
    const { data: funcionarios = [], isLoading: isLoadingFuncionarios } = useListarFuncionarios();
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Filtrar apenas médicos ativos
    // Busca por "médico", "médica", "medico", "medica" no início da função
    const medicos = useMemo(() => {
        return funcionarios.filter(f => {
            if (f.status !== "ATIVO") return false;
            const funcaoUpper = f.funcao.toUpperCase().trim();
            // Verifica se começa com MÉDICO/MEDICO ou contém a palavra MÉDICO/MEDICO
            return funcaoUpper.startsWith("MÉDICO") || 
                   funcaoUpper.startsWith("MEDICO") || 
                   funcaoUpper.startsWith("MÉDICA") || 
                   funcaoUpper.startsWith("MEDICA") ||
                   funcaoUpper.includes("MÉDICO") ||
                   funcaoUpper.includes("MEDICO");
        });
    }, [funcionarios]);

    const usuarioId = useMemo(() => {
        return user?.id ? parseInt(user.id) : 1;
    }, [user]);

    const form = useForm<AgendamentoFormValues>({
        resolver: zodResolver(AgendamentoSchema),
        defaultValues: {
            descricao: "",
            pacienteId: 0, 
            medicoId: 0,
            usuarioId: usuarioId,
            hora: "10:00"
        },
    });

    // Atualizar usuarioId quando o usuário mudar
    useEffect(() => {
        form.setValue("usuarioId", usuarioId);
    }, [usuarioId, form]);

    /**
     * Combina data e hora em formato ISO para o backend (LocalDateTime).
     * Formato: yyyy-MM-dd'T'HH:mm:ss (ex: 2025-12-15T10:30:00)
     */
    const combineDateTime = (date: Date, timeStr: string): string => {
        const [hours, minutes] = timeStr.split(':').map(Number);
        const combined = new Date(date);
        combined.setHours(hours, minutes, 0, 0); // Define horas, minutos, segundos e milissegundos
        
        // Formato ISO local (sem timezone) compatível com LocalDateTime do Java
        return format(combined, "yyyy-MM-dd'T'HH:mm:ss");
    };

    const onSubmit = async (values: AgendamentoFormValues) => {
        setIsSubmitting(true);
        
        const dataHoraFormatada = combineDateTime(values.dataHora, values.hora);

        const requestData: AgendamentoRequest = {
            dataHora: dataHoraFormatada,
            descricao: values.descricao,
            pacienteId: values.pacienteId,
            medicoId: values.medicoId,
            usuarioId: values.usuarioId,
        };

        try {
            await agendarConsulta(requestData);
            
            toast.success(
                `Consulta agendada com sucesso!`,
                {
                    description: `Data: ${format(values.dataHora, "dd/MM/yyyy")} às ${values.hora}`,
                    duration: 4000,
                }
            );
            
            form.reset({ 
                descricao: "",
                pacienteId: 0,
                medicoId: 0,
                usuarioId: usuarioId,
                hora: "10:00",
                dataHora: undefined
            });
            onAgendamentoSuccess(); // CHAMA O CALLBACK PARA FECHAR O MODAL E RECARREGAR A LISTA
            
        } catch (error) {
            console.error("Erro ao agendar consulta:", error);
            
            // Tratamento específico de erros
            if (error instanceof Error) {
                const errorMessage = error.message;
                
                // Erros específicos do backend
                if (errorMessage.includes("já existe") || errorMessage.includes("conflito")) {
                    toast.error("Conflito de horário", {
                        description: "Já existe uma consulta agendada neste horário. Por favor, escolha outro horário.",
                        duration: 5000,
                    });
                } else if (errorMessage.includes("não encontrado") || errorMessage.includes("inválido")) {
                    toast.error("Dados inválidos", {
                        description: "Paciente ou médico não encontrado. Verifique os dados e tente novamente.",
                        duration: 5000,
                    });
                } else if (errorMessage.includes("indisponível")) {
                    toast.error("Horário indisponível", {
                        description: "O médico não está disponível neste horário.",
                        duration: 5000,
                    });
                } else {
                    toast.error("Erro ao agendar consulta", {
                        description: errorMessage,
                        duration: 5000,
                    });
                }
            } else {
                toast.error("Erro desconhecido", {
                    description: "Ocorreu um erro inesperado. Por favor, tente novamente.",
                    duration: 5000,
                });
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                
                {/* 1. Seleção de Data */}
                <FormField
                    control={form.control}
                    name="dataHora"
                    render={({ field }) => (
                        <FormItem className="flex flex-col">
                            <FormLabel>Data da Consulta</FormLabel>
                            <Popover>
                                <PopoverTrigger asChild>
                                    <FormControl>
                                        <Button
                                            variant={"outline"}
                                            className={cn(
                                                "w-full justify-start text-left font-normal",
                                                !field.value && "text-muted-foreground"
                                            )}
                                        >
                                            <CalendarIcon className="mr-2 h-4 w-4" />
                                            {field.value ? format(field.value, "PPP") : <span>Selecione a data</span>}
                                        </Button>
                                    </FormControl>
                                </PopoverTrigger>
                                <PopoverContent className="w-auto p-0" align="start">
                                    <Calendar
                                        mode="single"
                                        selected={field.value}
                                        onSelect={field.onChange}
                                        disabled={(date) => {
                                            const today = new Date();
                                            today.setHours(0, 0, 0, 0);
                                            return date < today;
                                        }}
                                        initialFocus
                                    />
                                </PopoverContent>
                            </Popover>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                {/* 2. Entrada de Hora */}
                <FormField
                    control={form.control}
                    name="hora"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Hora (HH:MM)</FormLabel>
                            <FormControl>
                                <Input placeholder="10:30" {...field} className="w-full" />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />
                
                {/* 3. Descrição da Consulta */}
                <FormField
                    control={form.control}
                    name="descricao"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Descrição/Motivo da Consulta</FormLabel>
                            <FormControl>
                                <Textarea 
                                    placeholder="Ex: Primeira consulta com cardiologista..." 
                                    {...field} 
                                />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                {/* 4. Seleção de Paciente */}
                <FormField
                    control={form.control}
                    name="pacienteId"
                    render={({ field }) => {
                        const pacienteSelecionado = pacientes.find(p => p.id === field.value);
                        return (
                            <FormItem>
                                <FormLabel>Paciente *</FormLabel>
                                <Select 
                                    onValueChange={(value) => field.onChange(parseInt(value, 10))} 
                                    value={field.value ? String(field.value) : undefined}
                                >
                                    <FormControl>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione o paciente">
                                                {pacienteSelecionado ? pacienteSelecionado.name : "Selecione o paciente"}
                                            </SelectValue>
                                        </SelectTrigger>
                                    </FormControl>
                                    <SelectContent>
                                        {isLoadingPacientes ? (
                                            <div className="flex items-center justify-center p-4">
                                                <Loader2 className="w-4 h-4 animate-spin" />
                                            </div>
                                        ) : pacientes.length === 0 ? (
                                            <div className="p-4 text-sm text-muted-foreground text-center">
                                                Nenhum paciente cadastrado
                                            </div>
                                        ) : (
                                            pacientes.map((paciente) => (
                                                <SelectItem key={paciente.id} value={String(paciente.id)}>
                                                    {paciente.name}
                                                </SelectItem>
                                            ))
                                        )}
                                    </SelectContent>
                                </Select>
                                <FormMessage />
                            </FormItem>
                        );
                    }}
                />

                {/* 5. Seleção de Médico */}
                <FormField
                    control={form.control}
                    name="medicoId"
                    render={({ field }) => {
                        const medicoSelecionado = medicos.find(m => m.id === field.value);
                        return (
                            <FormItem>
                                <FormLabel>Médico *</FormLabel>
                                <Select 
                                    onValueChange={(value) => field.onChange(parseInt(value, 10))} 
                                    value={field.value ? String(field.value) : undefined}
                                >
                                    <FormControl>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione o médico">
                                                {medicoSelecionado 
                                                    ? `${medicoSelecionado.nome}${medicoSelecionado.funcao ? ` - ${medicoSelecionado.funcao}` : ''}`
                                                    : "Selecione o médico"}
                                            </SelectValue>
                                        </SelectTrigger>
                                    </FormControl>
                                    <SelectContent>
                                        {isLoadingFuncionarios ? (
                                            <div className="flex items-center justify-center p-4">
                                                <Loader2 className="w-4 h-4 animate-spin" />
                                            </div>
                                        ) : medicos.length === 0 ? (
                                            <div className="p-4 text-sm text-muted-foreground text-center">
                                                Nenhum médico disponível
                                            </div>
                                        ) : (
                                            medicos.map((medico) => (
                                                <SelectItem key={medico.id} value={String(medico.id)}>
                                                    {medico.nome} {medico.funcao ? `- ${medico.funcao}` : ''}
                                                </SelectItem>
                                            ))
                                        )}
                                    </SelectContent>
                                </Select>
                                <FormMessage />
                            </FormItem>
                        );
                    }}
                />
                
                {/* Campo Oculto para ID do Usuário Responsável */}
                <FormField
                    control={form.control}
                    name="usuarioId"
                    render={({ field }) => (
                        <FormItem hidden>
                            <FormControl>
                                <Input type="hidden" {...field} />
                            </FormControl>
                        </FormItem>
                    )}
                />

                <Button type="submit" disabled={isSubmitting} className="w-full">
                    {isSubmitting ? (
                        <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Agendando...
                        </>
                    ) : (
                        "Agendar Consulta"
                    )}
                </Button>
            </form>
        </Form>
    );
};