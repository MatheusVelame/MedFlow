Feature: Alteração de status de cobranças registradas

Como gerente,
Quero alterar o status de cobranças já registradas,
Para atualizar a situação dos pagamentos.

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(AlterarStatus)
Scenario: Alterar status de um faturamento existente para Cancelado (sucesso)
Given existe um faturamento com ID "FAT-3101" e status atual "Pendente"
And o usuário "Ana Lima" possui papel "Administrador Financeiro"
And o novo status solicitado é "Cancelado" com motivo "Erro de lançamento"
When a usuária alterar o status do faturamento "FAT-3101" em "21/09/2025 10:15"
Then o sistema deve atualizar o status para "Cancelado"
And deve registrar no log: ID "FAT-3101", status anterior "Pendente", novo status "Cancelado", usuário "Ana Lima", data/hora "21/09/2025 10:15", motivo "Erro de lançamento"
And deve disparar a notificação interna de ajuste contábil

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(AlterarStatus)
Scenario: Impedir alteração quando o faturamento não existe (falha)
Given não existe faturamento com ID "FAT-9999"
And o usuário "Ana Lima" possui papel "Administrador Financeiro"
And o novo status solicitado é "Cancelado" com motivo "ID incorreto"
When a usuária tentar alterar o status do faturamento "FAT-9999" em "21/09/2025 10:20"
Then o sistema deve rejeitar a operação informando "faturamento não encontrado"
And nenhum status deve ser alterado
And deve registrar log de tentativa negada com ID "FAT-9999", usuário "Ana Lima", data/hora "21/09/2025 10:20", ação "AlterarStatus", motivo "ID incorreto"

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(AlterarStatus)
Scenario: Alterar status por usuário com permissão administrativa (sucesso)
Given existe um faturamento com ID "FAT-3199" e status atual "Pendente"
And o usuário "Carlos Souza" possui papel "Administrador do Sistema"
And o novo status solicitado é "Pago" com motivo "Compensação bancária confirmada"
When o usuário alterar o status do faturamento "FAT-3199" em "21/09/2025 10:25"
Then o sistema deve atualizar o status para "Pago"
And deve registrar no log: ID "FAT-3199", status anterior "Pendente", novo status "Pago", usuário "Carlos Souza", data/hora "21/09/2025 10:25", motivo "Compensação bancária confirmada"
And deve disparar notificação interna de contabilização

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(AlterarStatus)
Scenario: Bloquear alteração por usuário sem permissão administrativa (falha)
Given existe um faturamento com ID "FAT-3200" e status atual "Pendente"
And o usuário "Beatriz Melo" possui papel "Atendente"
And o novo status solicitado é "Pago"
When a usuária tentar alterar o status do faturamento "FAT-3200" em "21/09/2025 10:30"
Then o sistema deve negar a operação informando "permissão administrativa necessária"
And o status deve permanecer "Pendente"
And deve registrar log de tentativa negada com ID "FAT-3200", usuário "Beatriz Melo", data/hora "21/09/2025 10:30", ação "AlterarStatus", motivo "Permissão insuficiente"

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(AlterarStatus)
Scenario: Reverter status Pago com permissão especial (sucesso)
Given existe um faturamento com ID "FAT-3220" e status atual "Pago"
And o usuário "Mariana Reis" possui papel "Administrador Financeiro" e possui "PermissãoEspecialReversao"
And o novo status solicitado é "Pendente" com motivo "Chargeback confirmado"
When a usuária alterar o status do faturamento "FAT-3220" em "21/09/2025 10:40"
Then o sistema deve atualizar o status para "Pendente"
And deve registrar no log: ID "FAT-3220", status anterior "Pago", novo status "Pendente", usuário "Mariana Reis", data/hora "21/09/2025 10:40", motivo "Chargeback confirmado"
And deve disparar notificação interna de ajuste contábil
