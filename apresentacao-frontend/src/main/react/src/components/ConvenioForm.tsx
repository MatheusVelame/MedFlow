import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useEffect } from "react";
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
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const formSchema = z.object({
  nome: z.string().min(3, "Nome deve ter pelo menos 3 caracteres"),
  codigoIdentificacao: z.string().min(1, "Código de identificação é obrigatório"),
  status: z.enum(["ATIVO", "INATIVO"]).optional(),
});

interface ConvenioFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => void;
  initialData?: any;
}

export function ConvenioForm({ open, onOpenChange, onSave, initialData }: ConvenioFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      nome: "",
      codigoIdentificacao: "",
      status: "ATIVO" as const,
    },
  });

  useEffect(() => {
    if (initialData) {
      form.reset({
        nome: initialData.nome || "",
        codigoIdentificacao: initialData.codigoIdentificacao || "",
        status: initialData.status || "ATIVO",
      });
    } else {
      form.reset({
        nome: "",
        codigoIdentificacao: "",
        status: "ATIVO",
      });
    }
  }, [initialData, open, form]);

  const handleSubmit = (data: z.infer<typeof formSchema>) => {
    onSave(data);
    if (!initialData) {
      form.reset();
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Editar Convênio" : "Novo Convênio"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="nome"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nome do Convênio</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: Unimed" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="codigoIdentificacao"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Código de Identificação</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Ex: UNI001 ou 12.345.678/0001-90" 
                      {...field}
                      disabled={!!initialData} // Não permite editar código ao editar
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {initialData && (
              <FormField
                control={form.control}
                name="status"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Status</FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Selecione o status" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="ATIVO">Ativo</SelectItem>
                        <SelectItem value="INATIVO">Inativo</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}

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
