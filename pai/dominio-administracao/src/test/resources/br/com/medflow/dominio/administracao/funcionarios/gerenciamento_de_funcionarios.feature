Feature: Gerenciamento de Funcionários

# ====================================================================
# 1. Cadastro de Funcionários
# ====================================================================

# Regra de Negócio: O nome do funcionário é obrigatório
Scenario: Cadastro de funcionário com nome preenchido
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Ana Silva"
	And preenche o campo "Função" com "Enfermeira"
	And preenche o campo "Contato" com "ana.silva@clinica.medfow"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro de funcionário sem informar o nome
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador deixa o campo "Nome" em branco
	And preenche o campo "Função" com "Enfermeira"
	And preenche o campo "Contato" com "vivian.costa@clinica.medfow"
	And o administrador tenta submeter o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: A função do funcionário é obrigatória
Scenario: Cadastro com função preenchida
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Bruno Costa"
	And preenche o campo "Função" com "Recepcionista"
	And preenche o campo "Contato" com "bruno.costa@clinica.medfow"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro sem informar a função
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Jorge Silva"
	And deixa o campo "Função" em branco
	And preenche o campo "Contato" com "jorge.silva@clinica.medfow"
	And o administrador tenta submeter o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: O nome deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços)
Scenario: Cadastro com nome válido
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Jéssica Maria"
	And preenche o campo "Função" com "Recepcionista"
	And preenche o campo "Contato" com "81988888888"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro com nome contendo números ou símbolos
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "João123!"
	And preenche o campo "Função" com "Recepcionista"
	And preenche o campo "Contato" com "81991111111"
	And o administrador submete o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: O contato (telefone e/ou e-mail) é obrigatório
Scenario: Cadastro com e-mail informado
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Carla Mendes"
	And preenche o campo "Função" com "Enfermeira"
	And preenche o campo "Contato" com "carla.mendes@clinica.medfow"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro sem informar contato
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Celia James"
	And preenche o campo "Função" com "Enfermeira"
	And deixa o campo "Contato" em branco
	And o administrador tenta submeter o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: O contato deve seguir formato válido (telefone com DDD ou e-mail válido)
Scenario: Cadastro com número de telefone válido
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Paola Caramela"
	And preenche o campo "Função" com "Recepcionista"
	And preenche o campo "Contato" com "74999999999"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro com e-mail em formato inválido
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Katia Dantas"
	And preenche o campo "Função" com "Enfermeira"
	And preenche o campo "Contato" com "katia.dantas@clinica"
	And o administrador tenta submeter o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: Não é permitido cadastrar dois funcionários com o mesmo nome e contato (combinação única)
Scenario: Cadastro de funcionário com combinação nome e e-mail únicos
	Given que o administrador tem permissão de administrador
	And já existe um funcionário com nome "Pedro Lima" e contato "pedro.lima@clinica.medfow"
	When o administrador preenche o campo "Nome" com "Pedro Lima"
	And preenche o campo "Função" com "Enfermeiro"
	And o campo "Contato" com "pedro.lima2@clinica.medfow"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso

Scenario: Cadastro de funcionário com nome e e-mail já existentes
	Given que o administrador tem permissão de administrador
	And já existe um funcionário com nome "Mateus Marino" e contato "mateus.marino@clinica.medfow"
	When o administrador preenche o campo "Nome" com "Mateus Marino"
	And preenche o campo "Função" com "Enfermeiro"
	And o campo "Contato" com "mateus.marino@clinica.medfow"
	And o administrador tenta submeter o formulário
	Then o sistema deve impedir o cadastro

# Regra de Negócio: O status inicial do funcionário cadastrado deve ser definido como "Ativo"
Scenario: Cadastro com status inicial definido automaticamente como ativo
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para cadastrar funcionários
	When o administrador preenche o campo "Nome" com "Geovanna Seco"
	And preenche o campo "Função" com "Recepcionista"
	And preenche o campo "Contato" com "geovanna.seco@clinica.medfow"
	And o administrador submete o formulário
	Then o sistema deve cadastrar o funcionário com sucesso
	And o campo "Status" deve ser automaticamente definido como "Ativo"

# ====================================================================
# 2. Atualização de Dados de Funcionários
# ====================================================================

# Regra de Negócio: Apenas nome, função e contato podem ser alterados
Scenario: Atualização apenas de campos permitidos (nome, função e contato)
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para alterar funcionários
    Given o funcionário "Ana Souza" possui status "Ativo"
	And o administrador acessa o cadastro do funcionário "Ana Souza"
	When o administrador altera o campo "Nome" para "Ana Carolina Souza"
	And altera o campo "Função" para "Enfermeira Chefe"
	And altera o campo "Contato" para "ana.carolina@clinica.medfow"
	And o administrador confirma a atualização
	Then o sistema deve salvar as alterações com sucesso

Scenario: Tentativa de alterar campo não permitido (status)
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para alterar funcionários
	
    Given o funcionário "Paulo Mendes" possui status "Ativo" 
	And o administrador acessa o cadastro do funcionário "Paulo Mendes"
	When o administrador tenta alterar o campo "Status" de "Ativo" para "Inativo"
	And o administrador confirma a atualização
	Then o sistema deve impedir a alteração

# Regra de Negócio: Os dados alterados devem seguir as mesmas validações do cadastro
Scenario: Atualização com nome inválido
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para alterar funcionários
	And o administrador acessa o cadastro do funcionário "Carlos Lima"
	When o administrador altera o campo "Nome" para "Carlos123!"
	And altera o campo "Função" para "Enfermeiro"
	And altera o campo "Contato" para "carlos.lima@clinica.medfow"
	And o administrador confirma a atualização
	Then o sistema deve impedir a atualização

