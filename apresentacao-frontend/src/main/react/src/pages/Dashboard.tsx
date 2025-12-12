import { useAuth } from "@/contexts/AuthContext";
import GestorDashboard from "@/components/dashboards/GestorDashboard";
import MedicoDashboard from "@/components/dashboards/MedicoDashboard";
import AtendenteDashboard from "@/components/dashboards/AtendenteDashboard";

export default function Dashboard() {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex items-center justify-center h-full">
        <p className="text-muted-foreground">Carregando...</p>
      </div>
    );
  }

  switch(user.role) {
    case 'gestor':
      return <GestorDashboard />;
    case 'medico':
      return <MedicoDashboard />;
    case 'atendente':
      return <AtendenteDashboard />;
    default:
      return (
        <div className="flex items-center justify-center h-full">
          <p className="text-muted-foreground">Perfil não reconhecido</p>
        </div>
      );
  }
}