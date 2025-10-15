Feature: Agendamento de exame
  Como funcionário da clínica
  Quero agendar um exame para um paciente
  Para organizar os atendimentos médicos e laboratoriais

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
