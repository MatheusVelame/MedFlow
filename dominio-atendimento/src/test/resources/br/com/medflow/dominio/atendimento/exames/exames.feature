Feature: Agendamento de exame

  # RN1 — O agendamento só pode ser realizado se o paciente e o médico já estiverem cadastrados no sistema
  Scenario: Agendamento com paciente e médico cadastrados
    Given que o paciente "Lucas" e o médico "Dr. Ana" estão cadastrados no sistema
    When o funcionário solicitar o agendamento de exame de "Raio-X"
    Then o sistema deve criar o agendamento com sucesso

  Scenario: Agendamento com paciente não cadastrado
    Given que o paciente "Paulo" não está cadastrado no sistema
    And o médico "Dr. Ana" está cadastrado
    When o funcionário tentar agendar o exame de "Raio-X"
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Paciente não cadastrado no sistema"

  # RN2 — O tipo de exame deve estar previamente cadastrado no sistema
  Scenario: Agendamento com tipo de exame cadastrado
    Given que o exame "Ultrassonografia" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Lucas"
    Then o sistema deve criar o agendamento com sucesso

  Scenario: Agendamento com tipo de exame não cadastrado
    Given que o exame "Eletroencefalograma" não está cadastrado no sistema
    When o funcionário tentar agendar esse exame para o paciente "Lucas"
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Tipo de exame não cadastrado no sistema"

  # RN3 — A data e o horário do exame são obrigatórios
  Scenario: Agendamento com data e horário informados
    Given que o paciente "Lucas" e o médico "Dr. Ana" estão cadastrados
    And o exame "Raio-X" está cadastrado
    When o funcionário agendar o exame para o dia "10/10/2025" às "09h"
    Then o sistema deve criar o agendamento com status "Agendado"

  Scenario: Agendamento sem data ou horário
    Given que o paciente "Lucas" e o médico "Dr. Ana" estão cadastrados
    And o exame "Raio-X" está cadastrado
    When o funcionário tentar agendar o exame sem informar data ou horário
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Data e horário são obrigatórios"

  # RN4 — Não é permitido agendar dois exames para o mesmo paciente no mesmo horário
  Scenario: Agendamento sem conflito de horário
    Given que o paciente "Lucas" não possui outro exame agendado às "09h" do dia "10/10/2025"
    When o funcionário agendar o exame nesse horário
    Then o sistema deve criar o agendamento com sucesso

  Scenario: Agendamento em horário já ocupado pelo paciente
    Given que o paciente "Lucas" já possui um exame agendado às "09h" do dia "10/10/2025"
    When o funcionário tentar agendar outro exame no mesmo horário
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Paciente já possui exame agendado neste horário"

  # RN5 — O médico vinculado ao exame deve estar ativo no sistema
  Scenario: Médico ativo
    Given que o médico "Dr. Ana" está ativo no sistema
    When o funcionário agendar um exame para "Lucas" com esse médico
    Then o sistema deve criar o agendamento com sucesso

  Scenario: Médico inativo
    Given que o médico "Dr. Ana" está inativo no sistema
    When o funcionário tentar agendar o exame com esse médico
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Médico inativo não pode ser vinculado ao exame"

  # RN6 — Não é permitido agendar exame em horário de indisponibilidade do médico
  Scenario: Agendamento em horário disponível do médico
    Given que o médico "Dr. Ana" está disponível às "10h" do dia "12/10/2025"
    When o funcionário agendar o exame para "Lucas" nesse horário
    Then o sistema deve criar o agendamento com sucesso

  Scenario: Agendamento em horário de indisponibilidade do médico
    Given que o médico "Dr. Ana" não está disponível às "10h" do dia "12/10/2025"
    When o funcionário tentar agendar o exame nesse horário
    Then o sistema deve rejeitar o agendamento
    And exibir a mensagem "Médico indisponível neste horário"

  # RN7 — O exame deve receber um status inicial “Agendado”
  Scenario: Verificação do status inicial
    Given que o paciente "Lucas" e o médico "Dr. Ana" estão cadastrados
    And o exame "Raio-X" está cadastrado
    When o funcionário agendar o exame
    Then o sistema deve criar o agendamento com status "Agendado"

  Scenario: Status inicial incorreto
    Given que o paciente "Lucas" e o médico "Dr. Ana" estão cadastrados
    And o exame "Raio-X" está cadastrado
    When o funcionário agendar o exame
    And o sistema criar o agendamento com status "Pendente"
    Then a operação deve ser considerada inválida
    And exibir a mensagem "O status inicial do exame deve ser ‘Agendado’"

# Feature: Atualização de agendamento de exame

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

# Feature: Exclusão ou cancelamento de exame agendado

  # RN1 — Não é permitido excluir exames já realizados ou em andamento (somente os com status "Agendado")
  Scenario: Exclusão de exame com status “Agendado”
    Given existe um exame com status "Agendado" para o paciente "Marina"
    When o funcionário solicitar a exclusão desse exame
    Then o sistema deve excluir o exame com sucesso
    And registrar a operação no histórico de alterações

  Scenario: Tentativa de excluir exame já realizado
    Given existe um exame com status "Realizado" para o paciente "Marina"
    When o funcionário solicitar a exclusão desse exame
    Then o sistema deve rejeitar a exclusão
    And exibir a mensagem "Exames realizados ou em andamento não podem ser excluídos"

  # RN2 — Caso o exame já esteja vinculado a um laudo, não pode ser excluído; deve ser apenas marcado como “Cancelado”
  Scenario: Exame vinculado a laudo é cancelado
    Given existe um exame vinculado a um laudo para o paciente "Carlos"
    When o funcionário tentar excluir o exame
    Then o sistema deve alterar o status do exame para "Cancelado"
    And registrar o motivo e a data do cancelamento

  Scenario: Tentativa de exclusão física de exame com laudo
    Given existe um exame vinculado a um laudo para o paciente "Carlos"
    When o funcionário solicitar a exclusão permanente
    Then o sistema deve impedir a exclusão
    And exibir a mensagem "Exames vinculados a laudos não podem ser excluídos, apenas cancelados"

  # RN3 — A exclusão só é permitida se o exame ainda não estiver associado a nenhum registro clínico no prontuário do paciente
  Scenario: Exclusão de exame sem vínculo com prontuário
    Given o exame de "Ultrassonografia" do paciente "Beatriz" não possui registros no prontuário
    When o funcionário solicitar a exclusão do exame
    Then o sistema deve excluir o exame com sucesso
    And registrar a exclusão no histórico de alterações

  Scenario: Tentativa de exclusão com registro clínico associado
    Given o exame de "Ultrassonografia" do paciente "Beatriz" está vinculado a registros no prontuário
    When o funcionário solicitar a exclusão
    Then o sistema deve rejeitar a operação
    And exibir a mensagem "Não é permitido excluir exames associados a registros clínicos do paciente"

  # RN4 — O cancelamento de exame deve registrar a data e o motivo do cancelamento
  Scenario: Cancelamento com registro de data e motivo
    Given existe um exame agendado para o paciente "Renato"
    When o funcionário cancelar o exame informando a data "06/10/2025" e o motivo "Paciente ausente"
    Then o sistema deve registrar o cancelamento com o status "Cancelado"
    And armazenar a data e o motivo informados
    And registrar a operação no histórico de alterações

  Scenario: Cancelamento sem motivo informado
    Given existe um exame agendado para o paciente "Renato"
    When o funcionário cancelar o exame sem informar o motivo
    Then o sistema deve rejeitar o cancelamento
    And exibir a mensagem "É obrigatório informar o motivo do cancelamento"