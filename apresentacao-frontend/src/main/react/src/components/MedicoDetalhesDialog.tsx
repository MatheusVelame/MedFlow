import React from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import {
  User,
  Phone,
  Briefcase,
  FileText,
  Calendar,
  Clock,
  Activity,
} from 'lucide-react';

interface MedicoDetalhesDialogProps {
  medico: any;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function MedicoDetalhesDialog({
  medico,
  open,
  onOpenChange,
}: MedicoDetalhesDialogProps) {
  if (!medico) return null;

  const statusColor = {
    ATIVO: 'bg-green-100 text-green-800',
    INATIVO: 'bg-red-100 text-red-800',
    AFASTADO: 'bg-yellow-100 text-yellow-800',
  }[medico.status] || 'bg-gray-100 text-gray-800';

  // Função para formatar data/hora com segurança
  const formatarDataHora = (dataHora: string) => {
    if (!dataHora) return 'N/A';
    try {
      const date = new Date(dataHora);
      return date.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch (e) {
      return 'Data inválida';
    }
  };

  // Agrupar horários por dia da semana com segurança
  const horariosAgrupados = medico.horariosDisponiveis?.reduce((acc: any, horario: any) => {
    if (!acc[horario.diaSemana]) {
      acc[horario.diaSemana] = [];
    }
    acc[horario.diaSemana].push(horario);
    return acc;
  }, {}) || {};

  const diasOrdenados = [
    'SEGUNDA',
    'TERCA',
    'QUARTA',
    'QUINTA',
    'SEXTA',
    'SABADO',
    'DOMINGO',
  ];

  const diaNomes: Record<string, string> = {
    SEGUNDA: 'Segunda-feira',
    TERCA: 'Terça-feira',
    QUARTA: 'Quarta-feira',
    QUINTA: 'Quinta-feira',
    SEXTA: 'Sexta-feira',
    SABADO: 'Sábado',
    DOMINGO: 'Domingo',
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-2xl">
            <User className="h-6 w-6" />
            Detalhes do Médico
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Informações Básicas */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Briefcase className="h-5 w-5" />
                Informações Básicas
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">Nome</p>
                  <p className="font-medium">{medico.nome || 'N/A'}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Função</p>
                  <p className="font-medium">{medico.funcao || 'N/A'}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">CRM</p>
                  <p className="font-medium">{medico.crm || 'N/A'}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Especialidade</p>
                  <p className="font-medium">{medico.especialidade || 'N/A'}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Contato</p>
                  <p className="font-medium flex items-center gap-1">
                    <Phone className="h-4 w-4" />
                    {medico.contato || 'N/A'}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Status</p>
                  <Badge className={statusColor}>{medico.status || 'N/A'}</Badge>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Horários de Disponibilidade */}
          {medico.horariosDisponiveis && medico.horariosDisponiveis.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Clock className="h-5 w-5" />
                  Horários de Disponibilidade
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {diasOrdenados.map((dia) => {
                    const horariosDoDia = horariosAgrupados[dia];
                    if (!horariosDoDia) return null;

                    return (
                      <div key={dia} className="p-3 bg-slate-50 rounded-md">
                        <p className="font-medium text-sm mb-2">{diaNomes[dia]}</p>
                        <div className="space-y-1">
                          {horariosDoDia.map((horario: any, idx: number) => (
                            <p key={idx} className="text-sm text-muted-foreground flex items-center gap-1">
                              <Clock className="h-3 w-3" />
                              {horario.horaInicio} - {horario.horaFim}
                            </p>
                          ))}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Registro Administrativo */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileText className="h-5 w-5" />
                Registro Administrativo
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Eventos Cadastrais */}
              <div>
                <h4 className="font-semibold text-sm mb-3 text-slate-700">
                  Eventos Cadastrais
                </h4>
                <div className="space-y-2">
                  {medico.historico && medico.historico.length > 0 ? (
                    medico.historico.map((evento: any, index: number) => (
                      <div
                        key={index}
                        className="p-3 bg-slate-50 rounded-md border border-slate-200"
                      >
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <p className="font-medium text-sm">{evento.descricao || 'Sem descrição'}</p>
                            <p className="text-xs text-muted-foreground mt-1">
                              Responsável: {evento.responsavelId || 'N/A'}
                            </p>
                          </div>
                          <p className="text-xs text-muted-foreground">
                            {formatarDataHora(evento.dataHora)}
                          </p>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p className="text-sm text-muted-foreground italic">
                      Nenhum evento cadastral registrado
                    </p>
                  )}
                </div>
              </div>

              <Separator />

              {/* Histórico Assistencial */}
              <div>
                <h4 className="font-semibold text-sm mb-3 text-slate-700">
                  Histórico Assistencial
                </h4>
                <div className="space-y-2">
                  {/* Consultas */}
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                    <span className="text-sm">Consultas</span>
                    <Badge
                      variant="outline"
                      className={
                        medico.temConsultas === true
                          ? 'bg-blue-50 text-blue-700 border-blue-200'
                          : 'bg-slate-50 text-slate-600 border-slate-200'
                      }
                    >
                      {medico.temConsultas === true ? 'Registros existentes' : 'Sem registros'}
                    </Badge>
                  </div>

                  {/* Exames */}
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                    <span className="text-sm">Exames</span>
                    <Badge
                      variant="outline"
                      className={
                        medico.temExames === true
                          ? 'bg-green-50 text-green-700 border-green-200'
                          : 'bg-slate-50 text-slate-600 border-slate-200'
                      }
                    >
                      {medico.temExames === true ? 'Registros existentes' : 'Sem registros'}
                    </Badge>
                  </div>

                  {/* Prontuários */}
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                    <span className="text-sm">Prontuários</span>
                    <Badge
                      variant="outline"
                      className={
                        medico.temProntuarios === true
                          ? 'bg-purple-50 text-purple-700 border-purple-200'
                          : 'bg-slate-50 text-slate-600 border-slate-200'
                      }
                    >
                      {medico.temProntuarios === true ? 'Registros existentes' : 'Sem registros'}
                    </Badge>
                  </div>
                </div>

                <p className="text-xs text-muted-foreground mt-3 italic">
                  * Informações consolidadas da base assistencial do sistema
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </DialogContent>
    </Dialog>
  );
}
