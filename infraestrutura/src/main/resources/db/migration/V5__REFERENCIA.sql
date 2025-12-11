-- =====================================================================
-- MÓDULO: REFERÊNCIA - TIPOS DE EXAMES
-- Descrição: Tabelas para gerenciamento de tipos de exames médicos
-- =====================================================================

-- Tabela principal: tipos_exames
CREATE TABLE tipos_exames (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255) NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    
    CONSTRAINT chk_tipos_exames_status CHECK (status IN ('ATIVO', 'INATIVO')),
    CONSTRAINT chk_tipos_exames_valor CHECK (valor >= 0)
);

-- Tabela de histórico: historico_tipos_exames
CREATE TABLE historico_tipos_exames (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_exame_id INT NOT NULL,
    acao VARCHAR(50) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    responsavel_id INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_historico_tipo_exame FOREIGN KEY (tipo_exame_id) 
        REFERENCES tipos_exames(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_historico_acao CHECK (acao IN (
        'CADASTRO',
        'ATUALIZACAO_DESCRICAO',
        'ATUALIZACAO_ESPECIALIDADE',
        'ATUALIZACAO_VALOR',
        'INATIVACAO',
        'EXCLUSAO'
    ))
);

-- Índices para melhor performance
CREATE INDEX idx_tipos_exames_codigo ON tipos_exames(codigo);
CREATE INDEX idx_tipos_exames_status ON tipos_exames(status);
CREATE INDEX idx_tipos_exames_especialidade ON tipos_exames(especialidade);
CREATE INDEX idx_historico_tipo_exame_id ON historico_tipos_exames(tipo_exame_id);
CREATE INDEX idx_historico_data_hora ON historico_tipos_exames(data_hora);