import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { AlertCircle, Loader2 } from "lucide-react";
import { useListarPacientes } from "@/api/usePacientesApi";

// Tabela de preços padrão dos procedimentos
const tabelaPrecos: Record<string, number> = {
  "Consulta Cardiologia": 250.00,
  "Consulta Clínica Geral": 150.00,
  "Consulta Pediatria": 180.00,
  "Raio-X Tórax": 180.00,
  "Ultrassonografia": 200.00,
  "Hemograma Completo": 80.00,
  "Eletrocardiograma": 120.00,
};

const formSchema = z.object({
  pacienteId: z.string().min(1, "Selecione um paciente"),
  pacienteNome: z.string().min(1, "Nome do paciente é obrigatório"),
  procedimentoTipo: z.enum(["consulta", "exame"], {
    required_error: "Selecione o tipo de procedimento",
  }),
  procedimentoDescricao: z.string().min(1, "Descrição do procedimento é obrigatória"),
  valor: z.coerce.number().positive("O valor deve ser positivo"),
  metodoPagamento: z.string().min(1, "Selecione o método de pagamento"),
  observacoes: z.string().optional().default(""),
  justificativaValor: z.string().optional().default(""),
});

type FormValues = z.infer<typeof formSchema>;

// Mapeamento de métodos de pagamento para valores do backend
const metodoPagamentoMap: Record<string, string> = {
  "Dinheiro": "DINHEIRO",
  "Cartão de Débito": "CARTAO_DEBITO",
  "Cartão de Crédito": "CARTAO_CREDITO",
  "PIX": "PIX",
  "Convênio Unimed": "CONVENIO_UNIMED",
  "Convênio Amil": "CONVENIO_AMIL",
  "Convênio Bradesco Saúde": "CONVENIO_BRADESCO",
};

interface FaturamentoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (data: FormValues) => void;
  initialData?: any;
  onCancel?: () => void;
}

