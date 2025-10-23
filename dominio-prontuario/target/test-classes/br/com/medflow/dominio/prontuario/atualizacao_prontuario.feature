Feature: Atualização de prontuário do paciente

  Como profissional de saúde, 
  Quero atualizar o prontuário de um paciente a cada atendimento, 
  Para registrar a evolução do tratamento e manter histórico completo.

  @boundedcontext(ProntuarioClinico) @aggregate(Prontuario) @command(AtualizarProntuario)
  Scenario: Sucesso — Atualizar prontuário em atendimento ativo (versão e histórico)
    Given o paciente "PAC-001" está em atendimento "ATD-1001" iniciado em "21/09/2025" às "09:30"
    And o prontuário "PRT-1001" está vinculado ao atendimento "ATD-1001" com status "Ativo"
    And o profissional responsável é "Dra. Ana Lima" (CRM 12345)
    When a médica registrar uma atualização às "10:05" com observações "Evolução favorável, PA 12x8, ajuste de dose"
    Then o sistema deve criar uma nova versão do prontuário vinculada ao atendimento "ATD-1001"
    And manter as versões anteriores preservadas (imutáveis)
    And manter o status do registro como "Ativo"
    And incluir a atualização no histórico consultável (data/hora, profissional, resumo da evolução)

  @boundedcontext(ProntuarioClinico) @aggregate(Prontuario) @command(AtualizarProntuario)
  Scenario: Falha — Tentativa de atualização sem vínculo de atendimento válido
    Given o profissional "Dr. Carlos Souza" tenta atualizar o prontuário "PRT-2002" do paciente "PAC-010"
    And não há atendimento ativo vinculado (ID do atendimento ausente ou encerrado)
    When o médico registrar a atualização às "10:20" com observações "Novo sintoma relatado: cefaleia"
    Then o sistema deve impedir a atualização informando que cada atualização deve estar vinculada a um atendimento válido
    And nenhum registro de versão deve ser criado
    And deve ser registrado um log de tentativa negada (usuário, data/hora, motivo)

  @boundedcontext(ProntuarioClinico) @aggregate(Prontuario) @command(FinalizarAtendimento)
  Scenario: Finalização do atendimento inativa o registro e bloqueia novas entradas
    Given o paciente "PAC-001" possui o prontuário "PRT-1001" com atualizações vinculadas ao atendimento "ATD-1001"
    And o profissional "Dra. Ana Lima" solicita a finalização do atendimento às "10:40"
    When o atendimento "ATD-1001" for finalizado
    Then o sistema deve alterar o status das atualizações daquele atendimento para "Inativado"
    And o prontuário permanece consultável (histórico preservado), porém novas atualizações não podem ser adicionadas nesse atendimento
    And para registrar novas evoluções, o sistema deve exigir a abertura de novo atendimento (nova vinculação)

  @boundedcontext(ProntuarioClinico) @aggregate(Prontuario) @query(ListarProntuarios)
  Scenario: Consulta do histórico completo (linhas do tempo por atendimento e versão)
    Given o prontuário "PRT-1001" possui múltiplas versões vinculadas aos atendimentos "ATD-1001" e "ATD-1020"
    And o usuário "Mariana Reis" (perfil permitido) solicita a visualização do histórico às "11:00"
    When o histórico do prontuário for consultado
    Then o sistema deve exibir a linha do tempo de evoluções em ordem cronológica, agrupadas por atendimento (ID do atendimento, data/hora, profissional, resumo)
    And cada versão anterior deve estar acessível em modo somente leitura, garantindo a imutabilidade
    And deve ser possível filtrar por atendimento, período e profissional responsável
