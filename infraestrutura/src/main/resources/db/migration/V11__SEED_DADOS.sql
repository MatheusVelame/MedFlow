-- ===========================================================================
-- Script de Seed: Dados Iniciais para Desenvolvimento
-- Versão: V10
-- Descrição: Inserção de dados iniciais para testes e desenvolvimento
-- ===========================================================================

-- ===== PACIENTES =====
INSERT INTO pacientes (nome_paciente, cpf_paciente, data_nascimento_paciente, telefone_paciente, endereco_paciente) VALUES
('Maria Silva Santos', '12345678901', '1985-05-15', '11987654321', 'Rua das Flores, 123 - São Paulo, SP'),
('João Pedro Oliveira', '98765432100', '1990-08-22', '11976543210', 'Av. Paulista, 1000 - São Paulo, SP'),
('Ana Costa Ferreira', '11122233344', '1988-12-03', '11965432109', 'Rua Augusta, 500 - São Paulo, SP'),
('Carlos Mendes Lima', '55566677788', '1992-03-18', '11954321098', 'Rua Consolação, 200 - São Paulo, SP'),
('Juliana Santos', '99988877766', '1987-07-25', '11943210987', 'Av. Faria Lima, 1500 - São Paulo, SP');

-- ===== FUNCIONÁRIOS =====
INSERT INTO funcionarios (nome, funcao, contato, status) VALUES
('Dr. Carlos Mendes', 'Médico Cardiologista', '11987654321', 'ATIVO'),
('Dra. Ana Paula Costa', 'Médica Pediatra', '11976543210', 'ATIVO'),
('Dr. Roberto Lima', 'Médico Ortopedista', '11965432109', 'ATIVO'),
('Enf. Juliana Santos', 'Enfermeira', '11954321098', 'ATIVO'),
('Dra. Mariana Oliveira', 'Médica Dermatologista', '11943210987', 'FERIAS');

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
