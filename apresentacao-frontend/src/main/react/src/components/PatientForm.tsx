import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
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

// Schema de Validação
const patientFormSchema = z.object({
  name: z.string().min(3, "Nome obrigatório"),
  cpf: z.string().min(11, "CPF incompleto"),
  phone: z.string().min(10, "Telefone inválido"),
  // ADICIONADO: Campo Address e BirthDate obrigatórios
  address: z.string().min(5, "Endereço é obrigatório"),
  birthDate: z.string().min(1, "Data de nascimento obrigatória"),
});

type PatientFormValues = z.infer<typeof patientFormSchema>;

interface PatientFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  patient?: any;
  onSave: (data: PatientFormValues) => void;
}

export function PatientForm({ open, onOpenChange, patient, onSave }: PatientFormProps) {
  const form = useForm<PatientFormValues>({
    resolver: zodResolver(patientFormSchema),
    defaultValues: {
      name: "",
      cpf: "",
      phone: "",
      address: "",
      birthDate: "",
    },
  });

  // Preenche o formulário se for edição
  useEffect(() => {
    if (patient) {
      form.reset({
        name: patient.name || "",
        cpf: patient.cpf || "",
        phone: patient.phone || "",
        address: patient.address || "", // Garante que o endereço venha
        birthDate: converterDataParaInput(patient.birthDate),
      });
    } else {
      form.reset({
        name: "",
        cpf: "",
        phone: "",
        address: "",
        birthDate: "",
      });
    }
  }, [patient, open, form]);

  // Converte dd/MM/yyyy (Java) para yyyy-MM-dd (Input HTML)
  const converterDataParaInput = (data?: string) => {
    if (!data) return "";
    if (data.includes("/")) {
      const [dia, mes, ano] = data.split("/");
      return `${ano}-${mes}-${dia}`;
    }
    return data;
  };

  const onSubmit = (data: PatientFormValues) => {
    onSave(data);
    // Não fechamos o modal aqui, deixamos o componente pai fechar após o sucesso
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-xl">
        <DialogHeader>
          <DialogTitle>{patient ? "Editar Paciente" : "Novo Paciente"}</DialogTitle>
          <DialogDescription>Preencha os dados obrigatórios para o sistema.</DialogDescription>
        </DialogHeader>
        
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nome Completo</FormLabel>
                  <FormControl><Input placeholder="Ex: Ana Silva" {...field} /></FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="cpf"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>CPF</FormLabel>
                    <FormControl><Input placeholder="000.000.000-00" {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="birthDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Data de Nascimento</FormLabel>
                    <FormControl><Input type="date" {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="phone"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Telefone</FormLabel>
                  <FormControl><Input placeholder="(00) 00000-0000" {...field} /></FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* O CAMPO QUE FALTAVA NA SUA TELA: ENDEREÇO */}
            <FormField
              control={form.control}
              name="address"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Endereço Completo</FormLabel>
                  <FormControl><Input placeholder="Rua, Número, Bairro" {...field} /></FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
              <Button type="submit">{patient ? "Salvar" : "Cadastrar"}</Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
