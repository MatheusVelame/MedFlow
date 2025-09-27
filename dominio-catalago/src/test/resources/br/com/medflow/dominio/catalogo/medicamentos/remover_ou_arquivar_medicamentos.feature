feature: Remover ou Arquivar Medicamentos

# Regra de Negócio: Remoção ou arquivamento deve ser restrita a perfis de alta autoridade no sistema

Scenario: Arquivamento Realizado por Usuário com Permissão de Alta Autoridade

	Given que o usuário "Dr. Helena" tem o perfil de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Buscopan" está com o status "Ativo"
	And o medicamento "Buscopan" não está vinculado a nenhuma prescrição ativa
	When a "Dr. Helena" arquivar o medicamento "Buscopan"
	Then o sistema deve arquivar o medicamento com sucesso
	And o status do medicamento "Buscopan" deve ser alterado para "Arquivado"
	
Scenario: Falha na Tentativa de Arquivamento por Usuário sem Permissão

	Given que o usuário "Enfermeiro João" tem o perfil de "Enfermeiro"
	And o perfil "Enfermeiro" não tem permissão para arquivar ou remover medicamentos
	And o medicamento "Cefalexina" está com o status "Ativo"
	And o medicamento "Cefalexina" não está vinculado a nenhuma prescrição ativa
	When o "Enfermeiro João" tentar arquivar o medicamento "Cefalexina"
	Then o sistema deverá informar que o usuário não tem permissão para realizar esta ação
	And o sistema deve bloquear a tentativa de arquivamento do medicamento
	And o histórico não deve ser atualizado
	
# Regra de Negócio: É preferível a ação de arquivar um medicamento, ao invés de excluí-lo, para manter um registro de seu histórico

Scenario: Arquivamento de Medicamento com Sucesso para manter uma Preservação de Histórico

	Given que o usuário "Dr. Helena" tem permissão de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Amoxicilina" está com o status "Ativo"
	And o medicamento "Amoxicilina" não está vinculado a nenhuma prescrição ativa
	When a "Dr. Helena" acionar a opção de arquivar o medicamento "Amoxicilina"
	Then o status do medicamento "Amoxicilina" deve ser alterado para "Arquivado"
	And o histórico de uso e alterações do medicamento deve ser integralmente preservado
	And uma entrada de histórico deve ser criada, registrando o arquivamento e a "Dr. Helena" como responsável
	
Scenario: Tentativa de remover um medicamento

	Given que o usuário "Dr. Carlos" tem permissão de "Administrador Sênior"
	And o perfil "Administrador Sênior" tem permissão para arquivar medicamentos
	And o medicamento "Diclofenaco" está com o status "Arquivado"
	And o sistema prioriza o arquivamento sobre a exclusão
	When o "Dr. Carlos" tentar excluir o medicamento "Diclofenaco"	
	Then o sistema deve informar que é sugerido manter o registro arquivado
	And o sistema deve exigir uma justificativa específica para a exclusão permanente
	And o medicamento "Diclofenaco" deve permanecer "Arquivado" até que a justificativa seja fornecida e aprovada

# Regra de Negócio: Não é permitido remover ou arquivar medicamentos vinculados a prescrições ativas

Scenario: Arquivamento de Medicamento com Sucesso

	Given que o usuário "Dr. Helena" tem permissão de administrador
	And o medicamento "Amoxicilina" está com o status "Ativo"
	And o medicamento "Amoxicilina" não está vinculado a nenhuma prescrição ativa
	When a "Dr. Helena" arquivar o medicamento "Amoxicilina"
	Then o sistema deve registrar a alteração com sucesso
	And o status do medicamento "Amoxicilina" deve ser alterado para "Arquivado"
	And o medicamento "Amoxicilina" não deve aparecer na lista de medicamentos ativos
	And o medicamento "Amoxicilina" deve aparecer na lista de medicamentos arquivados
	And uma entrada de histórico deve ser criada, registrando a data do arquivamento e a "Dr. Helena" como responsável
	
Scenario: Falha na Remoção/Arquivamento Devido a Vínculo Ativo

	Given que o usuário "Dr. Carlos" tem permissão de administrador
	And o medicamento "Dipirona" está com o status "Ativo"
	And o medicamento "Dipirona" está vinculado a uma prescrição ativa
	When o "Dr. Carlos" tentar arquivar ou remover o medicamento "Dipirona"
	Then o sistema deverá informar que a ação não pode ser realizada devido a vínculos com prescrições ativas
	And o status do medicamento "Dipirona" deve permanecer "Ativo"
	And o histórico não deve ser atualizado
	
# Regra de Negócio: Medicamentos arquivados não devem aparecer nas listas de consulta padrão, mas podem ser acessados via filtros específicos

Scenario: Ocultação de Medicamento Arquivado na Lista Padrão

	Given que o medicamento "Sertralina" está com o status "Arquivado"
	And a funcionária da recepção "Ana" acessa a lista padrão de medicamentos ativos
	When a "Ana" pesquisar por "Sertralina" na lista padrão
	Then o sistema não deve listar o medicamento "Sertralina"
	And o sistema deve listar apenas medicamentos com o status "Ativo"
	
Scenario: Exibição de Medicamento Arquivado em Consulta Filtrada

	Given que o medicamento "Sertralina" está com o status "Arquivado"
	And o usuário "Dr. Carlos" tem permissão para visualizar arquivos
	When o "Dr. Carlos" filtrar a lista para "Mostrar medicamentos arquivados" e pesquisar por "Sertralina"
	Then o sistema deve lsitar o medicamento "Sertralina"
	And o status do medicamento deve ser claramente indicado como **"Arquivado"**