// Localização: apresentacao-frontend/src/main/react/src/components/SolicitarRevisaoForm.tsx

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useSolicitarRevisao, SolicitarRevisaoPayload } from "../api/useMedicamentosApi";
import { Button } from "./ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form";
import { Textarea } from "./ui/textarea";

// Simulação: ID do usuário logado (Gestor/Revisor)
const RESPONSAVEL_ID = 1;

// Esquema de validação para a nova contraindicação
const formSchema = z.object({
  novaContraindicacao: z.string().min(10, { message: "Descreva a nova contraindicação em detalhes (mínimo 10 caracteres)." }),
});

type RevisaoFormValues = z.infer<typeof formSchema>;

interface SolicitarRevisaoFormProps {
  medicamentoId: number;
  contraindicacoesAtuais: string;
  onFinish: () => void;
}

export function SolicitarRevisaoForm({ medicamentoId, contraindicacoesAtuais, onFinish }: SolicitarRevisaoFormProps) {
  const solicitarMutation = useSolicitarRevisao();

  const form = useForm<RevisaoFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      novaContraindicacao: "",
    },
  });

  const onSubmit = (values: RevisaoFormValues) => {
    
    const payload: SolicitarRevisaoPayload = {
        novaContraindicacao: values.novaContraindicacao,
        responsavelId: RESPONSAVEL_ID, 
    };

    solicitarMutation.mutate(
      { id: medicamentoId, payload: payload },
      {
        onSuccess: () => {
          form.reset();
          onFinish(); // Fecha o modal após o sucesso
        },
        // O tratamento de erro já está no hook
      }
    );
  };
  
  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        
        {/* Exibe o valor atual para referência */}
        <div className="text-sm text-muted-foreground p-3 border rounded-md">
            <p className="font-semibold mb-1">Contraindicações Atuais:</p>
            <p className="whitespace-pre-wrap max-h-24 overflow-y-auto">{contraindicacoesAtuais}</p>
        </div>

        {/* Campo Nova Contraindicação */}
        <FormField
          control={form.control}
          name="novaContraindicacao"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Nova Contraindicação Sugerida</FormLabel>
              <FormControl>
                <Textarea placeholder="Ex: Adicionar restrição para pacientes pediátricos." {...field} rows={4} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <Button 
            type="submit" 
            className="w-full"
            disabled={solicitarMutation.isPending} 
        >
            {solicitarMutation.isPending ? 'Enviando Revisão...' : 'Solicitar Revisão'}
        </Button>

      </form>
    </Form>
  );
}