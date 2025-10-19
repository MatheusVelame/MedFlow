Feature: Gerenciamento de Consultas

# 1. Marcar Consulta

# Regra de Negócio: A consulta só pode ser marcada se o horário estiver disponível na agenda do médico.

  Scenario: Marcação de consulta com horário disponível (Sucesso)

    Given que o usuário "Julia" tem permissão de recepcionista
    And o médico "Dr. Eduardo" tem o horário "09:00" do dia "05/01/2026" livre
    When o usuário "Julia" tentar marcar uma consulta com "Dr. Eduardo" para o dia "05/01/2026"" às "09:00"
    Then o sistema deve registrar a consulta com sucesso
    And o sistema deve atualizar a agenda do "Dr. Eduardo", marcando "09:00" como ocupado

  Scenario: Falha na marcação devido a conflito de horário (Falha)

    Given que o usuário "Julia" tem permissão de recepcionista
    And o médico "Dr. Eduardo" já tem uma consulta agendada para o dia "08/01/2026" às "10:30"
    When o usuário "Julia" tentar marcar uma nova consulta com "Dr. Eduardo" para o dia "08/01/2026" às "10:30"
    Then o sistema deve impedir a marcação da consulta
    And o sistema deve informar que o horário "10:30" já está ocupado
    And nenhuma notificação deve ser enviada

# Regra de Negócio: Paciente e médico devem estar previamente cadastrados no sistema.

  Scenario: Marcação com paciente e médico cadastrados (Sucesso)

    Given que o usuário "Julia" tem permissão de recepcionista
    And o paciente "Pedro Alves" está cadastrado no sistema
    And o médico "Dra. Helena" está cadastrado no sistema
    When o usuário "Julia" tentar marcar uma consulta com "Dra. Helena" para o paciente "Pedro Alves"
    Then o sistema deve prosseguir com a marcação da consulta

  Scenario: Tentativa de marcação com paciente não cadastrado (Falha)

    Given que o usuário "Julia" tem permissão de recepcionista
    And o médico "Dr. Bruno" está cadastrado no sistema
    And o paciente "Junio Torresmo" não está cadastrado no sistema
    When o usuário "Julia" tentar marcar uma consulta com "Dr. Bruno" para o paciente "Junio Torresmo"
    Then o sistema deve impedir a marcação da consulta
    And o sistema deve solicitar o cadastro prévio do paciente "Junio Torresmo"
    And o Status da consulta não deve ser atualizado

  Scenario: Tentativa de marcação com médico não cadastrado (Falha)

    Given que o usuário "Julia" tem permissão de recepcionista
    And o paciente "Joana Lima" está cadastrado no sistema
    And o médico "Dr. House" não está cadastrado no sistema
    When o usuário "Julia" tentar marcar uma consulta com "Dr. House" para o paciente "Joana Lima"
    Then o sistema deve impedir a marcação da consulta
    And o sistema deve informar que o médico "Dr. House" não foi encontrado
    And o Status da consulta não deve ser atualizado

