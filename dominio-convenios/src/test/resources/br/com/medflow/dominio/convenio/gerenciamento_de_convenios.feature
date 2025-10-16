feature: Gerenciamento de Convenios

# 1. Cadastro de convênio

# Regra de Negócio: O cadastro de convênios/planos deve conter nome do convênio, código de identificação e status (ativo/inativo)

Scenario: Tentativa de cadastrar um convênio com nome ainda não registrado

	Given que o usuário "Dra. Camila" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And o convênio "Saúde Total" ainda não está registrado no sistema  
	When o usuário "Dra. Camila" tentar cadastrar um novo convênio com o nome "Saúde Total"  
	And o código de identificação "SATL0001"  
	Then o sistema deve registrar o convênio com sucesso  
	And o Status do convênio recém-cadastrado deve ser automaticamente definido como "Ativo"  
	And o sistema deve exibir a mensagem "Convênio cadastrado com sucesso!"  

Scenario: Tentativa de cadastro sem preencher o nome do convênio

	Given que o usuário "Dr. Marcos" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And um novo convênio está sendo cadastrado  
	When o usuário "Dr. Marcos" tentar cadastrar o nome do convênio como ""  
	And o código de identificação é "DJTA0001"  
	Then o sistema deverá informar que o nome do convênio é obrigatório  
	And o convênio não deve ser cadastrado no sistema

# Regra de Negócio: Não é permitido cadastrar identificações de convênios duplicados (código já existente)

Scenario: Cadastro de convênio com código único

	Given que o usuário "Dra. Paula" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And já existe um convênio cadastrado com o código "AMSA0001"  
	When o usuário "Dra. Paula" tentar cadastrar um novo convênio com o nome "Amparo Saúde"  
	And o código de identificação "AMPS0001"  
	Then o sistema deve registrar o convênio com sucesso  
	And o Status do convênio deve ser automaticamente definido como "Ativo"  
	And o sistema deve exibir a mensagem "Convênio cadastrado com sucesso!"  
	
Scenario: Tentativa de cadastro com código de identificação duplicado

	Given que o usuário "Dr. Henrique" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And já existe um convênio cadastrado com o código "VIDA001"  
	When o usuário "Dr. Henrique" tentar cadastrar um novo convênio com o nome "Vida Mais"  
	And o código de identificação "VIDA001"  
	Then o sistema deve impedir o cadastro do convênio  
	And o sistema deve informar que o código de identificação já está em uso  
	And o histórico não deve ser atualizado 

# Regra de Negócio: O convênio recém-cadastrado deve iniciar com status “Ativo” por padrão

Scenario: Verificação do status padrão em um novo cadastro

	Given que o usuário "Dra. Júlia" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And o convênio "Bem Cuidar" ainda não está registrado no sistema  
	When o usuário "Dra. Júlia" tentar cadastrar um novo convênio com o nome "Bem Cuidar"  
	And o código de identificação "BC202"  
	Then o sistema deve registrar o convênio com sucesso  
	And o Status do convênio deve ser automaticamente definido como "Ativo"  
	And o sistema deve exibir a mensagem "Convênio cadastrado com sucesso!"  

Scenario: Sistema ignora a tentativa de definir um status inicial diferente de “Ativo”

	Given que o usuário "Dr. André" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para cadastrar convênios  
	And o convênio "Plano Saúde Premium" ainda não está registrado no sistema  
	When o usuário "Dr. André" tentar cadastrar o novo convênio com o nome "Plano Saúde Premium"  
	And o código de identificação "PSPM001"  
	And o sistema recebe uma tentativa de definir a requisição com Status inicial “Inativo”  
	Then o sistema deve registrar o convênio com sucesso  
	And o Status do convênio deve ser automaticamente definido como “Ativo”, independentemente do valor recebido na requisição  
	And o sistema deve exibir a mensagem "Convênio cadastrado com sucesso!"  

# 2. Remoção de Convênios Inativos

# Regra de Negócio: A exclusão definitiva de um convênio só pode ocorrer se este estiver marcado como “Inativo”

Scenario: Exclusão de convênio com status Inativo

	Given que o usuário "Dr. Rafael" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para excluir convênios  
	And existe um convênio cadastrado com o nome "Saúde Vida"  
	And o código de identificação "SAVA001"  
	And o status do convênio está definido como "Inativo"  
	When o usuário "Dr. Rafael" seleciona o convênio "Saúde Vida"  
	And solicita a exclusão definitiva  
	Then o sistema deve remover o convênio com sucesso  
	And o sistema deve exibir a mensagem "Convênio removido com sucesso!"  
	And o sistema deve registrar a ação no histórico com data, hora e usuário responsável 

Scenario: Tentativa de exclusão de convênio com status Ativo

	Given que o usuário "Dra. Juliana" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para excluir convênios  
	And existe um convênio cadastrado com o nome "Plano Total"  
	And o código de identificação "PATA002"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Dra. Juliana" tenta excluir o convênio  
	Then o sistema deve impedir a exclusão  
	And o sistema deve exibir a mensagem "Somente convênios inativos podem ser removidos."  
	And o histórico de ações não deve ser alterado  

