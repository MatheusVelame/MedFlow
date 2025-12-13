Feature: Gerenciamento da Folha de Pagamento
  Como Administrador Financeiro
  Desejo registrar e gerenciar as folhas de pagamento dos funcionários
  Para garantir a conformidade e o controle dos custos de pessoal

# ====================================================================
# 1. Registro de Folha de Pagamento
# ====================================================================

# RN1 – É obrigatório vincular a folha de pagamento a um funcionário ativo

  Scenario: RN1 - Registro - Sucesso - Registro de folha para funcionário ativo
    Given que a funcionária "Cláudia Nogueira", com ID "123", possui o status "Ativo" no sistema
    And não existe folha de pagamento registrada para ela no período de referência "09/2025"
    And que o administrador está na tela de criação de uma nova folha de pagamento para o funcionário "Cláudia Nogueira", ID "123"
    And informa Tipo de Registro "PAGAMENTO"
    And informa Salário base 2800.00
    And informa Benefícios 550.00
    And informa Método de Pagamento "Transferência Bancária"
    When o administrador registra uma nova folha de pagamento para ela com todos os dados necessários
    Then o sistema deve criar o registro da folha de pagamento com sucesso
    And o status do novo registro deve ser "Pendente"


  Scenario: RN1 - Registro - Insucesso - Bloqueio ao tentar registrar folha para funcionário inativo
    Given que o ex-funcionário "Roberto Dias", com ID "234", possui o status "Inativo" no sistema
    When o administrador tenta registrar uma nova folha de pagamento para "Roberto Dias"
    Then o sistema deve bloquear a operação
    And exibir a mensagem de erro "Não é permitido registrar folha de pagamento para funcionários inativos."

# ====================================================================
# Atualização de Folha de Pagamento
# ====================================================================

  Scenario: RN1 - Atualização - Sucesso - Atualização de valores em um registro Pendente
    Given que existe um registro de folha de pagamento para a funcionária "Camila Dias" para o período "09/2025"
    When o administrador edita este registro e altera o valor dos Benefícios para 450.00
    Then o sistema deve salvar a alteração com sucesso
    And o novo valor dos Benefícios deve ser 450.00

  Scenario: RN1 - Atualização - Insucesso - Tentativa de alterar o Período de Referência de um registro existente
    Given que existe um registro de folha de pagamento para a funcionária "Larissa Bastos" para o período "09/2025"
    When o administrador tenta alterar o "periodoReferencia" de "09/2025" para "10/2025"
    Then o sistema deve bloquear a ação
    And exibir uma mensagem informativa "Para corrigir o funcionário ou o período, cancele este registro e crie um novo."

  Scenario: RN2 - Atualização - Sucesso - Alteração de status de Pendente para Pago
    Given que existe o registro "PENDENTE" para "Bruno Alves" para o período "09/2025"
    When o administrador confirma a quitação e altera o status do registro para "Pago"
    Then o sistema deve atualizar o status para "PAGO" com sucesso
    And todos os campos do registro devem se tornar não editáveis

  Scenario: RN2 - Atualização - Insucesso - Tentativa de reverter um status Cancelado para Pendente
    Given que a folha de pagamento da funcionária "Vanessa Moraes" para "09/2025" já possui o status "CANCELADO"
    When um usuário tenta reverter o status de "CANCELADO" para "PENDENTE"
    Then o sistema deve bloquear a alteração
    And exibir a mensagem de erro "Status 'Cancelado' não pode ser revertido."

  Scenario: RN3 - Atualização - Sucesso - Alteração de Registro "Pendente"
    Given que o registro de pagamento do funcionário "Felipe Costa" para o período "09/2025" tem o status "PENDENTE"
    When o administrador edita este registro e altera o valor do Salário Base para 8500.00
    Then o sistema deve salvar a alteração com sucesso
    And o novo valor do Salário Base deve ser 8500.00

  Scenario: RN3 - Atualização - Insucesso - Imutabilidade de um registro Pago
    Given que o registro de pagamento do funcionário "Felipe Costa" para o período "09/2025" tem o status "PAGO"
    When o administrador abre este registro na tela de edição
    Then todos os campos devem estar desabilitados impedindo qualquer alteração

# ====================================================================
# Consulta de Folha de Pagamento
# ====================================================================

  Scenario: RN1 - Consulta - Sucesso - Busca por ID de funcionário
    Given que o histórico de pagamentos contém registro de "André Gomes" com ID 111
    And que o histórico de pagamentos contém registro de "Sofia Lima" com ID 222
    When o gestor digita o ID 222 no campo de busca
    Then a lista de resultados deve exibir apenas o registro de "Sofia Lima"

  Scenario: RN1 - Consulta - Insucesso - Busca por funcionário não encontrado
    Given que o histórico de pagamentos contém registro de "Júlia Nogueira"
    And não existe nenhum registro para a funcionária "Mariana Costa"
    When o gestor busca por "Mariana Costa"
    Then o sistema deve exibir uma lista vazia
    And exibir a mensagem "Nenhum registro encontrado para a sua busca."

  Scenario: RN2 - Consulta - Sucesso - Filtro por Período de Referência
    Given que o histórico de pagamentos contém registro de "Rafael Borges" para período "08/2025"
    And que o histórico de pagamentos contém registro de "Clara Macedo" para período "09/2025"
    When o gestor aplica o filtro de Período "08/2025"
    Then a lista de resultados deve exibir apenas o registro de "Rafael Borges"

  Scenario: RN2 - Consulta - Sucesso - Filtro por Status do Pagamento
    Given que o histórico de pagamentos contém registro de "Rafael Borges" com status "PAGO"
    And que o histórico de pagamentos contém registro de "Clara Macedo" com status "PENDENTE"
    When o gestor aplica o filtro de Status "PENDENTE"
    Then a lista de resultados deve exibir apenas o registro de "Clara Macedo"

# ====================================================================
# Remoção de Folha de Pagamento
# ====================================================================

  Scenario: RN1 - Remoção - Sucesso - Remoção de registro com status "Pendente"
    Given que existe um registro de folha de pagamento para a funcionária "Fabiana Lima" criado por engano
    And o registro possui Status "PENDENTE"
    When o administrador seleciona este registro para remoção e informa o motivo "Lançamento duplicado"
    Then o sistema deve remover permanentemente o registro da base de dados
    And exibir a mensagem "Registro de pagamento removido com sucesso"
    And registrar a ação de exclusão na trilha de auditoria, incluindo o usuário responsável e o motivo

  Scenario: RN1 - Remoção - Insucesso - Tentativa de remover um registro com status "Pago"
    Given que existe um registro de folha de pagamento para o funcionário "Caio Ribeiro"
    And o registro possui Status "PAGO"
    When o administrador tenta remover este registro
    Then o sistema deve bloquear a ação
    And exibir a mensagem de erro "Não é permitido remover registros com status 'Pago', pois fazem parte do histórico financeiro."