export function FaturamentoForm({ open, onOpenChange, onSubmit, initialData, onCancel }: FaturamentoFormProps) {
  const [valorDiferente, setValorDiferente] = useState(false);
  const [valorPadrao, setValorPadrao] = useState<number | null>(null);
  
  // Buscar pacientes da API
  const { data: pacientes = [], isLoading: isLoadingPacientes } = useListarPacientes();

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: initialData || {
      pacienteId: "",
      pacienteNome: "",
      procedimentoTipo: undefined,
      procedimentoDescricao: "",
      valor: undefined as any,
      metodoPagamento: "",
      observacoes: "",
      justificativaValor: "",
    },
  });

  // Atualizar valores quando initialData ou open mudar
  useEffect(() => {
    if (open) {
      if (initialData) {
        form.reset(initialData);
      } else {
        form.reset({
          pacienteId: "",
          pacienteNome: "",
          procedimentoTipo: undefined,
          procedimentoDescricao: "",
          valor: undefined as any,
          metodoPagamento: "",
          observacoes: "",
          justificativaValor: "",
        });
        setValorDiferente(false);
        setValorPadrao(null);
      }
    }
  }, [initialData, open, form]);

  const handlePacienteSelecionado = (pacienteId: string) => {
    const pacienteIdNum = parseInt(pacienteId);
    const paciente = pacientes.find(p => p.id === pacienteIdNum);
    if (paciente) {
      form.setValue("pacienteNome", paciente.name);
    }
  };

  const handleProcedimentoSelecionado = (procedimento: string) => {
    const preco = tabelaPrecos[procedimento];
    if (preco) {
      setValorPadrao(preco);
      form.setValue("valor", preco);
      setValorDiferente(false);
      form.setValue("justificativaValor", "");
    }
  };

  const handleValorChange = (valor: number) => {
    if (valorPadrao && !isNaN(valor) && Math.abs(valor - valorPadrao) > 0.01) {
      setValorDiferente(true);
    } else {
      setValorDiferente(false);
      form.setValue("justificativaValor", "");
    }
  };

  const handleSubmit = (data: FormValues) => {
    if (valorDiferente && (!data.justificativaValor || data.justificativaValor.trim() === "")) {
      toast.error("Justificativa obrigatória para valor diferente do padrão");
      return;
    }

    // Mapear método de pagamento para o formato do backend
    const metodoPagamentoBackend = metodoPagamentoMap[data.metodoPagamento] || data.metodoPagamento.toUpperCase().replace(/\s+/g, "_");
    
    // Converter tipo de procedimento para maiúsculas
    const tipoProcedimentoBackend = data.procedimentoTipo.toUpperCase() as "CONSULTA" | "EXAME";
    
    onSubmit({
      ...data,
      metodoPagamento: metodoPagamentoBackend,
      procedimentoTipo: tipoProcedimentoBackend,
    });
    
    if (!initialData) {
      // Reset completo do formulário e estados apenas se for novo
      form.reset({
        pacienteId: "",
        pacienteNome: "",
        procedimentoTipo: undefined,
        procedimentoDescricao: "",
        valor: undefined as any,
        metodoPagamento: "",
        observacoes: "",
        justificativaValor: "",
      });
      setValorDiferente(false);
      setValorPadrao(null);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Faturamento" : "Novo Faturamento"}
          </DialogTitle>
          <DialogDescription>
            Preencha os dados do procedimento realizado para registrar o faturamento
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        <div className="grid gap-4 md:grid-cols-2">
          <FormField
            control={form.control}
            name="pacienteId"
            render={({ field }) => {
              const pacienteSelecionado = field.value 
                ? pacientes.find(p => String(p.id) === field.value)
                : null;
              
              return (
                <FormItem>
                  <FormLabel>Paciente *</FormLabel>
                  <Select
                    onValueChange={(value) => {
                      field.onChange(value);
                      handlePacienteSelecionado(value);
                    }}
                    value={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione o paciente">
                          {pacienteSelecionado 
                            ? pacienteSelecionado.name
                            : "Selecione o paciente"}
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

          <FormField
            control={form.control}
            name="procedimentoTipo"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tipo de Procedimento *</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione o tipo" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="consulta">Consulta</SelectItem>
                    <SelectItem value="exame">Exame</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="procedimentoDescricao"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Descrição do Procedimento *</FormLabel>
              <Select
                onValueChange={(value) => {
                  field.onChange(value);
                  handleProcedimentoSelecionado(value);
                }}
                value={field.value}
              >
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o procedimento" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {Object.entries(tabelaPrecos).map(([procedimento, preco]) => (
                    <SelectItem key={procedimento} value={procedimento}>
                      {procedimento} - R$ {preco.toFixed(2)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormDescription>
                O valor será preenchido automaticamente com base na tabela de preços
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="grid gap-4 md:grid-cols-2">
          <FormField
            control={form.control}
            name="valor"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Valor (R$) *</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="0.00"
                    value={field.value || ""}
                    onChange={(e) => {
                      const valor = e.target.value === "" ? undefined : parseFloat(e.target.value);
                      field.onChange(valor);
                      if (valor) {
                        handleValorChange(valor);
                      }
                    }}
                  />
                </FormControl>
                {valorPadrao && (
                  <FormDescription>
                    Valor padrão: R$ {valorPadrao.toFixed(2)}
                  </FormDescription>
                )}
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="metodoPagamento"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Método de Pagamento *</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione o método" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="Dinheiro">Dinheiro</SelectItem>
                    <SelectItem value="Cartão de Débito">Cartão de Débito</SelectItem>
                    <SelectItem value="Cartão de Crédito">Cartão de Crédito</SelectItem>
                    <SelectItem value="PIX">PIX</SelectItem>
                    <SelectItem value="Convênio Unimed">Convênio Unimed</SelectItem>
                    <SelectItem value="Convênio Amil">Convênio Amil</SelectItem>
                    <SelectItem value="Convênio Bradesco Saúde">Convênio Bradesco Saúde</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {valorDiferente && (
          <Alert>
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>
              O valor informado é diferente do valor padrão. Por favor, forneça uma justificativa.
            </AlertDescription>
          </Alert>
        )}

        {valorDiferente && (
          <FormField
            control={form.control}
            name="justificativaValor"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Justificativa para Valor Diferente *</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="Explique o motivo da diferença no valor..."
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        <FormField
          control={form.control}
          name="observacoes"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Observações</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Informações adicionais sobre o faturamento..."
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

            <div className="flex gap-3 pt-4">
              <Button type="button" variant="outline" onClick={() => {
                if (onCancel) onCancel();
                onOpenChange(false);
              }} className="flex-1">
                Cancelar
              </Button>
              <Button type="submit" className="flex-1">
                {initialData ? "Atualizar Faturamento" : "Registrar Faturamento"}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
