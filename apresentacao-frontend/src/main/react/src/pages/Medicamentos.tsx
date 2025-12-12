// Localização: apresentacao-frontend/src/main/react/src/pages/Medicamentos.tsx

import React, { useState, useMemo } from 'react';
import { PlusCircle, Search, MoreVertical, Archive, CheckCircle, XCircle, RefreshCcw } from "lucide-react";
import { 
    useListarMedicamentos, 
    useArquivarMedicamento, 
    useAprovarRevisao, 
    useRejeitarRevisao, 
    useSolicitarRevisao, 
    AcaoResponsavelPayload, 
    MedicamentoResumo 
} from "../api/useMedicamentosApi";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "../components/ui/dialog";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "../components/ui/dropdown-menu";
import { Badge } from "../components/ui/badge";
import { toast } from 'sonner';

// Importar componentes de formulário
import { MedicamentoForm } from "../components/MedicamentoForm";
import { SolicitarRevisaoForm } from "../components/SolicitarRevisaoForm"; 
import { UsoPrincipalEdicaoForm } from "../components/UsoPrincipalEdicaoForm"; // <-- IMPORTADO AQUI

// Componente Layout (Presumido)
interface MedicalLayoutProps {
    title: string;
    breadcrumbs: string[];
    children: React.ReactNode;
}
const MedicalLayout = ({ children }: MedicalLayoutProps) => <div className="p-6">{children}</div>;


