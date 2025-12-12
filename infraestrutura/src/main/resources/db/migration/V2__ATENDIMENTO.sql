-- Localização: infraestrutura/src/main/resources/db/migration/V2__ATENDIMENTO.sql

-- Tabela principal para Consultas
CREATE TABLE consultas (
    id SERIAL PRIMARY KEY,
    
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    descricao TEXT,
    status VARCHAR(50) NOT NULL,
    
    -- Assumindo que Paciente e Medico são referências de IDs externos ou de outro BC
    paciente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL
);


-- Tabela para Histórico de Consultas
CREATE TABLE historico_consultas (
    id BIGSERIAL PRIMARY KEY,
    consulta_id INTEGER NOT NULL REFERENCES consultas(id), 
    
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER NOT NULL, 
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- =====================================================================
-- MÓDULO: ATENDIMENTO - EXAMES
-- =====================================================================

-- Tabela principal: exames
CREATE TABLE exames (
    id SERIAL PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL,
    tipo_exame VARCHAR(100) NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL,
    responsavel_id INTEGER NOT NULL
);

-- Tabela de histórico: historico_exames
CREATE TABLE historico_exames (
    id BIGSERIAL PRIMARY KEY,
    exame_id INTEGER NOT NULL REFERENCES exames(id),
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Índices para performance
CREATE INDEX idx_exames_paciente_data ON exames(paciente_id, data_hora);
CREATE INDEX idx_exames_medico_data ON exames(medico_id, data_hora);