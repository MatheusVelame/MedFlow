Feature: Gerenciamento de Convênios

# ====================================================================
# 1. Cadastro de convênio
# ====================================================================

# Regra de Negócio: O cadastro de convênios/planos deve conter nome, código e status Ativo.

Scenario: Cadastro bem-sucedido (Preenchendo dados obrigatórios)
    Given que o administrador "Dra. Camila" tem permissão para cadastrar
    When cadastra o convênio "Saúde Total" com código "SATL0001"
    Then o registro deve ser criado com sucesso
    And o status deve ser "Ativo"

Scenario: Tentativa de cadastro sem preencher o nome
    Given que o administrador "Dr. Marcos" tem permissão para cadastrar
    When tenta cadastrar um convênio sem informar o nome
    Then o sistema deve impedir o cadastro
    And deve exibir a mensagem "O nome do convênio é obrigatório"

# Regra de Negócio: Não é permitido cadastrar identificações de convênios duplicados (código já existente)

Scenario: Tentativa de cadastro com código de identificação duplicado
    Given que o convênio com código "VIDA001" já existe
    When o administrador "Dr. Henrique" tenta cadastrar o código "VIDA001" novamente
    Then o sistema deve impedir o cadastro
    And o histórico de ações não deve ser alterado 

# Regra de Negócio: O convênio recém-cadastrado deve iniciar com status “Ativo” por padrão

Scenario: Sistema ignora tentativa de definir status inicial como "Inativo"
    Given que o administrador "Dr. André" tem permissão para cadastrar
    When tenta cadastrar o convênio "Plano Premium" com status inicial "Inativo"
    Then o registro deve ser criado com sucesso
    And o status deve ser "Ativo"

# ====================================================================
# 2. Remoção de Convênios Inativos
# ====================================================================

# Regra de Negócio: A exclusão definitiva de um convênio só pode ocorrer se este estiver marcado como “Inativo”

Scenario: Exclusão de convênio com status Inativo
    Given que o convênio "Saúde Vida" com código "SAVA001" está com status "Inativo"
    When o administrador "Dr. Rafael" solicita a exclusão definitiva
    Then o convênio deve ser removido do sistema
    And o histórico de remoções deve ser registrado

Scenario: Tentativa de exclusão de convênio com status Ativo
    Given que o convênio "Plano Total" está com status "Ativo"
    When o administrador "Dra. Juliana" tenta excluir o convênio
    Then o sistema deve impedir a exclusão
    And o histórico de ações não deve ser alterado

# Regra de Negócio: O sistema deve manter histórico de remoções, registrando data, hora e responsável pela ação

Scenario: Falha ao registrar histórico após exclusão (Falha de Auditoria)
    Given que o convênio "Viva Saúde" está com status "Inativo" e código "VASA004"
    When a exclusão é solicitada e o registro de histórico falha
    Then deve ser exibida uma falha no processo de auditoria
    And o convênio não deve ser removido do sistema 
    
# ====================================================================
# 3. Alteração de Dados de Convênios
# ====================================================================

# Regra de Negócio: Apenas usuários com permissão administrativa podem alterar dados de convênio

Scenario: Tentativa de alteração por usuário sem permissão administrativa
    Given que o convênio "Vida Plena" está ativo
    And que o usuário "Carla Nogueira" tem perfil "Recepcionista"
    When o usuário "Carla Nogueira" tenta alterar o nome
    Then o sistema deve impedir a alteração

# Regra de Negócio: A alteração de convênio só pode ocorrer se este estiver marcado como “Ativo”

Scenario: Alteração de dados em convênio com status Ativo
    Given que o administrador "Dr. Henrique" tem permissão para alterar
    And o convênio "Bem Estar" com código "BEST007" está com status "Ativo"
    When o usuário "Dr. Henrique" altera o nome para "Bem Estar Gold"
    Then o convênio deve ser atualizado com sucesso
    And o histórico de alterações deve ser registrado

Scenario: Tentativa de alteração de convênio com status Inativo
    Given que o convênio "Plano Vida" está com status "Inativo"
    When o administrador "Dra. Marina" tenta alterar o nome
    Then o sistema deve impedir a alteração

# Regra de Negócio: O sistema deve manter histórico de alterações, registrando data, hora e responsável pela ação

Scenario: Registro de histórico após alteração bem-sucedida
    Given que o convênio "Viva Saúde" está com status "Ativo"
    When o administrador "Luiza Oliveira" altera o nome para "Viva Saúde Premium"
    Then o histórico deve registrar a ação do usuário "Luiza Oliveira"