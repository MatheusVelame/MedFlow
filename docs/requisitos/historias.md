# Histórias

- Histórias de usuários desenvolvidas a partir das **regras de negócio** e dos **requisitos funcionais**.
- Descrevem [**funcionalidades** do sistema](../../docs/requisitos/funcionalidades.md) sob a perspectiva do usuário final.
- Utilizam a estrutura padrão: "Como [tipo de usuário], quero [ação] para [benefício/valor]".
- Formam o **backlog do produto**, que será priorizado e implementado em sprints.
  - para cada história, o modelo de backlog traz os dados necessários, campos de dados e notas/observações
  - Cada história deve ser pequena o suficiente para ser concluída dentro de uma sprint
  - Devem ser escritas de forma clara e compreensível para todos os membros da equipe
  - Devem incluir critérios de aceite que definem quando a história está completa
  - Devem ser revisadas e refinadas regularmente com a equipe de desenvolvimento e o Product Owner
  - Devem ser rastreáveis até os requisitos funcionais e regras de negócio que suportam
  - Devem ser testáveis, permitindo a criação de casos de teste baseados nos critérios de aceite
- Cada história deve ser vinculada a um **epic** maior que agrupe funcionalidades relacionadas

## Epics

- Estas labels identificam **qual parte do sistema a issue pertence** (baseadas nos epics do mapa de histórias):

- `epic:pacientes`
- `epic:medicos`
- `epic:consultas`
- `epic:prontuario`
- `epic:exames`
- `epic:medicamentos`
- `epic:funcionarios`
- `epic:faturamento`
- `epic:convenios`

## Epic - Pacientes

### Gerenciamento de pacientes (cadastro, edição, exclusão e consulta) - Luziane

#### \[Pacientes] Cadastrar paciente