# Regra de Negócio: A data da consulta não pode ser anterior à data atual.

  Scenario: Marcação com data futura (Sucesso)

    Given que o usuário "Julia" tem permissão de recepcionista
    And a data da consulta é "11/12/2025" às "15:00"
    And a data atual é "12/10/2025"
    When o usuário "Julia" tentar marcar uma consulta para a data "11/12/2025"
    Then o sistema deve prosseguir com a marcação

  Scenario: Tentativa de marcação com data passada (Falha)

    Given que o usuário "Julia" tem permissão de recepcionista
    And a data da consulta é "11/01/2025"
    And a data atual é "12/10/2025"
    When o usuário "Julia" tentar marcar uma consulta para a data "11/01/2025"
    Then o sistema deve impedir a marcação da consulta
    And o sistema deve informar que não é possível agendar consultas para datas passadas
    And o Status da consulta não deve ser atualizado

 # Regra de Negócio: O sistema deve validar se o médico atende à especialidade escolhida.

  Scenario: Marcação com especialidade compatível (Sucesso)

	Given que o usuário "Julia" tem permissão de recepcionista
    And que o médico "Dra. Helena" tem a especialidade "Dermatologia" cadastrada em seu perfil
    When o usuário "Julia" tentar marcar uma consulta com "Dra. Helena" na especialidade "Dermatologia"
    Then o sistema deve prosseguir com a marcação

  Scenario: Tentativa de marcação com especialidade incompatível (Falha)

	Given que o usuário "Julia" tem permissão de recepcionista
    And que o médico "Dr. Bruno" tem a especialidade principal "Ortopedia"
    And a especialidade solicitada é "Ginecologia"
    When o usuário "Julia" tentar marcar uma consulta com "Dr. Bruno" na especialidade "Ginecologia"
    Then o sistema deve impedir a marcação da consulta
    And o sistema deve informar que o "Dr. Bruno" não atende "Ginecologia"
    And o Status da consulta não deve ser atualizado

 # Regra de Negócio: Após a marcação, deve ser enviada notificação de confirmação para paciente e médico (e-mail ou SMS).

  Scenario: Envio de notificação por e-mail (Sucesso - Preferência do Paciente)

    Given que a consulta foi marcada com sucesso
    And o paciente "Ana Silva" tem a preferência de notificação por "E-mail"
    And o médico "Dr. Eduardo" tem a preferência de notificação por "E-mail"
    When o sistema enviar a confirmação de agendamento
    Then o paciente "Ana Silva" deve receber uma confirmação por e-mail
    And o médico "Dr. Eduardo" deve receber uma confirmação por e-mail

  Scenario: Envio de notificação por SMS (Sucesso - Preferência Mista)

    Given que a consulta foi marcada com sucesso
    And o paciente "Pedro Alves" tem a preferência de notificação por "SMS"
    And o médico "Dra. Helena" tem a preferência de notificação por "E-mail"
    When o sistema enviar a confirmação de agendamento
    Then o paciente "Pedro Alves" deve receber uma confirmação via SMS
    And o médico "Dra. Helena" deve receber uma confirmação por e-mail
    


# 2. Remarcar Consulta

 # Regra de Negócio: O sistema deve registrar histórico da remarcação (data/hora originais e novos).

  Scenario: Registro completo de histórico após a primeira remarcação (Sucesso)

    Given que o usuário "Maria" tem permissão para remarcar consultas
    And uma consulta do paciente "João" está agendada para o dia "20/12/2025" às "14:00"
    And nenhuma consulta está agendada para o dia "28/12/2025" às "16:00"
    When o usuário "Maria" remarcar a consulta do paciente "João" para o dia "28/12/2025" às "16:00"
    Then o sitstema deverá remarcar a consulta do paciente "João" para o dia "28/12/2025" às "16:00"
    And o histórico da consulta deve conter uma entrada registrando a alteração
    And o histórico de remarcações deve ser consultável no prontuário do paciente e na agenda do médico

  Scenario: Data da consulta remarcada já em uso (Falha)

    Given que o usuário "Maria" tem permissão para remarcar consultas
    And uma consulta do paciente "João" está agendada para o dia "20/12/2025" às "14:00"
    And uma consulta do paciente "Gabriel" está agendada para o dia "28/12/2025" às "16:00"
    When o usuário "Maria" remarcar a consulta do paciente "João" para o dia "28/12/2025" às "16:00"
    Then o sitstema deve informar que já existe uma consulta marcada para o dia e o horário escolhido
    And a consulta não deve ser remarcada
    And o histórico não deve ser alterado

