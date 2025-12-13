-- ===========================================================================
-- Script de Seed: Dados Iniciais para Desenvolvimento
-- Versão: V11
-- Descrição: Inserção de dados iniciais para testes e desenvolvimento
-- ===========================================================================

-- ===== PACIENTES =====
INSERT INTO pacientes (nome_paciente, cpf_paciente, data_nascimento_paciente, telefone_paciente, endereco_paciente) VALUES
('Maria Silva Santos', '12345678901', '1985-05-15', '11987654321', 'Rua das Flores, 123 - São Paulo, SP'),
('João Pedro Oliveira', '98765432100', '1990-08-22', '11976543210', 'Av. Paulista, 1000 - São Paulo, SP'),
('Ana Costa Ferreira', '11122233344', '1988-12-03', '11965432109', 'Rua Augusta, 500 - São Paulo, SP'),
('Carlos Mendes Lima', '55566677788', '1992-03-18', '11954321098', 'Rua Consolação, 200 - São Paulo, SP'),
('Juliana Santos', '99988877766', '1987-07-25', '11943210987', 'Av. Faria Lima, 1500 - São Paulo, SP');

-- ===== ESPECIALIDADES =====
INSERT INTO especialidades (id, nome, descricao, status, possui_vinculo_historico) VALUES
(1, 'Cardiologia', 'Doenças do coração', 'ATIVA', false),
(2, 'Pediatria', 'Crianças e adolescentes', 'ATIVA', false),
(3, 'Ortopedia', 'Sistema locomotor', 'ATIVA', false),
(4, 'Dermatologia', 'Doenças de pele', 'ATIVA', false);

-- ===== FUNCIONÁRIOS =====
INSERT INTO funcionarios (nome, funcao, contato, status) VALUES
('Dr. Carlos Mendes', 'Médico Cardiologista', '11987654321', 'ATIVO'),
('Dra. Ana Paula Costa', 'Médica Pediatra', '11976543210', 'ATIVO'),
('Dr. Roberto Lima', 'Médico Ortopedista', '11965432109', 'ATIVO'),
('Enf. Juliana Santos', 'Enfermeira', '11954321098', 'ATIVO'),
('Dra. Mariana Oliveira', 'Médica Dermatologista', '11943210987', 'ATIVO'),
('Dr. Pedro Augusto', 'Residente de Cirurgia', '11932109876', 'ATIVO');

-- ===== MÉDICOS (Tabela filha - herança JOINED) =====
INSERT INTO medicos (funcionario_id, crm_numero, crm_uf, especialidade_id, data_nascimento) VALUES
(1, '12345', 'SP', 1, '1975-03-10'),
(2, '23456', 'SP', 2, '1982-07-22'),
(3, '34567', 'SP', 3, '1978-11-05'),
(5, '45678', 'SP', 4, '1985-09-18');

-- ===== DISPONIBILIDADES DOS MÉDICOS =====
INSERT INTO medico_disponibilidades (medico_id, dia_semana, hora_inicio, hora_fim) VALUES
(1, 'Segunda', '08:00:00', '12:00:00'),
(1, 'Segunda', '14:00:00', '18:00:00'),
(1, 'Quarta', '08:00:00', '12:00:00'),
(2, 'Terça', '08:00:00', '12:00:00'),
(2, 'Quinta', '14:00:00', '18:00:00'),
(3, 'Segunda', '14:00:00', '18:00:00'),
(3, 'Sexta', '08:00:00', '12:00:00'),
(5, 'Quarta', '08:00:00', '12:00:00'),
(5, 'Sexta', '14:00:00', '18:00:00');

-- ===== CONVÊNIOS =====
INSERT INTO convenios (nome, codigo_identificacao, status) VALUES
('Unimed', 'UNI001', 'ATIVO'),
('Bradesco Saúde', 'BRA001', 'ATIVO'),
('Amil', 'AMI001', 'ATIVO'),
('SulAmérica', 'SUL001', 'ATIVO');

-- ===== TIPOS DE EXAMES =====
INSERT INTO tipos_exames (codigo, descricao, especialidade, valor, status) VALUES
('EX001', 'Hemograma Completo', 'Laboratorial', 50.00, 'ATIVO'),
('EX002', 'Glicemia de Jejum', 'Laboratorial', 25.00, 'ATIVO'),
('EX003', 'Raio-X de Tórax', 'Imagem', 80.00, 'ATIVO'),
('EX004', 'Eletrocardiograma', 'Cardiologia', 120.00, 'ATIVO'),
('EX005', 'Ultrassom Abdominal', 'Imagem', 150.00, 'ATIVO');

-- ===== MEDICAMENTOS =====
INSERT INTO medicamentos (nome, uso_principal, contraindicacoes, status) VALUES
('Paracetamol 500mg', 'Analgésico e antitérmico', 'Hipersensibilidade ao paracetamol', 'ATIVO'),
('Dipirona 500mg', 'Analgésico e antitérmico', 'Hipersensibilidade à dipirona, porfiria', 'ATIVO'),
('Ibuprofeno 400mg', 'Anti-inflamatório não esteroidal', 'Úlcera péptica ativa, insuficiência renal grave', 'ATIVO'),
('Amoxicilina 500mg', 'Antibiótico', 'Hipersensibilidade à penicilina', 'ATIVO'),
('Omeprazol 20mg', 'Protetor gástrico', 'Hipersensibilidade ao omeprazol', 'ATIVO');

