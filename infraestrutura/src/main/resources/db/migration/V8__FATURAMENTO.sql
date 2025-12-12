-- =====================================================================
-- MÓDULO: FATURAMENTO
-- Descrição: Tabelas para gerenciamento de faturamentos e pagamentos
-- =====================================================================

-- Tabela principal: faturamentos
CREATE TABLE faturamentos (
    id VARCHAR(36) PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    tipo_procedimento VARCHAR(50) NOT NULL,
    descricao_procedimento VARCHAR(255) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_hora_faturamento TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    usuario_responsavel VARCHAR(36) NOT NULL,
    observacoes TEXT,
    valor_padrao DECIMAL(10, 2),
    justificativa_valor_diferente TEXT,
    
    CONSTRAINT fk_faturamentos_paciente FOREIGN KEY (paciente_id) 
        REFERENCES pacientes(paciente_id) ON DELETE RESTRICT,
    CONSTRAINT chk_faturamento_tipo_procedimento CHECK (tipo_procedimento IN ('CONSULTA', 'EXAME')),
    CONSTRAINT chk_faturamento_status CHECK (status IN ('PENDENTE', 'PAGO', 'CANCELADO', 'INVALIDO', 'REMOVIDO')),
    CONSTRAINT chk_faturamento_valor CHECK (valor > 0),
    CONSTRAINT chk_faturamento_valor_padrao CHECK (valor_padrao IS NULL OR valor_padrao >= 0)
);

-- Tabela de Histórico de Faturamento
CREATE TABLE historico_faturamento (
    id BIGSERIAL PRIMARY KEY,
    faturamento_id VARCHAR(36) NOT NULL,
    acao VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    responsavel_id VARCHAR(36) NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    
    CONSTRAINT fk_historico_faturamento_faturamento FOREIGN KEY (faturamento_id) 
        REFERENCES faturamentos(id) ON DELETE CASCADE,
    CONSTRAINT chk_historico_faturamento_acao CHECK (acao IN (
        'CRIACAO',
        'ATUALIZACAO',
        'CANCELAMENTO',
        'PAGAMENTO',
        'EXCLUSAO_LOGICA',
        'TENTATIVA_EXCLUSAO_NEGADA',
        'ALTERACAO_STATUS',
        'TENTATIVA_ALTERACAO_NEGADA'
    ))
);

-- Índices para melhorar performance das consultas
CREATE INDEX idx_faturamentos_paciente_id ON faturamentos(paciente_id);
CREATE INDEX idx_faturamentos_tipo_procedimento ON faturamentos(tipo_procedimento);
CREATE INDEX idx_faturamentos_status ON faturamentos(status);
CREATE INDEX idx_faturamentos_data_hora ON faturamentos(data_hora_faturamento);
CREATE INDEX idx_historico_faturamento_faturamento_id ON historico_faturamento(faturamento_id);
CREATE INDEX idx_historico_faturamento_data_hora ON historico_faturamento(data_hora);
CREATE INDEX idx_historico_faturamento_acao ON historico_faturamento(acao);
