@medico
Feature: Gerenciamento de Médicos - AV1
  Como funcionário da clínica, quero gerenciar médicos, garantindo a integridade dos dados e as regras de imutabilidade.

  # ====================================================================
# 1. Cadastro de Médicos
# ====================================================================

  Scenario: RN1 Cadastro - Sucesso – Cadastro com todos os campos obrigatórios preenchidos
    Given o funcionário da clínica informa o nome "Carlos Andrade", o ID "11122233344", o CRM "12345-PE", a data de nascimento "10/05/1980", contato "81999998888", o e-mail "dr.carlos.andrade@gmail.com" e a especialidade "Dermatologia"
    When solicitar o cadastro do médico
    Then o sistema deve criar o cadastro do médico
    And definir o status inicial como "ATIVO"
    And exibir uma mensagem de confirmação de sucesso

  Scenario: RN1 Cadastro - Insucesso – Bloqueio por ausência de um campo obrigatório (Nome)
    Given o funcionário da clínica informa o nome "", o ID "22233344455", o CRM "54321-PE", a data de nascimento "15/03/1975", contato "81987648735", o e-mail "dra.alicepaiva@gmail.com" e a especialidade "Pediatria"
    When solicitar o cadastro do médico
    Then o sistema deve bloquear o cadastro do médico
    And exibir a mensagem de erro: "Nome completo é obrigatório"

  Scenario: RN2 Cadastro - Sucesso – Cadastro com data de nascimento no formato válido
    Given o funcionário informa o nome "Dra. Helena Souza", o ID "44455566677", o CRM "55667-MG", a data de nascimento "20/11/1990", o contato "31977776666", o e-mail "helena.souza@clinica.com" e a especialidade "Gastroenterologia"
    When solicitar o cadastro do médico
    Then o sistema deve aceitar a data e criar o cadastro

  Scenario: RN2 Cadastro - Insucesso – Bloqueio por data de nascimento com formato inválido
    Given o funcionário informa o nome "Dr. Ricardo Alves", o ID "55566677788", o CRM "77889-BA", a data de nascimento "1990-11-20", o contato "71966665555", o e-mail "ricardo.alves@clinica.com" e a especialidade "Psiquiatria"
    When solicitar o cadastro do médico
    Then o sistema deve bloquear o cadastro do médico
    And exibir a mensagem de erro: "formato"

# ====================================================================
# 2. Atualização de Dados de Médicos
# ====================================================================

  Scenario: RN1 Atualização - Sucesso – Atualização de outros dados mantendo o CRM inalterado
    Given que o administrador acessa o perfil do médico "Carlos Andrade" cadastrado com o ID "11122233344" e CRM "12345-PE"
    When ele altera o contato para "81999990000" e o e-mail para "c.andrade.novo@clinica.com"
    Then o sistema deve salvar as alterações com sucesso
    And e o CRM do médico deve permanecer "12345-PE"

  Scenario: RN1 Atualização - Insucesso – Bloqueio ao tentar alterar o CRM
    Given que o administrador acessa o perfil do médico "Carlos Andrade" cadastrado com o ID "11122233344" e CRM "12345-PE"
    When ele tenta alterar o valor do CRM para "99999-PE"
    Then o sistema deve bloquear a alteração
    And exibir a mensagem de erro: "O CRM não pode ser alterado após o cadastro inicial"

  Scenario: RN2 Atualização - Sucesso – Atualização de nome e especialidade com sucesso
    Given que o administrador acessa o perfil do médico "Beatriz Lima", cujos dados são: ID "12345678901", CRM "11223-RJ", data de nascimento "25/07/1988", contato "21999998888", e-mail "beatriz.lima@clinica.com" e especialidade "Pediatria"
    When ele altera o nome para "Dra. Beatriz Lima de Souza" e a especialidade para "Clínico Geral"
    Then o sistema deve salvar as alterações com sucesso
    And registrar o usuário responsável pela alteração

  Scenario: RN2 Atualização - Insucesso – Bloqueio por inserir dados em formato inválido na atualização
    Given que o administrador acessa o perfil do médico "Beatriz Lima de Souza", cujos dados são: ID "12345678901", CRM "11223-RJ", data de nascimento "25/07/1988", contato "21999998888", e-mail "beatriz.lima@clinica.com" e especialidade "Clínico Geral"
    When ele tenta alterar a data de nascimento para formato inválido "1990/03/25"
    Then o sistema deve bloquear a alteração
    And exibir a mensagem de erro: "formato da data de nascimento é inválido"

  Scenario: RN3 Atualização - Insucesso – Bloqueio ao tentar alterar disponibilidade em período com consulta futura agendada
    Given que o administrador acessa o perfil do médico "Dra. Helena Souza", cujos dados são: ID "44455566677", CRM "55667-MG", data de nascimento "20/11/1990", contato "31977776666", e-mail "helena.souza@clinica.com" e especialidade "Cardiologia"
    And a médica tem uma consulta agendada com o paciente "José da Silva" na próxima quarta-feira às 10h
    When o administrador tenta marcar toda a manhã de quarta-feira incluindo o horário das 10h como indisponível
    Then o sistema deve bloquear a alteração
    And exibir a mensagem de erro: "consulta agendada"

# ====================================================================
# 3. Consulta de Dados de Médicos
# ====================================================================#

  Scenario: RN1 Consulta - Sucesso – Busca por CRM exato
    Given que existem os seguintes médicos ativos cadastrados: "Dr. André Vasconcelos" com CRM "77777-SC", "Dra. Patrícia Medeiros" com CRM "88888-PR" e "Dra. Gabriela Lima" com CRM "99999-GO"
    When o funcionário digita o CRM "88888-PR" no campo de busca principal
    Then a lista deve exibir apenas o registro da "Dra. Patrícia Medeiros"

  Scenario: RN1 Consulta - Insucesso – Busca por um termo que não corresponde a nenhum médico
    Given que a base de médicos está populada com 3 médicos ativos
    When o funcionário digita o CRM "00000-XX" no campo de busca
    Then o sistema deve exibir uma lista vazia
    And exibir a mensagem de erro na consulta: "Nenhum resultado encontrado"

# ====================================================================
# 4. Exclusão de Dados de Médicos
# ====================================================================#

  Scenario: RN1 Exclusão - Sucesso - Exclusão de médico sem vínculos
    Given que existe um médico cadastrado com CRM "123456"
    And o médico não possui consultas futuras
    And o médico não possui prontuários vinculados
    When o administrador solicita a exclusão do médico
    Then o sistema deve remover o médico
    And exibir mensagem de exclusão bem-sucedida

  Scenario: RN1 Exclusão - Insucesso - Tentativa de exclusão de médico com consultas futuras
    Given que existe um médico cadastrado com CRM "789012"
    And o médico possui consultas agendadas
    When o administrador solicita a exclusão do médico
    Then o sistema deve impedir a exclusão
    And exibir mensagem informando que médicos com consultas futuras não podem ser removidos