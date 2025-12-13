-- Migration: Criar tabela de histórico de especialidades
-- Cria tabela 'especialidade_historico' usada pela aplicação para auditoria de alterações de especialidades

CREATE TABLE especialidade_historico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    especialidade_id INT NOT NULL,
    campo VARCHAR(100),
    valor_anterior VARCHAR(255),
    novo_valor VARCHAR(255),
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(50) NOT NULL,

    CONSTRAINT fk_especialidade_historico_especialidade FOREIGN KEY (especialidade_id)
        REFERENCES especialidades(id) ON DELETE CASCADE
);

CREATE INDEX idx_especialidade_historico_especialidade_id ON especialidade_historico(especialidade_id);
CREATE INDEX idx_especialidade_historico_data_hora ON especialidade_historico(data_hora);
