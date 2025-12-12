-- ===========================================================================
-- Script de Migração: Tabelas do contexto Administração - Pacientes
-- Versão: V4
-- Descrição: Criação das tabelas pacientes e pacientes_historico
-- ===========================================================================

-- Tabela principal de Pacientes
CREATE TABLE pacientes (
    paciente_id INT AUTO_INCREMENT PRIMARY KEY,
    nome_paciente VARCHAR(255) NOT NULL,
    cpf_paciente CHAR(11) NOT NULL UNIQUE,
    data_nascimento_paciente VARCHAR(10) NOT NULL,
    telefone_paciente VARCHAR(11) NOT NULL,
    endereco_paciente VARCHAR(500)
);

-- Criar índice separadamente (compatível com H2)
CREATE INDEX idx_paciente_cpf ON pacientes(cpf_paciente);

-- Tabela de Histórico de Pacientes
CREATE TABLE pacientes_historico (
    paciente_id INT NOT NULL,
    ordem_historico INT NOT NULL,
    acao_paciente VARCHAR(50) NOT NULL,
    descricao_paciente VARCHAR(500) NOT NULL,
    responsavel_paciente_id INT NOT NULL,
    data_hora_paciente TIMESTAMP NOT NULL,
    PRIMARY KEY (paciente_id, ordem_historico),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(paciente_id) ON DELETE CASCADE
);