export default function Medicamentos() {
  const { data: medicamentos, isLoading, isError, error } = useListarMedicamentos();
  
  // Hooks de Ação
  const arquivarMutation = useArquivarMedicamento();
  const aprovarMutation = useAprovarRevisao();
  const rejeitarMutation = useRejeitarRevisao();
  const solicitarRevisaoMutation = useSolicitarRevisao();

  const [isCadastroOpen, setIsCadastroOpen] = useState(false);
  const [isRevisaoModalOpen, setIsRevisaoModalOpen] = useState(false);
  const [isEdicaoUsoPrincipalOpen, setIsEdicaoUsoPrincipalOpen] = useState(false); // <-- NOVO ESTADO
  const [medicamentoSelecionado, setMedicamentoSelecionado] = useState<MedicamentoResumo | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  // Simulação: ID do usuário logado (Gestor)
  const RESPONSAVEL_ID = 1; 

  const filteredMedicamentos = useMemo(() => {

    if (!Array.isArray(medicamentos)) return [];
    return medicamentos.filter((med) => 
        med.nome.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [medicamentos, searchTerm]);

  // Handler para ações simples (Arquivar, Aprovar, Rejeitar)
  const handleAcaoSimples = (medicamento: MedicamentoResumo, mutation: typeof arquivarMutation | typeof aprovarMutation | typeof rejeitarMutation) => {
      const payload: AcaoResponsavelPayload = { responsavelId: RESPONSAVEL_ID };
      mutation.mutate({ id: medicamento.id, payload: payload }); 
  };
  
  // Handlers específicos
  const handleAbrirSolicitarRevisao = (medicamento: MedicamentoResumo) => {
      setMedicamentoSelecionado(medicamento);
      setIsRevisaoModalOpen(true);
  };
  
  // FUNÇÃO QUE ABRE O NOVO MODAL DE EDIÇÃO
  const handleAbrirEdicaoUsoPrincipal = (medicamento: MedicamentoResumo) => {
      setMedicamentoSelecionado(medicamento);
      setIsEdicaoUsoPrincipalOpen(true); // Abre o modal
  };

  // Funções de Fechamento de Modal
  const fecharModalRevisao = () => {
      setMedicamentoSelecionado(null);
      setIsRevisaoModalOpen(false);
  };

  const fecharModalEdicaoUsoPrincipal = () => { // <-- NOVO FECHADOR
      setMedicamentoSelecionado(null);
      setIsEdicaoUsoPrincipalOpen(false);
  };


  if (isLoading) {
    return (
        <MedicalLayout title="Medicamentos" breadcrumbs={["Catálogo", "Medicamentos"]}>
            <div className="text-center py-10">Carregando medicamentos...</div>
        </MedicalLayout>
    );
  }

  if (isError) {
    return (
        <MedicalLayout title="Medicamentos" breadcrumbs={["Catálogo", "Medicamentos"]}>
            <div className="text-center py-10 text-red-500">
                Erro ao carregar medicamentos. Detalhes: {(error as Error)?.message}
            </div>
        </MedicalLayout>
    );
  }

  return (
    <MedicalLayout title="Medicamentos" breadcrumbs={["Catálogo", "Medicamentos"]}>
      <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0 md:space-x-4 mb-6">
        <div className="relative w-full md:w-1/3">
          <Search className="absolute left-2 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Buscar por nome do medicamento..."
            className="pl-8"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <Button onClick={() => setIsCadastroOpen(true)}>
          <PlusCircle className="mr-2 h-4 w-4" /> Cadastrar Novo
        </Button>
      </div>

      <Card>
        <CardHeader>
            <CardTitle>Catálogo de Medicamentos</CardTitle>
            <CardDescription>Lista de todos os medicamentos registrados no sistema.</CardDescription>
        </CardHeader>
        <CardContent>
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Nome</TableHead>
                        <TableHead>Uso Principal</TableHead>
                        <TableHead>Contraindicações</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead className="text-right">Ações</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {filteredMedicamentos.map((medicamento) => (
                        <TableRow key={medicamento.id}>
                            <TableCell className="font-medium">{medicamento.nome}</TableCell>
                            <TableCell>{medicamento.usoPrincipal}</TableCell>

                            <TableCell className="max-w-[200px] truncate">{medicamento.contraindicacoes}</TableCell> 
                            <TableCell>
                                <Badge 
                                    variant={
                                        medicamento.status === 'ATIVO' ? "default" : 
                                        (medicamento.status === 'REVISAO_PENDENTE' ? 'destructive' : 'secondary')
                                    }
                                >
                                    {medicamento.status.replace('_', ' ')}
                                </Badge>
                            </TableCell>
                            <TableCell className="text-right">
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" className="h-8 w-8 p-0">
                                            <span className="sr-only">Abrir menu</span>
                                            <MoreVertical className="h-4 w-4" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        <DropdownMenuLabel>Ações</DropdownMenuLabel>
                                        
                                        {/* AÇÃO: Editar Uso Principal (AGORA ABRE O MODAL) */}
                                        <DropdownMenuItem onClick={() => handleAbrirEdicaoUsoPrincipal(medicamento)}>
                                            <RefreshCcw className="mr-2 h-4 w-4" /> Editar Uso Principal
                                        </DropdownMenuItem>
                                        
                                        <DropdownMenuItem onClick={() => handleAbrirSolicitarRevisao(medicamento)}>
                                            <CheckCircle className="mr-2 h-4 w-4" /> Solicitar Revisão
                                        </DropdownMenuItem>
                                        
                                        <DropdownMenuSeparator />
                                        
                                        {/* AÇÃO: Arquivar (PUT /arquivar) */}
                                        {medicamento.status === 'ATIVO' && (
                                            <DropdownMenuItem 
                                                onClick={() => handleAcaoSimples(medicamento, arquivarMutation)}
                                                disabled={arquivarMutation.isPending}
                                                className="text-red-600"
                                            >
                                                <Archive className="mr-2 h-4 w-4" /> {arquivarMutation.isPending ? 'Arquivando...' : 'Arquivar'}
                                            </DropdownMenuItem>
                                        )}
                                        
                                        {/* AÇÕES DE REVISÃO */}
                                        {medicamento.status === 'REVISAO_PENDENTE' && (
                                            <>
                                                {/* AÇÃO: Aprovar Revisão */}
                                                <DropdownMenuItem 
                                                    onClick={() => handleAcaoSimples(medicamento, aprovarMutation)}
                                                    disabled={aprovarMutation.isPending}
                                                >
                                                    <CheckCircle className="mr-2 h-4 w-4" /> Aprovar Revisão
                                                </DropdownMenuItem>
                                                
                                                {/* AÇÃO: Rejeitar Revisão */}
                                                <DropdownMenuItem 
                                                    onClick={() => handleAcaoSimples(medicamento, rejeitarMutation)}
                                                    disabled={rejeitarMutation.isPending}
                                                >
                                                    <XCircle className="mr-2 h-4 w-4" /> Rejeitar Revisão
                                                </DropdownMenuItem>
                                            </>
                                        )}
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            </TableCell>
                        </TableRow>
                    ))}
                    {filteredMedicamentos.length === 0 && (
                        <TableRow>
                            <TableCell colSpan={5} className="h-24 text-center">
                                Nenhum medicamento encontrado.
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </CardContent>
      </Card>

      {/* 1. Modal de Cadastro (Existente) */}
      <Dialog open={isCadastroOpen} onOpenChange={setIsCadastroOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Cadastrar Medicamento</DialogTitle>
            <DialogDescription>Preencha os dados do novo medicamento para o catálogo.</DialogDescription>
          </DialogHeader>
          <MedicamentoForm onFinish={() => setIsCadastroOpen(false)} />
        </DialogContent>
      </Dialog>
      
      {/* 2. Modal de Solicitar Revisão (Existente) */}
      <Dialog open={isRevisaoModalOpen} onOpenChange={fecharModalRevisao}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Solicitar Revisão de Contraindicações</DialogTitle>
            <DialogDescription>Preencha a nova contraindicação para submeter à revisão.</DialogDescription>
          </DialogHeader>
          {medicamentoSelecionado && (
              <SolicitarRevisaoForm 
                  medicamentoId={medicamentoSelecionado.id} 
                  contraindicacoesAtuais={medicamentoSelecionado.contraindicacoes}
                  onFinish={fecharModalRevisao} 
              />
          )}
        </DialogContent>
      </Dialog>
      
      {/* 3. Modal de Edição de Uso Principal (NOVO MODAL DE EDIÇÃO) */}
      <Dialog open={isEdicaoUsoPrincipalOpen} onOpenChange={fecharModalEdicaoUsoPrincipal}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Editar Uso Principal</DialogTitle>
            <DialogDescription>Altere o uso principal do medicamento ID: {medicamentoSelecionado?.id}.</DialogDescription>
          </DialogHeader>
          {medicamentoSelecionado && (
              <UsoPrincipalEdicaoForm 
                  medicamentoId={medicamentoSelecionado.id} 
                  usoPrincipalAtual={medicamentoSelecionado.usoPrincipal}
                  onFinish={fecharModalEdicaoUsoPrincipal} 
              />
          )}
        </DialogContent>
      </Dialog>

    </MedicalLayout>
  );
}