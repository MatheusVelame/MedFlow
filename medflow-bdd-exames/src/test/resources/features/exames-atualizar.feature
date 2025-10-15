Feature: Atualização de agendamento de exame
  Como funcionário da clínica
  Quero atualizar um agendamento de exame
  Para corrigir ou remarcar informações de forma segura e controlada

  # RN1 — Só podem ser alterados a data, o horário, o tipo de exame e o médico responsável (desde que o novo médico esteja ativo)
  Scenario: Atualização válida dos campos permitidos
    Given existe um agendamento de exame de "Raio-X" com o médico "Dr. Paulo" para o dia "10/10/2025" às "10h"
    And "Dr. Carlos" é um médico ativo
    When o funcionário atualizar o agendamento alterando o tipo de exame para "Ultrassonografia" e o médico para "Dr. Carlos"
    Then o sistema deve registrar a atualização com sucesso

  Scenario: Tentativa de alterar campo não permitido
    Given existe um agendamento de exame de "Raio-X" com o paciente "Maria"
    When o funcionário tentar alterar o nome do paciente para "João"
    Then o sistema deve rejeitar a atualização
    And exibir a mensagem "O paciente vinculado não pode ser alterado"

  # RN2 — O paciente vinculado não pode ser alterado após a criação do agendamento
  Scenario: Tentativa de alterar paciente vinculada é bloqueada
    Given existe um agendamento de exame para o paciente "Carla"
    When o funcionário tentar mudar o paciente para "Lucas"
    Then o sistema deve impedir a alteração
    And exibir a mensagem "Não é permitido alterar o paciente de um agendamento existente"

  Scenario: Atualização de outros campos sem mudar o paciente
    Given existe um agendamento de exame para o paciente "Carla" com o médico "Dr. Paulo"
    When o funcionário alterar apenas o horário para "14h"
    Then o sistema deve salvar a atualização corretamente
    And manter o paciente "Carla" vinculado

  # RN3 — A alteração só será válida se não gerar conflito de horário para o paciente ou para o médico
  Scenario: Atualização sem conflito de horário
    Given o paciente "Marcos" e o médico "Dr. Paulo" não possuem outros agendamentos no dia "12/10/2025" às "09h"
    When o funcionário alterar o horário do exame para "09h"
    Then o sistema deve confirmar a atualização com sucesso

  Scenario: Atualização com conflito de horário para o médico
    Given o médico "Dr. Paulo" já possui outro exame agendado no dia "12/10/2025" às "09h"
    When o funcionário tentar remarcar o exame para esse mesmo horário
    Then o sistema deve rejeitar a atualização
    And exibir a mensagem "Conflito de horário detectado para o médico ou paciente"

  # RN4 — O histórico de alterações de data/hora do exame deve ser registrado no sistema
  Scenario: Registro automático de alteração de data/hora
    Given existe um agendamento de exame para o dia "10/10/2025" às "10h"
    When o funcionário alterar o horário para "14h"
    Then o sistema deve salvar a alteração
    And registrar no "Histórico de Alterações" a data antiga "10/10/2025 às 10h" e a nova "10/10/2025 às 14h"

  Scenario: Alteração sem registro no histórico (falha)
    Given existe um agendamento de exame para o dia "10/10/2025" às "10h"
    When o funcionário alterar o horário para "14h"
    And o sistema não registrar a mudança no "Histórico de Alterações"
    Then a atualização deve ser considerada inválida
    And o sistema deve exibir "Falha ao registrar histórico de alterações"