# Regra de Negócio: O sistema deve manter histórico de remoções, registrando data, hora e responsável pela ação

Scenario: Registro de histórico após exclusão bem-sucedida

	Given que o usuário "Carlos Andrade" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para excluir convênios  
	And existe um convênio cadastrado com o nome "Bem Estar"  
	And o código de identificação "BEST003"  
	And o status do convênio está definido como "Inativo"  
	When o usuário "Carlos Andrade" solicita a exclusão definitiva  
	Then o sistema deve remover o convênio com sucesso  
	And o sistema deve exibir a mensagem "Convênio removido com sucesso!"  
	And o sistema deve registrar no histórico a data, hora e o usuário "Carlos Andrade" como responsável pela ação  
	And o registro deve estar disponível para consulta em auditorias futuras  

Scenario: Falha ao registrar histórico após exclusão

	Given que o usuário "Fernanda Lima" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para excluir convênios  
	And existe um convênio cadastrado com o nome "Viva Saúde"  
	And o código de identificação "VASA004"  
	And o status do convênio está definido como "Inativo"  
	When o usuário "Fernanda Lima" solicita a exclusão definitiva  
	And o sistema não registra a ação no histórico  
	Then deve ser exibida uma falha no processo de auditoria  
	And o sistema deve exibir a mensagem "Não foi possível registrar o histórico da exclusão. Ação cancelada."  
	And o convênio não deve ser removido do sistema  

# 3. Alteração de Dados de Convênios

# Regra de Negócio: Apenas usuários com permissão administrativa podem alterar dados de convênio

Scenario: Alteração realizada por usuário com permissão administrativa

	Given que o usuário "Dr. Marcelo" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Saúde Total"  
	And o código de identificação "SATA005"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Dr. Marcelo" altera o nome do convênio para "Saúde Total Plus"  
	And confirma a alteração  
	Then o sistema deve atualizar o cadastro do convênio com sucesso  
	And o sistema deve exibir a mensagem "Convênio atualizado com sucesso!"  
	And o sistema deve registrar a ação no histórico com data, hora e o usuário responsável  

Scenario: Tentativa de alteração por usuário sem permissão administrativa

	Given que o usuário "Carla Nogueira" tem perfil de recepcionista  
	And o perfil "Recepcionista" não possui permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Vida Plena"  
	And o código de identificação "VAPA002"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Carla Nogueira" tenta alterar o nome do convênio para "Vida Plena Premium"  
	Then o sistema deve impedir a alteração  
	And o sistema deve exibir a mensagem "Operação bloqueada."  
	And o histórico de alterações não deve ser atualizado  

# Regra de Negócio: A alteração de convênio só pode ocorrer se este estiver marcado como “Ativo”

Scenario: Alteração de convênio com status Ativo

	Given que o usuário "Dr. Henrique" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Bem Estar"  
	And o código de identificação "BEST007"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Dr. Henrique" altera o nome do convênio para "Bem Estar Gold"  
	And confirma a alteração  
	Then o sistema deve atualizar o convênio com sucesso  
	And o sistema deve exibir a mensagem "Convênio atualizado com sucesso!"  
	And o histórico deve registrar a alteração  

Scenario: Tentativa de alteração de convênio com status Inativo

	Given que o usuário "Dra. Marina" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Plano Vida"  
	And o código de identificação "PV004"  
	And o status do convênio está definido como "Inativo"  
	When o usuário "Dra. Marina" tenta alterar o nome do convênio para "Plano Vida Familiar"  
	Then o sistema deve impedir a alteração  
	And o sistema deve exibir a mensagem "Somente convênios ativos podem ser alterados."  
	And o histórico de alterações não deve ser atualizado  

# Regra de Negócio: O sistema deve manter histórico de alterações, registrando data, hora e responsável pela ação

Scenario: Registro de histórico após alteração bem-sucedida

	Given que o usuário "Luiza Oliveira" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Viva Saúde"  
	And o código de identificação "VASA005"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Luiza Oliveira" altera o nome do convênio para "Viva Saúde Premium"  
	And confirma a alteração  
	Then o sistema deve atualizar o convênio com sucesso  
	And o sistema deve exibir a mensagem "Convênio atualizado com sucesso!"  
	And o sistema deve registrar no histórico a data, hora e o usuário "Luiza Oliveira" como responsável pela ação  
	And o registro deve estar disponível para auditorias futuras  

Scenario: Falha ao registrar histórico após alteração

	Given que o usuário "Fernanda Lima" tem permissão de administrador  
	And o perfil "Administrador" tem permissão para alterar convênios  
	And existe um convênio cadastrado com o nome "Clin Saúde"  
	And o código de identificação "CLSA006"  
	And o status do convênio está definido como "Ativo"  
	When o usuário "Fernanda Lima" altera o nome do convênio para "Clin Saúde Plus"  
	And confirma a alteração  
	And o sistema não registra a ação no histórico  
	Then deve ser exibida uma falha no processo de auditoria  
	And o sistema deve exibir a mensagem "Não foi possível registrar o histórico da alteração. Ação cancelada."  
	And o convênio não deve ser atualizado no sistema  


