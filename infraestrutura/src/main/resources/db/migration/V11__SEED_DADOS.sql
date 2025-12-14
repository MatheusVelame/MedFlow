-- ===========================================================================
-- Script de Seed: Dados Iniciais para Desenvolvimento (CORRIGIDO V2)
-- Versão: V11
-- Descrição: Ajustado para respeitar CONSTRAINT (Apenas CONSULTA/EXAME permitidos)
-- ===========================================================================

-- ===== PACIENTES =====
INSERT INTO pacientes (nome_paciente, cpf_paciente, data_nascimento_paciente, telefone_paciente, endereco_paciente) VALUES
('Maria Silva Santos', '12345678901', '1985-05-15', '11987654321', 'Rua das Flores, 123 - São Paulo, SP'),
('João Pedro Oliveira', '98765432100', '1990-08-22', '11976543210', 'Av. Paulista, 1000 - São Paulo, SP'),
('Ana Costa Ferreira', '11122233344', '1988-12-03', '11965432109', 'Rua Augusta, 500 - São Paulo, SP'),
('Carlos Mendes Lima', '55566677788', '1992-03-18', '11954321098', 'Rua Consolação, 200 - São Paulo, SP'),
('Juliana Santos', '99988877766', '1987-07-25', '11943210987', 'Av. Faria Lima, 1500 - São Paulo, SP'),
('Roberto Almeida', '44455566677', '1980-01-10', '11933334444', 'Rua Pamplona, 800 - SP');

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
('Pedro Augusto', 'Estagiario', '11932109876', 'ATIVO');

-- ===== MÉDICOS =====
INSERT INTO medicos (funcionario_id, crm_numero, crm_uf, especialidade_id, data_nascimento) VALUES
(1, '12345', 'SP', 1, '1975-03-10'),
(2, '23456', 'SP', 2, '1982-07-22'),
(3, '34567', 'SP', 3, '1978-11-05'),
(5, '45678', 'SP', 4, '1985-09-18');

-- ===== DISPONIBILIDADES =====
INSERT INTO medico_disponibilidades (medico_id, dia_semana, hora_inicio, hora_fim) VALUES
(1, 'Segunda', '08:00:00', '18:00:00'),
(1, 'Quarta', '08:00:00', '18:00:00'),
(2, 'Terça', '08:00:00', '12:00:00'),
(2, 'Quinta', '14:00:00', '18:00:00'),
(3, 'Segunda', '14:00:00', '18:00:00'),
(3, 'Sexta', '08:00:00', '18:00:00');

-- ===== CONVÊNIOS =====
INSERT INTO convenios (nome, codigo_identificacao, status) VALUES
('Unimed', 'UNI001', 'ATIVO'),
('Bradesco Saúde', 'BRA001', 'ATIVO'),
('Amil', 'AMI001', 'ATIVO');

-- ===== TIPOS DE EXAMES =====
INSERT INTO tipos_exames (codigo, descricao, especialidade, valor, status) VALUES
('EX001', 'Hemograma Completo', 'Laboratorial', 50.00, 'ATIVO'),
('EX002', 'Glicemia de Jejum', 'Laboratorial', 25.00, 'ATIVO'),
('EX003', 'Raio-X de Tórax', 'Imagem', 80.00, 'ATIVO'),
('EX004', 'Eletrocardiograma', 'Cardiologia', 120.00, 'ATIVO'),
('EX005', 'Ultrassom Abdominal', 'Imagem', 150.00, 'ATIVO'),
('EX006', 'Ressonância Magnética', 'Imagem', 800.00, 'ATIVO');

-- ===== MEDICAMENTOS =====
INSERT INTO medicamentos (nome, uso_principal, contraindicacoes, status) VALUES
('Paracetamol 500mg', 'Analgésico', 'Hipersensibilidade', 'ATIVO'),
('Dipirona 1g', 'Analgésico', 'Alergia', 'ATIVO'),
('Morfina', 'Dor intensa', 'Vários', 'ATIVO'),
('Soro Fisiológico', 'Hidratação', 'Nenhuma', 'ATIVO');

