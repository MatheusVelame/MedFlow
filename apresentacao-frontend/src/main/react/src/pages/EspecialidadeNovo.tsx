import { useNavigate } from "react-router-dom";
import { EspecialidadeForm } from "@/components/EspecialidadeForm";
import { useCriarEspecialidade } from "@/hooks/useEspecialidades";
import { toast } from "@/hooks/use-toast";

export default function EspecialidadeNovo() {
  const navigate = useNavigate();
  const criar = useCriarEspecialidade();

  const handleSave = async (data: any) => {
    try {
      const nome = (data.nome ?? "").toString().trim();
      const descricao = data.descricao ?? null;

      // Validações cliente (mesmas regras do domínio)
      if (!nome) {
        toast({ title: "Erro", description: "O nome da especialidade é obrigatório" });
        return;
      }

      // Permite letras, espaços e acentos (Unicode letters)
      const nomeValido = /^[\p{L}\s]+$/u.test(nome);
      if (!nomeValido) {
        toast({ title: "Erro", description: "O nome da especialidade deve conter apenas caracteres alfabéticos e espaços" });
        return;
      }

      if (descricao && descricao.length > 255) {
        toast({ title: "Erro", description: "A descrição não pode exceder 255 caracteres" });
        return;
      }

      await criar.mutateAsync({ nome, descricao });
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