Feature: Gerenciamento de Agendamento de Exames

  # Contexto:
   # Dado que o funcionário "atendente" está logado no sistema

  # Features de Agendamento de Exame

  @agendamento
  Scenario: Agendamento de exame com sucesso
    Dado que o paciente "Lucas" está cadastrado no sistema
    E que o médico "Dr. Ana" está cadastrado e ativo no sistema
    E que o tipo de exame "Raio-X" está cadastrado no sistema
    E que o médico "Dr. Ana" está disponível na data "20/12/2025" às "10h"
    Quando o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" na data "20/12/2025" às "10h"
    Então o agendamento do exame deve ser criado com sucesso
    E o status do exame deve ser "Agendado"

  @agendamento
  Scenario: Tentativa de agendamento para paciente não cadastrado
    Dado que o paciente "Carlos" não está cadastrado no sistema
    E que o médico "Dr. Ana" está cadastrado e ativo no sistema
    E que o tipo de exame "Raio-X" está cadastrado no sistema
    Quando o funcionário agendar um exame do tipo "Raio-X" para o paciente "Carlos" com o médico "Dr. Ana" na data "20/12/2025" às "11h"
    Então o sistema deve exibir a mensagem de erro "Paciente não cadastrado no sistema."

  @agendamento
  Scenario: Tentativa de agendamento para médico não cadastrado
    Dado que o paciente "Lucas" está cadastrado no sistema
    E que o médico "Dr. Jonas" não está cadastrado no sistema
    E que o tipo de exame "Raio-X" está cadastrado no sistema
    Quando o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Jonas" na data "20/12/2025" às "12h"
    Então o sistema deve exibir a mensagem de erro "Médico não cadastrado no sistema."

  @agendamento
  Scenario: Tentativa de agendamento com tipo de exame não cadastrado
    Dado que o paciente "Lucas" está cadastrado no sistema
    E que o médico "Dr. Ana" está cadastrado e ativo no sistema
    E que o tipo de exame "Densitometria Óssea" não está cadastrado no sistema
    Quando o funcionário agendar um exame do tipo "Densitometria Óssea" para o paciente "Lucas" com o médico "Dr. Ana" na data "21/12/2025" às "10h"
    Então o sistema deve exibir a mensagem de erro "Tipo de exame não cadastrado no sistema."

  @agendamento
  Scenario: Tentativa de agendamento sem informar a data
    Dado que o paciente "Lucas" está cadastrado no sistema
    E que o médico "Dr. Ana" está cadastrado e ativo no sistema
    E que o tipo de exame "Raio-X" está cadastrado no sistema
    Quando o funcionário agendar um exame do tipo "Raio-X" para o paciente "Lucas" com o médico "Dr. Ana" sem data e hora
    Então o sistema deve exibir a mensagem de erro "Data e horário do exame são obrigatórios."
  
  @agendamento
  Scenario: Tentativa de agendar exame em horário já ocupado pelo paciente
    Dado que o paciente "Lucas" está cadastrado no sistema
    E que o médico "Dr. Ana" está cadastrado e ativo no sistema
    E que o tipo de exame "Raio-X" está cadastrado no sistema
    E existe um exame agendado para o paciente "Lucas" na data "22/12/2025" às "14h"
    Quando o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Lucas" com o médico "Dr. Carlos" na data "22/12/2025" às "14h"
    Então o sistema deve exibir a mensagem de erro "Paciente já possui um exame agendado neste horário."

  @agendamento
  Scenario: Tentativa de agendamento com médico inativo
    Dado que o paciente "Maria" está cadastrado no sistema
    E que o médico "Dr. Paulo" está cadastrado mas inativo no sistema
    E que o tipo de exame "Ultrassonografia" está cadastrado no sistema
    Quando o funcionário agendar um exame do tipo "Ultrassonografia" para o paciente "Maria" com o médico "Dr. Paulo" na data "23/12/2025" às "09h"
    Então o sistema deve exibir a mensagem de erro "Médico vinculado ao exame deve estar ativo no sistema."
  
  @agendamento
  Scenario: Tentativa de agendamento em horário indisponível do médico
    Dado que o paciente "Beatriz" está cadastrado no sistema
    E que o médico "Dr. Carlos" está cadastrado e ativo no sistema
    E que o tipo de exame "Sangue" está cadastrado no sistema
    E que o médico "Dr. Carlos" está indisponível na data "24/12/2025" às "15h"
    Quando o funcionário agendar um exame do tipo "Sangue" para o paciente "Beatriz" com o médico "Dr. Carlos" na data "24/12/2025" às "15h"
    Então o sistema deve exibir a mensagem de erro "Não é permitido agendar exame em horário de indisponibilidade do médico."

  # Features de Atualização de Agendamento
  
  @atualizacao
  Scenario: Atualização de horário do exame com sucesso
    Dado que existe um exame de "Raio-X" agendado para o paciente "Marina" com o médico "Dr. Ana" na data "15/01/2026" às "08h"
    E que o médico "Dr. Ana" está disponível na data "16/01/2026" às "10h"
    Quando o funcionário alterar a data e hora do exame para "16/01/2026" às "10h"
    Então a alteração deve ser salva com sucesso
    E o histórico de alterações do exame deve ser registrado

  @atualizacao
  Scenario: Tentativa de alterar o paciente de um agendamento
    Dado que existe um exame de "Raio-X" agendado para o paciente "Marina"
    Quando o funcionário tentar alterar o paciente do exame para "Carla"
    Então o sistema deve exibir a mensagem de erro "O paciente de um exame não pode ser alterado."

  @atualizacao
  Scenario: Tentativa de remarcar exame para horário com conflito
    Dado que existe um exame de "Ultrassonografia" agendado para o paciente "Paulo" na data "18/01/2026" às "11h"
    E que o médico "Dr. Carlos" está indisponível na data "19/01/2026" às "14h"
    Quando o funcionário alterar a data e hora do exame para "19/01/2026" às "14h"
    Então o sistema deve exibir a mensagem de erro "A alteração não pode gerar conflito de horário para o médico."

  # Features de Exclusão e Cancelamento de Exame

  @cancelamento
  Scenario: Cancelamento de exame com sucesso
    Dado que existe um exame de "Raio-X" agendado para o paciente "Beatriz"
    Quando o funcionário cancelar o exame com o motivo "Paciente solicitou"
    Então o status do exame deve ser alterado para "Cancelado"
    E o motivo e a data do cancelamento devem ser registrados

  @cancelamento
  Scenario: Tentativa de excluir um exame já realizado
    Dado que existe um exame de "Sangue" para o paciente "Lucas" com status "Realizado"
    Quando o funcionário tentar excluir o exame
    Então o sistema deve exibir a mensagem de erro "Não é permitido excluir exames já realizados."
    
  @cancelamento
  Scenario: Tentativa de excluir exame vinculado a um laudo
    Dado que existe um exame de "Ultrassonografia" agendado para o paciente "Maria"
    E o exame está vinculado a um laudo
    Quando o funcionário tentar excluir o exame
    Então o sistema deve exibir a mensagem de erro "Exame com laudo não pode ser excluído, apenas cancelado."

  @cancelamento
  Scenario: Tentativa de excluir exame associado a um registro clínico
    Dado que existe um exame de "Raio-X" agendado para o paciente "Paulo"
    E o exame está associado a um registro clínico no prontuário
    Quando o funcionário tentar excluir o exame
    Então o sistema deve exibir a mensagem de erro "Exame associado a um registro clínico não pode ser excluído."