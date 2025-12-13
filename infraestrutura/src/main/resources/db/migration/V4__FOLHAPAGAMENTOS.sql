-- Migration: Criar tabela folha_pagamento
-- Database: H2

CREATE TABLE folha_pagamento (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    funcionario_id INTEGER NOT NULL,
    periodo_referencia VARCHAR(7) NOT NULL,
    tipo_registro VARCHAR(20) NOT NULL,
    salario_base DECIMAL(10,2) NOT NULL,
    beneficios DECIMAL(10,2) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,
    tipo_vinculo VARCHAR(20) NOT NULL DEFAULT 'CLT',  -- ← ADICIONE ESTA LINHA
    status VARCHAR(20) NOT NULL,

    -- Constraints
    CONSTRAINT chk_tipo_registro CHECK (tipo_registro IN ('PAGAMENTO', 'AJUSTE')),
    CONSTRAINT chk_tipo_vinculo CHECK (tipo_vinculo IN ('CLT', 'ESTAGIARIO', 'PJ')),  -- ← ADICIONE ESTA LINHA
    CONSTRAINT chk_status CHECK (status IN ('PENDENTE', 'PAGO', 'CANCELADO')),
    CONSTRAINT chk_salario_positivo CHECK (salario_base > 0),
    CONSTRAINT chk_beneficios_nao_negativo CHECK (beneficios >= 0)
);

-- Índices
CREATE INDEX idx_folha_funcionario ON folha_pagamento(funcionario_id);
CREATE INDEX idx_folha_periodo ON folha_pagamento(periodo_referencia);
CREATE INDEX idx_folha_status ON folha_pagamento(status);

-- Índice único para evitar duplicatas de PAGAMENTO
CREATE UNIQUE INDEX idx_folha_unico_pagamento
    ON folha_pagamento(funcionario_id, periodo_referencia, tipo_registro);