Feature: Gerenciamento de Tipos de Exames

Rule: Cadastro de tipo de exame

  # RN1 – Não é possível cadastrar um tipo de exame sem descrição

  Scenario: Cadastro com descrição informada #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX200"
    And existe uma especialidade "Hematologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX200", descrição "Hemograma", especialidade "Hematologia" e o valor "80.00"
    Then o sistema deve criar o tipo de exame
    And definir o status inicial como "Ativo"
    And exibir confirmação de sucesso

  Scenario: Bloqueio por ausência de descrição
    Given que não existe tipo de exame cadastrado com o código "EX201"
    And existe uma especialidade "Hematologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX201", especialidade "Hematologia" e valor "50.00"
    And deixa a descrição em branco
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que a descrição é obrigatória


  # RN2 – Não é possível cadastrar um tipo de exame sem especialidade

  Scenario: Cadastro com especialidade informada #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX202"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX202", descrição "Raio-X do Tórax", especialidade "Radiologia" e valor "150.00"
    Then o sistema deve criar o tipo de exame
    And definir o status inicial como "Ativo"
    And exibir confirmação de sucesso

  Scenario: Bloqueio por ausência de especialidade
    Given que não existe tipo de exame cadastrado com o código "EX203"
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX203", descrição "Ultrassom Abdômen" e valor "100.00"
    And não informa a especialidade
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que a especialidade é obrigatória


  # RN3 – Não é possível cadastrar um tipo de exame sem valor

  Scenario: Cadastro com valor informado #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX204"
    And existe uma especialidade "Cardiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX204", descrição "ECG", especialidade "Cardiologia" e valor "100.00"
    Then o sistema deve criar o tipo de exame
    And definir o status inicial como "Ativo"
    And exibir confirmação de sucesso

  Scenario: Bloqueio por ausência de valor
    Given que não existe tipo de exame cadastrado com o código "EX205"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com o código "EX205", descrição "Ressonância Magnética" e especialidade "Radiologia"
    And não informa o valor
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o valor é obrigatório


  # RN4 – O valor deve ser maior que 0

  Scenario: Cadastro com valor maior que zero #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX206"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX206", a descrição "Raio-X de Mãos e Punho", a especialidade "Radiologia" e o valor "100.00"
    Then o sistema deve criar o tipo de exame
    And definir o status inicial como "Ativo"
    And exibir confirmação de sucesso

  Scenario: Bloqueio por valor negativo
    Given que não existe tipo de exame cadastrado com o código "EX207"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX207", a descrição "Raio-X do Tornozelo", a especialidade "Radiologia" e o valor "-30.00"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o valor deve ser maior que 0


  # RN5 – Não é possível cadastrar um tipo de exame com status inicial Inativo

  Scenario: Cadastro com status inicial Ativo #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX208"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX208", a descrição "Raio-X dos Pés", a especialidade "Radiologia" e o valor "100.00"
    And define o status inicial como "Ativo"
    Then o sistema deve criar o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio por status inicial Inativo
    Given que não existe tipo de exame cadastrado com o código "EX209"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX209", a descrição "Raio-X de Coluna", a especialidade "Radiologia" e o valor "100.00"
    And define o status inicial como "Inativo"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o status inicial deve ser "Ativo"


  # RN6 – O código do tipo de exame deve ser único no sistema

  Scenario: Cadastro com código único #Sucesso
    Given que não existe tipo de exame cadastrado com o código "EX210"
    And existe uma especialidade "Cardiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX210", descrição "Teste Ergométrico", a especialidade "Cardiologia" e o valor "200.00"
    Then o sistema deve criar o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio por código duplicado
    Given que já existe tipo de exame cadastrado com o código "EX210"
    And existe uma especialidade "Radiologia" cadastrada no sistema
    When a gerente "Ana Paula" solicita o cadastro do tipo de exame com código "EX210", a descrição "Raio-X dos Pés", a especialidade "Radiologia" e o valor "100.00"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o código deve ser único


