Feature: Alteração de especialidade médica


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

# Feature: Cadastro de nova especialidade médica


  # RN1 — O nome da especialidade é obrigatório
  Scenario: Cadastro com nome informado
    Given que o administrador informa o nome da especialidade como "Cardiologia"
    When ele tentar cadastrar a especialidade
    Then o sistema deve criar a especialidade com sucesso
    And registrar a operação no histórico

  Scenario: Cadastro sem nome
    Given que o administrador não informa o nome da especialidade
    When ele tentar cadastrar a especialidade
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "Nome da especialidade é obrigatório"

  # RN2 — Não é permitido cadastrar duas especialidades com o mesmo nome
  Scenario: Cadastro de especialidade com nome único
    Given que não existe nenhuma especialidade chamada "Dermatologia" no sistema
    When o administrador cadastrar "Dermatologia"
    Then o sistema deve criar a especialidade com sucesso

  Scenario: Cadastro de especialidade duplicada
    Given que já existe uma especialidade chamada "Dermatologia" no sistema
    When o administrador tentar cadastrar novamente "Dermatologia"
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "Especialidade já cadastrada"

  # RN3 — O nome da especialidade deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços)
  Scenario: Nome válido com caracteres alfabéticos
    Given que o administrador informa o nome "Medicina Interna"
    When ele cadastrar a especialidade
    Then o sistema deve criar a especialidade com sucesso

  Scenario: Nome inválido com caracteres numéricos ou símbolos
    Given que o administrador informa o nome "Cardiologia123!"
    When ele tentar cadastrar a especialidade
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "Nome da especialidade deve conter apenas letras e espaços"

  # RN4 — A descrição da especialidade (opcional) pode conter até 255 caracteres
  Scenario: Descrição dentro do limite permitido
    Given que o administrador informa a descrição "Especialidade responsável pelo diagnóstico e tratamento de doenças do coração"
    When ele cadastrar a especialidade
    Then o sistema deve criar a especialidade com sucesso

  Scenario: Descrição acima do limite de 255 caracteres
    Given que o administrador informa uma descrição com 300 caracteres
    When ele tentar cadastrar a especialidade
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "Descrição deve conter no máximo 255 caracteres"

  # RN5 — O status inicial da especialidade cadastrada deve ser definido como "Ativa"
  Scenario: Verificação do status inicial
    Given que o administrador cadastra a especialidade "Neurologia"
    When o sistema criar a especialidade
    Then o status da especialidade deve ser automaticamente definido como "Ativa"

  Scenario: Status inicial incorreto
    Given que o administrador cadastra a especialidade "Neurologia"
    When o sistema criar a especialidade com status "Inativa"
    Then o cadastro deve ser considerado inválido
    And exibir a mensagem "O status inicial da especialidade deve ser 'Ativa'"

# Feature: Exclusão de especialidade médica

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