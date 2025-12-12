// Localização: apresentacao-frontend/src/main/react/src/components/MedicamentoForm.tsx (CORRIGIDO)

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useCadastrarMedicamento, CadastrarMedicamentoPayload } from "../api/useMedicamentosApi";
import { Button } from "./ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "./ui/form";
import { Input } from "./ui/input";
import { Textarea } from "./ui/textarea";

// Simulação: ID do usuário logado (Gestor)
const RESPONSAVEL_ID = 1;

// 1. Definição do Schema de Validação (Zod)
const formSchema = z.object({
  nome: z.string().min(2, { message: "O nome deve ter pelo menos 2 caracteres." }),
  usoPrincipal: z.string().min(5, { message: "O uso principal é obrigatório." }),
  contraindicacoes: z.string().min(5, { message: "As contraindicações são obrigatórias." }),
});

type MedicamentoFormValues = z.infer<typeof formSchema>;

interface MedicamentoFormProps {
  onFinish: () => void;
}

export function MedicamentoForm({ onFinish }: MedicamentoFormProps) {
  // 2. Integração com a Mutação do React Query
  const cadastrarMutation = useCadastrarMedicamento();

  const form = useForm<MedicamentoFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      nome: "",
      usoPrincipal: "",
      contraindicacoes: "",
    },
  });

  // 3. Função de Submissão
  const onSubmit = (values: MedicamentoFormValues) => {
    
    // Mapeamento explícito para garantir a conformidade com a interface CadastrarMedicamentoPayload
    const payload: CadastrarMedicamentoPayload = {
        nome: values.nome,
        usoPrincipal: values.usoPrincipal,
        contraindicacoes: values.contraindicacoes,
        responsavelId: RESPONSAVEL_ID, 
    };

    cadastrarMutation.mutate(
      payload, 
      {
        onSuccess: () => {
          form.reset();
          onFinish(); 
        },
        // O tratamento de erro já está no hook useCadastrarMedicamento
      }
    );
  };
  
  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        
        {/* Campo Nome */}
        <FormField
          control={form.control}
          name="nome"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Nome</FormLabel>
              <FormControl>
                <Input placeholder="Ex: Paracetamol 500mg" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        {/* Campo Uso Principal */}
        <FormField
          control={form.control}
          name="usoPrincipal"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Uso Principal</FormLabel>
              <FormControl>
                <Input placeholder="Ex: Analgésico, Antipirético" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Campo Contraindicações */}
        <FormField
          control={form.control}
          name="contraindicacoes"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Contraindicações</FormLabel>
              <FormControl>
                <Textarea placeholder="Ex: Pacientes com insuficiência hepática, alergia a componentes..." {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <Button 
            type="submit" 
            className="w-full"
            disabled={cadastrarMutation.isPending} 
        >
            {cadastrarMutation.isPending ? 'Cadastrando...' : 'Salvar Medicamento'}
        </Button>

      </form>
    </Form>
  );
}