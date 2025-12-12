import { useEffect } from "react";
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
  FormDescription,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

const formSchema = z.object({
  codigo: z.string()
    .min(1, "Código é obrigatório")
    .regex(/^[A-Z0-9-]+$/, "Use apenas letras maiúsculas, números e hífen"),
  descricao: z.string()
    .min(3, "Descrição deve ter pelo menos 3 caracteres")
    .max(200, "Descrição deve ter no máximo 200 caracteres"),
  especialidade: z.string()
    .min(1, "Especialidade é obrigatória"),
  valor: z.coerce.number()
    .positive("Valor deve ser maior que 0")
    .min(0.01, "Valor deve ser maior que 0"),
  observacoes: z.string().optional(),
});

interface TipoExameFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => void;
  initialData?: any;
  existingCodes: string[];
  isLoading?: boolean;
}

export function TipoExameForm({ 
  open, 
  onOpenChange, 
  onSave, 
  initialData,
  existingCodes,
  isLoading = false
}: TipoExameFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      codigo: "",
      descricao: "",
      especialidade: "",
      valor: 0,
      observacoes: "",
    },
  });

  // CORREÇÃO: useEffect para resetar o formulário ao abrir
  useEffect(() => {
    if (open) {
      if (initialData) {
        // Modo Edição: Carrega os dados
        form.reset({
          codigo: initialData.codigo,
          descricao: initialData.descricao,
          especialidade: initialData.especialidade,
          valor: Number(initialData.valor),
          observacoes: initialData.observacoes || "",
        });
      } else {
        // Modo Criação: Limpa o formulário
        form.reset({
          codigo: "",
          descricao: "",
          especialidade: "",
          valor: 0,
          observacoes: "",
        });
      }
    }
  }, [open, initialData, form]);

  const handleSubmit = (data: z.infer<typeof formSchema>) => {
    // Validar código único apenas para novos cadastros (Criação)
    if (!initialData && existingCodes.includes(data.codigo.toUpperCase())) {
      form.setError("codigo", {
        type: "manual",
        message: "Este código já está em uso. O código deve ser único.",
      });
      return;
    }

    onSave({
      ...data,
      codigo: data.codigo.toUpperCase(),
    });
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Tipo de Exame" : "Cadastrar Novo Tipo de Exame"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="codigo"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Código *</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Ex: HEMOG-001" 
                      {...field}
                      onChange={(e) => field.onChange(e.target.value.toUpperCase())}
                      // Desabilitado na edição para não permitir trocar a chave primária
                      disabled={!!initialData || isLoading}
                    />
                  </FormControl>
                  <FormDescription>
                    Código único para identificar o tipo de exame (apenas letras maiúsculas, números e hífen)
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="descricao"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Descrição *</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="Descreva o tipo de exame" 
                      className="resize-none" 
                      rows={3}
                      {...field}
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="especialidade"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Especialidade *</FormLabel>
                    <FormControl>
                        <Input 
                            placeholder="Ex: Cardiologia" 
                            {...field}
                            disabled={isLoading}
                        />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

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
                        min="0.01"
                        placeholder="0.00" 
                        {...field}
                        disabled={isLoading}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="observacoes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Observações</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="Informações adicionais sobre o tipo de exame" 
                      className="resize-none" 
                      rows={2}
                      {...field}
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="bg-muted p-4 rounded-lg">
              <p className="text-sm text-muted-foreground">
                <strong>Observação:</strong> Todos os tipos de exame são cadastrados com status inicial <strong>"Ativo"</strong>.
              </p>
            </div>

            <div className="flex justify-end gap-3 pt-4">
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isLoading}>
                Cancelar
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? "Salvando..." : (initialData ? "Atualizar" : "Cadastrar")}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
