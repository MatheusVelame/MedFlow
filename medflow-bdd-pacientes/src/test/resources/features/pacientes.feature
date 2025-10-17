Feature: Gerenciamento de Pacientes

Rule: Cadastro de paciente

  # RN1 – Nome, CPF, data de nascimento e telefone são obrigatórios

  Scenario: Cadastro com todos os campos obrigatórios preenchidos #Sucesso
    Given que não existe paciente com o CPF "12345678901"
    When a funcionária "Juliana" solicita o cadastro do paciente com nome "Ana Paula Ribeiro", CPF "12345678901", telefone "81990001111", endereço "Rua A, 123" e data de nascimento "10/05/1992"
    Then o sistema deve criar o paciente
    And exibir confirmação de sucesso

  Scenario: Falta de nome
    Given que nome é uma informação obrigatória
    When a funcionária "Juliana" solicitar o cadastro do paciente com CPF "32165498701", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1993"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem indicando que nome é obrigatório

  Scenario: Falta de CPF
    Given que CPF é uma informação obrigatória
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Paula Ribeiro", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1993"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem indicando que CPF é obrigatório

  Scenario: Falta de data de nascimento
    Given que data de nascimento é uma informação obrigatória
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Paula Ribeiro", CPF "32165498701", telefone "81990002222" e endereço "Rua B, 123"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem indicando que data de nascimento é obrigatória

  Scenario: Falta de telefone
    Given que telefone é uma informação obrigatória
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Paula Ribeiro", CPF "32165498701", endereço "Rua B, 123" e data de nascimento "10/05/1993"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem indicando que telefone é obrigatório


  # RN2 – O CPF deve conter exatamente 11 dígitos numéricos

  Scenario: CPF válido com 11 dígitos #Sucesso
    Given que o CPF deve ter 11 dígitos
    And não há paciente cadastrado com o CPF "32165498701"
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Paula Ribeiro", CPF "32165498701", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1993"
    Then o sistema deve criar o paciente
    And exibir confirmação de sucesso

  Scenario: CPF com tamanho inválido (10 dígitos)
    Given que o CPF deve ter 11 dígitos
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Ribeiro", CPF "4567891231", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1970"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o CPF deve conter exatamente 11 dígitos numéricos

  Scenario: CPF com caracteres não numéricos
    Given que o CPF deve ter apenas caracteres numéricos
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Ribeiro", CPF "456.789.123-11", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1970"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o CPF deve conter apenas dígitos (11 caracteres)


  # RN3 – A data de nascimento deve estar no formato dd/mm/aaaa

  Scenario: Data de nascimento no formato válido #Sucesso
    Given que a data de nascimento deve estar no formato "dd/mm/aaaa"
    And não há paciente cadastrado com o CPF "45678912311"
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Maria Ribeiro", CPF "45678912311", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1970"
    Then o sistema deve criar o paciente
    And exibir confirmação de sucesso

  Scenario: Formato de data inválido
    Given que a data de nascimento deve estar no formato "dd/mm/aaaa"
    And não há paciente cadastrado com o CPF "65498732122"
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Paulo Ribeiro", CPF "65498732122", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10-05-1969"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que a data de nascimento deve estar no formato dd/mm/aaaa


  # RN4 – Não é permitido cadastrar um paciente com CPF já existente no sistema

  Scenario: CPF ainda não cadastrado #Sucesso
    Given que não existe paciente cadastrado com o CPF "65498732122"
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Paulo Ribeiro", CPF "65498732122", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1969"
    Then o sistema deve criar o paciente
    And exibir confirmação de sucesso

  Scenario: CPF já cadastrado
    Given que já existe um paciente cadastrado com o CPF "65498732122"
    When a funcionária "Juliana" solicitar o cadastro do paciente com nome "Renato Ribeiro", CPF "65498732122", telefone "81990002222", endereço "Rua B, 123" e data de nascimento "10/05/1997"
    Then o sistema deve bloquear o cadastro
    And exibir mensagem informando que o CPF já está cadastrado
    
