-- Migration para o módulo de Atendimento: Criação da tabela de consultas (Read Model)

-- A tabela 'consulta' armazena os dados desnormalizados e otimizados para consultas (Queries) 
-- da camada de Aplicação.

CREATE TABLE consulta (
    -- Chave Primária
    id SERIAL PRIMARY KEY,

    -- Dados do Paciente (denormalizados do módulo de Administração/Paciente)
    paciente_id INTEGER NOT NULL,
    paciente_nome VARCHAR(255) NOT NULL,

    -- Dados do Médico (denormalizados do módulo de Referência/Médicos)
    medico_id INTEGER NOT NULL,
    medico_nome VARCHAR(255) NOT NULL,

    -- Informações da Consulta
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    observacoes TEXT,
    status VARCHAR(50) NOT NULL, -- Ex: AGENDADA, REALIZADA, CANCELADA
    
    -- Auditoria
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Índices de otimização de busca para as Queries mais comuns:

-- Otimiza a busca por ID do paciente (pesquisarConsultasPorPaciente)
CREATE INDEX idx_consulta_paciente_id ON consulta (paciente_id);

-- Otimiza a busca por status (caso seja necessário listar todas as agendadas, por exemplo)
CREATE INDEX idx_consulta_status ON consulta (status);


-- =======================================================================================
-- INSERÇÃO DE DADOS DE EXEMPLO (OPCIONAL, mas útil para testes de desenvolvimento)
-- =======================================================================================

INSERT INTO consulta (paciente_id, paciente_nome, medico_id, medico_nome, data_hora, observacoes, status, data_criacao)
VALUES
    (101, 'Ana Silva', 201, 'Dr. João Pereira', '2025-12-15 10:00:00', 'Paciente com histórico de hipertensão. Necessário ajuste de dose.', 'AGENDADA', NOW()),
    (102, 'Bruno Costa', 202, 'Dra. Maria Oliveira', '2025-12-10 14:30:00', 'Check-up de rotina. Sem alterações relevantes.', 'REALIZADA', NOW()),
    (101, 'Ana Silva', 203, 'Dr. Carlos Souza', '2025-11-01 09:00:00', 'Motivo pessoal do médico. Agendamento cancelado e remarcado.', 'CANCELADA', NOW());