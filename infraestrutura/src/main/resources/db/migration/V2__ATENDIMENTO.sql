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