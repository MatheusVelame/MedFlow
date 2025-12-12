// Localização: apresentacao-frontend/src/main/react/src/components/UsoPrincipalEdicaoForm.tsx

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useAtualizarUsoPrincipal, UsoPrincipalPayload } from "../api/useMedicamentosApi";
import { Button } from "./ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form";
import { Textarea } from "./ui/textarea";
import { Separator } from "./ui/separator";

// Simulação: ID do usuário logado (Gestor)
const RESPONSAVEL_ID = 1;

// Esquema de validação para o novo uso principal
const formSchema = z.object({
  novoUsoPrincipal: z.string().min(5, { message: "O novo Uso Principal é obrigatório e deve ter no mínimo 5 caracteres." }),
});

type UsoPrincipalFormValues = z.infer<typeof formSchema>;

interface UsoPrincipalEdicaoFormProps {
  medicamentoId: number;
  usoPrincipalAtual: string;
  onFinish: () => void;
}

export function UsoPrincipalEdicaoForm({ medicamentoId, usoPrincipalAtual, onFinish }: UsoPrincipalEdicaoFormProps) {
  // Hook de mutação para a ação PATCH
  const atualizarMutation = useAtualizarUsoPrincipal();

  const form = useForm<UsoPrincipalFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      novoUsoPrincipal: usoPrincipalAtual, // Preenche com o valor atual
    },
  });

  const onSubmit = (values: UsoPrincipalFormValues) => {
    
    const payload: UsoPrincipalPayload = {
        novoUsoPrincipal: values.novoUsoPrincipal,
        responsavelId: RESPONSAVEL_ID, 
    };

    // Chamada à mutação: usa o id e o payload para a requisição PATCH
    atualizarMutation.mutate(
      { id: medicamentoId, payload: payload },
      {
        onSuccess: () => {
          form.reset();
          onFinish(); // Fecha o modal após o sucesso
        },
        // O tratamento de erro já está no hook useAtualizarUsoPrincipal
      }
    );
  };
  
  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        
        <div className="text-sm text-muted-foreground">
            <p className="font-semibold mb-1">Uso Principal Atual:</p>
            <p className="whitespace-pre-wrap">{usoPrincipalAtual}</p>
        </div>
        
        <Separator />

        {/* Campo Novo Uso Principal */}
        <FormField
          control={form.control}
          name="novoUsoPrincipal"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Novo Uso Principal</FormLabel>
              <FormControl>
                <Textarea placeholder="Descreva o novo uso principal aqui." {...field} rows={3} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <Button 
            type="submit" 
            className="w-full"
            disabled={atualizarMutation.isPending} 
        >
            {atualizarMutation.isPending ? 'Atualizando...' : 'Salvar Alteração'}
        </Button>

      </form>
    </Form>
  );
}