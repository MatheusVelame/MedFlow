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
import type { FuncionarioResumo, StatusFuncionario } from "@/api/useFuncionariosApi";

const funcionarioSchema = z.object({
  nome: z.string().min(3, "Nome deve ter pelo menos 3 caracteres"),
  funcao: z.string().min(1, "Função é obrigatória"),
  contato: z.string().min(1, "Contato é obrigatório"),
  status: z.enum(["ATIVO", "INATIVO", "FERIAS", "AFASTADO"]).optional(),
});

type FuncionarioFormData = z.infer<typeof funcionarioSchema>;

interface FuncionarioFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  initialData?: FuncionarioResumo | null;
  onSave: (data: FuncionarioFormData) => void;
}

export function FuncionarioForm({ 
  open, 
  onOpenChange, 
  initialData, 
  onSave 
}: FuncionarioFormProps) {
  const form = useForm<FuncionarioFormData>({
    resolver: zodResolver(funcionarioSchema),
    defaultValues: {
      nome: "",
      funcao: "",
      contato: "",
      status: "ATIVO",
    },
  });

  useEffect(() => {
    if (initialData) {
      form.reset({
        nome: initialData.nome,
        funcao: initialData.funcao,
        contato: initialData.contato,
        status: initialData.status,
      });
    } else {
      form.reset({
        nome: "",
        funcao: "",
        contato: "",
        status: "ATIVO",
      });
    }
  }, [initialData, form]);

  const handleSubmit = (data: FuncionarioFormData) => {
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
            {initialData ? "Editar Funcionário" : "Novo Funcionário"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="nome"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nome Completo</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: João Silva" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="funcao"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Função</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: Médico, Enfermeiro, Recepcionista" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="contato"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Contato</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: (11) 99999-9999 ou email@exemplo.com" {...field} />
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
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Selecione o status" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="ATIVO">Ativo</SelectItem>
                        <SelectItem value="INATIVO">Inativo</SelectItem>
                        <SelectItem value="FERIAS">Férias</SelectItem>
                        <SelectItem value="AFASTADO">Afastado</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}

            <div className="flex justify-end gap-2 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
              >
                Cancelar
              </Button>
              <Button type="submit">
                {initialData ? "Atualizar" : "Cadastrar"}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

