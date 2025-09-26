feature: Cadastro de Medicamentos

# Regra de Negócio: O nome do medicamento deve ser único no sistema, não sendo permitido cadastrar dois medicamentos com o mesmo nome.

Scenario: Tentativa de cadastrar um medicamento com nome ainda não registrado

	Given que o medicamento "Paracetamol" ainda não está registrado no sistema
    When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Paracetamol"
    And o uso principal é "Analgésico e antitérmico"
    Then o sistema deve registrar o medicamento com sucesso

Scenario: Tentativa de cadastrar um medicamento com nome já em uso
	
	Given que o medicamento "Amoxicilina" já está registrado no sistema
    When o funcionário autorizado tenta cadastrar um novo medicamento com o nome "Amoxicilina"
    Then o sistema deve impedir o cadastro do medicamento
    And o sistema deve informar que o nome do medicamento já está em uso

# Regra de Negócio: Todo medicamento cadastrado deve ter o status inicial definido automaticamente como “Ativo”

Scenario: Verificação do status padrão em um novo cadastro

	Given que o medicamento "Ibuprofeno" ainda não está registrado no sistema
	When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Ibuprofeno"
	And o uso principal é "Anti-inflamatório"
	And as contraindicações são "Úlcera gástrica"
	Then o sistema deve registrar o medicamento com sucesso
	And o Status do medicamento recém-cadastrado deve ser automaticamente definido como "Ativo"
	
Scenario: Sistema ignora a tentativa de definir um status inicial diferente de "Ativo"

	Given que o medicamento "Paracetamol" ainda não está registrado no sistema
    When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Paracetamol"
    And o uso principal é "Analgésico e antitérmico"
    And o sistema recebe uma tentativa de definir o Status inicial como "Inativo"
    Then o sistema deve registrar o medicamento com sucesso
    And o Status do medicamento deve ser "Ativo" independentemente do valor fornecido na requisição inicial

# Regra de Negócio: Os campos Nome do medicamento e Uso principal são obrigatórios. O sistema não deve permitir o cadastro caso estejam em branco ou inválidos.

Scenario: Cadastro de medicamento com todos os campos obrigatórios preenchidos

	Given que o medicamento "Dipirona" ainda não está registrado no sistema
    When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Dipirona"
    And o uso principal é "Analgésico e antitérmico"
    Then o sistema deve registrar o medicamento com sucesso
    And o Status do medicamento recém-cadastrado deve ser automaticamente definido como "Ativo"

Scenario: Tentativa de cadastro sem preencher o nome do medicamento

	Given que um novo medicamento está sendo cadastrado
	When o funcionário tentar cadastrar o medicamento sem nome
	And o uso principal é "Analgésico"
	Then o sistema deverá informar que o nome é obrigatório
	And o medicamento não deve ser cadastrado no sistema

# Regra de Negócio: O Contraindicações é opcional, mas quando preenchido deve aceitar apenas texto (sem anexos ou caracteres especiais inválidos), servindo para registrar informações adicionais relevantes sobre o medicamento.

Scenario: Cadastro de medicamento com Contraindicações válidas

	Given que o medicamento "Amoxicilina" ainda não está registrado no sistema
	When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Amoxicilina"
	And o uso principal é "Antibiótico"
	And as contraindicações são "Alergia à penicilina"
	Then o sistema deve registrar o medicamento com sucesso

Scenario: Tentativa de incluir caracteres especiais inválidos no campo Contraindicações

	Given que o medicamento "Loratadina" ainda não está registrado no sistema
	When o funcionário autorizado tentar cadastrar um novo medicamento de nome "Loratadina"
	And o uso principal é "Antialérgico"
	And as contraindicações são "@Gravidez e %lactação%"
	Then o sistema deve informar que possui caracteres que não são aceitos em Contraindicações
	And o novo medicamento não deve ser cadastrado