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
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Button } from "@/components/ui/button";

const formSchema = z.object({
  nome: z.string().min(3, "Nome deve ter pelo menos 3 caracteres"),
  principioAtivo: z.string().min(3, "Princípio ativo obrigatório"),
  fabricante: z.string().min(3, "Fabricante obrigatório"),
  tipo: z.string().min(3, "Tipo obrigatório"),
  dosagem: z.string().min(1, "Dosagem obrigatória"),
  uso: z.string().min(10, "Descreva o uso do medicamento"),
  contraindicacoes: z.string().min(5, "Descreva as contraindicações"),
  ativo: z.boolean().default(true),
});

interface MedicamentoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => void;
  initialData?: any;
}

export function MedicamentoForm({ open, onOpenChange, onSave, initialData }: MedicamentoFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: initialData || {
      nome: "",
      principioAtivo: "",
      fabricante: "",
      tipo: "",
      dosagem: "",
      uso: "",
      contraindicacoes: "",
      ativo: true,
    },
  });

  const handleSubmit = (data: z.infer<typeof formSchema>) => {
    onSave(data);
    form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Medicamento" : "Novo Medicamento"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="nome"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nome Comercial</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: Paracetamol 500mg" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="principioAtivo"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Princípio Ativo</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: Paracetamol" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="dosagem"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Dosagem</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: 500mg" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="fabricante"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Fabricante</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: Medley" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="tipo"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Tipo</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: Analgésico" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="uso"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Uso/Indicações</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="Descreva as indicações de uso" 
                      className="resize-none" 
                      rows={3}
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="contraindicacoes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Contraindicações</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="Descreva as contraindicações" 
                      className="resize-none" 
                      rows={3}
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="ativo"
              render={({ field }) => (
                <FormItem className="flex items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">Medicamento Ativo</FormLabel>
                    <div className="text-sm text-muted-foreground">
                      Desative se o medicamento não estiver mais disponível
                    </div>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
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
