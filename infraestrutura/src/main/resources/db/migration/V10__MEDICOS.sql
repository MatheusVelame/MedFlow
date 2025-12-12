-- Tabela de médicos (herança JOINED de funcionarios)
CREATE TABLE IF NOT EXISTS medicos (
    funcionario_id INT PRIMARY KEY,
    crm_numero VARCHAR(20) NOT NULL,
    crm_uf CHAR(2) NOT NULL,
    especialidade_id INT NOT NULL,
    data_nascimento DATE,

    -- FK para funcionarios (herança JOINED)
    CONSTRAINT fk_medico_funcionario
        FOREIGN KEY (funcionario_id)
        REFERENCES funcionarios(id)
        ON DELETE CASCADE,

    -- FK para especialidades (tabela já existe!)
    CONSTRAINT fk_medico_especialidade
        FOREIGN KEY (especialidade_id)
        REFERENCES especialidades(id),

    -- CRM deve ser único (número + UF)
    CONSTRAINT uk_medico_crm
        UNIQUE (crm_numero, crm_uf)
);

-- Índices para performance
CREATE INDEX idx_medico_especialidade ON medicos(especialidade_id);
CREATE INDEX idx_medico_crm ON medicos(crm_numero, crm_uf);

-- =====================================================================
-- TABELA DE DISPONIBILIDADES
-- =====================================================================

CREATE TABLE IF NOT EXISTS medico_disponibilidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,

    -- FK para médicos
    CONSTRAINT fk_disponibilidade_medico
        FOREIGN KEY (medico_id)
        REFERENCES medicos(funcionario_id)
        ON DELETE CASCADE,

    -- Validação de horário
    CONSTRAINT ck_disponibilidade_horario
        CHECK (hora_fim > hora_inicio)
);

-- Índice
CREATE INDEX idx_disponibilidade_medico ON medico_disponibilidades(medico_id);