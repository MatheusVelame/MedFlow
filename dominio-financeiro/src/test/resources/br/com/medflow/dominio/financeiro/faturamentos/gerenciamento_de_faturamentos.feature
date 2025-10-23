Feature: Registro de faturamentos de consultas e exames

Como funcionário do setor financeiro,
Quero registrar os valores de consultas e exames,
Para controlar os faturamentos da clínica.

RN1 — O registro deve conter obrigatoriamente paciente, procedimento, valor e método de pagamento.

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Registrar faturamento com todos os campos obrigatórios
Given existe o paciente "PAC-001" cadastrado
And existe na tabela o procedimento "Consulta Clínica"
And o usuário "Ana Lima" (Administrador Financeiro) está autenticado
When a usuária registrar um faturamento com:
| paciente | PAC-001 |
| procedimento | Consulta Clínica |
| valor | 200.00 |
| metodo | Cartão |
| dataHora | 21/09/2025 10:05 |
Then o sistema deve criar o faturamento com status inicial "Pendente"
And o registro deve conter paciente, procedimento, valor e método de pagamento

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Impedir registro quando faltar método de pagamento
Given existe o paciente "PAC-002" cadastrado
And existe na tabela o procedimento "Exame Hemograma"
And o usuário "Carlos Souza" (Administrador Financeiro) está autenticado
When o usuário tentar registrar um faturamento com:
| paciente | PAC-002 |
| procedimento | Exame Hemograma |
| valor | 45.00 |
| metodo | |
| dataHora | 21/09/2025 10:15 |
Then o sistema deve rejeitar o registro informando que "método de pagamento é obrigatório"
And nenhum faturamento deve ser criado

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Registrar faturamento com valor compatível à tabela
Given existe o paciente "PAC-003" cadastrado
And a tabela de preços define "Ultrassom Abdominal" = 180.00
And o usuário "Mariana Reis" (Administrador Financeiro) está autenticado
When a usuária registrar um faturamento com:
| paciente | PAC-003 |
| procedimento | Ultrassom Abdominal |
| valor | 180.00 |
| metodo | Cartão |
| dataHora | 21/09/2025 10:30 |
Then o sistema deve aceitar o registro
And a validação automática de valor deve indicar "compatível com tabela"

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Bloquear registro com valor não positivo ou divergente sem justificativa
Given existe o paciente "PAC-004" cadastrado
And a tabela de preços define "Raio-X Tórax" = 95.00
And o usuário "Ana Lima" (Administrador Financeiro) está autenticado
When a usuária tentar registrar um faturamento com:
| paciente | PAC-004 |
| procedimento | Raio-X Tórax |
| valor | 0.00 |
| metodo | Dinheiro |
| dataHora | 21/09/2025 10:40 |
Then o sistema deve rejeitar o registro informando "valor deve ser positivo"
And nenhum faturamento deve ser criado

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Bloquear registro quando valor divergir da tabela sem justificativa
Given a tabela de preços define "Consulta Clínica" = 200.00
And existe o paciente "PAC-005" cadastrado
And o usuário "Carlos Souza" (Administrador Financeiro) está autenticado
When o usuário tentar registrar um faturamento com:
| paciente | PAC-005 |
| procedimento | Consulta Clínica |
| valor | 160.00 |
| metodo | Cartão |
| dataHora | 21/09/2025 10:50 |
| justificativa | |
Then o sistema deve rejeitar o registro informando "justificativa obrigatória para valor diferente da tabela"
And nenhum faturamento deve ser criado

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Forçar status inicial Pendente no ato do registro
Given existe o paciente "PAC-006" cadastrado
And a tabela de preços define "Exame Glicemia" = 30.00
And o usuário "Mariana Reis" (Administrador Financeiro) está autenticado
When a usuária registrar um faturamento com:
| paciente | PAC-006 |
| procedimento | Exame Glicemia |
| valor | 30.00 |
| metodo | Convênio |
| dataHora | 21/09/2025 11:00 |
Then o sistema deve criar o faturamento com status "Pendente" (independente de qualquer entrada de status)

@boundedcontext(Financeiro) @aggregate(Faturamento) @command(RegistrarFaturamento)
Scenario: Ignorar status enviado pelo cliente e manter "Pendente"
Given existe o paciente "PAC-007" cadastrado
And a tabela de preços define "Consulta Clínica" = 200.00
And o usuário "Ana Lima" (Administrador Financeiro) está autenticado
When a usuária enviar um registro com campo de status "Pago"
| paciente | PAC-007 |
| procedimento | Consulta Clínica |
| valor | 200.00 |
| metodo | Cartão |
| dataHora | 21/09/2025 11:10 |
| status | Pago |
Then o sistema deve ignorar o valor recebido para status e salvar como "Pendente"
And registrar em log que o status informado foi sobrescrito pela regra de negócio
