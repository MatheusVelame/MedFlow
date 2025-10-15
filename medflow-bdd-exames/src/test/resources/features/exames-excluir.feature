Feature: Exclusão ou cancelamento de exame agendado
  Como funcionário da clínica
  Quero excluir ou cancelar um exame agendado
  Para manter os registros corretos e consistentes no sistema

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
