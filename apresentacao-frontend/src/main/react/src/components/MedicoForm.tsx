import { useEffect } from "react";
import { useFieldArray, useForm } from "react-hook-form";
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
import { Plus, Trash2 } from "lucide-react";
import type { MedicoResumo } from "@/api/useMedicosApi";

const disponibilidadeSchema = z.object({
  diaSemana: z.string().min(1, "Dia da semana é obrigatório"),
  horaInicio: z.string().min(1, "Hora início é obrigatória"),
  horaFim: z.string().min(1, "Hora fim é obrigatória"),
});

const medicoSchema = z.object({
  nome: z.string().min(3, "Nome deve ter pelo menos 3 caracteres"),
  contato: z.string().min(1, "Contato é obrigatório"),

  // Só no cadastro (quando não tem initialData) - a UI controla isso
  crmNumero: z.string().optional(),
  crmUf: z.string().optional(),
  especialidadeId: z
    .union([z.number().int().positive(), z.nan()])
    .optional()
    .transform((v) => (Number.isNaN(v as number) ? undefined : (v as number))),

  disponibilidades: z.array(disponibilidadeSchema).optional(),
});

export type MedicoFormData = z.infer<typeof medicoSchema>;

interface MedicoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  initialData?: MedicoResumo | null;
  onSave: (data: MedicoFormData) => void;
}

export function MedicoForm({
  open,
  onOpenChange,
  initialData,
  onSave,
}: MedicoFormProps) {
  const form = useForm<MedicoFormData>({
    resolver: zodResolver(medicoSchema),
    defaultValues: {
      nome: "",
      contato: "",
      crmNumero: "",
      crmUf: "",
      especialidadeId: undefined,
      disponibilidades: [],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "disponibilidades",
  });

  useEffect(() => {
    if (initialData) {
      // No Swagger do PUT não pede crm/especialidadeId (só nome/contato/disponibilidades)
      form.reset({
        nome: initialData.nome,
        contato: initialData.contato,
        disponibilidades: [],
      });
    } else {
      form.reset({
        nome: "",
        contato: "",
        crmNumero: "",
        crmUf: "",
        especialidadeId: undefined,
        disponibilidades: [],
      });
    }
  }, [initialData, form]);

  const handleSubmit = (data: MedicoFormData) => {
    // Regras de UI:
    // - cadastro: exige crmNumero, crmUf, especialidadeId
    // - edição: envia só nome/contato/disponibilidades
    if (!initialData) {
      if (!data.crmNumero?.trim()) {
        form.setError("crmNumero", { message: "CRM número é obrigatório" });
        return;
      }
      if (!data.crmUf?.trim()) {
        form.setError("crmUf", { message: "UF do CRM é obrigatória" });
        return;
      }
      if (!data.especialidadeId) {
        form.setError("especialidadeId", { message: "EspecialidadeId é obrigatório" });
        return;
      }
    }

    onSave(data);

    if (!initialData) form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[650px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{initialData ? "Editar Médico" : "Novo Médico"}</DialogTitle>
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
                    <Input placeholder="Ex: Dra. Maria Silva" {...field} />
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
                    <Input placeholder="Ex: (81) 99999-9999 / email@exemplo.com" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {!initialData && (
              <div className="grid grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="crmNumero"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>CRM (Número)</FormLabel>
                      <FormControl>
                        <Input placeholder="Ex: 12345" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="crmUf"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>CRM (UF)</FormLabel>
                      <FormControl>
                        <Input placeholder="Ex: SP" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="especialidadeId"
                  render={({ field }) => (
                    <FormItem className="col-span-2">
                      <FormLabel>Especialidade (ID)</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          placeholder="Ex: 1"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(Number(e.target.value))}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
            )}

            {/* Disponibilidades */}
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">Disponibilidades</p>
                  <p className="text-sm text-muted-foreground">
                    Informe os dias e horários de atendimento
                  </p>
                </div>
                <Button
                  type="button"
                  variant="outline"
                  className="gap-2"
                  onClick={() =>
                    append({ diaSemana: "", horaInicio: "", horaFim: "" })
                  }
                >
                  <Plus className="h-4 w-4" />
                  Adicionar
                </Button>
              </div>

              {fields.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                  Nenhuma disponibilidade adicionada.
                </p>
              ) : (
                <div className="space-y-3">
                  {fields.map((f, idx) => (
                    <div key={f.id} className="grid grid-cols-3 gap-3 items-end">
                      <FormField
                        control={form.control}
                        name={`disponibilidades.${idx}.diaSemana` as const}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Dia</FormLabel>
                            <FormControl>
                              <Input placeholder="Ex: Segunda" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name={`disponibilidades.${idx}.horaInicio` as const}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>Início</FormLabel>
                            <FormControl>
                              <Input placeholder="08:00" {...field} />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <div className="flex gap-2">
                        <FormField
                          control={form.control}
                          name={`disponibilidades.${idx}.horaFim` as const}
                          render={({ field }) => (
                            <FormItem className="flex-1">
                              <FormLabel>Fim</FormLabel>
                              <FormControl>
                                <Input placeholder="18:00" {...field} />
                              </FormControl>
                              <FormMessage />
                            </FormItem>
                          )}
                        />

                        <Button
                          type="button"
                          variant="outline"
                          size="icon"
                          onClick={() => remove(idx)}
                          title="Remover"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="flex justify-end gap-2 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
              >
                Cancelar
              </Button>
              <Button type="submit">{initialData ? "Atualizar" : "Cadastrar"}</Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}