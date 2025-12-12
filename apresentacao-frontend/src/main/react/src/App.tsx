import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { MedicalLayout } from "./components/MedicalLayout";
import Dashboard from "./pages/Dashboard";
import Agendamentos from "./pages/Agendamentos";
import Pacientes from "./pages/Pacientes";
import Triagem from "./pages/Triagem";
import Prontuarios from "./pages/Prontuarios";
import Exames from "./pages/Exames";
import Financeiro from "./pages/Financeiro";
import Faturamentos from "./pages/Faturamentos";
import Estoque from "./pages/Estoque";
import Profissionais from "./pages/Profissionais";
import Relatorios from "./pages/Relatorios";
import Especialidades from "./pages/Especialidades";
import Convenios from "./pages/Convenios";
import Medicamentos from "./pages/Medicamentos";
import Login from "./pages/Login";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <AuthProvider>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="*" element={
              <ProtectedRoute>
                <MedicalLayout>
                  <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/agendamentos" element={<Agendamentos />} />
                    <Route path="/pacientes" element={<Pacientes />} />
                    <Route path="/triagem" element={
                      <ProtectedRoute allowedRoles={['gestor', 'atendente', 'medico']}>
                        <Triagem />
                      </ProtectedRoute>
                    } />
                    <Route path="/prontuarios" element={
                      <ProtectedRoute allowedRoles={['gestor', 'medico']}>
                        <Prontuarios />
                      </ProtectedRoute>
                    } />
                    <Route path="/exames" element={
                      <ProtectedRoute allowedRoles={['gestor', 'medico']}>
                        <Exames />
                      </ProtectedRoute>
                    } />
                    <Route path="/financeiro" element={
                      <ProtectedRoute allowedRoles={['gestor', 'atendente']}>
                        <Financeiro />
                      </ProtectedRoute>
                    } />
                    <Route path="/faturamentos" element={
                      <ProtectedRoute allowedRoles={['gestor', 'atendente']}>
                        <Faturamentos />
                      </ProtectedRoute>
                    } />
                    <Route path="/estoque" element={
                      <ProtectedRoute allowedRoles={['gestor']}>
                        <Estoque />
                      </ProtectedRoute>
                    } />
                    <Route path="/profissionais" element={
                      <ProtectedRoute allowedRoles={['gestor']}>
                        <Profissionais />
                      </ProtectedRoute>
                    } />
                    <Route path="/especialidades" element={
                      <ProtectedRoute allowedRoles={['gestor']}>
                        <Especialidades />
                      </ProtectedRoute>
                    } />
                    <Route path="/convenios" element={
                      <ProtectedRoute allowedRoles={['gestor', 'atendente']}>
                        <Convenios />
                      </ProtectedRoute>
                    } />
                    <Route path="/medicamentos" element={
                      <ProtectedRoute allowedRoles={['gestor', 'medico']}>
                        <Medicamentos />
                      </ProtectedRoute>
                    } />
                    <Route path="/relatorios" element={
                      <ProtectedRoute allowedRoles={['gestor']}>
                        <Relatorios />
                      </ProtectedRoute>
                    } />
                    <Route path="*" element={<NotFound />} />
                  </Routes>
                </MedicalLayout>
              </ProtectedRoute>
            } />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </AuthProvider>
  </QueryClientProvider>
);

export default App;
