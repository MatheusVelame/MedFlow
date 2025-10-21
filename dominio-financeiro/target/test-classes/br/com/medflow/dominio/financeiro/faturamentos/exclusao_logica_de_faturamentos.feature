Feature: Exclusão lógica de faturamentos inválidos

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(ExcluirFaturamento)
Scenario: Administrador exclui logicamente um faturamento pendente
Given existe um faturamento com ID "FAT-1023" e status "Pendente"
And o usuário "Ana Lima" possui papel "Administrador Financeiro"
And o motivo informado é "Duplicidade de lançamento"
When a usuária solicitar a exclusão do faturamento "FAT-1023" em "21/09/2025 10:32"
Then o sistema deve marcar o faturamento "FAT-1023" como "removido" (exclusão lógica)
And deve registrar no log: ID "FAT-1023", status atual "Pendente", usuário "Ana Lima", data/hora "21/09/2025 10:32", ação "Exclusão Lógica", motivo "Duplicidade de lançamento"
And o registro não deve aparecer nas listagens operacionais padrão

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(ExcluirFaturamento)
Scenario: Usuário sem permissão tenta excluir faturamento
Given existe um faturamento com ID "FAT-3001" e status "Pendente"
And o usuário "Beatriz Melo" possui papel "Atendente" (sem permissão administrativa)
And o motivo informado é "Erro de digitação"
When a usuária tentar excluir o faturamento "FAT-3001" em "21/09/2025 09:47"
Then o sistema deve negar a operação informando "Apenas administradores podem excluir faturamentos"
And o faturamento deve permanecer inalterado
And deve ser registrado log de segurança da tentativa negada com ID, usuário, data/hora, ação tentada e motivo

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(ExcluirFaturamento)
Scenario: Administrador exclui logicamente um faturamento marcado como Inválido
Given existe um faturamento com ID "FAT-2045" e status "Inválido"
And o usuário "Carlos Souza" possui papel "Administrador Financeiro"
And o motivo informado é "Erro de identificação do paciente"
When o usuário excluir o faturamento "FAT-2045" em "21/09/2025 11:05"
Then o sistema deve marcar o faturamento "FAT-2045" como "removido" (exclusão lógica)
And deve registrar no log os campos: ID, status "Inválido", usuário "Carlos Souza", data/hora "21/09/2025 11:05", ação "Exclusão Lógica", motivo "Erro de identificação do paciente"

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(ExcluirFaturamento)
Scenario: Bloqueio de exclusão para faturamento com status não permitido
Given existe um faturamento com ID "FAT-4500" e status "Pago"
And o usuário "Ana Lima" possui papel "Administrador Financeiro"
And o motivo informado é "Ajuste operacional"
When a usuária tentar excluir o faturamento "FAT-4500" em "21/09/2025 08:20"
Then o sistema deve impedir a exclusão informando "Apenas Pendente ou Inválido podem ser excluídos"
And o faturamento deve permanecer inalterado
And deve ser registrado log de tentativa negada com ID, status atual "Pago", usuário, data/hora, ação tentada e motivo