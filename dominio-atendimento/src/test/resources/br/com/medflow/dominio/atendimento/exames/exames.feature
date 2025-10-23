Feature: Gerenciamento de Agendamento de Exames

# -------------------------------------------------------------
# CASO DE USO: AGENDAR NOVO EXAME
# -------------------------------------------------------------

  # RN1 — O agendamento de exame só pode ser realizado se o paciente e o médico já estiverem previamente cadastrados no sistema.
  Scenario: [RN 1.1 - Sucesso] Agendamento com paciente e médico cadastrados
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "20/12/2025" às "10h"
    Then o agendamento do exame deve ser criado com sucesso

  Scenario: [RN 1.2 - Falha] Tentativa de agendamento com paciente não cadastrado
    Given que o paciente "Carlos" não está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Carlos" com o médico "Dr. Ana" na data "20/12/2025" às "11h"
    Then o sistema deve exibir a mensagem de erro "Paciente não cadastrado no sistema."

  Scenario: [RN 1.3 - Falha] Tentativa de agendamento com médico não cadastrado
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Jonas" não está cadastrado no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Jonas" na data "20/12/2025" às "12h"
    Then o sistema deve exibir a mensagem de erro "Médico não cadastrado no sistema."

  # RN2 — O tipo de exame deve estar previamente cadastrado no sistema.
  Scenario: [RN 2.1 - Sucesso] Agendamento com tipo de exame cadastrado
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Ultrassonografia" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Lucas" com o médico "Dr. Ana" na data "21/12/2025" às "09h"
    Then o agendamento do exame deve ser criado com sucesso

  Scenario: [RN 2.2 - Falha] Tentativa de agendamento com tipo de exame não cadastrado
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Densitometria Óssea" não está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Densitometria Óssea" para o paciente "Lucas" com o médico "Dr. Ana" na data "21/12/2025" às "10h"
    Then o sistema deve exibir a mensagem de erro "Tipo de exame não cadastrado no sistema."

  # RN3 — A data e o horário do exame são obrigatórios.
  Scenario: [RN 3.1 - Sucesso] Agendamento com data e hora preenchidas
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "22/12/2025" às "10h"
    Then o agendamento do exame deve ser criado com sucesso
  
  Scenario: [RN 3.2 - Falha] Tentativa de agendamento sem data e hora
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" sem data e hora
    Then o sistema deve exibir a mensagem de erro "Data e horário do exame são obrigatórios."

  # RN4 — Não é permitido agendar dois exames para o mesmo paciente no mesmo horário.
  Scenario: [RN 4.1 - Sucesso] Agendamento para paciente com horário livre
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    And não existe um exame agendado para o paciente "Lucas" na data "22/12/2025" às "15h"
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "22/12/2025" às "15h"
    Then o agendamento do exame deve ser criado com sucesso

  Scenario: [RN 4.2 - Falha] Tentativa de agendar exame em horário já ocupado pelo paciente
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    And existe um exame agendado para o paciente "Lucas" na data "22/12/2025" às "14h"
    When o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Lucas" com o médico "Dr. Carlos" na data "22/12/2025" às "14h"
    Then o sistema deve exibir a mensagem de erro "Paciente já possui um exame agendado neste horário."

  # RN5 — O médico vinculado ao exame deve estar ativo no sistema.
  Scenario: [RN 5.1 - Sucesso] Agendamento com médico ativo
    Given que o paciente "Maria" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Ultrassonografia" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Maria" com o médico "Dr. Ana" na data "23/12/2025" às "10h"
    Then o agendamento do exame deve ser criado com sucesso

  Scenario: [RN 5.2 - Falha] Tentativa de agendamento com médico inativo
    Given que o paciente "Maria" está cadastrado no sistema
    And que o médico "Dr. Paulo" está cadastrado mas inativo no sistema
    And que o tipo de exame "Ultrassonografia" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Maria" com o médico "Dr. Paulo" na data "23/12/2025" às "09h"
    Then o sistema deve exibir a mensagem de erro "Médico vinculado ao exame deve estar ativo no sistema."

  # RN6 — Não é permitido agendar exame em horário de indisponibilidade do médico.
  Scenario: [RN 6.1 - Sucesso] Agendamento em horário disponível do médico
    Given que o paciente "Beatriz" está cadastrado no sistema
    And que o médico "Dr. Carlos" está cadastrado e ativo no sistema
    And que o tipo de exame "Sangue" está cadastrado no sistema
    And que o médico "Dr. Carlos" está disponível na data "24/12/2025" às "16h"
    When o funcionário agendar um exame do tipo "Sangue" para o paciente "Beatriz" com o médico "Dr. Carlos" na data "24/12/2025" às "16h"
    Then o agendamento do exame deve ser criado com sucesso

  Scenario: [RN 6.2 - Falha] Tentativa de agendamento em horário indisponível do médico
    Given que o paciente "Beatriz" está cadastrado no sistema
    And que o médico "Dr. Carlos" está cadastrado e ativo no sistema
    And que o tipo de exame "Sangue" está cadastrado no sistema
    And que o médico "Dr. Carlos" está indisponível na data "24/12/2025" às "15h"
    When o funcionário agendar um exame do tipo "Sangue" para o paciente "Beatriz" com o médico "Dr. Carlos" na data "24/12/2025" às "15h"
    Then o sistema deve exibir a mensagem de erro "Não é permitido agendar exame em horário de indisponibilidade do médico."

  # RN7 — O exame deve receber um status inicial "Agendado".
  Scenario: [RN 7.1 - Sucesso] Verificação do status inicial do exame
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "25/12/2025" às "10h"
    Then o agendamento do exame deve ser criado com sucesso
    And o status do exame deve ser "Agendado"
    
  Scenario: [RN 7.2 - Falha] Tentativa de agendamento com data/hora passada
    Given que o paciente "Lucas" está cadastrado no sistema
    And que o médico "Dr. Ana" está cadastrado e ativo no sistema
    And que o tipo de exame "Raio-X" está cadastrado no sistema
    When o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "01/01/2000" às "10h"
    Then o sistema deve exibir a mensagem de erro "Não é permitido agendar exames para datas passadas."

