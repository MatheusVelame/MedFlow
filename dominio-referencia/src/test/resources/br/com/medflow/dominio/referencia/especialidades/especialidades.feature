Feature: Gerenciamento de Especialidades Médicas
  
  # Contexto: Precondições
    #Dado que a especialidade "Cardiologia" não está cadastrada
    # E a especialidade "Pediatria" está cadastrada e não possui médicos ativos vinculados
    # E a especialidade "Dermatologia" está cadastrada e possui 2 médicos ativos vinculados
    # E a especialidade "Gastroenterologia" está cadastrada e possui histórico de vínculo, mas está Inativa
    # E a especialidade "Fisioterapia" foi cadastrada e nunca foi vinculada a um médico

  # -------------------------------------------------------------
  # CASO DE USO: CADASTRAR NOVA ESPECIALIDADE
  # -------------------------------------------------------------

  # RN1 — O nome da especialidade é obrigatório.
  Scenario: [RN 1.1 - Sucesso] Cadastro de especialidade com nome preenchido
    When o administrador solicitar o cadastro de especialidade com nome "Oftalmologia" e descrição "Tratamento de doenças oculares"
    Then o sistema deve cadastrar a especialidade com sucesso
    And a especialidade "Oftalmologia" deve estar cadastrada

  Scenario: [RN 1.2 - Falha] Tentativa de cadastro com nome vazio
    When o administrador tentar cadastrar uma nova especialidade com nome vazio
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "O nome da especialidade é obrigatório"

  # RN2 — Não é permitido cadastrar duas especialidades com o mesmo nome.
  Scenario: [RN 2.1 - Sucesso] Cadastro com nome único (implicitamente testado no 1.1)
    Given que a especialidade "Ortopedia" não está cadastrada
    When o administrador solicitar o cadastro de especialidade com nome "Ortopedia"
    Then o sistema deve cadastrar a especialidade com sucesso

  Scenario: [RN 2.2 - Falha] Tentativa de cadastro com nome duplicado
    Given que a especialidade "Psiquiatria" já está cadastrada
    When o administrador tentar cadastrar uma nova especialidade com nome "Psiquiatria"
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "Já existe uma especialidade com este nome"

  # RN3 — O nome da especialidade deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços).
  Scenario: [RN 3.1 - Sucesso] Cadastro com nome alfabético válido
    When o administrador solicitar o cadastro de especialidade com nome "Otorrinolaringologia e Cabeça e Pescoço"
    Then o sistema deve cadastrar a especialidade com sucesso

  Scenario: [RN 3.2 - Falha] Tentativa de cadastro com caracteres não alfabéticos
    When o administrador tentar cadastrar uma nova especialidade com nome "Fonoaudiologia 123"
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "O nome da especialidade deve conter apenas caracteres alfabéticos e espaços"

  # RN4 — A descrição da especialidade (opcional) pode conter até 255 caracteres.
  Scenario: [RN 4.1 - Sucesso] Cadastro com descrição dentro do limite de 255 caracteres
    Given que a descrição é uma string de 255 caracteres
    When o administrador solicitar o cadastro de especialidade com nome "Nutrologia" e a descrição informada
    Then o sistema deve cadastrar a especialidade com sucesso

  Scenario: [RN 4.2 - Falha] Tentativa de cadastro com descrição excedendo 255 caracteres
    Given que a descrição é uma string de 256 caracteres
    When o administrador tentar cadastrar uma nova especialidade com nome "Psicologia" e a descrição informada
    Then o sistema deve rejeitar o cadastro
    And exibir a mensagem "A descrição não pode exceder 255 caracteres"

  # RN5 — O status inicial da especialidade cadastrada deve ser definido como "Ativa"
  Scenario: [RN 5.1 - Sucesso] Verificação do status inicial após cadastro
    When o administrador solicitar o cadastro de uma nova especialidade com nome "Reumatologia"
    Then o sistema deve criar a especialidade "Reumatologia"
    And a especialidade "Reumatologia" deve ter o status "Ativa"

  Scenario: [RN 5.2 - Falha] Tentativa de cadastrar forçando o status como "Inativa"
    When o administrador tentar cadastrar uma nova especialidade com nome "Oncologia" e status "Inativa"
    Then o sistema deve ignorar o status fornecido
    And a especialidade "Oncologia" deve ter o status "Ativa"
    # Nota: A falha ocorre na tentativa de setar um status proibido, mas o sistema corrige para o status obrigatório.

  # -------------------------------------------------------------
  # CASO DE USO: ALTERAR ESPECIALIDADE
  # -------------------------------------------------------------

  # RN6 — Apenas a descrição ou o nome da especialidade podem ser alterados.
  Scenario: [RN 6.1 - Sucesso] Alteração permitida de nome e descrição
    When o administrador solicitar a alteração da especialidade "Pediatria" para o nome "Pediatria Geral" e nova descrição "Cuidado infantil"
    Then o sistema deve atualizar a especialidade com sucesso
    And a especialidade "Pediatria" deve ter o nome "Pediatria Geral"

  Scenario: [RN 6.2 - Falha] Tentativa de alterar campos não permitidos
    When o administrador tentar alterar a especialidade "Pediatria" mudando o seu status para "Inativa"
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Apenas o nome e a descrição podem ser alterados"
    And a especialidade "Pediatria" deve manter o status "Ativa"

  # RN7 — O nome alterado deve passar novamente pela validação de unicidade (alfabético, único).
  Scenario: [RN 7.1 - Sucesso] Alteração de nome para um nome válido e único
    Given que a especialidade "Geral" está cadastrada
    When o administrador solicitar a alteração do nome da especialidade "Pediatria" para "Pediatria Infantil"
    Then o sistema deve atualizar o nome da especialidade "Pediatria" para "Pediatria Infantil" com sucesso

  Scenario: [RN 7.2 - Falha] Alteração de nome para um nome duplicado
    Given que a especialidade "Geral" está cadastrada
    When o administrador tentar alterar o nome da especialidade "Pediatria" para "Geral"
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Já existe outra especialidade com este nome"
    And o nome da especialidade "Pediatria" deve permanecer inalterado

  # RN8 — Não é permitido inativar ou alterar o nome de uma especialidade que esteja vinculada a médicos ativos, sem que haja um tratamento para essa vinculação (ex.: reatribuição).
  Scenario: [RN 8.1 - Sucesso] Alteração de nome com tratamento bem-sucedido
    Given que existe um mecanismo de reatribuição de médicos
    When o administrador tenta alterar o nome da especialidade "Dermatologia" para "Dermatologia Clínica"
    And o mecanismo de reatribuição é executado com sucesso
    Then a especialidade "Dermatologia" deve ter o nome "Dermatologia Clínica"

  Scenario: [RN 8.2 - Falha] Tentativa de alteração de nome sem tratamento da vinculação
    When o administrador tentar alterar o nome da especialidade "Dermatologia" para "Dermato"
    Then o sistema deve rejeitar a alteração
    And exibir a mensagem "Não é possível alterar o nome: existem médicos ativos vinculados"

  # -------------------------------------------------------------
  # CASO DE USO: EXCLUIR ESPECIALIDADE
  # -------------------------------------------------------------

  # RN9 — Não é permitido excluir uma especialidade que esteja vinculada a pelo menos um médico ativo.
  Scenario: [RN 9.1 - Sucesso] Exclusão de especialidade sem médicos ativos vinculados
    Given que a especialidade "Pediatria" não possui médicos ativos vinculados
    When o administrador solicitar a exclusão da especialidade "Pediatria"
    Then o sistema deve processar a exclusão com sucesso
    And a especialidade "Pediatria" não deve mais existir no sistema

  Scenario: [RN 9.2 - Falha] Tentativa de exclusão com médicos ativos vinculados
    Given que a especialidade "Dermatologia" possui médicos ativos vinculados
    When o administrador tentar excluir a especialidade "Dermatologia"
    Then o sistema deve rejeitar a exclusão
    And exibir a mensagem "Não é possível excluir: existem médicos ativos vinculados"
    And a especialidade "Dermatologia" deve manter o status "Ativa"

  # RN10 — A exclusão física de uma especialidade só pode ser realizada caso nunca tenha sido vinculada a nenhum médico.
  Scenario: [RN 10.1 - Sucesso] Exclusão física por nunca ter tido vínculo
    Given que a especialidade "Fisioterapia" nunca foi vinculada a um médico
    When o administrador solicitar a exclusão da especialidade "Fisioterapia"
    Then a especialidade "Fisioterapia" deve ser removida fisicamente do banco de dados

  Scenario: [RN 10.2 - Falha] Tentativa de exclusão física onde houve histórico de vínculo
    Given que a especialidade "Gastroenterologia" possui histórico de vínculo com médicos
    When o administrador tentar excluir a especialidade "Gastroenterologia"
    Then o sistema deve negar a exclusão física
    And a especialidade "Gastroenterologia" deve ter seu status alterado para "Inativa"

  # RN11 — Caso haja histórico de vínculo com médicos, a especialidade deve ser marcada como "Inativa" em vez de excluída.
  Scenario: [RN 11.1 - Sucesso] Inativação em vez de exclusão devido a histórico
    Given que a especialidade "Gastroenterologia" possui histórico de vínculo com médicos
    When o administrador solicitar a exclusão da especialidade "Gastroenterologia"
    Then o sistema deve converter a exclusão em inativação
    And a especialidade "Gastroenterologia" deve ter o status "Inativa"

  Scenario: [RN 11.2 - Falha] Tentativa de inativar especialidade sem histórico
    Given que a especialidade "Fisioterapia" nunca foi vinculada a um médico
    When o administrador tenta marcar a especialidade "Fisioterapia" como "Inativa" durante a exclusão
    Then a especialidade "Fisioterapia" deve ser removida fisicamente do sistema
    # Nota: A "falha" neste caso é a negação da inativação para forçar o caminho correto: a exclusão física (RN10).

  # RN12 — Especialidades inativas não podem ser atribuídas a novos médicos.
  Scenario: [RN 12.1 - Sucesso] Atribuição de médico a especialidade Ativa
    Given que o médico "Dr. João" está ativo
    And a especialidade "Pediatria" tem o status "Ativa"
    When o administrador solicita a atribuição do médico "Dr. João" à especialidade "Pediatria"
    Then a atribuição deve ser realizada com sucesso

  Scenario: [RN 12.2 - Falha] Tentativa de atribuir médico a especialidade Inativa
    Given que o médico "Dra. Maria" está ativo
    And a especialidade "Gastroenterologia" tem o status "Inativa"
    When o administrador tenta atribuir a médica "Dra. Maria" à especialidade "Gastroenterologia"
    Then o sistema deve rejeitar a atribuição
    And exibir a mensagem "Não é possível atribuir a médicos: a especialidade está inativa"