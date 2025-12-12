-- =====================================================================
-- MÓDULO: PRONTUÁRIO
-- Descrição: Tabelas para gerenciamento de prontuários eletrônicos
-- =====================================================================

-- Tabela principal: prontuarios
CREATE TABLE prontuarios (
    id VARCHAR(36) PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    atendimento_id VARCHAR(36),
    status VARCHAR(50) NOT NULL,
    data_hora_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    profissional_responsavel VARCHAR(255) NOT NULL,
    observacoes_iniciais TEXT,
    
    CONSTRAINT fk_prontuarios_paciente FOREIGN KEY (paciente_id) 
        REFERENCES pacientes(paciente_id) ON DELETE RESTRICT,
    CONSTRAINT chk_prontuario_status CHECK (status IN ('ATIVO', 'INATIVO', 'INATIVADO', 'ARQUIVADO', 'EXCLUIDO'))
);

-- Tabela de Histórico Clínico
CREATE TABLE historico_clinico (
    id VARCHAR(36) PRIMARY KEY,
    prontuario_id VARCHAR(36) NOT NULL,
    paciente_id INTEGER NOT NULL,
    sintomas TEXT NOT NULL,
    diagnostico TEXT NOT NULL,
    conduta TEXT NOT NULL,
    data_hora_registro TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    profissional_responsavel VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_historico_clinico_prontuario FOREIGN KEY (prontuario_id) 
        REFERENCES prontuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_historico_clinico_paciente FOREIGN KEY (paciente_id) 
        REFERENCES pacientes(paciente_id) ON DELETE RESTRICT
);

-- Tabela de anexos referenciados (tabela de relacionamento many-to-many)
CREATE TABLE historico_clinico_anexos (
    historico_id VARCHAR(36) NOT NULL,
    anexo_referenciado VARCHAR(255) NOT NULL,
    PRIMARY KEY (historico_id, anexo_referenciado),
    CONSTRAINT fk_anexos_historico FOREIGN KEY (historico_id) 
        REFERENCES historico_clinico(id) ON DELETE CASCADE
);

-- Tabela de Histórico de Atualizações
CREATE TABLE historico_atualizacoes (
    id VARCHAR(36) PRIMARY KEY,
    prontuario_id VARCHAR(36) NOT NULL,
    atendimento_id VARCHAR(36) NOT NULL,
    data_hora_atualizacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    profissional_responsavel VARCHAR(255) NOT NULL,
    observacoes TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    
    CONSTRAINT fk_historico_atualizacoes_prontuario FOREIGN KEY (prontuario_id) 
        REFERENCES prontuarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_historico_atualizacoes_status CHECK (status IN ('ATIVO', 'INATIVO', 'INATIVADO', 'ARQUIVADO', 'EXCLUIDO'))
);

-- Índices para melhorar performance das consultas
CREATE INDEX idx_prontuarios_paciente_id ON prontuarios(paciente_id);
CREATE INDEX idx_prontuarios_atendimento_id ON prontuarios(atendimento_id);
CREATE INDEX idx_prontuarios_status ON prontuarios(status);
CREATE INDEX idx_historico_clinico_prontuario_id ON historico_clinico(prontuario_id);
CREATE INDEX idx_historico_clinico_paciente_id ON historico_clinico(paciente_id);
CREATE INDEX idx_historico_clinico_data_hora ON historico_clinico(data_hora_registro);
CREATE INDEX idx_historico_atualizacoes_prontuario_id ON historico_atualizacoes(prontuario_id);
CREATE INDEX idx_historico_atualizacoes_atendimento_id ON historico_atualizacoes(atendimento_id);
CREATE INDEX idx_historico_atualizacoes_data_hora ON historico_atualizacoes(data_hora_atualizacao);