Rule: Alteração de dados de paciente

  # RN1 – O CPF não pode ser alterado
  
  Scenario: Atualização sem alterar o CPF #Sucesso
    Given existe um paciente com CPF "12345678901"
    When a funcionária "Juliana" solicitar a atualização dos dados do paciente para nome "Ana Paula Ribeiro Silva", telefone "81990003333", data de nascimento "10/05/1992" e endereço "Rua Nova, 100"
    And mantém o CPF "12345678901" inalterado
    Then o sistema deve salvar as alterações
    And exibir confirmação de sucesso

  Scenario: Tentativa de alterar o CPF
    Given existe um paciente com CPF "12345678901"
    When a funcionária "Juliana" tentar alterar o CPF para "10987654321"
    And mantém os demais dados inalterados
    Then o sistema deve bloquear a atualização
    And exibir mensagem informando que o CPF não pode ser alterado

  # RN2 – A alteração não pode ser feita sem os dados obrigatórios preenchidos
  
  Scenario: Atualização com todos os campos obrigatórios #Sucesso
    Given existe um paciente com CPF "22233344455"
    When a funcionária "Juliana" solicita alteração dos dados para nome "Marina Silva", telefone "81998887777" e data de nascimento "15/04/1990"
    And mantém o CPF "22233344455" inalterado
    Then o sistema deve salvar as alterações
    And exibir confirmação de sucesso

  Scenario: Falta de nome na atualização
    Given existe um paciente com CPF "22233344455"
    When a funcionária "Juliana" solicita alteração dos dados para telefone "81998888888"
    And deixa o campo nome em branco
    And mantém os demais dados inalterados
    Then o sistema deve bloquear a atualização
    And exibir mensagem indicando que nome, CPF, telefone e data de nascimento são obrigatórios

  Scenario: Falta de CPF na atualização
    Given existe um paciente com CPF "22233344455"
    When a funcionária "Juliana" solicita alteração dos dados para nome "Marina Silva", telefone "81998887777" e data de nascimento "15/04/1990"
    And deixa o campo CPF em branco
    Then o sistema deve bloquear a atualização
    And exibir mensagem indicando que nome, CPF, telefone e data de nascimento são obrigatórios

  Scenario: Falta de telefone na atualização
    Given existe um paciente com CPF "22233344455"
    When a funcionária "Juliana" solicita alteração dos dados para nome "Marina Silva" e data de nascimento "15/04/1990"
    And deixa o campo telefone em branco
    And mantém o CPF "22233344455" inalterado
    Then o sistema deve bloquear a atualização
    And exibir mensagem indicando que nome, CPF, telefone e data de nascimento são obrigatórios

  Scenario: Falta de data de nascimento na atualização
    Given existe um paciente com CPF "22233344455"
    When a funcionária "Juliana" solicita alteração dos dados para nome "Marina Silva" e telefone "81998887777"
    And deixa o campo data de nascimento em branco
    And mantém o CPF "22233344455" inalterado
    Then o sistema deve bloquear a atualização
    And exibir mensagem indicando que nome, CPF, telefone e data de nascimento são obrigatórios

  # RN3 – A data de nascimento deve estar no formato dd/mm/aaaa
  
  Scenario: Data de nascimento no formato válido #Sucesso
    Given existe um paciente com CPF "98765432100"
    When a funcionária "Juliana" solicita a atualização dos dados do paciente para nome "Carlos Almeida", telefone "81991112222", data de nascimento "07/03/1988" e endereço "Rua 1"
    Then o sistema deve salvar as alterações
    And exibir confirmação de sucesso

  Scenario: Bloqueio por formato de data inválido
    Given existe um paciente com CPF "98765432100"
    When a funcionária "Juliana" solicitar a atualização dos dados do paciente para nome "Carlos Almeida", telefone "81991112222", data de nascimento "07-03-1988" e endereço "Rua 1"
    Then o sistema deve bloquear a atualização
    And exibir mensagem informando que a data de nascimento deve estar no formato dd/mm/aaaa

Rule: Remoção de paciente

  # RN1 – Não é possível remover paciente inexistente
  
  Scenario: Remoção de paciente existente #Sucesso
    Given existe um paciente com CPF "12345678901"
    And o paciente não possui prontuário
    And o paciente não possui consultas agendadas (históricas ou futuras)
    And o paciente não possui exames agendados (históricos ou futuros)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve remover o paciente
    And exibir confirmação de sucesso

  Scenario: Tentativa de remover paciente inexistente
    Given que não existe paciente com o CPF "00000000000"
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve bloquear a remoção
    And exibir mensagem informando que o paciente não existe

  # RN2 – Não é possível remover paciente com prontuário
  
  Scenario: Remoção sem prontuário vinculado #Sucesso
    Given existe um paciente com CPF "12345678902"
    And o paciente não possui prontuário
    And o paciente não possui consultas agendadas (históricas ou futuras)
    And o paciente não possui exames agendados (históricos ou futuros)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve remover o paciente
    And exibir confirmação de sucesso
    
  Scenario: Bloqueio por prontuário existente 
    Given existe um paciente com CPF "12345678903"
    And o paciente possui prontuário eletrônico
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve bloquear a remoção
    And exibir mensagem informando que pacientes com prontuário não podem ser removidos

  # RN3 – Não é possível remover paciente com consultas agendadas
  
  Scenario: Remoção sem consultas agendadas #Sucesso
    Given existe um paciente com CPF "12345678904"
    And o paciente não possui consultas agendadas (históricas ou futuras)
    And o paciente não possui prontuário
    And o paciente não possui exames agendados (históricos ou futuros)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve remover o paciente
    And exibir confirmação de sucesso
    
  Scenario: Bloqueio por consultas agendadas
    Given existe um paciente com CPF "12345678905"
    And o paciente possui consulta agendada (histórica ou futura)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve bloquear a remoção
    And exibir mensagem informando que pacientes com consultas agendadas não podem ser removidos

  # RN4 – Não é possível remover paciente com exames agendados
  
  Scenario: Remoção sem exames agendados #Sucesso
    Given existe um paciente com CPF "12345678906"
   	And o paciente não possui consultas agendadas (históricas ou futuras)
    And o paciente não possui prontuário
    And o paciente não possui exames agendados (históricos ou futuros)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve remover o paciente
    And exibir confirmação de sucesso
    
  Scenario: Bloqueio por exames agendados
    Given existe um paciente com CPF "12345678907"
    And o paciente possui exame agendado (histórico ou futuro)
    When a funcionária "Juliana" solicitar a remoção do cadastro
    Then o sistema deve bloquear a remoção
    And exibir mensagem informando que pacientes com exames agendados não podem ser removidos