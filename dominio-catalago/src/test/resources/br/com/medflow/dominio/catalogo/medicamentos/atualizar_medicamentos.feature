feature: Atualizar Medicamentos

# Regra de Negócio: Apenas usuários com permissão podem alterar dados (ex: médicos ou administradores)

Scenario: Atualização de Uso Principal com Sucesso

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o medicamento "Paracetamol" está cadastrado com o uso "Analgésico"
	When o "Dr. Carlos" atualizar o uso principal para "Analgésico e antitérmico"
	Then o sistema deve registrar a alteração com sucesso
	And uma entrada de histórico deve ser criada, registrando a data da alteração e o "Dr. Carlos" como responsável
	
Scenario: Falha na Atualização por Falta de Permissão

	Given que o usuário "Ana", funcionária da recepção, não tem permissão para alterar dados de medicamentos
	And o medicamento "Amoxicilina" está cadastrado
	When a "Ana" tentar atualizar as informações da "Amoxicilina"
	Then o sistema deverá informar que o usuário não tem permissão
	And a alteração não deve ser realizada
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Sistema deve manter histórico de alterações, registrando data da alteração e responsável
	
Scenario: Atualização de Status de Ativo para Inativo e registrando alteração no histórico
	
	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o medicamento "Dipirona" está com o status "Ativo"
	When o "Dr. Carlos" mudar o status do medicamento para "Inativo"
	Then o sistema deve registrar a alteração com sucesso
	And o status do medicamento deve ser "Inativo"
	And uma entrada de histórico deve ser criada, registrando a data da alteração e o "Dr. Carlos" como responsável
	
Scenario: tentativa de atualização de Uso principal do medicamento e não registrando alteração no histórico

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o medicamento "Paracetamol" está cadastrado com o uso "Analgésico"
	When o "Dr. Carlos" atualizar o uso principal para ""
	Then o sistema informar que não é permitido alterar campos obrigatórios para valor em branco
	And a alteração não deve ser realizada
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Alterações críticas, como contraindicações, devem ser revisadas por um responsável antes que a alteração seja aplicada

Scenario: Scenario: Alteração Crítica Entra em Status de Revisão

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o medicamento "Aspirina" está cadastrado
	And as "Contraindicações" atuais são "Hipersensibilidade"
	When o "Dr. Carlos" tentar adicionar a contraindicação "Gravidez"
	Then o sistema deve informar sobre alteração crítica no sistema
	And o sistema deve registrar a alteração como "Pendente de Revisão"
	And o campo "Contraindicações" do medicamento deve permanecer inalterado (em "Hipersensibilidade")
	And uma entrada de histórico deve ser criada, registrando a solicitação de alteração e o "Dr. Carlos" como responsável
	
Scenario: Alteração Crítica Aplicada com Sucesso Após Aprovação

	Given que o medicamento "Aspirina" tem uma alteração pendente de revisão em Contraindicações
	And a alteração pendente é a adição de "Risco em pacientes com Dengue"
	And o usuário "Dra. Helena" tem permissão de revisor
	When a "Dra. Helena" aprovar a alteração pendente
	Then o sistema deve registrar a aprovação com sucesso
	And o campo "Contraindicações" do medicamento deve ser atualizado com a nova informação adicionada
	And o status de revisão da alteração deve ser mudado para "Aprovada"
	And o histórico deve ser atualizado com a decisão de "Aprovação" e a responsável "Dra. Helena"
	And o sistema deve notificar o solicitante "Dr. Carlos" sobre a aprovação.

Scenario: Falha na Aprovação de Alteração Crítica por Falta de Permissão

	Given que o medicamento "Sertralina" tem uma alteração pendente de revisão em Contraindicações
	And a alteração pendente é a adição de "Risco de Síndrome Serotoninérgica"
	And o usuário "Técnico João", funcionário do TI, não tem permissão de revisor
	When o "Técnico João" tentar aprovar a alteração pendente
	Then o sistema deverá informar que o usuário não tem permissão para aprovar alterações críticas
	And a alteração não deve ser aplicada às "Contraindicações" do medicamento
	And o status de revisão da alteração deve permanecer como "Pendente de Revisão"
	And o histórico não deve ser atualizado com a aprovação do "Técnico João"