> [Issue #72](https://github.com/MatheusVelame/MedFlow/issues/72)

Como funcionário da clínica, quero cadastrar pacientes com seus dados pessoais, para que possam ser vinculados a consultas, exames e prontuários.

**Critérios de aceite:**

- **RN**: Nome, CPF, data de nascimento e telefone são obrigatórios.
- **RN**: O CPF deve conter exatamente 11 dígitos numéricos.
- **RN**: A data de nascimento deve estar no formato **dd/mm/aaaa**.
- **RN**: Não é permitido cadastrar um paciente com CPF já existente no sistema.

**Dados necessários / Campos:**

- Nome completo
- CPF (11 dígitos)
- Telefone
- Endereço
- Data de nascimento

**Notas / Observações:**

- As validações devem ser feitas tanto no frontend quanto no backend.

#### \[Pacientes] Atualizar dados de paciente

> [Issue #73](https://github.com/MatheusVelame/MedFlow/issues/73)

Como funcionário da clínica, quero atualizar os dados de um paciente, para manter o cadastro correto e atualizado.

**Critérios de aceite:**

- **RN**: O CPF não pode ser alterado.
- **RN**: A alteração não pode ser feita sem os dados obrigatórios preenchidos (nome, CPF, telefone, data de nascimento).
- **RN**: A data de nascimento deve estar no formato **dd/mm/aaaa**.

**Dados necessários / Campos:**

- Nome completo
- Telefone
- Endereço
- Data de nascimento

**Notas / Observações:**

- O sistema deve registrar data e responsável pela alteração no histórico do paciente.

#### \[Pacientes] Consultar paciente

> [Issue #74](https://github.com/MatheusVelame/MedFlow/issues/74)

Como funcionário da clínica, quero consultar os dados de pacientes, para obter informações de contato e histórico cadastral.

**Critérios de aceite:**

- **RN**: A consulta por CPF deve validar se o campo possui 11 dígitos numéricos.
- **RN**: A consulta por CPF deve retornar apenas o paciente correspondente.
- **RN**: A consulta por nome deve retornar todos os pacientes com o nome informado.
- **RN**: A consulta deve exibir todos os dados do paciente.

**Dados necessários / Campos:**

- CPF (11 dígitos)
- Nome completo
- Telefone
- Endereço
- Data de nascimento

**Notas / Observações:**

- A consulta deve permitir filtros por CPF ou por nome.

#### \[Pacientes] Remover paciente

> [Issue #75](https://github.com/MatheusVelame/MedFlow/issues/75)

Como funcionário da clínica, quero remover o cadastro de pacientes, para manter a base limpa de registros inativos ou inválidos.

**Critérios de aceite:**

- **RN**: Não é possível remover paciente que não exista no sistema.
- **RN**: Não é possível remover paciente que possua prontuário.
- **RN**: Não é possível remover paciente que tenha consulta agendada.
- **RN**: Não é possível remover paciente que tenha exame agendado.

**Dados necessários / Campos:**

- CPF do paciente
- Status da verificação (possui prontuário, consultas ou exames agendados)

**Notas / Observações:**

- Em vez da remoção definitiva, pode ser considerada a marcação como "inativo" para manter rastreabilidade.

## Epic - Médicos

### Gerenciamento de médicos (dados pessoais, CRM, especialidade) - Lisa

#### \[Médicos] Cadastrar médico

> [Issue #80](https://github.com/MatheusVelame/MedFlow/issues/80)

Como funcionário da clínica, quero cadastrar médicos com dados pessoais, CRM e especialidade vinculada, para que possam ser incluídos em atendimentos e agendamentos.

**Critérios de aceite:**

- **RN**: Nome, CPF, CRM, data de nascimento e número de contato são obrigatórios.
- **RN**: O CPF deve conter exatamente 11 dígitos numéricos.
- **RN**: A data de nascimento deve estar no formato dd/mm/aaaa.
- **RN**: Não é possível cadastrar um médico com CPF já existente.
- **RN**: Não é possível cadastrar um médico com CRM já existente.
- **RN**: O médico só pode ser vinculado a uma especialidade previamente cadastrada no sistema.
- **RN**: O cadastro da disponibilidade de horários é opcional no cadastro inicial.

**Dados necessários / Campos:**

- Nome completo
- CPF (11 dígitos)
- CRM (número do conselho)
- Data de nascimento
- Número de contato (telefone e/ou e-mail)
- Especialidade vinculada
- Status inicial = "Ativo"
- Disponibilidade de horários (opcional)

**Notas / Observações:**

- Validações devem ocorrer no frontend e backend.
- A especialidade deve estar previamente cadastrada no sistema.

#### \[Médicos] Atualizar dados cadastrais do médico

> [Issue #81](https://github.com/MatheusVelame/MedFlow/issues/81)

Como administrador da clínica, quero atualizar os dados cadastrais de médicos, para manter as informações corretas e atualizadas.

**Critérios de aceite:**

- **RN**: O campo CRM, uma vez salvo, não pode ser alterado.
- **RN**: Demais informações pessoais (nome, telefone, endereço, etc.) podem ser atualizadas a qualquer momento.
- **RN**: Alterações na disponibilidade de horários do médico só pode ser permitida em períodos que não possuam consultas futuras já agendadas.

**Dados necessários / Campos:**

- Nome completo
- Número de contato
- Endereço
- Especialidade vinculada
- Disponibilidade de horários
- Status (Ativo/Inativo)

**Notas / Observações:**

- O sistema deve registrar data, hora e usuário responsável pela alteração.
- Mensagens claras devem ser exibidas se houver conflito com consultas futuras.

#### \[Médicos] Consultar médicos

> [Issue #82](https://github.com/MatheusVelame/MedFlow/issues/82)

Como funcionário da clínica, quero consultar a lista de médicos cadastrados e seus detalhes, para visualizar informações e status de cada profissional.

**Critérios de aceite:**

- **RN**: Por padrão, apenas médicos com status Ativo devem ser exibidos.
- **RN**: A interface deve permitir filtrar a lista de médicos por especialidade e por status (Ativos ou Inativos).
- **RN**: A lista deve exibir no mínimo: nome completo, CRM e especialidade principal.

**Dados necessários / Campos:**

- Nome completo
- CRM
- Especialidade vinculada
- Número de contato
- Status (Ativo/Inativo)

**Notas / Observações:**

- O sistema deve permitir filtros rápidos e ordenação alfabética.
- Detalhes completos podem ser acessados clicando no médico listado.

#### \[Médicos] Inativar ou remover médico

> [Issue #83](https://github.com/MatheusVelame/MedFlow/issues/83)

Como administrador da clínica, quero inativar ou remover médicos, para manter a base de profissionais consistente com a realidade da clínica.

**Critérios de aceite:**

- **RN**: Médico não pode ser excluído se possuir registros históricos (consultas, prontuários, exames).
- **RN**: Em casos de vínculo histórico, deve-se apenas **inativar** o médico.
- **RN**: Médicos inativos não podem ser selecionados para novos agendamentos de consultas ou exames.
- **RN**: Todos os registros associados a um médico inativado devem ser mantidos no sistema para fins de consulta ao histórico.
- **RN**: Exclusão física só é permitida se não houver nenhum vínculo no sistema.

**Dados necessários / Campos:**

- CPF do médico
- CRM
- Status atual
- Indicador de vínculo (consultas, prontuários ou exames vinculados: Sim/Não)

**Notas / Observações:**

- Inativação preserva rastreabilidade e histórico.
- O sistema deve avisar claramente quando a exclusão não for permitida devido a vínculos existentes.

### Gerenciamento de especialidades médicas - Thaís

#### \[Especialidades] Cadastrar nova especialidade

> [Issue #15](https://github.com/MatheusVelame/MedFlow/issues/15)

Como administrador da clínica, quero cadastrar uma nova especialidade médica, para que ela possa ser atribuída a médicos.

Critérios de aceite:
- **RN**: O nome da especialidade é obrigatório.
- **RN**: Não é permitido cadastrar duas especialidades com o mesmo nome.
- **RN**: O nome da especialidade deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços).
- **RN**: A descrição da especialidade (opcional) pode conter até 255 caracteres.
- **RN**: O status inicial da especialidade cadastrada deve ser definido como "Ativa"

Dados necessários / Campos:
- Nome
- Descrição (opcional, até 255 caracteres)
- Status (definido automaticamente como "Ativa")

Notas / Observações:
- Validação deve ser feita tanto no frontend quanto no backend.

#### \[Especialidades] Alterar especialidade existente

> [Issue #16](https://github.com/MatheusVelame/MedFlow/issues/16)

Como administrador da clínica, quero alterar o nome ou a descrição de uma especialidade, para manter os dados corretos e atualizados.

Critérios de aceite:
- **RN**: Apenas a descrição ou o nome da especialidade podem ser alterados.
- **RN**: O nome alterado deve passar novamente pela validação de unicidade (alfabético, único).
- **RN**: Não é permitido inativar ou alterar o nome de uma especialidade que esteja vinculada a médicos ativos, sem que haja um tratamento para essa vinculação (ex.: reatribuição).

Dados necessários / Campos:
- Nome
- Descrição

Notas / Observações:
- Alterações devem ser auditáveis (registradas no histórico de alterações).

#### \[Especialidades] Excluir especialidade

> [Issue #17](https://github.com/MatheusVelame/MedFlow/issues/17)

Como administrador da clínica, quero excluir uma especialidade, para remover informações desnecessárias ou descontinuadas do sistema.

Critérios de aceite:
- **RN**: Não é permitido excluir uma especialidade que esteja vinculada a pelo menos um médico ativo.
- **RN**: A exclusão física de uma especialidade só pode ser realizada caso nunca tenha sido vinculada a nenhum médico.
- **RN**: Caso haja histórico de vínculo com médicos, a especialidade deve ser marcada como "Inativa" em vez de excluída.
- **RN**: Especialidades inativas não podem ser atribuídas a novos médicos.

Dados necessários / Campos:
- Nome
- Status

Notas / Observações:
- Operações de exclusão devem ser registradas no histórico de alterações.

#### \[Especialidades] Listar especialidades

> [Issue #18](https://github.com/MatheusVelame/MedFlow/issues/18)

Como administrador da clínica, quero listar as especialidades cadastradas, para consultar e gerenciar seus dados.

Critérios de aceite:
- **RN**: A listagem deve exibir, no mínimo, o nome da especialidade, seu status (Ativa/Inativa) e, se houver, a descrição.
- **RN**: Por padrão, devem ser listadas apenas especialidades ativas.
- **RN**: A interface deve permitir filtrar especialidades por status (Ativas ou Inativas).
- **RN**: A listagem deve permitir ordenação alfabética pelo nome da especialidade.

Dados necessários / Campos:
- Nome
- Descrição
- Status

Notas / Observações:
- A listagem deve suportar paginação caso haja muitas especialidades.

## Epic - Consultas

### Agendamento de consultas (marcar, remarcar, cancelar, consultar) - Velame

#### \[Consultas] Marcar consulta

> [Issue #92](https://github.com/MatheusVelame/MedFlow/issues/92)

Como recepcionista da clínica, quero marcar consultas escolhendo paciente, médico, especialidade, data e hora, para organizar os atendimentos de forma estruturada.

**Critérios de aceite:**

- **RN**: A consulta só pode ser marcada se o horário estiver disponível na agenda do médico.
- **RN**: Paciente e médico devem estar previamente cadastrados no sistema.
- **RN**: A data da consulta não pode ser anterior à data atual.
- **RN**: O sistema deve validar se o médico atende à especialidade escolhida.
- **RN**: Após a marcação, deve ser enviada notificação de confirmação para paciente e médico (e-mail ou SMS).

**Dados necessários / Campos:**

- Paciente
- Médico
- Especialidade
- Data da consulta
- Hora da consulta
- Status inicial = "Agendada"

**Notas / Observações:**

- Notificações devem seguir preferências cadastradas pelo usuário (e-mail/SMS).
- Disponibilidade do médico deve ser checada contra a agenda em tempo real.

#### \[Consultas] Remarcar consulta

> [Issue #93](https://github.com/MatheusVelame/MedFlow/issues/93)

Como recepcionista da clínica, quero remarcar consultas já agendadas, para reorganizar horários de pacientes e médicos quando necessário.

**Critérios de aceite:**

- **RN**: Só é possível remarcar se o novo horário estiver disponível na agenda do médico.
- **RN**: O sistema deve registrar histórico da remarcação (data/hora originais e novos).
- **RN**: Limite de remarcações por consulta: no máximo 2 vezes.
- **RN**: Notificações devem ser enviadas ao paciente e ao médico sobre a alteração.
- **RN**: Remarcações feitas com menos de 24h de antecedência devem seguir regra específica (ex.: bloquear ou cobrar taxa).

**Dados necessários / Campos:**

- Paciente
- Médico
- Especialidade
- Data e hora originais
- Nova data e hora
- Motivo da remarcação
- Quantidade de remarcações já feitas

**Notas / Observações:**

- Histórico de remarcações deve ser consultável no prontuário e na agenda.
- Regra de limite (2x) deve gerar alerta automático no sistema.

#### \[Consultas] Cancelar consulta

> [Issue #94](https://github.com/MatheusVelame/MedFlow/issues/94)

Como paciente ou recepcionista da clínica, quero cancelar consultas, para liberar a agenda do médico em caso de desistência.

**Critérios de aceite:**

- **RN**: Cancelamento só permitido até 24h antes da consulta.
- **RN**: Deve ser registrado motivo do cancelamento.
- **RN**: Ao cancelar, o horário do médico deve ser liberado automaticamente.
- **RN**: Notificação deve ser enviada ao paciente e ao médico.
- **RN**: Pacientes com cancelamentos frequentes podem receber penalidades (ex.: restrição de futuras marcações ou taxa).

**Dados necessários / Campos:**

- Paciente
- Médico
- Especialidade
- Data e hora da consulta
- Motivo do cancelamento
- Status final = "Cancelada"

**Notas / Observações:**

- Políticas de cancelamento (24h / penalidades) devem ser configuráveis pelo administrador.
- Cancelamentos frequentes devem gerar alertas no perfil do paciente.

#### \[Consultas] Consultar agenda

> [Issue #95](https://github.com/MatheusVelame/MedFlow/issues/95)

Como médico ou administrador da clínica, quero consultar a agenda em diferentes visões (diária, semanal, mensal), para acompanhar os horários disponíveis e ocupados.

**Critérios de aceite:**

- **RN**: Agenda deve exibir apenas horários ocupados/disponíveis, sem mostrar informações de outros pacientes (privacidade).
- **RN**: Visualização deve estar disponível em diferentes períodos (diária, semanal, mensal).
- **RN**: Agenda do médico só pode ser acessada por ele mesmo ou por administradores.
- **RN**: Deve ser possível filtrar por médico, especialidade e status da consulta (confirmada, remarcada, cancelada).
- **RN**: Informações exibidas: nome do paciente, horário da consulta e especialidade do atendimento.

**Dados necessários / Campos:**

- Médico
- Especialidade
- Data/período (dia, semana, mês)
- Status das consultas (Agendada, Remarcada, Cancelada)
- Nome do paciente vinculado

**Notas / Observações:**

- Privacidade deve ser garantida: apenas informações autorizadas podem ser exibidas.
- Agenda deve ser otimizada para visualização rápida em telas de recepção e no app do médico.

## Epic - Prontuário

### Gerenciamento de prontuários eletrônicos (histórico clínico do paciente) - Paulo

#### \[Prontuários] Registrar histórico clínico

> [Issue #96](https://github.com/MatheusVelame/MedFlow/issues/96)

Como médico ou enfermeiro autorizado, quero registrar o histórico clínico de um paciente (sintomas, diagnóstico, tratamento), para manter o acompanhamento adequado da saúde do paciente.

**Critérios de aceite:**

- **RN**: O registro deve conter obrigatoriamente: sintomas, diagnóstico e conduta/tratamento.
- **RN**: Cada registro deve estar associado a um paciente previamente cadastrado.
- **RN**: Data, hora e profissional responsável devem ser gravados automaticamente.

**Dados necessários / Campos:**

- ID do paciente
- Sintomas
- Diagnóstico
- Conduta/tratamento
- Data e hora do registro
- Profissional responsável

**Notas / Observações:**

- O registro deve ser salvo de forma **imutável** (não sobrescrever), permitindo apenas adição de novos registros.
- Deve haver suporte a anexos (exames, laudos, imagens médicas) em versões futuras.

#### \[Prontuários] Atualizar informações do prontuário

> [Issue #97](https://github.com/MatheusVelame/MedFlow/issues/97)

Como profissional de saúde, quero atualizar o prontuário de um paciente a cada atendimento, para registrar a evolução do tratamento e manter histórico completo.

**Critérios de aceite:**

- **RN**: Cada atualização deve estar vinculada a um atendimento.
- **RN**: O registro deve ser inativado quando o atendimento for finalizado.
- **RN**: Versões anteriores do prontuário devem ser preservadas (imutabilidade).
- **RN**: Histórico de alterações e evoluções deve ser consultável.

**Dados necessários / Campos:**

- ID do paciente
- ID do atendimento
- Data e hora da atualização
- Profissional responsável
- Observações adicionais (evolução, novos sintomas, condutas)
- Status (Ativo / Inativado)

**Notas / Observações:**

- Ao finalizar o atendimento, o prontuário passa a estado **Inativo**, mas ainda consultável.
- Atualizações devem ser vinculadas ao fluxo de atendimento da agenda.

#### \[Prontuários] Consultar histórico clínico completo

> [Issue #98](https://github.com/MatheusVelame/MedFlow/issues/98)

Como médico ou administrador clínico, quero consultar o histórico clínico completo de um paciente, para ter visão cronológica de seus atendimentos e diagnósticos.

**Critérios de aceite:**

- **RN**: O histórico clínico do paciente deve ser exibido de forma completa e cronológica (mais antigo → mais recente).
- **RN**: Apenas profissionais autorizados (médicos, enfermeiros, administradores clínicos) podem acessar.
- **RN**: O sistema deve mostrar todos os registros ativos e inativos.

**Dados necessários / Campos exibidos:**

- Nome do paciente
- Dados cadastrais básicos (idade, sexo, contato)
- Lista de registros clínicos:
  - Data e hora
  - Profissional responsável
  - Sintomas
  - Diagnóstico
  - Conduta/tratamento
  - Status (Ativo/Inativo)

**Notas / Observações:**

- Visualização deve ser otimizada para pesquisa rápida (filtros por data, profissional, diagnóstico).
- Deve haver versão “somente leitura” para evitar alterações indevidas.

#### \[Prontuários] Arquivar ou excluir prontuário

> [Issue #99](https://github.com/MatheusVelame/MedFlow/issues/99)

Como administrador do sistema, quero arquivar ou excluir prontuários, para manter conformidade com regras legais e gerenciar dados obsoletos.

**Critérios de aceite:**

- **RN**: Apenas usuários com permissão administrativa podem arquivar ou excluir um prontuário.
- **RN**: Ação deve registrar log com data, hora, responsável e motivo.
- **RN**: Arquivamento mantém o registro inacessível em consultas comuns, mas preservado em base segura.
- **RN**: Exclusão só pode ocorrer em situações autorizadas por norma legal (ex.: erro cadastral grave).
- **RN**: (Auditlogs) O sistema deve manter trilha de auditoria para todas as operações realizadas no prontuário (criação, atualização, consulta, arquivamento/exclusão).

**Dados necessários / Campos:**

- ID do paciente
- Tipo da ação (Arquivar / Excluir)
- Data e hora da ação
- Usuário responsável
- Motivo da ação

**Notas / Observações:**

- Arquivamento deve ser reversível (reativar prontuário).
- Exclusão deve ser irreversível e marcada como **destruição lógica** (não física), exceto quando LGPD exigir deleção definitiva.

#### \[Prontuários] Auditoria e logs

> [Issue #100](https://github.com/MatheusVelame/MedFlow/issues/100)

Como gestor da clínica, quero manter trilha de auditoria em todos os prontuários, para garantir conformidade legal e rastreabilidade das ações.

**Critérios de aceite:**

- **RN**: O sistema deve manter log de todas as operações realizadas: criação, atualização, consulta, arquivamento e exclusão.
- **RN**: Cada log deve conter usuário responsável, data, hora, ação realizada e objeto afetado.
- **RN**: Logs não podem ser alterados ou excluídos.

**Dados necessários / Campos (log):**

- ID do prontuário/paciente
- Ação (criação, atualização, consulta, arquivamento, exclusão)
- Usuário responsável
- Data e hora da ação
- Detalhes adicionais (ex.: campo alterado, motivo de arquivamento)

**Notas / Observações:**

- Logs devem ser acessíveis apenas para perfis de auditoria/gestão.
- Necessário compliance com **LGPD** e **Resolução CFM nº 1.821/07** (prontuário eletrônico).

## Epic - Exames

### Gerenciamento de tipos de exames - Luziane

#### \[Tipos de Exames] Cadastrar tipo de exame

> [Issue #76](https://github.com/MatheusVelame/MedFlow/issues/76)

Como administrador da clínica, quero cadastrar novos tipos de exame, para que possam ser utilizados em agendamentos e faturamento.

**Critérios de aceite:**

- **RN**: Não é possível cadastrar um tipo de exame sem **descrição**.
- **RN**: Não é possível cadastrar um tipo de exame sem **especialidade**.
- **RN**: Não é possível cadastrar um tipo de exame sem **valor**.
- **RN**: O valor deve ser **maior ou igual a 0**.
- **RN**: Não é possível cadastrar um tipo de exame com status inicial **Inativo**.
- **RN**: O **código do tipo de exame** deve ser único no sistema.

**Dados necessários / Campos:**

- Código (único)
- Descrição
- Especialidade
- Valor (≥ 0)
- Status inicial = "Ativo"

**Notas / Observações:**

- As validações devem ser feitas tanto no frontend quanto no backend.

#### \[Tipos de Exames] Atualizar tipo de exame

> [Issue #77](https://github.com/MatheusVelame/MedFlow/issues/77)

Como administrador da clínica, quero atualizar os dados de um tipo de exame, para manter o cadastro atualizado.

**Critérios de aceite:**

- **RN**: Não é possível alterar o **código** do tipo de exame.
- **RN**: Não é possível atualizar o exame inserindo um valor **inferior a 0**.
- **RN**: Não é possível atualizar um exame que possua agendamentos vinculados.

**Dados necessários / Campos:**

- Descrição
- Especialidade
- Valor (≥ 0)
- Status (Ativo/Inativo)

**Notas / Observações:**

- Caso haja agendamentos vinculados, deve ser apresentada mensagem clara ao usuário sobre a impossibilidade da alteração.

#### \[Tipos de Exames] Consultar tipo de exame

> [Issue #78](https://github.com/MatheusVelame/MedFlow/issues/78)

Como funcionário da clínica, quero consultar tipos de exame cadastrados, para obter informações detalhadas sobre código, descrição, especialidade, valor e status.

**Critérios de aceite:**

- **RN**: Não é possível consultar exame sem informar **código** ou **descrição**.
- **RN**: A busca por **código** deve retornar no máximo **1 registro**.
- **RN**: A busca por **código** deve retornar exatamente o tipo de exame correspondente.

**Dados necessários / Campos:**

- Código (11 dígitos ou identificador único)
- Descrição
- Especialidade
- Valor
- Status (Ativo/Inativo)

**Notas / Observações:**

- A consulta por descrição pode retornar múltiplos resultados.

#### \[Tipos de Exames] Excluir ou inativar tipo de exame

> [Issue #79](https://github.com/MatheusVelame/MedFlow/issues/79)

Como administrador da clínica, quero excluir ou inativar tipos de exame, para manter a base de dados organizada e sem registros obsoletos.

**Critérios de aceite:**

- **RN**: Não é possível excluir tipo de exame com agendamentos vinculados (histórico ou futuros).
- **RN**: Não é possível excluir tipo de exame inexistente.
- **RN**: Não é possível inativar tipo de exame inexistente.

**Dados necessários / Campos:**

- Código do exame
- Status (Ativo/Inativo)
- Indicador de vínculo (existe agendamento vinculado: Sim/Não)

**Notas / Observações:**

- Para manter rastreabilidade, recomenda-se inativar em vez de excluir definitivamente.


### Gerenciamento de exames - Thaís

#### \[Exames] Agendar exame para paciente

> [Issue #19](https://github.com/MatheusVelame/MedFlow/issues/19)

Como funcionário da clínica, quero agendar um exame para um paciente, para organizar os atendimentos médicos e laboratoriais.

Critérios de aceite:

- **RN**: O agendamento de exame só pode ser realizado se o paciente e o médico já estiverem previamente cadastrados no sistema.
- **RN**: O tipo de exame deve estar previamente cadastrado no sistema (ex.: raio-x, sangue, ultrassonografia).
- **RN**: A data e o horário do exame são obrigatórios.
- **RN**: Não é permitido agendar dois exames para o mesmo paciente no mesmo horário.
- **RN**: O médico vinculado ao exame deve estar ativo no sistema.
- **RN**: Não é permitido agendar exame em horário de indisponibilidade do médico.
- **RN**: O exame deve receber um status inicial "Agendado".

Dados necessários / Campos:

- Paciente
- Médico
- Tipo de exame
- Data
- Hora
- Status inicial = "Agendado"

Notas / Observações:
- Regras de disponibilidade do médico devem ser verificadas com base na agenda.

#### \[Exames] Atualizar agendamento de exame

> [Issue #20](https://github.com/MatheusVelame/MedFlow/issues/20)

Como funcionário da clínica, quero atualizar um agendamento de exame, para corrigir ou remarcar informações.

Critérios de aceite:
- **RN**: Só podem ser alterados a data, o horário, o tipo de exame e o médico responsável (desde que o novo médico esteja ativo).
- **RN**: O paciente vinculado não pode ser alterado após a criação do agendamento.
- **RN**: A alteração só será válida se não gerar conflito de horário para o paciente ou para o médico.
- **RN**: O histórico de alterações de data/hora do exame deve ser registrado no sistema.

Dados necessários / Campos:

- Novo médico (ativo)
- Nova data
- Novo horário
- Novo tipo de exame

Notas / Observações:

- Alterações devem ser registradas na entidade "Histórico de Alterações".

#### \[Exames] Excluir ou cancelar exame agendado

> [Issue #21](https://github.com/MatheusVelame/MedFlow/issues/21)

Como funcionário da clínica, quero excluir ou cancelar um exame agendado, para manter os registros corretos no sistema.

Critérios de aceite:

- **RN**: Não é permitido excluir exames já realizados ou em andamento (somente os com status "Agendado").
- **RN**: Caso o exame já esteja vinculado a um laudo, não pode ser excluído; deve ser apenas marcado como "Cancelado".
- **RN**: A exclusão só é permitida se o exame ainda não estiver associado a nenhum registro clínico no prontuário do paciente.
- **RN**: O cancelamento de exame deve registrar a data e o motivo do cancelamento.

Dados necessários / Campos:

- Status (Agendado, Cancelado)
- Data do cancelamento
- Motivo do cancelamento

Notas / Observações:

- Operações devem ser registradas no histórico de alterações.

#### \[Exames] Consultar exames agendados

> [Issue #22](https://github.com/MatheusVelame/MedFlow/issues/22)

Como funcionário da clínica, quero consultar exames agendados, para visualizar e organizar os procedimentos dos pacientes.

Critérios de aceite:

- **RN**: A consulta deve permitir filtrar por paciente, médico, tipo de exame, status ou intervalo de datas.
- **RN**: A pesquisa por paciente deve validar o CPF (11 dígitos).
- **RN**: O resultado da consulta deve exibir, no mínimo: paciente, médico responsável, tipo de exame, data/hora e status.
- **RN**: Exames cancelados ou realizados devem aparecer apenas em consultas históricas, não na listagem padrão de agendamentos futuros.

Dados necessários / Campos:

- Filtros: paciente (CPF), médico, tipo de exame, status, intervalo de datas
- Exibição: paciente, médico, tipo de exame, data/hora, status

Notas / Observações:

- Listagem deve suportar paginação e ordenação por data.

## Epic - Medicamentos

### Gerenciamento de medicamentos (cadastro de medicamentos usados em prescrições) - Velame

#### \[Medicamentos] Cadastrar medicamento

> [Issue #88](https://github.com/MatheusVelame/MedFlow/issues/88)

Como funcionário autorizado da clínica, quero cadastrar medicamentos com nome, uso e contraindicações, para que possam ser utilizados em prescrições e controle farmacológico.

**Critérios de aceite:**

- **RN**: O cadastro deve incluir **nome**, **uso principal** e **contraindicações** (se existirem).
- **RN**: O nome do medicamento deve ser **único** no sistema para evitar duplicidade e confusão.
- **RN**: Campos de uso e contraindicações suportam texto livre, mas é ideal que haja campos específicos para informações padronizadas (ex: "Uso: analgésico", "Uso: anti-inflamatório").
- **RN**: O sistema deve validar a unicidade do nome no momento do cadastro.

**Dados necessários / Campos:**

- Nome do medicamento
- Uso principal
- Contraindicações
- Status inicial = "Ativo"

**Notas / Observações:**

- Validações de unicidade devem ocorrer no frontend e backend.
- Campos de uso/contraindicações podem ter sugestões padronizadas para facilitar consistência.

#### \[Medicamentos] Consultar medicamentos

> [Issue #89](https://github.com/MatheusVelame/MedFlow/issues/89)

Como usuário autorizado (médico ou farmacêutico), quero consultar a lista de medicamentos cadastrados, para verificar informações antes de prescrever.

**Critérios de aceite:**

- **RN**: Consulta deve permitir busca por **nome** ou por **tipo de uso** do medicamento.
- **RN**: Lista deve exibir claramente nome, uso e contraindicações.
- **RN**: Permitir ordenação, por exemplo, em ordem alfabética pelo nome.
- **RN**: Consulta deve ser acessível apenas por usuários autorizados, como médicos e farmacêuticos.

**Dados necessários / Campos:**

- Nome do medicamento
- Uso principal
- Contraindicações
- Status (Ativo/Arquivado)

**Notas / Observações:**

- Medicamentos arquivados não aparecem na listagem padrão.
- Filtros devem permitir exibir medicamentos arquivados quando necessário.

#### \[Medicamentos] Atualizar medicamento

> [Issue #90](https://github.com/MatheusVelame/MedFlow/issues/90)

Como usuário autorizado, quero atualizar informações de um medicamento, para manter os dados corretos e seguros para prescrição.

**Critérios de aceite:**

- **RN**: Apenas usuários com permissão podem alterar dados (ex: médicos ou administradores).
- **RN**: Sistema deve manter histórico de alterações, registrando data da alteração e responsável.
- **RN**: Alterações críticas, como contraindicações, devem ser revisadas por um responsável antes que a alteração seja aplicada.

**Dados necessários / Campos:**

- Nome do medicamento
- Uso principal
- Contraindicações
- Status
- Data da alteração
- Usuário responsável

**Notas / Observações:**

- Histórico de alterações deve ser auditável e protegido contra edição indevida.
- Mensagens de alerta devem ser exibidas em alterações de informações críticas.

#### \[Medicamentos] Remover ou arquivar medicamento

> [Issue #91](https://github.com/MatheusVelame/MedFlow/issues/91)

Como administrador da clínica, quero remover ou arquivar medicamentos que não são mais utilizados, para manter o sistema limpo e preservar a integridade dos registros.

**Critérios de aceite:**

- **RN**: Não é permitido remover medicamentos vinculados a prescrições ativas.
- **RN**: É preferível a ação de arquivar um medicamento, ao invés de excluí-lo, para manter um registro de seu histórico.
- **RN**: Medicamentos arquivados não devem aparecer nas listas de consulta padrão, mas podem ser acessados via filtros específicos (ex: "Mostrar medicamentos arquivados").
- **RN**: Remoção ou arquivamento deve ser restrita a perfis de alta autoridade no sistema.

**Dados necessários / Campos:**

- Nome do medicamento
- Status atual (Ativo/Arquivado)
- Indicador de vínculo com prescrição ativa (Sim/Não)
- Usuário responsável pela ação

**Notas / Observações:**

- Arquivamento preserva rastreabilidade e histórico de uso do medicamento.
- Sistema deve informar ao usuário quando a remoção não for permitida devido a vínculos ativos.

## Epic - Funcionários

### Gerenciamento de funcionários (enfermeiros, recepcionistas, etc.) - Maju

#### \[Funcionários] Cadastrar funcionário

> [Issue #65](https://github.com/MatheusVelame/MedFlow/issues/65)

Como administrador da clínica, quero cadastrar funcionários (enfermeiros, recepcionistas, auxiliares, etc.) com nome, função e contato, para que possam ser vinculados às atividades da clínica.

**Critérios de aceite:**

- **RN**: O nome do funcionário é obrigatório.
- **RN**: A função do funcionário é obrigatória.
- **RN**: O nome deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços).
- **RN**: O contato (telefone e/ou e-mail) é obrigatório.
- **RN**: Não é permitido cadastrar dois funcionários com o mesmo nome e contato (combinação única).
- **RN**: O contato deve seguir formato válido (telefone com DDD ou e-mail válido).
- **RN**: O status inicial do funcionário cadastrado deve ser definido como "Ativo".

**Dados necessários / Campos:**

- Nome
- Função
- Contato (telefone e/ou e-mail)
- Status (definido automaticamente como "Ativo")

**Notas / Observações:**

- Validação deve ser feita tanto no frontend quanto no backend.

#### \[Funcionários] Atualizar dados de funcionário

> [Issue #66](https://github.com/MatheusVelame/MedFlow/issues/66)

Como administrador da clínica, quero atualizar os dados cadastrais de um funcionário, para que as informações permaneçam corretas e atualizadas.

**Critérios de aceite:**

- **RN**: Apenas nome, função e contato podem ser alterados.
- **RN**: Os dados alterados devem seguir as mesmas validações do cadastro.
- **RN**: Alterações não devem afetar registros históricos vinculados ao funcionário.
- **RN**: Não é permitido inativar ou alterar o nome de uma função vinculada a funcionários ativos sem tratamento adequado (ex.: reatribuição).

**Dados necessários / Campos:**

- Nome
- Função
- Contato (telefone/e-mail)

**Notas / Observações:**

- Histórico de atendimentos/escalas não deve ser modificado retroativamente.

#### \[Funcionários] Consultar lista de funcionários

> [Issue #67](https://github.com/MatheusVelame/MedFlow/issues/67)

Como administrador da clínica, quero consultar a lista de funcionários e suas respectivas funções, para ter uma visão geral da equipe e do status de cada um.

**Critérios de aceite:**

- **RN**: A listagem deve exibir no mínimo: nome, função, status (Ativo/Inativo) e contato.
- **RN**: Por padrão, devem ser listados apenas funcionários Ativos.
- **RN**: Deve ser possível filtrar por função e status (Ativo/Inativo) via interface.
- **RN**: Deve ser possível ordenar alfabeticamente pelo nome.

**Notas / Observações:**

- O layout deve priorizar clareza e permitir fácil identificação da função de cada funcionário.

#### \[Funcionários] Alterar status de funcionário

> [Issue #68](https://github.com/MatheusVelame/MedFlow/issues/68)

Como administrador da clínica, quero alterar o status de um funcionário (Ativo/Inativo), para controlar sua participação em atividades da clínica.

**Critérios de aceite:**

- **RN**: É permitido alterar o status de um funcionário de Ativo para Inativo e vice-versa.
- **RN**: Funcionários Inativos não podem ser atribuídos a novas escalas, atendimentos ou agendamentos.
- **RN**: A inativação de um funcionário deve manter seu histórico de atuação preservado no sistema.
- **RN**: Não é permitido inativar funcionário vinculado a atividades futuras (ex.: plantões agendados) sem resolver previamente os vínculos.

**Notas / Observações:**

- Deve haver mensagem clara para o administrador quando houver impedimentos para inativação.

## Epic - Faturamento

### Controle de faturamentos (consultas, exames, serviços) - Paulo

#### \[Faturamento] Registrar valores de consultas e exames

> [Issue #101](https://github.com/MatheusVelame/MedFlow/issues/101)

Como funcionário do setor financeiro, quero registrar os valores de consultas e exames realizados, para controlar os faturamentos da clínica.

**Critérios de aceite:**

- **RN**: O registro de um faturamento deve conter obrigatoriamente: identificação do paciente, procedimento (consulta ou exame), valor e método de pagamento.
- **RN**: O valor deve ser positivo e compatível com os valores cadastrados previamente para o procedimento.
- **RN**: O faturamento deve receber status inicial "Pendente".

**Dados necessários / Campos:**

- ID do paciente
- Tipo e descrição do procedimento (consulta ou exame)
- Valor
- Método de pagamento (dinheiro, cartão, convênio etc.)
- Data e hora do faturamento
- Status (default: Pendente)
- Usuário responsável pelo registro

**Notas / Observações:**

- Deve haver validação automática do valor com base na tabela de preços cadastrada.
- Caso o valor seja diferente do padrão, o sistema deve exigir justificativa.

#### \[Faturamento] Alterar status da cobrança

> [Issue #102](https://github.com/MatheusVelame/MedFlow/issues/102)

Como administrador financeiro, quero alterar o status de cobranças já registradas, para atualizar a situação dos pagamentos.

**Critérios de aceite:**

- **RN**: O sistema deve permitir alterar o status apenas para valores registrados.
- **RN**: Opções de status: **Pendente, Pago, Cancelado**.
- **RN**: A alteração de status deve ser restrita a usuários com permissão administrativa (ex.: setor financeiro ou administrador do sistema).
- **RN**: Após ser alterado para "Pago", o status não pode ser revertido sem permissão especial.

**Dados necessários / Campos:**

- ID do faturamento
- Status atual
- Novo status
- Data e hora da alteração
- Usuário responsável pela alteração
- (Opcional) Motivo da alteração

**Notas / Observações:**

- Alterações devem ser registradas no log de auditoria.
- Mudança de status pode disparar notificações internas (ex.: contabilização automática).

#### \[Faturamento] Consultar histórico financeiro

> [Issue #103](https://github.com/MatheusVelame/MedFlow/issues/103)

Como gestor da clínica, quero consultar o histórico financeiro, para acompanhar cobranças realizadas e pendentes.

**Critérios de aceite:**

- **RN**: A consulta deve permitir filtros por período, paciente, procedimento ou status da cobrança.
- **RN**: O histórico deve exibir: paciente, procedimento, valor, método de pagamento, status atual.
- **RN**: O sistema deve permitir ordenar os resultados por data, valor ou status.

**Dados necessários / Campos exibidos:**

- ID do faturamento
- Paciente
- Procedimento
- Valor
- Método de pagamento
- Status da cobrança
- Data do registro

**Notas / Observações:**

- Relatórios devem estar disponíveis para exportação (CSV, PDF).
- Histórico deve respeitar regras de acesso (apenas setor financeiro e administradores).

#### \[Faturamento] Excluir registros inválidos

> [Issue #104](https://github.com/MatheusVelame/MedFlow/issues/104)

Como administrador financeiro, quero excluir registros de faturamento inválidos, para manter a base de dados correta e sem duplicidades.

**Critérios de aceite:**

- **RN**: A exclusão só pode ser feita por administradores.
- **RN**: Só podem ser excluídos registros com status **Pendente** ou marcados como inválidos.
- **RN**: Exclusões e alterações devem ser registradas em log, incluindo usuário responsável, data, ação realizada e motivo.

**Dados necessários / Campos:**

- ID do faturamento
- Status atual
- Usuário responsável
- Data e hora da exclusão
- Motivo da exclusão

**Notas / Observações:**

- Exclusão deve ser lógica (registro marcado como removido) para garantir rastreabilidade.
- Em conformidade com LGPD, pode haver cenários de exclusão definitiva mediante requisição do paciente.

#### \[Faturamento] Logs e auditoria

> [Issue #105](https://github.com/MatheusVelame/MedFlow/issues/105)

Como gestor, quero que todas as ações de faturamento sejam registradas em log, para garantir rastreabilidade e conformidade legal.

**Critérios de aceite:**

- **RN**: O sistema deve registrar log de todas as ações: criação, alteração, exclusão.
- **RN**: Cada log deve conter: usuário, data, hora, ação e objeto afetado.
- **RN**: Logs não podem ser alterados ou excluídos.

**Dados necessários / Campos (log):**

- ID do faturamento
- Ação realizada (registro, alteração de status, exclusão)
- Usuário responsável
- Data e hora da ação
- Detalhes da ação

**Notas / Observações:**

- Logs devem ser acessíveis apenas a perfis de auditoria/gestão.
- Necessário compliance com **LGPD** e normas fiscais brasileiras.

### Controle de pagamentos de funcionários (salários, benefícios) - Lisa

#### \[Folha de Pagamento] Registrar folha de pagamento

> [Issue #84](https://github.com/MatheusVelame/MedFlow/issues/84)

Como administrador da clínica, quero registrar a folha de pagamento de um funcionário, para controlar salários e benefícios de forma organizada e precisa.

**Critérios de aceite:**

- **RN**: Para registrar uma folha de pagamento, é obrigatório vinculá-la a um funcionário ativo. Não é permitido gerar pagamentos para funcionários inativos.
- **RN**: O sistema deve impedir o registro de mais de uma folha de pagamento para o mesmo funcionário dentro do mesmo período de referência (mês/ano) para evitar duplicidade.
- **RN**: Ao ser registrada, a folha de pagamento deve receber status padrão **"Pendente"**.

**Dados necessários / Campos:**

- Funcionário (nome, CPF ou ID)
- Período de referência (mês/ano)
- Salário base
- Benefícios (vale-transporte, vale-alimentação, etc.)
- Status inicial = "Pendente"

**Notas / Observações:**

- O sistema deve validar a existência e status do funcionário antes de registrar a folha.
- Evitar duplicidade de registros para o mesmo funcionário no mesmo período.

#### \[Folha de Pagamento] Atualizar valores ou status

> [Issue #85](https://github.com/MatheusVelame/MedFlow/issues/85)

Como administrador da clínica, quero atualizar os valores ou o status de uma folha de pagamento, para corrigir lançamentos e registrar pagamentos realizados.

**Critérios de aceite:**

- **RN**: Os valores só podem ser atualizados enquanto o status da folha for **"Pendente"**.
- **RN**: O status de um pagamento pode ser alterado de **"Pendente"** para **"Pago"**. Após ser marcado como **"Pago"**, os valores não podem ser alterados.
- **RN**: Uma vez marcado como **"Pago"**, o status não pode ser revertido sem permissão de administrador, para garantir a integridade do histórico financeiro.

**Dados necessários / Campos:**

- Folha de pagamento (ID ou funcionário + período)
- Valores (salário, benefícios)
- Status (Pendente/Pago)

**Notas / Observações:**

- O sistema deve registrar data, hora e usuário responsável por cada alteração.
- Alterações em pagamentos já quitados exigem validação de permissão administrativa.

#### \[Folha de Pagamento] Consultar histórico de pagamentos

> [Issue #86](https://github.com/MatheusVelame/MedFlow/issues/86)

Como administrador ou gestor financeiro, quero consultar o histórico de pagamentos de funcionários, para acompanhar valores pagos e pendentes.

**Critérios de aceite:**

- **RN**: Deve ser possível filtrar por funcionário e por período (mês/ano).
- **RN**: O histórico deve exibir valores, status (**Pago**, **Pendente**) e data de referência de cada pagamento.

**Dados necessários / Campos:**

- Funcionário (nome, CPF ou ID)
- Período de referência (mês/ano)
- Valor total da folha
- Benefícios incluídos
- Status do pagamento
- Data de registro

**Notas / Observações:**

- O histórico deve permitir ordenação por data, funcionário ou status.
- Informações sensíveis devem ser acessíveis apenas a perfis autorizados.

#### \[Folha de Pagamento] Remover registros antigos/inválidos

> [Issue #87](https://github.com/MatheusVelame/MedFlow/issues/87)

Como administrador da clínica, quero remover registros de folha de pagamento inválidos, para manter a base limpa e corrigir duplicidades ou lançamentos incorretos.

**Critérios de aceite:**

- **RN**: Não é permitido remover registros com status **"Pago"**.
- **RN**: A remoção só pode ser feita em registros **inválidos** (duplicados ou incorretos) e com status **"Pendente"**.

**Dados necessários / Campos:**

- Folha de pagamento (ID ou funcionário + período)
- Status da folha (Pendente/Pago)
- Motivo da remoção (duplicidade, erro de lançamento, etc.)

**Notas / Observações:**

- O sistema deve registrar data, hora e usuário responsável pela remoção.
- Deve haver validação para impedir a exclusão de registros pagos.

## Epic - Convênios

### Gerenciamento de convênios/planos de saúde aceitos pela clínica - Maju

#### \[Convênios] Cadastrar convênio/plano

> [Issue #69](https://github.com/MatheusVelame/MedFlow/issues/69)

Como administrador da clínica, quero cadastrar novos convênios/planos de saúde aceitos, para que pacientes possam utilizá-los no atendimento.

**Critérios de aceite:**

- **RN**: O cadastro de convênios/planos deve conter nome do convênio, código de identificação e status (ativo/inativo).
- **RN**: Não é permitido cadastrar identificações de convênios duplicados (código já existente).
- **RN**: O convênio recém-cadastrado deve iniciar com status **Ativo** por padrão.

**Dados necessários / Campos:**

- Nome do convênio
- Código de identificação
- Status (definido automaticamente como "Ativo")

**Notas / Observações:**

- Validações devem ser feitas tanto no frontend quanto no backend.

#### \[Convênios] Alterar ou remover convênio inativo

> [Issue #70](https://github.com/MatheusVelame/MedFlow/issues/70)

Como administrador da clínica, quero alterar dados de convênios ou remover convênios inativos, para manter o cadastro atualizado e sem informações obsoletas.

**Critérios de aceite:**

- **RN**: Apenas usuários com permissão administrativa podem alterar dados de convênio.
- **RN**: A exclusão definitiva de um convênio só pode ocorrer se este estiver marcado como **Inativo**.
- **RN**: O sistema deve manter histórico de alterações e remoções, registrando data, hora e responsável pela ação.

**Dados necessários / Campos:**
- Nome do convênio
- Código de identificação
- Status (Ativo/Inativo)
- Histórico (data, hora e usuário responsável pela ação)

**Notas / Observações:**

- Histórico deve ser consultável em caso de auditoria.

#### \[Convênios] Consultar pacientes vinculados a convênio

> [Issue #71](https://github.com/MatheusVelame/MedFlow/issues/71)

Como profissional autorizado, quero consultar quais pacientes estão vinculados a um convênio específico, para organizar atendimentos e faturamento.

**Critérios de aceite:**

- **RN**: A consulta deve permitir filtrar pacientes vinculados a um convênio específico.
- **RN**: O sistema deve exibir nome, número de registro e contato do paciente.
- **RN**: O acesso deve ser restrito apenas a profissionais autorizados, respeitando regras de confidencialidade.

**Dados necessários / Campos:**

- Convênio selecionado
- Paciente (nome completo)
- Número de registro do paciente
- Contato do paciente (telefone e/ou e-mail)

**Notas / Observações:**

- O resultado deve ser paginado em caso de grande volume de pacientes.

---