-- ===== FOLHAS DE PAGAMENTO =====
INSERT INTO folha_pagamento (funcionario_id, periodo_referencia, tipo_registro, salario_base, beneficios, metodo_pagamento, tipo_vinculo, status) VALUES
-- Dr. Carlos Mendes (Médico Cardiologista - CLT)
(1, '10/2024', 'PAGAMENTO', 15000.00, 1500.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(1, '11/2024', 'PAGAMENTO', 15000.00, 1500.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(1, '12/2024', 'PAGAMENTO', 15000.00, 1500.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),

-- Dra. Ana Paula Costa (Médica Pediatra - CLT)
(2, '10/2024', 'PAGAMENTO', 12000.00, 1200.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '11/2024', 'PAGAMENTO', 12000.00, 1200.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '12/2024', 'PAGAMENTO', 12000.00, 1200.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),

-- Dr. Roberto Lima (Médico Ortopedista - CLT)
(3, '10/2024', 'PAGAMENTO', 14000.00, 1400.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(3, '11/2024', 'PAGAMENTO', 14000.00, 1400.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(3, '12/2024', 'PAGAMENTO', 14000.00, 1400.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),

-- Enf. Juliana Santos (Enfermeira - CLT)
(4, '10/2024', 'PAGAMENTO', 5000.00, 500.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(4, '11/2024', 'PAGAMENTO', 5000.00, 500.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(4, '12/2024', 'PAGAMENTO', 5000.00, 500.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),

-- Dra. Mariana Oliveira (Médica Dermatologista - PJ - SEM descontos)
(5, '10/2024', 'PAGAMENTO', 18000.00, 0.00, 'PIX', 'PJ', 'PAGO'),
(5, '11/2024', 'PAGAMENTO', 18000.00, 0.00, 'PIX', 'PJ', 'PAGO'),
(5, '12/2024', 'PAGAMENTO', 18000.00, 0.00, 'PIX', 'PJ', 'PENDENTE'),

-- Dr. Pedro Augusto (Residente - Estagiário - SEM descontos)
(6, '10/2024', 'PAGAMENTO', 3000.00, 300.00, 'Transferência Bancária', 'ESTAGIARIO', 'PAGO'),
(6, '11/2024', 'PAGAMENTO', 3000.00, 300.00, 'Transferência Bancária', 'ESTAGIARIO', 'PAGO'),
(6, '12/2024', 'PAGAMENTO', 3000.00, 300.00, 'Transferência Bancária', 'ESTAGIARIO', 'PENDENTE'),

-- Ajustes e Bonificações (sempre SEM descontos)
(1, '10/2024', 'AJUSTE', 2000.00, 0.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(1, '11/2024', 'AJUSTE', 1800.00, 0.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '11/2024', 'AJUSTE', 1500.00, 0.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '12/2024', 'AJUSTE', 1200.00, 0.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),
(4, '10/2024', 'AJUSTE', 800.00, 0.00, 'Transferência Bancária', 'CLT', 'CANCELADO'),
(3, '11/2024', 'AJUSTE', 2500.00, 0.00, 'Transferência Bancária', 'CLT', 'PAGO');

-- 9. FATURAMENTOS (Com UUIDs e Valor > 0)
INSERT INTO faturamentos (
    id, paciente_id, tipo_procedimento, descricao_procedimento, valor,
    metodo_pagamento, status, data_hora_faturamento, usuario_responsavel, observacoes
) VALUES
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 'CONSULTA', 'Consulta Cardiológica', 450.00,
    'PIX', 'PAGO', '2024-10-02 09:00:00', 'user-admin-001', 'Pagamento à vista'
),
(
    'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 1, 'EXAME', 'Ecocardiograma', 280.00,
    'CARTAO_DEBITO', 'PAGO', '2024-10-02 10:00:00', 'user-admin-001', NULL
),
(
    'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 2, 'CONSULTA', 'Consulta Pediátrica', 300.00,
    'DINHEIRO', 'PAGO', '2024-10-05 14:30:00', 'user-recepcao-002', 'Mãe pagou em espécie'
),
(
    'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 3, 'EXAME', 'Raio-X Torax', 120.00,
    'CARTAO_CREDITO', 'PAGO', '2024-10-10 11:00:00', 'user-recepcao-002', 'Parcelado 1x'
),
(
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 1, 'EXAME', 'Holter 24h', 180.00,
    'CARTAO_CREDITO', 'PENDENTE', '2024-12-15 08:30:00', 'user-admin-001', 'Aguardando autorização'
),
(
    'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 3, 'CONSULTA', 'Cirurgia Ortopédica (Sinal)', 2500.00,
    'BOLETO', 'PENDENTE', '2024-12-22 07:00:00', 'user-financeiro-003', 'Boleto vence dia 20'
),
(
    'a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 5, 'CONSULTA', 'Retorno Médico', 0.01, -- CORRIGIDO DE 0.00 PARA 0.01
    'CONVENIO', 'PENDENTE', '2024-12-28 09:00:00', 'user-recepcao-002', 'Retorno (Valor simbólico)'
);

-- 10. RESTART DA SEQUENCIA (Segurança)
ALTER TABLE folha_pagamento ALTER COLUMN id RESTART WITH 25;