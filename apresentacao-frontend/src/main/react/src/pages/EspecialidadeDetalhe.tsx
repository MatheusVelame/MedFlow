import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useEspecialidade, useAtualizarEspecialidade } from "@/hooks/useEspecialidades";
import { EspecialidadeForm } from "@/components/EspecialidadeForm";
import { toast } from "@/hooks/use-toast";

export default function EspecialidadeDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data, isLoading } = useEspecialidade(id ? Number(id) : undefined);
  const atualizar = useAtualizarEspecialidade();

  const handleSave = async (formData: any) => {
    try {
      await atualizar.mutateAsync({ id: Number(id), payload: { novoNome: formData.nome, novaDescricao: formData.descricao } });
      toast({ title: "Especialidade atualizada", description: "Alterações salvas." });
      navigate('/especialidades');
    } catch (e: any) {
      toast({ title: "Erro", description: e?.message ?? "Erro ao atualizar" });
    }
  };

  if (isLoading) return <div>Carregando...</div>;
  if (!data) return <div>Especialidade não encontrada</div>;

  return (
    <div className="animate-fade-in">
      <h1 className="text-3xl font-bold mb-2">Editar Especialidade</h1>
      <p className="text-muted-foreground mb-6">Atualize os dados da especialidade</p>
      <EspecialidadeForm open={true} onOpenChange={() => navigate('/especialidades')} onSave={handleSave} initialData={{ nome: data.nome, descricao: data.descricao }} />
    </div>
  );
}
