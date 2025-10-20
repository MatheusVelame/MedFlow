Feature: Arquivar ou excluir prontuários

  Como administrador do sistema,
  Quero arquivar ou excluir prontuários,
  Para manter conformidade com regras legais e gerenciar dados obsoletos.

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ArquivarProntuario)
  Scenario: RN1 - Arquivar prontuário com permissão administrativa (sucesso)
    Given existe o prontuário "PRT-1001" do paciente "PAC-001"
    And o usuário "Ana Lima" possui papel "Administrador do Sistema"
    And o motivo do arquivamento é "Inatividade superior a 5 anos"
    When a usuária arquivar o prontuário "PRT-1001" em "21/09/2025 10:05"
    Then o estado do prontuário deve mudar para "Arquivado"
    And o prontuário não deve aparecer em consultas comuns
    And deve ser gerado log imutável com ação "Arquivar", usuário "Ana Lima", data/hora "21/09/2025 10:05" e motivo "Inatividade superior a 5 anos"

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ExcluirProntuario)
  Scenario: RN1 - Bloquear exclusão por usuário sem permissão (falha)
    Given existe o prontuário "PRT-1002" do paciente "PAC-002"
    And o usuário "Beatriz Melo" possui papel "Atendente"
    And o motivo informado é "Limpeza de dados"
    When a usuária tentar excluir o prontuário "PRT-1002" em "21/09/2025 10:20"
    Then o sistema deve negar a ação informando "permissão administrativa necessária"
    And nenhum arquivamento ou exclusão deve ocorrer
    And deve registrar log de segurança da tentativa negada com usuário, data/hora, ação tentada e objeto "PRT-1002"

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ArquivarProntuario)
  Scenario: RN2 - Registrar log completo ao arquivar (sucesso)
    Given existe o prontuário "PRT-1100" do paciente "PAC-010"
    And o usuário "Carlos Souza" possui papel "Administrador do Sistema"
    And o motivo do arquivamento é "Óbito"
    When o usuário arquivar o prontuário "PRT-1100" em "21/09/2025 10:30"
    Then deve ser criado log com campos: objeto "PRT-1100", ação "Arquivar", usuário "Carlos Souza", data/hora "21/09/2025 10:30", motivo "Óbito"
    And o log deve ser imutável e acessível apenas a perfis de auditoria/gestão

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ArquivarProntuario)
  Scenario: RN2 - Rejeitar ação quando motivo não for informado (falha)
    Given existe o prontuário "PRT-1200" do paciente "PAC-020"
    And o usuário "Ana Lima" possui papel "Administrador do Sistema"
    And o motivo não foi informado
    When a usuária tentar arquivar o prontuário "PRT-1200" em "21/09/2025 10:35"
    Then o sistema deve rejeitar a operação informando "motivo é obrigatório"
    And nenhum log incompleto deve ser criado
    And o prontuário deve permanecer "Ativo"

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ArquivarProntuario)
  Scenario: RN3 - Arquivar mantém registro fora de consultas comuns e preservado em base segura (sucesso)
    Given existe o prontuário "PRT-1300" do paciente "PAC-030" em estado "Ativo"
    And o usuário "Mariana Reis" possui papel "Administrador do Sistema"
    And o motivo do arquivamento é "Inatividade superior a 5 anos"
    When a usuária arquivar o prontuário "PRT-1300" em "21/09/2025 10:45"
    Then o prontuário não deve aparecer nas consultas comuns
    And o prontuário deve estar disponível na base segura de arquivados
    And deve ser possível reativar posteriormente mediante ação "Reativar" com log correspondente

  @boundedcontext(Prontuario) @aggregate(Prontuario) @query(ListarProntuarios)
  Scenario: RN3 - Impedir exibição de arquivados em consultas comuns (falha de acesso)
    Given o prontuário "PRT-1300" está em estado "Arquivado"
    And o usuário "Operador" realiza uma consulta comum de prontuários às "21/09/2025 11:00"
    When a listagem padrão for retornada
    Then o prontuário "PRT-1300" não deve constar na listagem
    And um indicador de filtro "incluir arquivados" deve ser necessário para visualização especial por auditoria/gestão

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ExcluirProntuario)
  Scenario: RN4 - Excluir com autorização legal e destruição lógica irreversível (sucesso)
    Given existe o prontuário "PRT-2001" do paciente "PAC-040"
    And há parecer jurídico anexado autorizando exclusão por "erro cadastral grave" conforme norma aplicável
    And o usuário "Carlos Souza" possui papel "Administrador do Sistema"
    And o motivo da exclusão é "Erro cadastral grave confirmado pela auditoria"
    When o usuário excluir o prontuário "PRT-2001" em "21/09/2025 11:10"
    Then o sistema deve realizar exclusão irreversível marcada como "destruição lógica" sem possibilidade de restauração
    And se exigido pela LGPD, realizar deleção definitiva dos dados pessoais
    And deve ser gerado log com ação "Excluir", usuário "Carlos Souza", data/hora "21/09/2025 11:10", motivo informado e referência ao parecer jurídico

  @boundedcontext(Prontuario) @aggregate(Prontuario) @command(ExcluirProntuario)
  Scenario: RN4 - Negar exclusão sem autorização legal (falha)
    Given existe o prontuário "PRT-2002" do paciente "PAC-050"
    And não há documentação jurídica que autorize a exclusão
    And o usuário "Ana Lima" possui papel "Administrador do Sistema"
    And o motivo da exclusão é "Limpeza de base"
    When a usuária tentar excluir o prontuário "PRT-2002" em "21/09/2025 11:15"
    Then o sistema deve negar a operação informando "exclusão somente com autorização legal"
    And o prontuário deve permanecer inalterado
    And deve registrar log de tentativa negada com usuário, data/hora, ação tentada e objeto "PRT-2002"
