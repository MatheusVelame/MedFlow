Feature: Alteração de especialidade médica
  Como administrador da clínica
  Quero alterar o nome ou a descrição de uma especialidade
  Para manter os dados corretos e atualizados

  # RN1 — Apenas a descrição ou o nome da especialidade podem ser alterados
  Scenario: Alteração válida de descrição e nome
    Given que a especialidade "Cardiologia" está cadastrada
    When o administrador alterar o nome para "Cardiologia Avançada"
    And alterar a descrição para "Especialidade responsável pelo tratamento avançado de doenças cardíacas"
    Then o sistema deve salvar as alterações com sucesso
    And registrar a operação no histórico de alterações

  Scenario: Tentativa de alterar campo não permitido
    Given que a especialidade "Cardiologia" está cadastrada
    When o administrador tentar alterar o status da especialidade para "Inativa"
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Apenas o nome ou a descrição podem ser alterados"

  # RN2 — O nome alterado deve passar novamente pela validação de unicidade (alfabético, único)
  Scenario: Nome alterado válido e único
    Given que não existe nenhuma especialidade chamada "Cardiologia Avançada" no sistema
    When o administrador alterar o nome de "Cardiologia" para "Cardiologia Avançada"
    Then o sistema deve salvar a alteração com sucesso

  Scenario: Nome alterado inválido ou duplicado
    Given que já existe uma especialidade chamada "Cardiologia Avançada" no sistema
    When o administrador tentar alterar o nome de "Cardiologia" para "Cardiologia Avançada"
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Nome da especialidade deve ser único e conter apenas letras e espaços"

  # RN3 — Não é permitido inativar ou alterar o nome de uma especialidade vinculada a médicos ativos, sem tratamento adequado
  Scenario: Alteração de nome/descrição com especialidade sem médicos ativos
    Given que a especialidade "Neurologia" não possui médicos ativos vinculados
    When o administrador alterar o nome ou descrição
    Then o sistema deve salvar a alteração com sucesso

  Scenario: Tentativa de alterar nome de especialidade vinculada a médicos ativos
    Given que a especialidade "Neurologia" possui médicos ativos vinculados
    When o administrador tentar alterar o nome da especialidade sem reatribuir os médicos
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Não é permitido alterar o nome de especialidades vinculadas a médicos ativos sem reatribuição"
