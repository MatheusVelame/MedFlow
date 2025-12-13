import React from "react";
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
import { Button } from "@/components/ui/button";
import { toast } from "@/hooks/use-toast";

// Ajustado: o backend aceita apenas nome/descricao na criação/atualização
// Agora o schema faz trim no nome e valida em tempo real (regex para letras e espaços)
const nomeSchema = z
  .preprocess((v) => (typeof v === "string" ? v.trim() : v), z.string())
  .refine((s) => s.length >= 3, { message: "Nome deve ter pelo menos 3 caracteres" })
  .refine((s) => /^[\p{L}\s]+$/u.test(s), {
    message: "O nome da especialidade deve conter apenas caracteres alfabéticos e espaços",
  });

const formSchema = z.object({
  nome: nomeSchema,
  descricao: z
    .string()
    .max(255, "Descrição deve ter no máximo 255 caracteres")
    .optional(),
});

interface EspecialidadeFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => Promise<any> | void;
  initialData?: Partial<z.infer<typeof formSchema>> | null;
}

export function EspecialidadeForm({ open, onOpenChange, onSave, initialData }: EspecialidadeFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    mode: "onChange", // valida em tempo real enquanto o usuário digita
    defaultValues: initialData || {
      nome: "",
      descricao: "",
    },
  });

  // sincronizar initialData quando modal abrir para edição
  React.useEffect(() => {
    if (initialData) {
      form.reset({
        nome: initialData.nome ?? "",
        descricao: initialData.descricao ?? "",
      });
    } else {
      form.reset({ nome: "", descricao: "" });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialData, open]);

  const descricaoValor = form.watch("descricao");
  const descricaoLen = descricaoValor ? descricaoValor.length : 0;

  const handleSubmit = async (data: z.infer<typeof formSchema>) => {
    try {
      await onSave(data as any);
      form.reset();
      onOpenChange(false);
    } catch (e: any) {
      const payloadErr = e?.response?.data ?? e?.response?.data?.errors ?? null;

      // Caso backend retorne objeto de erros por campo: { errors: { campo: mensagem } }
      if (payloadErr && typeof payloadErr === "object" && payloadErr.errors && typeof payloadErr.errors === "object") {
        Object.entries(payloadErr.errors).forEach(([field, message]) => {
          try {
            form.setError(field as any, { type: "server", message: String(message) });
          } catch {}
        });
        return;
      }

      // Se backend retornar uma string com a mensagem (RegraNegocioException -> body:String)
      const serverMessage = typeof payloadErr === "string" ? payloadErr : e?.response?.data ?? e?.message;
      if (serverMessage && typeof serverMessage === "string") {
        // heurística: se a mensagem indicar duplicidade/nome já existe, lançamos erro no campo 'nome'
        const lower = serverMessage.toLowerCase();
        const isNomeError = /nome|especialidade|já existe|já existente|duplicad|existente|exists|duplicate/i.test(lower);
        if (isNomeError) {
          try {
            form.setError("nome", { type: "server", message: serverMessage });
          } catch {}
        } else {
          // fallback: mostrar toast genérico
          toast({ title: "Erro", description: serverMessage });
        }
        return;
      }

      // fallback: se payload não reconhecido, rethrow para que o caller possa tratar
      throw e;
    }
  };

  const isSubmitting = form.formState.isSubmitting;
  const isValid = form.formState.isValid;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Especialidade" : "Nova Especialidade"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="nome"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nome da Especialidade</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: Cardiologia" {...field} aria-invalid={Boolean(form.formState.errors.nome)} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="descricao"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Descrição</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Descreva a especialidade médica"
                      className="resize-none"
                      rows={3}
                      maxLength={255}
                      {...field}
                    />
                  </FormControl>
                  <div className="flex justify-between text-sm text-muted-foreground">
                    <FormMessage />
                    <span>{descricaoLen}/255</span>
                  </div>
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-3 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={isSubmitting}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={isSubmitting || !isValid}>
                {isSubmitting ? "Salvando..." : "Salvar"}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}