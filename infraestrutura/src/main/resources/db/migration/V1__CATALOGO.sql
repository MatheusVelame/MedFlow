-- Tabela para Revisão Pendente
CREATE TABLE revisoes_pendentes (
    id SERIAL PRIMARY KEY,
    
    novo_valor TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    solicitante_id INTEGER NOT NULL, 
    revisor_id INTEGER 
);

-- Tabela principal para Medicamentos
CREATE TABLE medicamentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    uso_principal VARCHAR(255) NOT NULL,
    contraindicacoes TEXT,
    status VARCHAR(50) NOT NULL,
    
    revisao_pendente_id INTEGER UNIQUE REFERENCES revisoes_pendentes(id) 
);


-- Tabela para Histórico
CREATE TABLE historico_medicamentos (
    id BIGSERIAL PRIMARY KEY,
    medicamento_id INTEGER NOT NULL REFERENCES medicamentos(id), 
    
    acao VARCHAR(50) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER NOT NULL, 
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL
);