Rule: Atualização de tipo de exame

  # RN1 – Não é possível alterar o código do tipo de exame

  Scenario: Atualização sem alterar o código #Sucesso
    Given que existe um tipo de exame com o código "EX301"
    And não existem agendamentos vinculados a esse exame
    When a gerente "Ana Paula" solicitar a alteração da descrição para "Hemograma Completo"
    And alterar a especialidade para "Hematologia"
    And alterar o valor para "95.00"
    And alterar o status para "Ativo"
    And manter o código do exame como "EX301"
    Then o sistema deve salvar as alterações
    And exibir confirmação de sucesso

  Scenario: Bloqueio ao tentar alterar o código
    Given que existe um tipo de exame com o código "EX302"
    And não existem agendamentos vinculados a esse exame
    When a gerente "Ana Paula" solicitar a alteração do código "EX302" para "EX999"
    Then o sistema deve bloquear a alteração
    And exibir mensagem informando que o código do tipo de exame não pode ser alterado


  # RN2 – Não é possível atualizar o exame inserindo um valor inferior a 0

  Scenario: Atualização com valor zero (válido) #Sucesso
    Given que existe um tipo de exame com o código "EX303"
    When a gerente "Ana Paula" solicitar a alteração do valor para "0"
    And mantiver os demais dados sem alteração
    Then o sistema deve salvar a alteração do valor
    And exibir confirmação de sucesso

  Scenario: Bloqueio por valor negativo na atualização
    Given que existe um tipo de exame com o código "EX304"
    When a gerente "Ana Paula" solicitar a alteração do valor para "-1.00"
    Then o sistema deve bloquear a alteração
    And exibir mensagem informando que o valor deve ser maior ou igual a 0


  # RN3 – Não é possível atualizar um exame que possua agendamentos vinculados

  Scenario: Atualização permitida sem agendamentos #Sucesso
    Given que existe um tipo de exame com o código "EX305"
    And não existem agendamentos vinculados a esse exame
    When a gerente "Ana Paula" solicitar a alteração da descrição para "Raio-X do Tórax (PA e Perfil)"
    And alterar a especialidade para "Radiologia"
    And alterar o valor para "120.00"
    And manter o código "EX305" inalterado
    Then o sistema deve salvar as alterações
    And exibir confirmação de sucesso

  Scenario: Bloqueio de atualização com agendamentos vinculados
    Given que existe um tipo de exame com o código "EX306"
    And existem agendamentos vinculados a esse exame
    When a gerente "Ana Paula" solicitar a alteração da descrição para "Raio-X dos Seios da Face"
    Then o sistema deve bloquear a alteração
    And exibir mensagem informando que não é possível alterar exames com agendamentos vinculados


Rule: Remoção e inativação de tipo de exame

  # RN1 – Não é possível excluir tipo de exame com agendamentos vinculados (históricos ou futuros)

  Scenario: Exclusão sem agendamentos vinculados #Sucesso
    Given que existe um tipo de exame com o código "EX801"
    And não existem agendamentos (históricos ou futuros) vinculados a esse exame
    When a gerente "Ana Paula" solicitar a exclusão do tipo de exame com o código "EX801"
    Then o sistema deve excluir o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio de exclusão com agendamentos vinculados
    Given que existe um tipo de exame com o código "EX802"
    And existem agendamentos vinculados a esse exame (históricos e/ou futuros)
    When a gerente "Ana Paula" solicitar a exclusão do tipo de exame com o código "EX802"
    Then o sistema deve bloquear a exclusão
    And exibir mensagem informando que não é possível excluir tipos de exame com agendamentos vinculados


  # RN2 – Não é possível inativar tipo de exame com agendamentos vinculados (futuros)

  Scenario: Inativação sem agendamentos futuros vinculados #Sucesso
    Given que existe um tipo de exame com o código "EX803"
    And não existem agendamentos (futuros) vinculados a esse exame
    When a gerente "Ana Paula" solicitar a inativação do tipo de exame com o código "EX803"
    Then o sistema deve inativar o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio de inativação com agendamentos futuros
    Given que existe um tipo de exame com o código "EX804"
    And existem agendamentos vinculados a esse exame (futuros)
    When a gerente "Ana Paula" solicitar a inativação do tipo de exame com o código "EX804"
    Then o sistema deve bloquear a inativação
    And exibir mensagem informando que não é possível inativar tipos de exame com agendamentos vinculados


  # RN3 – Não é possível excluir tipo de exame inexistente

  Scenario: Exclusão de exame existente #Sucesso
    Given que existe um tipo de exame com o código "EX805"
    And não existem agendamentos (históricos ou futuros) vinculados a esse exame
    When a gerente "Ana Paula" solicitar a exclusão do tipo de exame com o código "EX805"
    Then o sistema deve excluir o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio ao tentar excluir exame inexistente
    Given que não existe tipo de exame cadastrado com o código "EX806"
    When a gerente "Ana Paula" solicitar a exclusão do tipo de exame com o código "EX806"
    Then o sistema deve bloquear a operação
    And exibir mensagem informando que o tipo de exame não foi encontrado


  # RN4 – Não é possível inativar tipo de exame inexistente

  Scenario: Inativação de exame existente #Sucesso
    Given que existe um tipo de exame com o código "EX807"
    And não existem agendamentos (futuros) vinculados a esse exame
    When a gerente "Ana Paula" solicitar a inativação do tipo de exame com o código "EX807"
    Then o sistema deve inativar o tipo de exame
    And exibir confirmação de sucesso

  Scenario: Bloqueio ao tentar inativar exame inexistente
    Given que não existe tipo de exame cadastrado com o código "EX808"
    When a gerente "Ana Paula" solicitar a inativação do tipo de exame com o código "EX808"
    Then o sistema deve bloquear a operação
    And exibir mensagem informando que o tipo de exame não foi encontrado