-- ===== FOLHAS DE PAGAMENTO =====
INSERT INTO folha_pagamento (funcionario_id, periodo_referencia, tipo_registro, salario_base, beneficios, metodo_pagamento, tipo_vinculo, status) VALUES
(1, '10/2024', 'PAGAMENTO', 12000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '10/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(3, '10/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(1, '11/2024', 'PAGAMENTO', 12000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(2, '11/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(3, '11/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PAGO'),
(1, '12/2024', 'PAGAMENTO', 12000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),
(2, '12/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PENDENTE'),
(3, '12/2024', 'PAGAMENTO', 10000.00, 1000.00, 'Transferência Bancária', 'CLT', 'PENDENTE');

-- ===== CONSULTAS =====
INSERT INTO consultas (paciente_id, medico_id, data_hora, status, descricao) VALUES
(1, 1, '2025-12-13 15:00:00', 'AGENDADA', 'Check-up anual'),
(2, 2, '2025-12-13 16:30:00', 'AGENDADA', 'Dor de garganta'),
(3, 3, '2025-12-13 17:00:00', 'AGENDADA', 'Retorno ortopedia'),
(4, 1, '2025-12-13 18:00:00', 'AGENDADA', 'Avaliação pré-cirúrgica'),
(5, 5, '2025-12-14 09:00:00', 'AGENDADA', 'Consulta dermatologia'),
(1, 3, '2025-12-15 11:00:00', 'AGENDADA', 'Fisioterapia'),
(2, 2, '2025-12-16 10:00:00', 'AGENDADA', 'Vacinação'),
(3, 1, '2025-12-17 08:00:00', 'AGENDADA', 'Exames rotina'),
(4, 3, '2025-12-18 09:30:00', 'AGENDADA', 'Dor no joelho');

-- ===== FATURAMENTOS =====
-- CORREÇÃO: Todos os tipos_procedimento foram alterados para 'CONSULTA' ou 'EXAME'
INSERT INTO faturamentos (
    id, paciente_id, tipo_procedimento, descricao_procedimento, valor,
    metodo_pagamento, status, data_hora_faturamento, usuario_responsavel, observacoes
) VALUES
-- Dia 1
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 'CONSULTA', 'Consulta Cardiológica', 450.00, 'PIX', 'PAGO', '2025-12-10 09:00:00', 'user-admin', 'Pagamento à vista'),
-- Dia 2 (Cirurgia catalogada como CONSULTA para fins de faturamento alto)
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 2, 'CONSULTA', 'Cirurgia Ortopédica Joelho', 15000.00, 'CONVENIO', 'PAGO', '2025-12-08 10:00:00', 'user-admin', 'Autorizado Bradesco'),
-- Dia 3
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 3, 'EXAME', 'Bateria de Exames Completa', 850.00, 'CARTAO_CREDITO', 'PAGO', '2025-12-09 14:30:00', 'user-recepcao', NULL),
-- Dia 4 (Repasse Convênio catalogado como CONSULTA)
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 1, 'CONSULTA', 'Repasse Mensal Unimed', 45000.00, 'TRANSFERENCIA', 'PAGO', '2025-12-10 11:00:00', 'user-financeiro', 'Lote #5542'),
-- Dia 5 (Procedimento Estético catalogado como EXAME)
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 5, 'EXAME', 'Harmonização Facial', 3500.00, 'CARTAO_CREDITO', 'PAGO', '2025-12-11 15:30:00', 'user-admin', 'Parcelado 10x'),
-- Dia 6
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 4, 'CONSULTA', 'Consulta Particular', 600.00, 'PIX', 'PAGO', '2025-12-11 09:00:00', 'user-recepcao', NULL),
-- Dia 7 (Pequena Cirurgia como CONSULTA)
('a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 2, 'CONSULTA', 'Pequena Cirurgia', 2500.00, 'BOLETO', 'PENDENTE', '2025-12-12 10:00:00', 'user-financeiro', 'Vence dia 30'),
-- Dia 8
('b7eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 3, 'EXAME', 'Ressonância Magnética', 1200.00, 'CARTAO_DEBITO', 'PAGO', '2025-12-13 11:00:00', 'user-recepcao', NULL),
-- Dia 9
('c8eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 1, 'CONSULTA', 'Consulta Rotina', 400.00, 'DINHEIRO', 'PAGO', '2025-12-20 09:00:00', 'user-recepcao', NULL),
('d9eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 5, 'CONSULTA', 'Consulta Dermatologia', 500.00, 'PIX', 'PAGO', '2025-12-20 14:00:00', 'user-recepcao', NULL);

-- 10. RESTART DA SEQUENCIA
ALTER TABLE folha_pagamento ALTER COLUMN id RESTART WITH 50;