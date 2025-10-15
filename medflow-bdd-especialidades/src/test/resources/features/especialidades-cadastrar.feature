Feature: Cadastro de nova especialidade médica
  Como administrador da clínica
  Quero cadastrar uma nova especialidade médica
  Para que ela possa ser atribuída a médicos

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