Scenario: Atualização com função inválida
	Given que o administrador tem permissão de administrador
	And o perfil "Administrador" tem permissão para alterar funcionários
	And o administrador acessa o cadastro do funcionário "Luan Santana"
	When o administrador altera o campo "Função" para vazio
	And o administrador confirma a atualização
	Then o sistema deve impedir a atualização

# Regra de Negócio: Alterações não devem afetar registros históricos vinculados ao funcionário
Scenario: Atualização sem impacto no histórico
	Given que o administrador tem permissão de administrador
	And o funcionário "Fernanda Lopes" possui registros de atendimentos anteriores
	When o administrador altera o campo "Contato" para "fernanda.lopes@clinica.medfow"
	And o administrador confirma a atualização
	Then o sistema deve salvar a alteração nos dados cadastrais
	And deve manter todos os registros históricos inalterados

Scenario: Alteração que modifica registros históricos indevidamente
	Given que o administrador tem permissão de administrador
	And o funcionário "Lucas Oliveira" possui registros de escalas e atendimentos anteriores
	When o administrador altera o campo "Função" para "Recepcionista"
	And o administrador confirma a atualização
	And o sistema tenta atualizar também os registros históricos com a nova função
	Then o sistema deve impedir a atualização

# Regra de Negócio: Não é permitido inativar ou alterar o nome de uma função vinculada a funcionários ativos sem tratamento adequado
Scenario: Reatribuição correta de função ativa
	Given que o administrador tem permissão de administrador
	And o funcionário "Patrícia Gomes" está ativo na função "Auxiliar de Enfermagem"
	When o administrador altera a função para "Enfermagem"
	And o administrador confirma a atualização
	Then o sistema deve salvar a alteração com sucesso

Scenario: Alteração de função ativa sem tratamento de reatribuição
	Given que o administrador tem permissão de administrador
	And o funcionário "Eduardo Silva" está ativo na função "Recepcionista"
	When o administrador tenta alterar a função para "Coordenador" sem configurar reatribuição
	And o administrador confirma a atualização
	Then o sistema deve impedir a atualização

# ====================================================================
# 3. Gestão de Status do Funcionário
# ====================================================================

# Regra de Negócio: É permitido alterar o status de um funcionário de Ativo para Inativo e vice-versa
Scenario: Alteração de status de Ativo para Inativo
	Given que o administrador tem permissão de administrador
	And o funcionário "João Pereira" possui status "Ativo"
	When o administrador acessa o cadastro do funcionário
	And altera o campo "Status" para "Inativo"
	And o administrador confirma a alteração
	Then o sistema deve atualizar o status do funcionário com sucesso

Scenario: Salvar os dados sem alteração de status
	Given que o administrador tem permissão de administrador
	And o funcionário "Manoela Gomes" possui status "Ativo"
	When o administrador acessa o cadastro do funcionário
	And não altera o campo "Status" que está como "Ativo"
	And o administrador confirma a atribuição
	Then o sistema deve impedir a inclusão

# Regra de Negócio: Funcionários Inativos não podem ser atribuídos a novas escalas, atendimentos ou agendamentos
Scenario: Funcionário inativo sendo atribuído a nova escala
	Given que o administrador tem permissão de administrador
	And o funcionário "Marina Costa" possui status "Inativo"
	When o administrador tenta incluí-la em uma nova escala de atendimento
	And o administrador confirma a atribuição
	Then o sistema deve impedir a inclusão

Scenario: Funcionário inativo sendo atribuído a novo agendamento
	Given que o administrador tem permissão de administrador
	And o funcionário "Rita Nunes" possui status "Inativo"
	When o administrador tenta incluí-la em um novo agendamento
	And o administrador confirma a atribuição
	Then o sistema deve impedir a inclusão

# Regra de Negócio: A inativação de um funcionário deve manter seu histórico de atuação preservado no sistema
Scenario: Inativação mantendo histórico preservado
	Given que o administrador tem permissão de administrador
	And o funcionário "Fernanda Alves" possui histórico de atendimentos anteriores
	When o administrador altera o status do funcionário para "Inativo"
	And o administrador confirma a alteração
	Then o sistema deve atualizar o status do funcionário para "Inativo"
	And deve manter todos os registros de atendimentos anteriores inalterados

Scenario: Inativação que apaga registros anteriores indevidamente
	Given que o administrador tem permissão de administrador
	And o funcionário "Pedro Martins" possui histórico de escalas e atendimentos
	When o administrador altera o status do funcionário para "Inativo"
	And o administrador confirma a alteração
	And o sistema remove ou altera registros históricos indevidamente
	Then o sistema deve impedir a inativação

# Regra de Negócio: Não é permitido inativar funcionário vinculado a atividades futuras (ex.: plantões agendados) sem resolver previamente os vínculos
Scenario: Inativação após resolver vínculos futuros
	Given que o administrador tem permissão de administrador
	And o funcionário "Carla Nunes" possui plantões futuros agendados
	And o administrador remove ou realoca os vínculos dessas atividades
	When o administrador altera o status do funcionário para "Inativo"
	And o administrador confirma a alteração
	Then o sistema deve permitir a inativação

Scenario: Inativação com vínculos futuros pendentes
	Given que o administrador tem permissão de administrador
	And o funcionário "Rafael Souza" possui plantões futuros agendados
	When o administrador tenta alterar o status do funcionário para "Inativo"
	And o administrador confirma a alteração
	Then o sistema deve impedir a inativação
