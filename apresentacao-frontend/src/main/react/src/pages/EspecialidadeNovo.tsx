import { useNavigate } from "react-router-dom";
import { EspecialidadeForm } from "@/components/EspecialidadeForm";
import { useCriarEspecialidade } from "@/hooks/useEspecialidades";
import { toast } from "@/hooks/use-toast";

export default function EspecialidadeNovo() {
  const navigate = useNavigate();
  const criar = useCriarEspecialidade();

  const handleSave = async (data: any) => {
    try {
      await criar.mutateAsync({ nome: data.nome, descricao: data.descricao });
      toast({ title: "Especialidade cadastrada", description: "Nova especialidade foi adicionada com sucesso." });
      navigate("/especialidades");
    } catch (e: any) {
      toast({ title: "Erro", description: e?.message ?? "Erro ao cadastrar especialidade" });
    }
  };

  return (
    <div className="animate-fade-in">
      <h1 className="text-3xl font-bold">Nova Especialidade</h1>
      <p className="text-muted-foreground mb-6">Preencha os dados da especialidade médica</p>
      <EspecialidadeForm open={true} onOpenChange={() => navigate('/especialidades')} onSave={handleSave} />
    </div>
  );
}
