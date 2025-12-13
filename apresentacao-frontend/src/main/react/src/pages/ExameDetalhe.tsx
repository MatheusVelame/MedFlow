import { useParams, useNavigate } from "react-router-dom";
import { useExame, useAtualizarExame, useCancelarExame, useExcluirExame } from "@/hooks/useExames";
import { ExameForm } from "@/components/ExameForm";
import { useState } from "react";
import { toast } from "@/hooks/use-toast";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";

export default function ExameDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const exameId = id ? Number(id) : undefined;

  const { data: exame, isLoading } = useExame(exameId);
  const atualizar = useAtualizarExame();
  const cancelar = useCancelarExame();
  const excluir = useExcluirExame();
  const { user } = useAuth();

  const [cancelling, setCancelling] = useState(false);

  if (isLoading) return <div>Carregando...</div>;
  if (!exame) return <div>Exame não encontrado</div>;

  const handleSave = async (formData: any) => {
    try {
      const dataHora = formData.dataHora.length === 16 ? `${formData.dataHora}:00` : formData.dataHora;
      const responsavelId = Number(user?.id || 1);
      await atualizar.mutateAsync({ id: exame.id, payload: { medicoId: Number(formData.medicoId), tipoExame: formData.tipoExame, dataHora, responsavelId, observacoes: formData.observacoes } });
      toast({ title: 'Exame atualizado', description: 'Agendamento atualizado.' });
      navigate('/exames');
    } catch (e: any) {
      toast({ title: 'Erro', description: e?.message ?? 'Erro ao atualizar exame' });
    }
  };

  const handleCancel = async (motivo?: string) => {
    try {
      setCancelling(true);
      const responsavelId = Number(user?.id || 1);
      await cancelar.mutateAsync({ id: exame.id, payload: { motivo: motivo || 'Cancelado pelo usuário', responsavelId } });
      toast({ title: 'Exame cancelado', description: 'Exame cancelado com sucesso.' });
      navigate('/exames');
    } catch (e: any) {
      toast({ title: 'Erro', description: e?.message ?? 'Erro ao cancelar exame' });
    } finally {
      setCancelling(false);
    }
  };

  const handleDelete = async () => {
    try {
      const responsavelId = Number(user?.id || 1);
      await excluir.mutateAsync({ id: exame.id, responsavelId });
      toast({ title: 'Exame excluído', description: 'Exame removido.' });
      navigate('/exames');
    } catch (e: any) {
      toast({ title: 'Erro', description: e?.message ?? 'Erro ao excluir exame' });
    }
  };

  const ultimaObservacao = exame.historico && exame.historico.length > 0 ? exame.historico[exame.historico.length - 1].descricao : null;

  return (
    <div className="animate-fade-in">
      <h1 className="text-3xl font-bold mb-2">Detalhes do Exame</h1>
      <p className="text-muted-foreground mb-6">Visualize e edite as informações do exame</p>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <ExameForm open={true} onOpenChange={() => navigate('/exames')} onSave={handleSave} initialData={{ pacienteId: exame.pacienteId, tipoExame: exame.tipoExame, medicoId: exame.medicoId, dataHora: exame.dataHora, observacoes: ultimaObservacao }} />

          <div className="mt-4 flex gap-2">
            <Button variant="outline" onClick={() => handleCancel('Cancelado via UI')} disabled={cancelling}>{cancelling ? 'Cancelando...' : 'Cancelar Exame'}</Button>
            <Button variant="destructive" onClick={handleDelete}>Excluir Exame</Button>
          </div>
        </div>

        <aside className="bg-white border rounded-md p-4">
          <h2 className="font-semibold mb-2">Histórico de alterações</h2>
          {exame.historico && exame.historico.length > 0 ? (
            <ul className="space-y-2">
              {exame.historico.slice().reverse().map((h: any, idx: number) => (
                <li key={idx} className="border-b pb-2">
                  <div className="text-sm text-muted-foreground">{new Date(h.dataHora).toLocaleString()}</div>
                  <div className="font-medium">{h.acao}</div>
                  <div className="text-sm">{h.descricao}</div>
                  <div className="text-xs text-muted-foreground">Responsável: {h.responsavelId}</div>
                </li>
              ))}
            </ul>
          ) : (
            <div className="text-sm text-muted-foreground">Nenhuma alteração registrada.</div>
          )}
        </aside>
      </div>
    </div>
  );
}