// Localização: apresentacao-frontend/src/main/react/src/pages/Medicamentos.tsx

import React, { useState, useMemo } from 'react';
// MUDANÇA 1: Adicionar ícones 'Power' (Ativar) e 'ZapOff' (Inativar)
import { PlusCircle, Search, MoreVertical, Archive, CheckCircle, XCircle, RefreshCcw, AlertTriangle, Power, ZapOff } from "lucide-react"; 
import { 
    useListarMedicamentos, 
    useArquivarMedicamento, 
    useAprovarRevisao, 
    useRejeitarRevisao, 
    useSolicitarRevisao, 
    // MUDANÇA 2: Importar o novo hook
    useMudarStatus, 
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
import { UsoPrincipalEdicaoForm } from "../components/UsoPrincipalEdicaoForm"; 

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
  // MUDANÇA 3: Inicializar o novo hook
  const mudarStatusMutation = useMudarStatus(); 

  const [isCadastroOpen, setIsCadastroOpen] = useState(false);
  const [isRevisaoModalOpen, setIsRevisaoModalOpen] = useState(false);
  const [isEdicaoUsoPrincipalOpen, setIsEdicaoUsoPrincipalOpen] = useState(false);
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
  
  // MUDANÇA 4: NOVO HANDLER para mudar status (Ativo/Inativo)
  const handleMudarStatus = (medicamento: MedicamentoResumo, novoStatus: 'ATIVO' | 'INATIVO') => {
      // Usamos 'false' para temPrescricaoAtiva, pois a regra crítica é contra ARQUIVAMENTO
      const temPrescricaoAtiva = false; 
      
      const variables = {
          id: medicamento.id,
          payload: { responsavelId: RESPONSAVEL_ID },
          novoStatus: novoStatus,
          temPrescricaoAtiva: temPrescricaoAtiva
      };
      
      mudarStatusMutation.mutate(variables);
  }
  
  // Handlers específicos
  const handleAbrirSolicitarRevisao = (medicamento: MedicamentoResumo) => {
      setMedicamentoSelecionado(medicamento);
      setIsRevisaoModalOpen(true);
  };
  
  // FUNÇÃO QUE ABRE O NOVO MODAL DE EDIÇÃO
  const handleAbrirEdicaoUsoPrincipal = (medicamento: MedicamentoResumo) => {
      setMedicamentoSelecionado(medicamento);
      setIsEdicaoUsoPrincipalOpen(true);
  };

  // Funções de Fechamento de Modal
  const fecharModalRevisao = () => {
      setMedicamentoSelecionado(null);
      setIsRevisaoModalOpen(false);
  };

  const fecharModalEdicaoUsoPrincipal = () => {
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
                        {/* NOVA COLUNA: INDICADOR DE REVISÃO PENDENTE */}
                        <TableHead className="text-center">Revisão Pendente</TableHead> 
                        <TableHead className="text-right">Ações</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {filteredMedicamentos.map((medicamento) => (
                        // CORREÇÃO 1: O destaque visual agora usa 'possuiRevisaoPendente'
                        <TableRow key={medicamento.id} className={medicamento.possuiRevisaoPendente ? 'bg-yellow-50 hover:bg-yellow-100' : ''}>
                            
                            {/* CÉLULA: NOME */}
                            <TableCell className="font-medium">{medicamento.nome}</TableCell>

                            <TableCell>{medicamento.usoPrincipal}</TableCell>
                            <TableCell className="max-w-[200px] truncate">{medicamento.contraindicacoes}</TableCell> 
                            <TableCell>
                                {/* MUDANÇA 5: Atualizar a lógica de exibição do Badge para incluir INATIVO */}
                                <Badge 
                                    variant={
                                        medicamento.status === 'ATIVO' ? "default" : 
                                        (medicamento.status === 'INATIVO' ? 'outline' : // Status INATIVO
                                        (medicamento.status === 'REVISAO_PENDENTE' ? 'destructive' : 'secondary'))
                                    }
                                >
                                    {medicamento.status.replace('_', ' ')}
                                </Badge>
                            </TableCell>

                            {/* CORREÇÃO 2: LÓGICA DO INDICADOR DE REVISÃO PENDENTE */}
                            <TableCell className="text-center">
                                {
                                    medicamento.possuiRevisaoPendente ? (
                                        <div title="Revisão Pendente (Sim - Requer Aprovação)">
                                            {/* Usamos AlertTriangle e cor de alerta (orange/red) */}
                                            <AlertTriangle className="h-5 w-5 text-orange-500 mx-auto" /> 
                                        </div>
                                    ) : (
                                        <div title="Revisão Pendente (Não)">
                                            <XCircle className="h-5 w-5 text-green-500 mx-auto" />
                                        </div>
                                    )
                                }
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
                                        
                                        {/* AÇÃO: Editar Uso Principal */}
                                        <DropdownMenuItem 
                                            onClick={() => handleAbrirEdicaoUsoPrincipal(medicamento)}
                                            // CORREÇÃO 3: Desabilitar se houver revisão pendente ou se ARQUIVADO
                                            disabled={medicamento.possuiRevisaoPendente || medicamento.status === 'ARQUIVADO'} 
                                        >
                                            <RefreshCcw className="mr-2 h-4 w-4" /> Editar Uso Principal
                                        </DropdownMenuItem>
                                        
                                        {/* AÇÃO: Solicitar Revisão (AGORA DESABILITADA SE JÁ PENDENTE) */}
                                        <DropdownMenuItem 
                                            onClick={() => handleAbrirSolicitarRevisao(medicamento)}
                                            // CORREÇÃO 4: Desabilitar se já houver revisão pendente ou se ARQUIVADO
                                            disabled={medicamento.possuiRevisaoPendente || medicamento.status === 'ARQUIVADO'}
                                        >
                                            <CheckCircle className="mr-2 h-4 w-4" /> Solicitar Revisão
                                        </DropdownMenuItem>
                                        
                                        <DropdownMenuSeparator />
                                        
                                        {/* MUDANÇA 6: Ação Inativar (se ATIVO e sem revisão pendente) */}
                                        {medicamento.status === 'ATIVO' && !medicamento.possuiRevisaoPendente && (
                                            <DropdownMenuItem 
                                                onClick={() => handleMudarStatus(medicamento, 'INATIVO')}
                                                disabled={mudarStatusMutation.isPending}
                                                className="text-yellow-600"
                                            >
                                                <ZapOff className="mr-2 h-4 w-4" /> {mudarStatusMutation.isPending ? 'Inativando...' : 'Inativar'}
                                            </DropdownMenuItem>
                                        )}
                                        
                                        {/* MUDANÇA 7: Ação Reativar (se INATIVO e sem revisão pendente) */}
                                        {medicamento.status === 'INATIVO' && !medicamento.possuiRevisaoPendente && (
                                            <DropdownMenuItem 
                                                onClick={() => handleMudarStatus(medicamento, 'ATIVO')}
                                                disabled={mudarStatusMutation.isPending}
                                                className="text-green-600"
                                            >
                                                <Power className="mr-2 h-4 w-4" /> {mudarStatusMutation.isPending ? 'Reativando...' : 'Reativar'}
                                            </DropdownMenuItem>
                                        )}

                                        {/* MUDANÇA 8: Arquivar (permitir se for ATIVO ou INATIVO, mas não ARQUIVADO ou PENDENTE) */}
                                        {(medicamento.status === 'ATIVO' || medicamento.status === 'INATIVO') && !medicamento.possuiRevisaoPendente && (
                                            <DropdownMenuItem 
                                                onClick={() => handleAcaoSimples(medicamento, arquivarMutation)}
                                                disabled={arquivarMutation.isPending}
                                                className="text-red-600"
                                            >
                                                <Archive className="mr-2 h-4 w-4" /> {arquivarMutation.isPending ? 'Arquivando...' : 'Arquivar'}
                                            </DropdownMenuItem>
                                        )}
                                        
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            </TableCell>
                        </TableRow>
                    ))}
                    {filteredMedicamentos.length === 0 && (
                        <TableRow>
                            <TableCell colSpan={6} className="h-24 text-center">
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