Feature: Registro de histórico clínico do paciente
  Como médico ou enfermeiro autorizado,
  Quero registrar o histórico clínico de um paciente,
  Para manter o acompanhamento adequado da saúde do paciente.

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @command(RegistrarHistorico)
  Scenario: RN1 - Registrar histórico com todos os campos obrigatórios (sucesso)
    Given existe o paciente "PAC-001" cadastrado
    And o profissional "Dra. Ana Lima" possui perfil "Médico" e está autorizado
    When a profissional registrar um histórico clínico às "21/09/2025 10:15" com:
      | sintomas | febre e mialgia |
      | diagnostico| Influenza |
      | conduta | Oseltamivir 75mg 2x/dia por 5 dias |
    Then o sistema deve criar um novo registro imutável vinculado ao paciente "PAC-001"
    And o histórico do paciente deve exibir o novo registro em ordem cronológica

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @command(RegistrarHistorico)
  Scenario: RN1 - Rejeitar registro quando faltar campo obrigatório (falha)
    Given existe o paciente "PAC-002" cadastrado
    And o profissional "Enf. Carlos" possui perfil "Enfermeiro" e está autorizado
    When o profissional tentar registrar um histórico clínico às "21/09/2025 10:25" com:
      | sintomas | tosse seca |
      | diagnostico| |
      | conduta | sintomáticos e repouso |
    Then o sistema deve rejeitar o registro informando "diagnóstico é obrigatório"
    And nenhum novo registro deve ser criado

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @command(RegistrarHistorico)
  Scenario: RN2 - Registrar histórico associado a paciente previamente cadastrado (sucesso)
    Given existe o paciente "PAC-003" cadastrado
    And o profissional "Dra. Marina" possui perfil "Médico" e está autorizado
    When a profissional registrar um histórico clínico com:
      | sintomas | dor torácica leve |
      | diagnostico| Bronquite suspeita |
      | conduta | Broncodilatador PRN |
    Then o registro deve ficar associado ao paciente "PAC-003"
    And o histórico consultável do paciente deve refletir a nova entrada

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @command(RegistrarHistorico)
  Scenario: RN2 - Bloquear registro para paciente inexistente (falha)
    Given não existe cadastro para o paciente "PAC-9999"
    And o profissional "Enf. Paula" possui perfil "Enfermeiro" e está autorizado
    When a profissional tentar registrar um histórico clínico com:
      | sintomas | náusea e tontura |
      | diagnostico| Refluxo |
      | conduta | Omeprazol 20mg/dia |
    Then o sistema deve rejeitar o registro informando "paciente não encontrado"
    And nenhum registro deve ser criado

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @Policy(AuditoriaAuto)
  Scenario: RN3 - Data, hora e profissional gravados automaticamente e registro imutável (sucesso)
    Given existe o paciente "PAC-004" cadastrado
    And o profissional "Enf. Luiza" possui perfil "Enfermeiro" e está autorizado
    When a profissional registrar um histórico clínico com PAC-004:
      | sintomas | cefaleia moderada |
      | diagnostico| Enxaqueca |
      | conduta | Analgésico e repouso |
    Then o sistema deve gravar automaticamente data e hora da criação
    And o sistema deve gravar automaticamente o profissional responsável "Enf. Luiza"
    And o registro salvo deve ser imutável, permitindo apenas adição de novos registros

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @Policy(AuditoriaAuto)
  Scenario: RN3 - Impedir sobrescrever registro e impedir definir data/profissional manualmente (falha)
    Given existe o paciente "PAC-005" cadastrado
    And o profissional "Dra. Ana Lima" possui perfil "Médico" e está autorizado
    When a profissional tentar salvar um histórico clínico definindo manualmente:
      | dataHora | 20/09/2025 08:00 |
      | profissional | Dr. Terceiro |
      | sintomas | astenia |
      | diagnostico | Anemia leve |
      | conduta | Suplementação |
    Then o sistema deve rejeitar qualquer tentativa de sobrescrever um registro existente
    And se for atualização, um novo registro deve ser criado mantendo o anterior inalterado

  @boundedcontext(ProntuarioClinico)
  @aggregate(HistoricoClinico)
  @command(RegistrarHistorico)
  Scenario: Observação futura - Referenciar anexos para compatibilidade futura sem upload obrigatório
    Given existe o paciente "PAC-006" cadastrado
    And o profissional "Dra. Marina" possui perfil "Médico" e está autorizado
    When a profissional registrar um histórico clínico com PAC-006:
      | sintomas | dispneia leve |
      | diagnostico| Asma leve |
      | conduta | Inalador conforme necessidade |
    And a profissional informar referências de anexos "RX-2025-09-21" e "laudo-espirometria"
    Then o sistema deve salvar o registro clínico imutável com metadados de anexos referenciados
    And o histórico do paciente deve exibir a indicação de anexos para versões futuras
