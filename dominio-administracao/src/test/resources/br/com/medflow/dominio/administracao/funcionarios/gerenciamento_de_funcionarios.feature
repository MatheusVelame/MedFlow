Feature: Gerenciamento de Funcionários

# ====================================================================
# 1. Cadastro de Funcionários
# ====================================================================

# Regra de Negócio: O nome do funcionário é obrigatório

Scenario: Cadastro de funcionário com nome preenchido
    Given que o administrador tem permissão
    When preenche o nome "Ana Silva" e envia o formulário
    Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro de funcionário sem informar o nome
	Given que o administrador tem permissão
    When deixa o nome em branco
    Then o sistema impede o cadastro
    
# Regra de Negócio: A função do funcionário é obrigatória

Scenario: Cadastro com função preenchida
    Given que o administrador tem permissão
    When informa "Bruno Costa" e função "Recepcionista"
    Then o sistema deve cadastrar o funcionário

Scenario: Cadastro sem informar a função
    Given que o administrador tem permissão
    When deixa a função em branco
    Then o sistema deve impedir o cadastro
    
# Regra de Negócio: O nome deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços)

Scenario: Cadastro com nome válido
    Given que o administrador tem permissão
    When informa o nome "Jéssica Maria"
    Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro com nome contendo números ou símbolos
    Given que o administrador tem permissão
    When o nome contém caracteres inválidos
    Then o sistema deve impedir o cadastro
    
# Regra de Negócio: O contato (telefone e/ou e-mail) é obrigatório

Scenario: Cadastro com e-mail informado
    Given que o administrador tem permissão
    When preenche o e-mail "carla.mendes@clinica.medfow"
    Then o sistema deve cadastrar o funcionário

Scenario: Cadastro sem informar contato
    Given que o administrador tem permissão
    When deixa o contato em branco
    Then o sistema deve impedir o cadastro
    
# Regra de Negócio: O contato deve seguir formato válido (telefone com DDD ou e-mail válido)

Scenario: Cadastro com número de telefone válido
    Given que o administrador tem permissão
    When informa o telefone "74999999999"
    Then o sistema deve cadastrar o funcionário

Scenario: Cadastro com e-mail em formato inválido
    Given que o administrador tem permissão
    When o e-mail não possui formato válido
    Then o sistema deve impedir o cadastro
    
# Regra de Negócio: Não é permitido cadastrar dois funcionários com o mesmo nome e contato (combinação única)

Scenario: Cadastro de funcionário com combinação nome e e-mail únicos
	Given que já existe funcionário "Pedro Lima" com e-mail diferente
    When cadastra "Pedro Lima" com outro contato
    Then o sistema deve cadastrar o funcionário

Scenario: Cadastro de funcionário com nome e e-mail já existentes
    Given que já existe funcionário "Mateus Marino" com mesmo e-mail
    When tenta cadastrar novamente
    Then o sistema deve impedir o cadastro
    
# Regra de Negócio: O status inicial do funcionário cadastrado deve ser definido como "Ativo"

Scenario: Cadastro com status inicial definido automaticamente como ativo
    Given que o administrador tem permissão
    When cadastra um novo funcionário
    Then o status deve ser definido como "Ativo"
    
# ====================================================================
# 2. Atualização de Dados de Funcionários
# ====================================================================

# Regra de Negócio: Apenas nome, função e contato podem ser alterados

Scenario: Atualização apenas de campos permitidos (nome, função e contato)
    Given que o funcionário está ativo
    When altera nome, função e contato
    Then o sistema deve salvar as alterações

Scenario: Tentativa de alterar campo não permitido (status)
    Given que o funcionário "Paulo Mendes" está ativo
    When tenta alterar o status
    Then o sistema deve impedir a alteração
    
# Regra de Negócio: Os dados alterados devem seguir as mesmas validações do cadastro

Scenario: Atualização com nome inválido
    Given que o administrador acessa o cadastro
    When altera o nome para "Carlos123!"
    Then o sistema deve impedir a atualização

Scenario: Atualização com função inválida
    Given que o administrador acessa o cadastro
    When a função é deixada em branco
    Then o sistema deve impedir a atualização
    
# Regra de Negócio: Alterações não devem afetar registros históricos vinculados ao funcionário

Scenario: Atualização sem impacto no histórico
    Given que o funcionário possui registros anteriores
    When altera o contato
    Then o histórico deve permanecer inalterado

Scenario: Alteração que modifica registros históricos indevidamente
    Given que o funcionário possui registros de atendimento
    When altera função com impacto no histórico
    Then o sistema deve impedir a atualização
    
# Regra de Negócio: Não é permitido inativar ou alterar o nome de uma função vinculada a funcionários ativos sem tratamento adequado

Scenario: Reatribuição correta de função ativa
    Given que o funcionário está ativo na função atual
    When altera a função com reatribuição correta
    Then o sistema deve salvar a alteração

Scenario: Alteração de função ativa sem tratamento de reatribuição
    Given que o funcionário está ativo
    When altera a função sem reatribuição
    Then o sistema deve impedir a atualização
    
# ====================================================================
# 3. Gestão de Status do Funcionário
# ====================================================================

# Regra de Negócio: É permitido alterar o status de um funcionário de Ativo para Inativo e vice-versa

Scenario: Alteração de status de Ativo para Inativo
    Given que o funcionário está ativo
    When altera o status para inativo
    Then o sistema deve atualizar com sucesso

Scenario: Salvar os dados sem alteração de status
    Given que o funcionário está ativo
    When não altera o campo de status
    Then o sistema deve impedir a inclusão
    
# Regra de Negócio: Funcionários Inativos não podem ser atribuídos a novas escalas, atendimentos ou agendamentos

Scenario: Funcionário inativo sendo atribuído a nova escala
    Given que o funcionário está inativo
    When tenta incluí-lo em uma nova escala
    Then o sistema deve impedir a inclusão

Scenario: Funcionário inativo sendo atribuído a novo agendamento
    Given que o funcionário está inativo
    When tenta incluí-lo em novo agendamento
    Then o sistema deve impedir a inclusão
    
# Regra de Negócio: A inativação de um funcionário deve manter seu histórico de atuação preservado no sistema

Scenario: Inativação mantendo histórico preservado
    Given que o funcionário possui histórico
    When altera o status para inativo
    Then o histórico deve ser mantido

Scenario: Inativação que apaga registros anteriores indevidamente
    Given que o funcionário possui histórico
    When a inativação remove registros
    Then o sistema deve impedir a ação
    
# Regra de Negócio: Não é permitido inativar funcionário vinculado a atividades futuras (ex.: plantões agendados) sem resolver previamente os vínculos

Scenario: Inativação após resolver vínculos futuros
    Given que o funcionário possui plantões futuros
    When remove os vínculos e altera o status
    Then o sistema deve permitir a inativação

Scenario: Inativação com vínculos futuros pendentes
    Given que o funcionário possui plantões futuros
    When tenta alterar o status para inativo
    Then o sistema deve impedir a inativação
