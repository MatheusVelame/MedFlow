Feature: Exclusão de especialidade médica
  Como administrador da clínica
  Quero excluir uma especialidade médica
  Para remover informações desnecessárias ou descontinuadas do sistema

  # RN1 — Não é permitido excluir uma especialidade que esteja vinculada a pelo menos um médico ativo
  Scenario: Especialidade sem médicos ativos vinculados
    Given que a especialidade "Cardiologia" não possui médicos ativos vinculados
    When o administrador solicitar a exclusão da especialidade
    Then o sistema deve excluir a especialidade com sucesso
    And registrar a operação no histórico de alterações

  Scenario: Especialidade com médicos ativos vinculados
    Given que a especialidade "Cardiologia" possui médicos ativos vinculados
    When o administrador tentar excluir a especialidade
    Then o sistema deve rejeitar a exclusão
    And exibir a mensagem "Especialidade não pode ser excluída pois possui médicos ativos vinculados"

  # RN2 — Exclusão física só pode ser realizada caso nunca tenha sido vinculada a nenhum médico
  Scenario: Exclusão física permitida
    Given que a especialidade "Dermatologia" nunca foi vinculada a nenhum médico
    When o administrador solicitar a exclusão física
    Then o sistema deve excluir a especialidade permanentemente

  Scenario: Tentativa de exclusão física com histórico de vínculo
    Given que a especialidade "Dermatologia" já foi vinculada a médicos no passado
    When o administrador tentar excluir fisicamente
    Then o sistema deve impedir a exclusão
    And exibir a mensagem "Especialidade com histórico de vínculo não pode ser excluída fisicamente"

  # RN3 — Caso haja histórico de vínculo com médicos, a especialidade deve ser marcada como "Inativa"
  Scenario: Especialidade com histórico de vínculo é inativada
    Given que a especialidade "Neurologia" já foi vinculada a médicos anteriormente
    When o administrador solicitar a exclusão da especialidade
    Then o sistema deve alterar o status da especialidade para "Inativa"
    And registrar a operação no histórico de alterações

  Scenario: Exclusão física ignorando histórico
    Given que a especialidade "Neurologia" já foi vinculada a médicos anteriormente
    When o administrador tentar excluir a especialidade permanentemente
    Then o sistema deve rejeitar a operação
    And exibir a mensagem "Especialidade com histórico de vínculo deve ser apenas inativada, não excluída"

  # RN4 — Especialidades inativas não podem ser atribuídas a novos médicos
  Scenario: Tentativa de vincular especialidade ativa
    Given que a especialidade "Cardiologia" está ativa
    When o administrador tentar vincular a especialidade a um novo médico
    Then o sistema deve permitir o vínculo

  Scenario: Tentativa de vincular especialidade inativa
    Given que a especialidade "Neurologia" está inativa
    When o administrador tentar vincular a especialidade a um novo médico
    Then o sistema deve rejeitar o vínculo
    And exibir a mensagem "Especialidade inativa não pode ser atribuída a novos médicos"
