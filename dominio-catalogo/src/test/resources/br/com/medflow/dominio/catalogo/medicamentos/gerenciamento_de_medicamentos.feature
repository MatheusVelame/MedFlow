Feature: Gerenciamento de Medicamentos

# 1. Cadastro de medicamentos

# Regra de Negócio: O nome do medicamento deve ser único no sistema, não sendo permitido cadastrar dois medicamentos com o mesmo nome

Scenario: Tentativa de cadastrar um medicamento com nome ainda não registrado

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Paracetamol" ainda não está registrado no sistema
    When o usuário "Dr. Carlos" tentar cadastrar um novo medicamento de nome "Paracetamol"
    And o uso principal é "Analgésico e antitérmico"
    Then o sistema deve registrar o medicamento com sucesso
    And uma entrada de histórico deve ser criada, registrando a criação do medicamento e o "Dr. Carlos" como responsável

Scenario: Tentativa de cadastrar um medicamento com nome já em uso
	
	Given que o usuário "Dra. Juliana" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Amoxicilina" já está registrado no sistema
    When o usuário "Dra. Juliana" tentar cadastrar um novo medicamento com o nome "Amoxicilina"
    Then o sistema deve impedir o cadastro do medicamento
    And o sistema deve informar que o nome do medicamento já está em uso
    And o histórico não deve ser atualizado

# Regra de Negócio: Todo medicamento cadastrado deve ter o status inicial definido automaticamente como “Ativo”

Scenario: Verificação do status padrão em um novo cadastro

	Given que o usuário "Dra. Juliana" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Ibuprofeno" ainda não está registrado no sistema
	When o usuário "Dra. Juliana" tentar cadastrar um novo medicamento de nome "Ibuprofeno"
	And o uso principal é "Anti-inflamatório"
	And as contraindicações são "Úlcera gástrica"
	Then o sistema deve registrar o medicamento com sucesso
	And o Status do medicamento recém-cadastrado deve ser automaticamente definido como "Ativo"
	
Scenario: Sistema ignora a tentativa de definir um status inicial diferente de "Ativo" ( AVERIGUAR )

	Given que o usuário "Dra. Juliana" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Paracetamol" ainda não está registrado no sistema
    When o usuário "Dra. Juliana" tentar cadastrar um novo medicamento de nome "Paracetamol"
    And o uso principal é "Analgésico e antitérmico"
    And o sistema recebe uma tentativa de definir a requisição do Status inicial como "Inativo"
    Then o sistema deve registrar o medicamento com sucesso
    And o Status do medicamento deve ser "Ativo" independentemente do valor fornecido na requisição inicial

# Regra de Negócio: Os campos Nome do medicamento e Uso principal são obrigatórios. O sistema não deve permitir o cadastro caso estejam em branco ou inválidos

Scenario: Cadastro de medicamento com todos os campos obrigatórios preenchidos

	Given que o usuário "Dr. Alberto" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Dipirona" ainda não está registrado no sistema
    When o usuário "Dr. Alberto" tentar cadastrar um novo medicamento de nome "Dipirona"
    And o uso principal é "Analgésico e antitérmico"
    Then o sistema deve registrar o medicamento com sucesso

Scenario: Tentativa de cadastro sem preencher o nome do medicamento

	Given que o usuário "Dr. Alberto" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And um novo medicamento está sendo cadastrado
	When o usuário "Dr. Alberto" tentar cadastrar o nome do medicamento como ""
	And o uso principal é "Analgésico"
	Then o sistema deverá informar que o nome é obrigatório
	And o medicamento não deve ser cadastrado no sistema

# Regra de Negócio: O Contraindicações é opcional, mas quando preenchido deve aceitar apenas texto (sem anexos ou caracteres especiais inválidos), servindo para registrar informações adicionais relevantes sobre o medicamento

Scenario: Cadastro de medicamento com Contraindicações válidas

	Given que o usuário "Dr. Alberto" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Amoxicilina" ainda não está registrado no sistema
	When o usuário "Dr. Alberto" tentar cadastrar um novo medicamento de nome "Amoxicilina"
	And o uso principal é "Antibiótico"
	And as contraindicações são "Alergia à penicilina"
	Then o sistema deve registrar o medicamento com sucesso

Scenario: Tentativa de incluir caracteres especiais inválidos no campo Contraindicações

	Given que o usuário "Dra. Letícia" tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar medicamentos
	And o medicamento "Loratadina" ainda não está registrado no sistema
	When o usuário "Dra. letícia" tentar cadastrar um novo medicamento de nome "Loratadina"
	And o uso principal é "Antialérgico"
	And as contraindicações são "@Gravidez e %lactação%"
	Then o sistema deve informar que há caracteres inválidos nas contraindicações
	And o novo medicamento não deve ser cadastrado

# 2. Atualização de medicamentos

# Regra de Negócio: Apenas usuários com permissão podem alterar dados (ex: médicos ou administradores)

