// src/components/FolhaPagamentoForm.tsx

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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import type { TipoRegistro, TipoVinculo, FolhaPagamentoDetalhes } from "@/api/useFolhaPagamentoApi";

const formSchema = z.object({
  funcionarioId: z.string().min(1, "Selecione um funcionário"),
  periodoReferencia: z.string().regex(/^(0[1-9]|1[0-2])\/\d{4}$/, "Formato deve ser MM/AAAA"),
  tipoRegistro: z.enum(["PAGAMENTO", "AJUSTE"] as const),
  tipoVinculo: z.enum(["CLT", "ESTAGIARIO", "PJ"] as const),
  salarioBase: z.number().positive("Valor deve ser positivo"),
  beneficios: z.number().min(0, "Valor não pode ser negativo"),
  metodoPagamento: z.string().min(1, "Selecione o método de pagamento"),
});

interface FolhaPagamentoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: z.infer<typeof formSchema>) => void;
  initialData?: FolhaPagamentoDetalhes | null;
  funcionarios: Array<{ id: number; nome: string; funcao: string }>;
  isEditing?: boolean;
}

export function FolhaPagamentoForm({
  open,
  onOpenChange,
  onSave,
  initialData,
  funcionarios,
  isEditing = false
}: FolhaPagamentoFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      funcionarioId: "",
      periodoReferencia: "",
      tipoRegistro: "PAGAMENTO" as TipoRegistro,
      tipoVinculo: "CLT" as TipoVinculo,
      salarioBase: 0,
      beneficios: 0,
      metodoPagamento: "",
    },
  });

  // Atualiza form quando initialData mudar
  useEffect(() => {
    if (initialData) {
      form.reset({
        funcionarioId: String(initialData.funcionarioId),
        periodoReferencia: initialData.periodoReferencia,
        tipoRegistro: initialData.tipoRegistro,
        tipoVinculo: "CLT" as TipoVinculo, // Você precisaria adicionar tipoVinculo no DTO de detalhes
        salarioBase: initialData.salarioBase,
        beneficios: initialData.beneficios,
        metodoPagamento: initialData.metodoPagamento,
      });
    } else {
      form.reset({
        funcionarioId: "",
        periodoReferencia: "",
        tipoRegistro: "PAGAMENTO" as TipoRegistro,
        tipoVinculo: "CLT" as TipoVinculo,
        salarioBase: 0,
        beneficios: 0,
        metodoPagamento: "",
      });
    }
  }, [initialData, form]);

  const tipoVinculo = form.watch("tipoVinculo");
  const tipoRegistro = form.watch("tipoRegistro");
  const salarioBase = Number(form.watch("salarioBase")) || 0;
  const beneficios = Number(form.watch("beneficios")) || 0;

  // Cálculo de descontos baseado no tipo de vínculo
  const calcularDescontos = () => {
    if (tipoRegistro === "AJUSTE") return 0;
    if (tipoVinculo === "CLT") {
      const inss = salarioBase * 0.11;
      const baseIRRF = salarioBase - inss;
      const irrf = baseIRRF * 0.15;
      return inss + irrf;
    }
    return 0;
  };

  const descontos = calcularDescontos();
  const valorLiquido = salarioBase + beneficios - descontos;

  const handleSubmit = (data: z.infer<typeof formSchema>) => {
    onSave(data);
    if (!isEditing) {
      form.reset();
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {isEditing ? "Editar Valores da Folha" : "Registrar Nova Folha de Pagamento"}
          </DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="funcionarioId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Funcionário</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    value={field.value}
                    disabled={isEditing}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione o funcionário" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {funcionarios.length === 0 ? (
                        <div className="p-2 text-sm text-muted-foreground">
                          Nenhum funcionário disponível
                        </div>
                      ) : (
                        funcionarios.map((func) => (
                          <SelectItem key={func.id} value={String(func.id)}>
                            {func.nome} - {func.funcao}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="periodoReferencia"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Período de Referência (MM/AAAA)</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="12/2024"
                      {...field}
                      disabled={isEditing}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="tipoRegistro"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Tipo de Registro</FormLabel>
                    <Select
                      onValueChange={field.onChange}
                      value={field.value}
                      disabled={isEditing}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="PAGAMENTO">Pagamento</SelectItem>
                        <SelectItem value="AJUSTE">Ajuste</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="tipoVinculo"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Tipo de Vínculo</FormLabel>
                    <Select
                      onValueChange={field.onChange}
                      value={field.value}
                      disabled={isEditing}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="CLT">CLT</SelectItem>
                        <SelectItem value="ESTAGIARIO">Estagiário</SelectItem>
                        <SelectItem value="PJ">PJ</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            {isEditing && (
              <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg text-sm text-blue-800">
                <strong>Nota:</strong> Na edição, você só pode alterar o salário base e os benefícios.
                Outros campos não podem ser modificados.
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="salarioBase"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Salário Base (R$)</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        value={field.value || ''}
                        onChange={(e) => {
                          const value = e.target.value === '' ? 0 : parseFloat(e.target.value);
                          field.onChange(value);
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="beneficios"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Benefícios (R$)</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        value={field.value || ''}
                        onChange={(e) => {
                          const value = e.target.value === '' ? 0 : parseFloat(e.target.value);
                          field.onChange(value);
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="p-4 bg-muted rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span>Salário Base:</span>
                <span className="font-medium">{salarioBase.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Benefícios:</span>
                <span className="font-medium text-success">{beneficios.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</span>
              </div>
              {descontos > 0 && (
                <div className="flex justify-between text-sm">
                  <span>Descontos (INSS + IRRF):</span>
                  <span className="font-medium text-destructive">-{descontos.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</span>
                </div>
              )}
              <div className="border-t pt-2 mt-2">
                <div className="flex justify-between items-center">
                  <span className="text-sm font-medium">Valor Líquido:</span>
                  <span className="text-2xl font-bold text-primary">
                    {valorLiquido.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                  </span>
                </div>
              </div>
              {tipoVinculo === "CLT" && tipoRegistro === "PAGAMENTO" && (
                <p className="text-xs text-muted-foreground mt-2">
                  * Descontos: INSS 11% + IRRF 15%
                </p>
              )}
              {tipoRegistro === "AJUSTE" && (
                <p className="text-xs text-muted-foreground mt-2">
                  * Ajustes não possuem descontos
                </p>
              )}
            </div>

            <FormField
              control={form.control}
              name="metodoPagamento"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Método de Pagamento</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    value={field.value}
                    disabled={isEditing}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione o método" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="Transferência Bancária">Transferência Bancária</SelectItem>
                      <SelectItem value="PIX">PIX</SelectItem>
                      <SelectItem value="Cheque">Cheque</SelectItem>
                      <SelectItem value="Dinheiro">Dinheiro</SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-3 pt-4">
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                Cancelar
              </Button>
              <Button type="submit">
                {isEditing ? "Atualizar" : "Salvar"}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}