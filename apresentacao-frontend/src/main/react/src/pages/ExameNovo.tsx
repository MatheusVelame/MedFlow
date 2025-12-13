import { useNavigate } from "react-router-dom";
import { ExameForm } from "@/components/ExameForm";
import { useAgendarExame } from "@/hooks/useExames";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";

export default function ExameNovo() {
  const navigate = useNavigate();
  const agendar = useAgendarExame();
  const { user } = useAuth();

  const handleSave = async (data: any) => {
    try {
      // data.dataHora is in datetime-local (YYYY-MM-DDTHH:mm), backend expects seconds
      const dataHora = data.dataHora.length === 16 ? `${data.dataHora}:00` : data.dataHora;
      const responsavelId = Number(user?.id || 1);
      await agendar.mutateAsync({ pacienteId: Number(data.pacienteId), medicoId: Number(data.medicoId), tipoExame: data.tipoExame, dataHora, responsavelId });
      toast({ title: "Exame agendado", description: "Agendamento criado com sucesso." });
      navigate('/exames');
    } catch (e: any) {
      toast({ title: "Erro", description: e?.message ?? "Erro ao agendar exame" });
    }
  };

  return (
    <div className="animate-fade-in">
      <h1 className="text-3xl font-bold">Agendar Exame</h1>
      <p className="text-muted-foreground mb-6">Preencha os dados para solicitar um exame</p>
      <ExameForm open={true} onOpenChange={() => navigate('/exames')} onSave={handleSave} />
    </div>
  );
}