Scenario: Atualização de Uso Principal com Sucesso

	Given que o usuário "Dr. Carlos", médico da clínica, tem permissão de administrador
	And o perfil "Administrador" tem permissão para atualizar medicamentos
	And o medicamento "Paracetamol" está cadastrado com o uso principal "Analgésico"
	When o "Dr. Carlos" atualizar o uso principal do medicamento "Paracetamol" para "Analgésico e antitérmico"
	Then o sistema deve registrar a alteração com sucesso
	And uma entrada de histórico deve ser criada, registrando a data da alteração e o "Dr. Carlos" como responsável
	
Scenario: Falha na Atualização por Falta de Permissão

	Given que o usuário "Ana", funcionária da recepção, não tem permissão para alterar dados de medicamentos
	And o medicamento "Amoxicilina" já está registrado no sistema
	When a "Ana" tentar atualizar as informações do medicamento "Amoxicilina"
	Then o sistema deverá informar que o usuário não tem permissão
	And a alteração não deve ser realizada
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Sistema deve manter histórico de alterações, registrando data da alteração e responsável
	
Scenario: Atualização de Status de Ativo para Inativo e registrando alteração no histórico
	
	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o perfil "Administrador" tem permissão para atualizar medicamentos
	And o medicamento "Dipirona" está cadastrado com o status "Ativo"
	When o "Dr. Carlos" mudar o status do medicamento "Dipirona" para "Inativo"
	Then o sistema deve registrar a alteração com sucesso
	And o status do medicamento deve ser "Inativo"
	And uma entrada de histórico deve ser criada, registrando a data da alteração e o "Dr. Carlos" como responsável
	
Scenario: Tentativa de atualização em branco de Uso principal, sem registro de alteração no histórico

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o perfil "Administrador" tem permissão para atualizar medicamentos
	And o medicamento "Paracetamol" está cadastrado com o uso principal "Analgésico"
	When o "Dr. Carlos" atualizar o uso principal do medicamento "Paracetamol" para ""
	Then o sistema deve informar que não é permitido alterar campos obrigatórios para valor em branco
	And a alteração não deve ser realizada
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Alterações críticas, como contraindicações, devem ser revisadas por um responsável antes que a alteração seja aplicada

Scenario: Scenario: Alteração crítica entra em status de revisão

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o perfil "Administrador" tem permissão para atualizar medicamentos
	And o medicamento "Aspirina" está cadastrado com as contraindicações "Hipersensibilidade"
	When o "Dr. Carlos" tentar adicionar a contraindicação "Gravidez" no medicamento "Aspirina"
	Then o sistema deve informar sobre alteração crítica no sistema
	And o sistema deve registrar a alteração como "Pendente de Revisão"
	And o campo "Contraindicações" do medicamento deve permanecer inalterado (em "Hipersensibilidade")
	And uma entrada de histórico deve ser criada, registrando a criação do medicamento e o "Dr. Carlos" como responsável
	
Scenario: Alteração crítica aplicada com sucesso após aprovação

	Given que o medicamento "Aspirina" está cadastrado com uma alteração pendente de revisão em Contraindicações
	And a alteração pendente é a adição "Risco em pacientes com Dengue"
	And o usuário "Dra. Helena" tem permissão de revisor
	When a "Dra. Helena" aprovar a alteração pendente do medicamento "Aspirina"
	Then o sistema deve registrar a aprovação da alteração com sucesso
	And o campo "Contraindicações" do medicamento deve ser atualizado com a nova informação adicionada
	And o status de revisão da alteração deve ser mudado para "Aprovada"
	And o histórico deve ser atualizado com a decisão de "Aprovação" e a responsável "Dra. Helena"
	And o sistema deve notificar o solicitante "Dr. Carlos" sobre a aprovação

Scenario: Falha na aprovação de alteração crítica por falta de permissão

	Given que o medicamento "Sertralina" está cadastrado com uma alteração pendente de revisão em Contraindicações
	And a alteração pendente é a adição "Risco de Síndrome Serotoninérgica"
	And o usuário "Técnico João", funcionário do TI, não tem permissão de revisor
	When o "Técnico João" tentar aprovar a alteração pendente do medicamento "Sertralina"
	Then o sistema deverá informar que o usuário não tem permissão para aprovar alterações críticas
	And a alteração não deve ser aplicada às "Contraindicações" do medicamento
	And o status de revisão da alteração deve permanecer como "Pendente de Revisão"
	And o histórico não deve ser atualizado com a aprovação do "Técnico João"
	
# 3. Deletar/Arquivar medicamentos
 
# Regra de Negócio: Remoção ou arquivamento deve ser restrita a perfis de alta autoridade no sistema

Scenario: Arquivamento Realizado por Usuário com Permissão de Alta Autoridade

	Given que o usuário "Dr. Helena" tem o perfil de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Buscopan" está cadastrado com o status "Ativo"
	And o medicamento "Buscopan" não está vinculado a nenhuma prescrição ativa
	When o usuário "Dr. Helena" arquivar o medicamento "Buscopan"
	Then o sistema deve arquivar o medicamento com sucesso
	And o status do medicamento "Buscopan" deve ser alterado para "Arquivado"
	
