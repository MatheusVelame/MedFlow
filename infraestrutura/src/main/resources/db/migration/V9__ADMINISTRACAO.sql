-- Migration para o módulo de Administração: Criação das tabelas de funcionários

-- Tabela principal para Funcionários
CREATE TABLE funcionarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    funcao VARCHAR(255) NOT NULL,
    contato VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Tabela para Histórico de Funcionários
CREATE TABLE historico_funcionarios (
    id BIGSERIAL PRIMARY KEY,
    funcionario_id INTEGER NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Índices de otimização de busca
CREATE INDEX idx_historico_funcionarios_funcionario_id ON historico_funcionarios (funcionario_id);
CREATE INDEX idx_funcionarios_status ON funcionarios (status);
