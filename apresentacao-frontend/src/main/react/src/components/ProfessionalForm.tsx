import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { toast } from "sonner";

const professionalSchema = z.object({
  nome: z.string().min(3, "Nome deve ter pelo menos 3 caracteres"),
  conselho: z.string().min(1, "Selecione o conselho"),
  numeroConselho: z.string().min(1, "Número do conselho é obrigatório"),
  especialidade: z.string().min(1, "Especialidade é obrigatória"),
  telefone: z.string().optional(),
  email: z.string().email("Email inválido").optional().or(z.literal("")),
  status: z.enum(["ativo", "ferias", "afastado"]),
});

type ProfessionalFormData = z.infer<typeof professionalSchema>;

interface ProfessionalFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  professional?: any;
  onSave: (data: ProfessionalFormData) => void;
}

export function ProfessionalForm({ open, onOpenChange, professional, onSave }: ProfessionalFormProps) {
  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<ProfessionalFormData>({
    resolver: zodResolver(professionalSchema),
    defaultValues: {
      status: "ativo"
    }
  });

  useEffect(() => {
    if (professional) {
      reset(professional);
    } else {
      reset({
        nome: "",
        conselho: "",
        numeroConselho: "",
        especialidade: "",
        telefone: "",
        email: "",
        status: "ativo"
      });
    }
  }, [professional, reset]);

  const onSubmit = (data: ProfessionalFormData) => {
    onSave(data);
    toast.success(professional ? "Profissional atualizado!" : "Profissional cadastrado!");
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {professional ? "Editar Profissional" : "Novo Profissional"}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <Label htmlFor="nome">Nome Completo *</Label>
              <Input id="nome" {...register("nome")} />
              {errors.nome && <p className="text-sm text-destructive">{errors.nome.message}</p>}
            </div>

            <div>
              <Label htmlFor="conselho">Conselho *</Label>
              <Select onValueChange={(value) => setValue("conselho", value)} defaultValue={professional?.conselho}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="CRM">CRM - Médico</SelectItem>
                  <SelectItem value="COREN">COREN - Enfermeiro</SelectItem>
                  <SelectItem value="CRO">CRO - Odontólogo</SelectItem>
                  <SelectItem value="CRF">CRF - Farmacêutico</SelectItem>
                  <SelectItem value="CREFITO">CREFITO - Fisioterapeuta</SelectItem>
                </SelectContent>
              </Select>
              {errors.conselho && <p className="text-sm text-destructive">{errors.conselho.message}</p>}
            </div>

            <div>
              <Label htmlFor="numeroConselho">Número do Conselho *</Label>
              <Input id="numeroConselho" {...register("numeroConselho")} placeholder="12345/SP" />
              {errors.numeroConselho && <p className="text-sm text-destructive">{errors.numeroConselho.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="especialidade">Especialidade *</Label>
              <Input id="especialidade" {...register("especialidade")} />
              {errors.especialidade && <p className="text-sm text-destructive">{errors.especialidade.message}</p>}
            </div>

            <div>
              <Label htmlFor="telefone">Telefone</Label>
              <Input id="telefone" {...register("telefone")} placeholder="(11) 99999-9999" />
            </div>

            <div>
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" {...register("email")} />
              {errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="status">Status *</Label>
              <Select onValueChange={(value: any) => setValue("status", value)} defaultValue={professional?.status || "ativo"}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ativo">Ativo</SelectItem>
                  <SelectItem value="ferias">Férias</SelectItem>
                  <SelectItem value="afastado">Afastado</SelectItem>
                </SelectContent>
              </Select>
              {errors.status && <p className="text-sm text-destructive">{errors.status.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              {professional ? "Atualizar" : "Cadastrar"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