# Regra de Negócio: Limite de remarcações por consulta: no máximo 2 vezes.

  Scenario: Segunda e última remarcação permitida (Sucesso)

    Given que o usuário "João" tem permissão para remarcar consultas
    And uma consulta já foi remarcada "1" vez
    When o usuário "João" tentar remarcar a consulta pela segunda vez
    Then o sistema deve registrar a remarcação com sucesso
    And o campo "Quantidade de remarcações já feitas" deve ser atualizado para "2"
    And o sistema deve informar que o limite de remarcações foi atingido

  Scenario: Tentativa de remarcação após o limite (Falha)

    Given que o usuário "João" tem permissão para remarcar consultas
    And uma consulta já foi remarcada "2" vezes
    When o usuário "João" tentar remarcar a consulta pela terceira vez
    Then o sistema deve impedir a remarcação da consulta
    And o sistema deve informar que o limite máximo de 2 remarcações foi atingido
    And o Status e o Histórico da consulta não devem ser atualizados

# Regra de Negócio: Notificações devem ser enviadas ao paciente e ao médico sobre a alteração.

  Scenario: Envio de notificação de alteração para paciente e médico (Sucesso)

    Given que a remarcação da consulta foi concluída com sucesso
    And o paciente "Ana Silva" tem preferência por "E-mail"
    And o médico "Dr. Eduardo" tem preferência por "SMS"
    When o sistema enviar a notificação de remarcação
    Then o paciente "Ana Silva" deve receber uma notificação de alteração por e-mail
    And o médico "Dr. Eduardo" deve receber uma notificação de alteração via SMS
    And a notificação deve incluir a Data e Hora Originais e a Nova Data e Hora

 # Regra de Negócio: Remarcações feitas com menos de 24h de antecedência devem seguir regra específica (ex.: bloquear ou cobrar taxa).

  Scenario: Remarcação dentro do prazo (mais de 24h de antecedência) (Sucesso)

    Given que o usuário "João" tem permissão para remarcar consultas
    And a data e hora atual é "20/12/2025" às "10:00"
    And a consulta agendada é para "22/12/2025" às "14:00"
    When o usuário "João" tentar remarcar a consulta
    Then o sistema deve processar a remarcação normalmente
    And não deve ser aplicada nenhuma taxa ou bloqueio

  Scenario: Tentativa de remarcação de última hora (Falha - Bloqueio)

    Given que o usuário "João" tem permissão para remarcar consultas
    And a data e hora atual é "21/12/2025" às "10:00"
    And a consulta agendada é para "22/12/2025" às "09:00"
    When o usuário "João" tentar remarcar a consulta
    Then o sistema deve impedir a remarcação da consulta
    And o sistema deve informar que a remarcação só é possível com mais de 24h de antecedência

  Scenario: Tentativa de remarcação de última hora (menos de 24h) (Cenário Alternativo - Taxa)

    Given que o usuário "João" tem permissão para remarcar consultas
    And a data e hora atual é "21/12/2025" às "10:00"
    And a consulta agendada é para "22/12/2025" às "14:00" 
    When o usuário "João" tentar remarcar a consulta
    Then o sistema deve alertar sobre a aplicação de uma taxa de 50% do valor da consulta
    And o sistema deve exigir a confirmação do usuário para prosseguir com a remarcação e a cobrança
    
 # 3. Cancelar Consulta

 # Regra de Negócio: Cancelamento só permitido até 24h antes da consulta.

  Scenario: Cancelamento realizado com antecedência (Sucesso)

	Given que o usuário "João" tem permissão para cancelar consultas
    And que a política de cancelamento é de 24 horas
    And a consulta está marcada para "16/01/2026" às "14:00"
    And a data atual do sistema é "16/01/2026" às "16:00"
    When o usuário "João" tentar cancelar a consulta
    Then o sistema deve permitir o cancelamento normalmente

  Scenario: Tentativa de cancelamento de última hora (menos de 24h) (Falha)

	Given que o usuário "João" tem permissão para cancelar consultas
    And que a política de cancelamento é de 24 horas
    And a consulta está marcada para "16/01/2026" às "09:00"
    And a data atual do sistema é "18/01/2026" às "16:00"
    When o usuário "João" tentar cancelar a consulta
    Then o sistema deve impedir o cancelamento
    And o sistema deve informar que o prazo limite de 24 horas foi excedido
    And o Status da consulta deve permanecer "Agendada"

 # Regra de Negócio: Deve ser registrado motivo do cancelamento.

  Scenario: Cancelamento com registro do motivo (Sucesso)

	Given que o usuário "Gabriel" tem permissão para cancelar consultas
    And que o cancelamento da consulta do paciente "Pedro" foi permitido
    When o usuário "Gabriel" cancelar a consulta do paciente "Pedro" e preencher o motivo como "Compromisso pessoal inadiável"
    Then o sistema deve registrar o motivo "Compromisso pessoal inadiável" no histórico da consulta
    And o histórico deve indicar o responsável pelo cancelamento ("Gabriel")
    And o Status da consulta deve ser alterada para "Cancelada"

  Scenario: Tentativa de cancelamento sem preencher o motivo (Falha)

	Given que o usuário "Gabriel" tem permissão para cancelar consultas
    And que o cancelamento da consulta do paciente "Pedro" foi permitido
    When o usuário "Gabriel" tentar cancelar a consulta do paciente "Pedro" e deixar o campo "Motivo do cancelamento" em branco
    Then o sistema deve impedir o cancelamento
    And o sistema deve informar que o motivo é obrigatório
    And o Status da consulta deve permanecer "Agendada"

 # Regra de Negócio: Ao cancelar, o horário do médico deve ser liberado automaticamente.

  Scenario: Liberação automática do horário (Sucesso)

    Given que a consulta de "Ana Silva" com o "Dr. Eduardo" foi cancelada com sucesso
    And a consulta estava agendada para "25/11/2025" às "16:00"
    When o sistema processar o cancelamento
    Then o horário "16:00" do dia "25/11/2025" deve ser marcado como "Livre" na agenda do "Dr. Eduardo"
    And uma nova marcação de consulta para este horário deve ser permitida

 # Regra de Negócio: Notificação deve ser enviada ao paciente e ao médico.

  Scenario: Envio de notificação de cancelamento para paciente e médico (Sucesso)

    Given que a consulta foi cancelada com sucesso
    And o paciente "Ana Silva" tem preferência por "E-mail"
    And o médico "Dr. Eduardo" tem preferência por "SMS"
    When o sistema enviar a notificação de cancelamento
    Then o paciente "Ana Silva" deve receber uma notificação de cancelamento por e-mail
    And o médico "Dr. Eduardo" deve receber uma notificação de cancelamento via SMS
    And a notificação deve informar que o horário está liberado

 # Regra de Negócio: Pacientes com cancelamentos frequentes podem receber penalidades.

  Scenario: Penalidade aplicada a paciente com histórico de cancelamentos frequentes (Sucesso)

    Given que o paciente "João Costa" possui "3" cancelamentos nos últimos 6 meses
    And a regra de penalidade para cancelamentos frequentes está ativa (limite = 2)
    When o paciente "João Costa" tentar cancelar uma nova consulta
    Then o sistema deve registrar o cancelamento
    And o sistema deve adicionar um alerta no perfil do "João Costa"
    And o sistema deve aplicar uma "restrição de agendamento por 30 dias"
    And o paciente "João Costa" deve ser notificado sobre a penalidade

  Scenario: Não aplicação de penalidade a paciente com histórico aceitável (Sucesso)

    Given que o paciente "Maria Lima" possui "1" cancelamento nos últimos 6 meses
    And a regra de penalidade para cancelamentos frequentes está ativa (limite = 2)
    When o paciente "Maria Lima" cancelar uma nova consulta
    Then o sistema deve registrar o cancelamento
    And o sistema não deve aplicar nenhuma restrição ao perfil da "Maria Lima"
    And o sistema não deve gerar alerta de penalidade