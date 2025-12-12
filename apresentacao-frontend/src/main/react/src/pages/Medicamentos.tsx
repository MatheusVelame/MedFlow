// Localização: apresentacao-frontend/src/main/react/src/pages/Medicamentos.tsx (CORRIGIDO)

import React, { useState, useMemo } from 'react';
import { PlusCircle, Search, MoreVertical, Archive, CheckCircle, XCircle, RefreshCcw } from "lucide-react";
import { useListarMedicamentos, useArquivarMedicamento, AcaoResponsavelPayload, MedicamentoResumo } from "../api/useMedicamentosApi";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "../components/ui/dialog";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "../components/ui/dropdown-menu";
import { Badge } from "../components/ui/badge";
import { toast } from 'sonner';

// Componente do Formulário de Cadastro
import { MedicamentoForm } from "../components/MedicamentoForm";

// Componente Layout (Presumido)
interface MedicalLayoutProps {
    title: string;
    breadcrumbs: string[];
    children: React.ReactNode;
}
const MedicalLayout = ({ children }: MedicalLayoutProps) => <div className="p-6">{children}</div>;


export default function Medicamentos() {
  const { data: medicamentos, isLoading, isError, error } = useListarMedicamentos();
  const arquivarMutation = useArquivarMedicamento();
  
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  // Simulação: ID do usuário logado (Gestor)
  const RESPONSAVEL_ID = 1; 

  // CORREÇÃO: Usando Array.isArray(medicamentos) para garantir que .filter() é chamado apenas em um array.
  const filteredMedicamentos = useMemo(() => {
    if (!Array.isArray(medicamentos)) return [];
    return medicamentos.filter((med) => 
        med.nome.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [medicamentos, searchTerm]);

  const handleArquivar = (medicamento: MedicamentoResumo) => {
      const payload: AcaoResponsavelPayload = { responsavelId: RESPONSAVEL_ID };
      
      arquivarMutation.mutate({ 
          id: medicamento.id, 
          payload: payload 
      }, {
          onSuccess: () => {
              // Handle success (via hook toast)
          },
          onError: (err) => {
              console.error("Erro ao arquivar:", err);
          }
      });
  };
  
  // Lógica de manipulação de erros e loading
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
            {/* O erro 404 será exibido aqui se a API estiver fora do ar ou o endpoint for incorreto */}
            <div className="text-center py-10 text-red-500">
                Erro ao carregar medicamentos. Verifique se o backend está rodando e o endpoint (/backend/medicamentos) está correto.
                <br/>Detalhes: {(error as Error)?.message}
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
        
        <Button onClick={() => setIsFormOpen(true)}>
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
                                        
                                        <DropdownMenuItem onClick={() => console.log(`Editar Uso Principal: ${medicamento.id}`)}>
                                            <RefreshCcw className="mr-2 h-4 w-4" /> Editar Uso Principal
                                        </DropdownMenuItem>
                                        <DropdownMenuItem onClick={() => console.log(`Solicitar Revisão: ${medicamento.id}`)}>
                                            <CheckCircle className="mr-2 h-4 w-4" /> Solicitar Revisão
                                        </DropdownMenuItem>
                                        
                                        <DropdownMenuSeparator />
                                        
                                        {medicamento.status === 'ATIVO' && (
                                            <DropdownMenuItem 
                                                onClick={() => handleArquivar(medicamento)}
                                                disabled={arquivarMutation.isPending}
                                                className="text-red-600"
                                            >
                                                <Archive className="mr-2 h-4 w-4" /> {arquivarMutation.isPending ? 'Arquivando...' : 'Arquivar'}
                                            </DropdownMenuItem>
                                        )}
                                        
                                        {medicamento.status === 'REVISAO_PENDENTE' && (
                                            <>
                                                <DropdownMenuItem onClick={() => console.log(`Aprovar Revisão: ${medicamento.id}`)}>
                                                    <CheckCircle className="mr-2 h-4 w-4" /> Aprovar Revisão
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => console.log(`Rejeitar Revisão: ${medicamento.id}`)}>
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

      {/* Formulário de Cadastro em Dialog */}
      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Cadastrar Medicamento</DialogTitle>
            <DialogDescription>
              Preencha os dados do novo medicamento para o catálogo.
            </DialogDescription>
          </DialogHeader>
          <MedicamentoForm onFinish={() => setIsFormOpen(false)} />
        </DialogContent>
      </Dialog>
    </MedicalLayout>
  );
}