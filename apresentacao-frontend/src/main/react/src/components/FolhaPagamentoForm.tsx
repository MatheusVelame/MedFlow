import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
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
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { CalendarIcon } from "lucide-react";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

const formSchema = z.object({
  profissionalId: z.string().min(1, "Selecione um profissional"),
  profissionalNome: z.string().min(1, "Nome obrigatório"),
  mesReferencia: z.date({
    required_error: "Selecione o mês de referência",
  }),
  salarioBase: z.coerce.number().positive("Valor deve ser positivo"),
  bonus: z.coerce.number().min(0, "Valor não pode ser negativo").default(0),
  descontos: z.coerce.number().min(0, "Valor não pode ser negativo").default(0),
  metodoPagamento: z.string().min(1, "Selecione o método de pagamento"),
  observacoes: z.string().optional(),
});

interface FolhaPagamentoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => void;
  initialData?: any;
  profissionais: Array<{ id: string; nome: string; especialidade: string; salario: number }>;
}

export function FolhaPagamentoForm({ 
  open, 
  onOpenChange, 
  onSave, 
  initialData,
  profissionais 
}: FolhaPagamentoFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: initialData || {
      profissionalId: "",
      profissionalNome: "",
      mesReferencia: new Date(),
      salarioBase: 0,
      bonus: 0,
      descontos: 0,
      metodoPagamento: "",
      observacoes: "",
    },
  });

  const handleProfissionalChange = (profissionalId: string) => {
    const profissional = profissionais.find(p => p.id === profissionalId);
    if (profissional) {
      form.setValue("profissionalNome", profissional.nome);
      form.setValue("salarioBase", profissional.salario);
    }
  };

  const salarioBase = form.watch("salarioBase") || 0;
  const bonus = form.watch("bonus") || 0;
  const descontos = form.watch("descontos") || 0;
  const valorLiquido = salarioBase + bonus - descontos;

  const handleSubmit = (data: z.infer<typeof formSchema>) => {
    onSave(data);
    form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Pagamento" : "Registrar Novo Pagamento"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="profissionalId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Profissional</FormLabel>
                  <Select 
                    onValueChange={(value) => {
                      field.onChange(value);
                      handleProfissionalChange(value);
                    }}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione o profissional" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {profissionais.map((prof) => (
                        <SelectItem key={prof.id} value={prof.id}>
                          {prof.nome} - {prof.especialidade}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="mesReferencia"
              render={({ field }) => (
                <FormItem className="flex flex-col">
                  <FormLabel>Mês de Referência</FormLabel>
                  <Popover>
                    <PopoverTrigger asChild>
                      <FormControl>
                        <Button
                          variant="outline"
                          className={cn(
                            "w-full pl-3 text-left font-normal",
                            !field.value && "text-muted-foreground"
                          )}
                        >
                          {field.value ? (
                            format(field.value, "MMMM 'de' yyyy")
                          ) : (
                            <span>Selecione o mês</span>
                          )}
                          <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        initialFocus
                        className="pointer-events-auto"
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-3 gap-4">
              <FormField
                control={form.control}
                name="salarioBase"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Salário Base (R$)</FormLabel>
                    <FormControl>
                      <Input type="number" step="0.01" placeholder="0.00" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="bonus"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Bônus (R$)</FormLabel>
                    <FormControl>
                      <Input type="number" step="0.01" placeholder="0.00" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="descontos"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Descontos (R$)</FormLabel>
                    <FormControl>
                      <Input type="number" step="0.01" placeholder="0.00" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="p-4 bg-primary/10 rounded-lg">
              <div className="flex justify-between items-center">
                <span className="text-sm font-medium">Valor Líquido:</span>
                <span className="text-2xl font-bold text-primary">
                  {valorLiquido.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                </span>
              </div>
            </div>

            <FormField
              control={form.control}
              name="metodoPagamento"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Método de Pagamento</FormLabel>
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione o método" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="transferencia">Transferência Bancária</SelectItem>
                      <SelectItem value="pix">PIX</SelectItem>
                      <SelectItem value="cheque">Cheque</SelectItem>
                      <SelectItem value="dinheiro">Dinheiro</SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="observacoes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Observações</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="Informações adicionais sobre o pagamento" 
                      className="resize-none" 
                      rows={3}
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-3 pt-4">
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                Cancelar
              </Button>
              <Button type="submit">Salvar</Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
