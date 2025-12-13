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
('Dra. Mariana Oliveira', 'Médica Dermatologista', '11943210987', 'ATIVO');

-- ===== MÉDICOS (Tabela filha - herança JOINED) =====
-- IMPORTANTE: Funcionários 1, 2, 3 e 5 são médicos (4 é enfermeira)
INSERT INTO medicos (funcionario_id, crm_numero, crm_uf, especialidade_id, data_nascimento) VALUES
(1, '12345', 'SP', 1, '1975-03-10'),  -- Dr. Carlos Mendes - Cardiologia
(2, '23456', 'SP', 2, '1982-07-22'),  -- Dra. Ana Paula Costa - Pediatria
(3, '34567', 'SP', 3, '1978-11-05'),  -- Dr. Roberto Lima - Ortopedia
(5, '45678', 'SP', 4, '1985-09-18');  -- Dra. Mariana Oliveira - Dermatologia

-- ===== DISPONIBILIDADES DOS MÉDICOS =====
INSERT INTO medico_disponibilidades (medico_id, dia_semana, hora_inicio, hora_fim) VALUES
-- Dr. Carlos Mendes (Cardiologia - ID 1)
(1, 'Segunda', '08:00:00', '12:00:00'),
(1, 'Segunda', '14:00:00', '18:00:00'),
(1, 'Quarta', '08:00:00', '12:00:00'),

-- Dra. Ana Paula Costa (Pediatria - ID 2)
(2, 'Terça', '08:00:00', '12:00:00'),
(2, 'Quinta', '14:00:00', '18:00:00'),

-- Dr. Roberto Lima (Ortopedia - ID 3)
(3, 'Segunda', '14:00:00', '18:00:00'),
(3, 'Sexta', '08:00:00', '12:00:00'),

-- Dra. Mariana Oliveira (Dermatologia - ID 5)
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