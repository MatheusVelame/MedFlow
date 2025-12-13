// Localização: apresentacao-frontend/src/main/react/src/components/AppointmentForm.tsx

import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { format } from "date-fns";
import { Calendar as CalendarIcon, Loader2 } from "lucide-react";
import { toast } from "sonner";

import { useConsultasApi, AgendamentoRequest } from "../api/useConsultasApi";
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
import { cn } from "../lib/utils";
import { Textarea } from "./ui/textarea";

// --- Definição do Schema de Validação (Zod) ---
const AgendamentoSchema = z.object({
    dataHora: z.date({
        required_error: "A data e hora são obrigatórias.",
    }).refine(date => date.getTime() >= new Date().getTime() - 60000, { // Permite 60s de tolerância para testes
        message: "O agendamento deve ser para o futuro ou presente.",
    }),
    descricao: z.string().min(1, {
        message: "A descrição é obrigatória.",
    }),
    pacienteId: z.coerce.number().int().positive({
        message: "O ID do paciente é obrigatório e deve ser um número inteiro positivo."
    }),
    medicoId: z.coerce.number().int().positive({
        message: "O ID do médico é obrigatório e deve ser um número inteiro positivo."
    }),
    usuarioId: z.coerce.number().int().positive({
        message: "O ID do usuário responsável é obrigatório."
    }),
    hora: z.string().regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, {
        message: "Hora inválida. Use o formato HH:MM (ex: 10:30)."
    }),
});

type AgendamentoFormValues = z.infer<typeof AgendamentoSchema>;

const MOCK_USUARIO_ID = 100; 

/**
 * Componente de Formulário para Agendamento de Consultas.
 * @param onAgendamentoSuccess Callback executado após o sucesso do agendamento (usado para fechar o modal e recarregar a lista).
 */
export const AppointmentForm: React.FC<{ onAgendamentoSuccess: () => void }> = ({ onAgendamentoSuccess }) => {
    const { agendarConsulta } = useConsultasApi();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const form = useForm<AgendamentoFormValues>({
        resolver: zodResolver(AgendamentoSchema),
        defaultValues: {
            descricao: "",
            pacienteId: 1, 
            medicoId: 5,
            usuarioId: MOCK_USUARIO_ID,
            hora: "10:00"
        },
    });

    const combineDateTime = (date: Date, timeStr: string): string => {
        const [hours, minutes] = timeStr.split(':').map(Number);
        const combined = new Date(date);
        combined.setHours(hours);
        combined.setMinutes(minutes);
        combined.setSeconds(0);
        combined.setMilliseconds(0);
        
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
            
            toast.success(`Consulta agendada com sucesso para ${format(values.dataHora, "dd/MM/yyyy")} às ${values.hora}.`);
            
            form.reset({ 
                descricao: "",
                pacienteId: values.pacienteId,
                medicoId: values.medicoId,
                usuarioId: MOCK_USUARIO_ID,
                hora: "10:00",
                dataHora: undefined
            });
            onAgendamentoSuccess(); // CHAMA O CALLBACK PARA FECHAR O MODAL E RECARREGAR A LISTA
            
        } catch (error) {
            if (error instanceof Error) {
                toast.error(`Falha no agendamento: ${error.message}`);
            } else {
                toast.error("Ocorreu um erro desconhecido ao agendar.");
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

                {/* 4. ID do Paciente */}
                <FormField
                    control={form.control}
                    name="pacienteId"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>ID do Paciente</FormLabel>
                            <FormControl>
                                <Input type="number" placeholder="101" {...field} onChange={e => field.onChange(parseInt(e.target.value, 10) || 0)} />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                {/* 5. ID do Médico */}
                <FormField
                    control={form.control}
                    name="medicoId"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>ID do Médico</FormLabel>
                            <FormControl>
                                <Input type="number" placeholder="5" {...field} onChange={e => field.onChange(parseInt(e.target.value, 10) || 0)} />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
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