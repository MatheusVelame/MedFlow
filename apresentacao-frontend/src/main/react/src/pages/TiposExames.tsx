// src/pages/TiposExames.tsx

import { useState } from "react";
import { Plus, Pencil, Archive, ArchiveRestore } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useForm } from "react-hook-form";
import { 
    useListarTiposExames, 
    useListarTiposInativos, 
    useCadastrarTipoExame, 
    useEditarTipoExame, 
    useInativarTipoExame,
    TipoExameResumo,
    CadastrarTipoExamePayload 
} from "@/api/useTiposExamesApi";

// Componente Principal
export default function TiposExames() {
  const [mostrarInativos, setMostrarInativos] = useState(false);
  
  // Busca dados dependendo do botão apertado
  const { data: ativos, isLoading: loadingAtivos } = useListarTiposExames();
  const { data: inativos, isLoading: loadingInativos } = useListarTiposInativos();

  const listaAtual = mostrarInativos ? inativos : ativos;
  const isLoading = mostrarInativos ? loadingInativos : loadingAtivos;

  return (
    <div className="p-8 space-y-6">
      
      {/* Cabeçalho */}
      <div className="flex justify-between items-center">
        <div>
            <h1 className="text-2xl font-bold text-slate-800">Tipos de Exames</h1>
            <p className="text-gray-500">Configuração completa de exames do sistema</p>
        </div>
        <div className="flex gap-2">
            {/* Botão de Alternar Ativos/Inativos */}
            <Button 
                variant="outline" 
                onClick={() => setMostrarInativos(!mostrarInativos)}
                className={mostrarInativos ? "bg-amber-50 border-amber-200 text-amber-700" : ""}
            >
                {mostrarInativos ? (
                    <><ArchiveRestore className="w-4 h-4 mr-2" /> Voltar para Ativos</>
                ) : (
                    <><Archive className="w-4 h-4 mr-2" /> Ver Inativos</>
                )}
            </Button>

            {/* Modal de Cadastro (Só aparece se estiver vendo Ativos) */}
            {!mostrarInativos && <DialogCadastro />} 
        </div>
      </div>

      {/* Listagem */}
      {isLoading ? (
        <p className="text-center py-10">Carregando...</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {listaAtual?.map((exame) => (
            <CardExame 
                key={exame.id} 
                exame={exame} 
                isInativo={mostrarInativos} 
            />
          ))}
          {listaAtual?.length === 0 && (
            <div className="col-span-3 text-center py-12 border-2 border-dashed rounded-lg bg-gray-50">
                <p className="text-gray-500">Nenhum exame encontrado nesta lista.</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

// --- Sub-componente: Card do Exame ---
function CardExame({ exame, isInativo }: { exame: TipoExameResumo, isInativo: boolean }) {
    const { mutate: inativar } = useInativarTipoExame();

    return (
        <div className={`bg-white p-5 rounded-xl border shadow-sm hover:shadow-md transition-shadow ${isInativo ? 'opacity-75 bg-gray-50' : ''}`}>
            <div className="flex justify-between items-start mb-3">
                <div className="flex items-center gap-2">
                    <span className="font-mono text-xs font-bold text-slate-500 bg-slate-100 px-2 py-1 rounded">
                        {exame.codigo}
                    </span>
                    <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wide ${
                        isInativo ? "bg-red-100 text-red-600" : "bg-emerald-100 text-emerald-600"
                    }`}>
                        {isInativo ? "INATIVO" : "ATIVO"}
                    </span>
                </div>
            </div>
            
            <h3 className="font-bold text-lg text-slate-800 mb-1 leading-tight">{exame.descricao}</h3>
            
            <div className="flex justify-between items-end mt-4 pt-4 border-t border-gray-100">
                <div className="flex flex-col">
                    <span className="text-xs text-gray-400 font-medium uppercase">Especialidade</span>
                    <span className="text-sm text-gray-700">{exame.especialidade}</span>
                </div>
                <span className="font-bold text-lg text-blue-600">
                    {exame.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                </span>
            </div>

            {/* Ações (Só mostra se for ATIVO) */}
            {!isInativo && (
                <div className="mt-4 flex gap-2">
                    <DialogEdicao exame={exame} />
                    <Button 
                        variant="ghost" 
                        className="flex-1 text-red-600 hover:text-red-700 hover:bg-red-50"
                        onClick={() => {
                            if(window.confirm("Tem certeza que deseja inativar este exame?")) {
                                inativar({ id: exame.id, payload: { responsavelId: 1 } })
                            }
                        }}
                    >
                        Inativar
                    </Button>
                </div>
            )}
        </div>
    );
}

// --- Sub-componente: Dialog de Edição ---
function DialogEdicao({ exame }: { exame: TipoExameResumo }) {
    const [open, setOpen] = useState(false);
    const { mutate: editar } = useEditarTipoExame();
    const { register, handleSubmit } = useForm<CadastrarTipoExamePayload>({
        defaultValues: {
            codigo: exame.codigo, // Apenas visual, backend não salva
            descricao: exame.descricao,
            especialidade: exame.especialidade,
            valor: exame.valor
        }
    });

    const onSubmit = (data: CadastrarTipoExamePayload) => {
        editar({ id: exame.id, payload: { ...data, responsavelId: 1 } }, {
            onSuccess: () => setOpen(false)
        });
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" className="flex-1 border-gray-200">
                    <Pencil className="w-4 h-4 mr-2" /> Editar
                </Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Editar Tipo de Exame</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-2">
                    {/* Código Desabilitado (pois não tem rota pra editar) */}
                    <div>
                        <Label className="text-gray-500">Código (Não editável)</Label>
                        <Input {...register("codigo")} disabled className="bg-gray-100" />
                    </div>
                    
                    <div>
                        <Label>Descrição</Label>
                        <Input {...register("descricao")} />
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Label>Especialidade</Label>
                            <Input {...register("especialidade")} />
                        </div>
                        <div>
                            <Label>Valor (R$)</Label>
                            <Input {...register("valor")} />
                        </div>
                    </div>
                    
                    <div className="flex justify-end gap-2 mt-6">
                        <Button type="button" variant="ghost" onClick={() => setOpen(false)}>Cancelar</Button>
                        <Button type="submit">Salvar Alterações</Button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    );
}

// --- Sub-componente: Dialog de Cadastro ---
function DialogCadastro() {
    const [open, setOpen] = useState(false);
    const { mutate: cadastrar } = useCadastrarTipoExame();
    const { register, handleSubmit, reset } = useForm<CadastrarTipoExamePayload>();

    const onSubmit = (data: CadastrarTipoExamePayload) => {
        cadastrar({ ...data, responsavelId: 1 }, {
            onSuccess: () => {
                setOpen(false);
                reset();
            }
        });
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button><Plus className="w-4 h-4 mr-2"/> Novo Tipo</Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader><DialogTitle>Cadastrar Novo Tipo</DialogTitle></DialogHeader>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div>
                        <Label>Código</Label>
                        <Input placeholder="Ex: HEM001" {...register("codigo")} />
                    </div>
                    <div>
                        <Label>Descrição</Label>
                        <Input placeholder="Ex: Hemograma Completo" {...register("descricao")} />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Label>Especialidade</Label>
                            <Input placeholder="Ex: Hematologia" {...register("especialidade")} />
                        </div>
                        <div>
                            <Label>Valor (R$)</Label>
                            <Input placeholder="0,00" {...register("valor")} />
                        </div>
                    </div>
                    <Button type="submit" className="w-full mt-4">Cadastrar</Button>
                </form>
            </DialogContent>
        </Dialog>
    );
}