Scenario: Falha na Tentativa de Arquivamento por Usuário sem Permissão

	Given que o usuário "Enfermeiro João" tem o perfil de "Enfermeiro"
	And o perfil "Enfermeiro" não tem permissão para arquivar ou remover medicamentos
	And o medicamento "Cefalexina" está cadastrado com o status "Ativo"
	And o medicamento "Cefalexina" não está vinculado a nenhuma prescrição ativa
	When o usuário "Enfermeiro João" tentar arquivar o medicamento "Cefalexina"
	Then o sistema deverá informar que o usuário não tem permissão para realizar esta ação
	And o sistema deve bloquear a tentativa de arquivamento do medicamento
	And o histórico não deve ser atualizado
	
# Regra de Negócio: É preferível a ação de arquivar um medicamento, ao invés de excluí-lo, para manter um registro de seu histórico

Scenario: Arquivamento de Medicamento com Sucesso para manter uma Preservação de Histórico

	Given que o usuário "Dr. Helena" tem permissão de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Amoxicilina" está cadastrado com o status "Ativo"
	And o medicamento "Amoxicilina" não está vinculado a nenhuma prescrição ativa
	When o usuário "Dr. Helena" acionar a opção de arquivar o medicamento "Amoxicilina"
	Then o status do medicamento "Amoxicilina" deve ser alterado para "Arquivado"
	And o histórico de uso e alterações do medicamento deve ser integralmente preservado
	And uma entrada de histórico deve ser criada, registrando o arquivamento e a "Dr. Helena" como responsável
	
Scenario: Tentativa de remover um medicamento

	Given que o usuário "Dr. Carlos" tem permissão de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Diclofenaco" está cadastrado com o status "Arquivado"
	And o sistema prioriza o arquivamento sobre a exclusão
	When o "Dr. Carlos" tentar excluir o medicamento "Diclofenaco"	
	Then o sistema deve informar que é sugerido manter o registro arquivado
	And o sistema deve exigir uma justificativa específica para a exclusão permanente
	And o medicamento "Diclofenaco" deve permanecer "Arquivado" até que a justificativa seja fornecida e aprovada por um responsável

# Regra de Negócio: Não é permitido remover ou arquivar medicamentos vinculados a prescrições ativas

Scenario: Arquivamento de Medicamento com Sucesso

	Given que o usuário "Dr. Helena" tem permissão de administrador
	And o perfil "Administrador" tem permissão para arquivar ou remover medicamentos
	And o medicamento "Amoxicilina" está cadastrado com o status "Ativo"
	And o medicamento "Amoxicilina" não está vinculado a nenhuma prescrição ativa
	When o usuário "Dr. Helena" arquivar o medicamento "Amoxicilina"
	Then o sistema deve registrar a alteração com sucesso
	And o status do medicamento "Amoxicilina" deve ser alterado para "Arquivado"
	And o medicamento "Amoxicilina" deve ser movido para a lista de medicamentos arquivados
	And uma entrada de histórico deve ser criada, registrando a data do arquivamento e a "Dr. Helena" como responsável
	
Scenario: Falha na Remoção/Arquivamento Devido a Vínculo Ativo

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o perfil "Administrador" tem permissão para arquivar ou remover medicamentos
	And o medicamento "Dipirona" está cadastrado com o status "Ativo"
	And o medicamento "Dipirona" está vinculado a uma prescrição ativa
	When o "Dr. Carlos" tentar arquivar ou remover o medicamento "Dipirona"
	Then o sistema deverá informar que a ação não pode ser realizada devido a vínculos com prescrições ativas
	And o status do medicamento "Dipirona" deve permanecer "Ativo"
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Medicamentos arquivados não devem aparecer nas listas de consulta padrão, mas podem ser acessados via filtros específicos

Scenario: Ocultação de Medicamento Arquivado na Lista Padrão

	Given que o medicamento "Sertralina" está cadastrado com o status "Arquivado"
	And o usuário "Gabriel", funcionário da recepção, possui acesso a lista de medicamentos
	When o usuario "Gabriel" pesquisar pelo o medicamento "Sertralina" na lista padrão
	Then o sistema deve informar que o medicamento não existe na lista padrão
	And nenhum medicamento deverá ser acessado
	
Scenario: Exibição de Medicamento Arquivado em Consulta Filtrada

	Given que o medicamento "Sertralina" está cadastrado com o status "Arquivado"
	And o usuário "Dr. Carlos" possui acesso a lista de medicamentos
	When o usuário "Dr. Carlos" ativar o filtro "Medicamentos Arquivados" na lista de medicamentos
	Then o sistema deve acessar o medicamento arquivado "Sertralina"
	And o status do medicamento deve ser claramente indicado como "Arquivado"