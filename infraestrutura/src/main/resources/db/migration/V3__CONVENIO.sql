-- Tabela principal para Convênios
CREATE TABLE convenios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo_identificacao VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Tabela para Histórico de Convênios
CREATE TABLE historico_convenio (
    id BIGSERIAL PRIMARY KEY,
    convenio_id INTEGER NOT NULL REFERENCES convenios(id) ON DELETE CASCADE,
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Índices para melhorar performance das consultas
CREATE INDEX idx_convenios_codigo_identificacao ON convenios(codigo_identificacao);
CREATE INDEX idx_convenios_status ON convenios(status);
CREATE INDEX idx_historico_convenio_convenio_id ON historico_convenio(convenio_id);