# -------------------------------------------------------------
# CASO DE USO: ATUALIZAR AGENDAMENTO DE EXAME
# -------------------------------------------------------------

  # RN8 — Só podem ser alterados a data, o horário, o tipo de exame e o médico.
  Scenario: [RN 8.1 - Sucesso] Atualização de dados permitidos do exame
    Given que existe um exame de "Raio-X" agendado para o paciente "Marina" com o médico "Dr. Ana" na data "15/01/2026" às "08h"
    And que o médico "Dr. Carlos" está cadastrado e ativo no sistema
    And que o médico "Dr. Carlos" está disponível na data "16/01/2026" às "10h"
    When o funcionário alterar o médico para "Dr. Carlos" e a data e hora do exame para "16/01/2026" às "10h"
    Then a alteração deve ser salva com sucesso
   
  Scenario: [RN 8.2 - Falha] Tentativa de alterar para médico inativo
    Given que existe um exame de "Raio-X" agendado para o paciente "Paula" com o médico "Dr. Pedro" na data "15/01/2026" às "11h"
    And que o médico "Dr. Pedro" está cadastrado mas inativo no sistema
    When o funcionário alterar o médico para "Dr. Pedro" e a data e hora do exame para "15/01/2026" às "12h"
    Then o sistema deve exibir a mensagem de erro "Médico inativo não pode ser vinculado ao exame."

  # RN9 — O paciente vinculado não pode ser alterado.
  Scenario: [RN 9.1 - Falha] Tentativa de alterar o paciente de um agendamento
    Given que existe um exame de "Raio-X" agendado para o paciente "Marina"
    When o funcionário tentar alterar o paciente do exame para "Carla"
    Then o sistema deve exibir a mensagem de erro "O paciente de um exame não pode ser alterado."
    
  Scenario: [RN 9.2 - Sucesso] Alteração de outro campo (data) mantém o paciente
    Given que existe um exame de "Raio-X" agendado para o paciente "Marina"
    When o funcionário alterar a data e hora do exame para "20/01/2026" às "14h"
    Then a alteração deve ser salva com sucesso
    And o paciente vinculado deve ser "Marina"

  # RN10 — A alteração só será válida se não gerar conflito de horário.
  Scenario: [RN 10.1 - Sucesso] Remarcar exame para horário vago
    Given que existe um exame de "Ultrassonografia" agendado para o paciente "Paulo" na data "18/01/2026" às "11h" com o médico "Dr. Carlos"
    And que o médico "Dr. Carlos" está disponível na data "19/01/2026" às "15h"
    When o funcionário alterar a data e hora do exame para "19/01/2026" às "15h"
    Then a alteração deve ser salva com sucesso

  Scenario: [RN 10.2 - Falha] Tentativa de remarcar exame para horário com conflito
    Given que existe um exame de "Ultrassonografia" agendado para o paciente "Paulo" na data "18/01/2026" às "11h" com o médico "Dr. Carlos"
    And que o médico "Dr. Carlos" está indisponível na data "19/01/2026" às "14h"
    When o funcionário alterar a data e hora do exame para "19/01/2026" às "14h"
    Then o sistema deve exibir a mensagem de erro "A alteração não pode gerar conflito de horário para o médico."
    
# -------------------------------------------------------------
# CASO DE USO: CANCELAR OU EXCLUIR EXAME
# -------------------------------------------------------------

  # RN11 — Não é permitido excluir/cancelar exames já realizados ou em andamento.
  Scenario: [RN 11.1 - Sucesso] Cancelamento de exame com status "Agendado"
    Given que existe um exame de "Raio-X" agendado para o paciente "Beatriz"
    When o funcionário cancelar o exame com o motivo "Paciente solicitou"
    Then o status do exame deve ser alterado para "Cancelado"
    And o motivo e a data do cancelamento devem ser registrados

  Scenario: [RN 11.2 - Falha] Tentativa de cancelar um exame já realizado
    Given que existe um exame de "Sangue" para o paciente "Lucas" com status "Realizado"
    When o funcionário tentar cancelar o exame com o motivo "Erro de sistema"
    Then o sistema deve exibir a mensagem de erro "Ação não permitida para o status atual do exame"

  # RN12 — Exame com laudo não pode ser excluído, apenas cancelado.
  Scenario: [RN 12.1 - Sucesso] Cancelamento de exame com laudo
    Given que existe um exame de "Ultrassonografia" agendado para o paciente "Maria"
    And o exame está vinculado a um laudo
    When o funcionário cancelar o exame com o motivo "Solicitação médica"
    Then o status do exame deve ser alterado para "Cancelado"

  Scenario: [RN 12.2 - Falha] Tentativa de excluir exame com laudo
    Given que existe um exame de "Ultrassonografia" agendado para o paciente "Maria"
    And o exame está vinculado a um laudo
    When o funcionário tentar excluir o exame
    Then o sistema deve exibir a mensagem de erro "Exame com laudo não pode ser excluído, apenas cancelado."