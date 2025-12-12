import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

const movimentoSchema = z.object({
  itemId: z.string().min(1, "Item é obrigatório"),
  tipo: z.enum(["entrada", "saida"]),
  quantidade: z.number().min(1, "Quantidade deve ser maior que 0"),
  motivo: z.string().min(3, "Motivo é obrigatório"),
  data: z.string().min(1, "Data é obrigatória"),
});

type MovimentoFormData = z.infer<typeof movimentoSchema>;

interface MovimentoEstoqueFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  itemNome?: string;
  tipo: "entrada" | "saida";
  onSave: (data: MovimentoFormData) => void;
}

export function MovimentoEstoqueForm({ open, onOpenChange, itemNome, tipo, onSave }: MovimentoEstoqueFormProps) {
  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<MovimentoFormData>({
    resolver: zodResolver(movimentoSchema),
    defaultValues: {
      tipo,
      data: new Date().toISOString().split('T')[0]
    }
  });

  useEffect(() => {
    if (!open) {
      reset({
        itemId: "",
        tipo,
        quantidade: 1,
        motivo: "",
        data: new Date().toISOString().split('T')[0]
      });
    }
  }, [open, tipo, reset]);

  const onSubmit = (data: MovimentoFormData) => {
    onSave(data);
    toast.success(`${tipo === "entrada" ? "Entrada" : "Saída"} registrada com sucesso!`);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>
            Registrar {tipo === "entrada" ? "Entrada" : "Saída"} de Estoque
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-4">
            <div>
              <Label htmlFor="itemId">Item *</Label>
              <Input 
                id="itemId" 
                {...register("itemId")} 
                placeholder={itemNome || "Nome ou código do item"} 
                defaultValue={itemNome}
              />
              {errors.itemId && <p className="text-sm text-destructive">{errors.itemId.message}</p>}
            </div>

            <div>
              <Label htmlFor="quantidade">Quantidade *</Label>
              <Input 
                id="quantidade" 
                type="number" 
                {...register("quantidade", { valueAsNumber: true })} 
                min="1"
              />
              {errors.quantidade && <p className="text-sm text-destructive">{errors.quantidade.message}</p>}
            </div>

            <div>
              <Label htmlFor="motivo">Motivo *</Label>
              <Textarea 
                id="motivo" 
                {...register("motivo")} 
                rows={3}
                placeholder={tipo === "entrada" ? "Ex: Compra, Doação" : "Ex: Uso clínico, Descarte"}
              />
              {errors.motivo && <p className="text-sm text-destructive">{errors.motivo.message}</p>}
            </div>

            <div>
              <Label htmlFor="data">Data *</Label>
              <Input id="data" type="date" {...register("data")} />
              {errors.data && <p className="text-sm text-destructive">{errors.data.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              Registrar
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
