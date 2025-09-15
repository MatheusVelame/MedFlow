
# Descrição das funcionalidades e histórias

## Funcionalidades principais da clínica

### Gerenciamento de pacientes (cadastro, edição, exclusão e consulta) - Luziane

- **Cadastrar dados (nome, CPF, telefone, endereço, data de nascimento).**
  - **RN**: nome, CPF, data de nascimento e telefone são dados obrigatórios para o cadastro de pacientes.
  - **RN**: O CPF deve conter apenas 11 dígitos numéricos.
  - **RN**: a data de nascimento deve estar no formato dd/mm/aaaa.
  - **RN**: não é possível cadastrar um paciente com o mesmo CPF de outro paciente já cadastrado.
- **Atualizar dados dos pacientes.**
  - **RN**: CPF não pode ser alterado.
  - **RN**: a alteração não pode ser realizada sem o preenchimento dos dados obrigatórios.
  - **RN**: a data de nascimento a ser alterada deve estar no formato dd/mm/aaaa.
- **Consultar dados dos pacientes.**
  - **RN**: a consulta por CPF deve validar se o campo tem 11 dígitos numéricos.
  - **RN**: a consulta por CPF deve retornar apenas o paciente com o CPF informado.
  - **RN**: a consulta por nome deve retornar todos os pacientes com o nome informado. 
  - **RN**: a consulta deve informar todos os dados do paciente.
- **Remover cadastro de pacientes.**
  - **RN**: não é possível remover paciente que não exista no sistema.
  - **RN**: não é possível remover paciente que tenha prontuário.
  - **RN**: não é possível remover paciente que tenha consulta agendada.
  - **RN**: não é possível remover paciente que tenha exame agendado.

### Gerenciamento de médicos (dados pessoais, CRM, especialidade) - Lisa

- **Cadastrar médicos com dados pessoais, CRM e especialidade vinculada.**
  - **RN**: nome, CPF, CRM, data de nascimento e número de contato são dados obrigatórios para o cadastro de médicos.
  - **RN**: O CPF deve conter apenas 11 dígitos numéricos.
  - **RN**: a data de nascimento deve estar no formato dd/mm/aaaa.
  - **RN**: não é possível cadastrar um médico com o mesmo CPF de outro médico já cadastrado.
  - **RN**: não é possível cadastrar um médico com o mesmo CRM de outro médico já cadastrado.
  - **RN**: O médico só pode ser vinculado a uma especialidade que já esteja previamente cadastrada no sistema.
  - **RN**: O cadastro da disponibilidade (horários de atendimento) pode ser realizado em um segundo momento, não sendo obrigatório no cadastro inicial.
- **Atualizar as informações cadastrais dos médicos quando necessário.**
  - **RN**: O campo CRM, uma vez salvo, não deve ser editável.
  - **RN**: Todas as informações pessoais, como nome e telefone, podem ser atualizadas a qualquer momento.
  - **RN**: A alteração na disponibilidade de horários do médico só pode ser permitida em períodos que não possuam consultas futuras já agendadas.
- **Consultar a lista de médicos cadastrados e seus detalhes.**
  - **RN**: Por padrão, a tela de listagem de médicos deve exibir apenas os médicos com status "Ativo".
  - **RN**: A interface deve permitir filtrar a lista de médicos por especialidade e por status (Ativos ou Inativos).
  - **RN**: A lista deve exibir, no mínimo, o nome completo do médico, seu CRM e a especialidade principal vinculada.
- **Remover ou inativar o cadastro de médicos que não atuam mais na clínica.**
  - **RN**: Um médico não pode ser permanentemente excluído do sistema se ele estiver vinculado a qualquer registro histórico, como consultas , prontuários eletrônicos ou solicitações de exames.
  - **RN**: Ao invés da exclusão, deve-se utilizar a função de "Inativar". Um médico inativo não aparecerá em listas para novos agendamentos de consultas ou exames.
  - **RN**: Todos os registros associados a um médico inativado devem ser mantidos no sistema para fins de consulta ao histórico.
  - **RN**: A exclusão física de um registro de médico só deve ser permitida se ele não possuir absolutamente nenhum outro registro vinculado a ele no sistema (por exemplo, um cadastro feito por engano e nunca utilizado).

### Gerenciamento de funcionários (enfermeiros, recepcionistas, etc.) - Maju

- **Cadastrar funcionários (enfermeiros, recepcionistas, etc.) com nome, função e contato.**
  - **RN**: O nome do funcionário é obrigatório.
  - **RN**: A função do funcionário é obrigatória (ex.: enfermeiro, recepcionista, auxiliar etc.).
  - **RN**: O nome do funcionário deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços).
  - **RN**: O contato (telefone e/ou e-mail) é obrigatório.
  - **RN**: Não é permitido cadastrar dois funcionários com o mesmo nome e contato (validar combinação única).
  - **RN**: O contato deve seguir um formato válido (telefone com DDD ou e-mail válido).
  - **RN**: O status inicial do funcionário cadastrado deve ser definido como "Ativo".
- **Atualizar os dados cadastrais dos funcionários.**
  - **RN**:  Apenas nome, função e contato podem ser alterados.
  - **RN**:  Os dados alterados devem seguir as mesmas validações aplicadas no cadastro
  - **RN**: Alterações de dados não devem afetar registros históricos vinculados a esse funcionário (ex.: atendimentos, escalas etc.).
  - **RN**: Não é permitido inativar ou alterar o nome de uma função que esteja vinculada a funcionários ativos, sem que haja um tratamento para essa vinculação (ex.: reatribuição).
- **Consultar a lista de funcionários e suas respectivas funções.**
  - **RN**: A listagem deve exibir, no mínimo: nome, função, status (Ativo/Inativo) e contato.
  - **RN**: Por padrão, devem ser listados apenas os funcionários Ativos.
  - **RN**: A interface deve permitir filtrar funcionários por função e por status (Ativo/Inativo).
  - **RN**: A listagem deve permitir ordenar alfabeticamente pelo nome do funcionário.
- **Alterar o status de um funcionário (ativo/inativo).**
  - **RN**: É permitido alterar o status de um funcionário de Ativo para Inativo, e vice-versa.
  - **RN**: Funcionários com status Inativo não podem ser atribuídos a novas escalas, atendimentos ou agendamentos.
  - **RN**: A inativação de um funcionário deve manter seu histórico de atuação preservado no sistema.
  - **RN**: Não é permitido inativar um funcionário que esteja vinculado a atividades futuras (ex.: plantões agendados), sem antes resolver esses vínculos.

### Gerenciamento de especialidades médicas (clínico geral, pediatria, cardiologia, etc.) - Thaís

- **Cadastrar especialidades (pediatria, cardiologia, ortopedia etc.).**
  - **RN**: O nome da especialidade é obrigatório.
  - **RN**: Não é permitido cadastrar duas especialidades com o mesmo nome.
  - **RN**: O nome da especialidade deve conter apenas caracteres alfabéticos (com exceção de acentos e espaços).
  - **RN**: A descrição da especialidade (opcional) pode conter até 255 caracteres.
  - **RN**: O status inicial da especialidade cadastrada deve ser definido como "Ativa"
- **Alterar especialidade.**
  - **RN**: Apenas a descrição ou o nome da especialidade podem ser alterados.
  - **RN**: O nome alterado deve passar novamente pela validação de unicidade (não pode haver duas especialidades iguais).
  - **RN**: Não é permitido inativar ou alterar o nome de uma especialidade que esteja vinculada a médicos ativos, sem que haja um tratamento para essa vinculação (ex.: reatribuição).
- **Excluir especialidades.**
  - **RN**: Não é permitido excluir uma especialidade que esteja vinculada a pelo menos um médico ativo.
  - **RN**: A exclusão física de uma especialidade só pode ser realizada caso nunca tenha sido vinculada a nenhum médico.
  - **RN**: Caso haja histórico de vínculo com médicos, a especialidade deve ser marcada como "Inativa" em vez de excluída.
  - **RN**: Especialidades inativas não podem ser atribuídas a novos médicos.
- **Listar especialidades.**
  - **RN**: A listagem deve exibir, no mínimo, o nome da especialidade, seu status (Ativa/Inativa) e, se houver, a descrição.
  - **RN**: Por padrão, devem ser listadas apenas especialidades ativas.
  - **RN**: A interface deve permitir filtrar especialidades por status (Ativas ou Inativas).
  - **RN**: A listagem deve permitir ordenação alfabética pelo nome da especialidade.

### Agendamento de consultas (marcar, remarcar, cancelar, consultar) - Velame

- **Marcar consulta escolhendo paciente, médico, especialidade, data e hora.**
  - **RN**: Uma consulta só pode ser marcada se o horário estiver disponível na agenda do médico.
  - **RN**: O paciente e o médico devem ser selecionados de uma lista de usuários cadastrados no sistema.
  - **RN**: A data da consulta não pode ser anterior à data atual.
  - **RN**: O sistema deve validar se o médico atende a especialidade escolhida.
  - **RN**: Após a marcação, o sistema deve enviar uma notificação de confirmação para o paciente e para o médico, por e-mail ou SMS.
- **Remarcar caso precise alterar.**
  - **RN**: A remarcação só pode ser feita se o novo horário estiver disponível para o médico.
  - **RN**: É necessário um registro do histórico da remarcação, incluindo a data e o horário originais, bem como a nova data e o novo horário.
  - **RN**: Deve haver um limite de quantas vezes uma consulta pode ser remarcada (ex: no máximo 2 vezes).
  - **RN**: O sistema deve notificar o paciente e o médico sobre a alteração do horário.
  - **RN**: Se a remarcação ocorrer com menos de 24 horas de antecedência, pode haver uma regra de negócio específica (ex: não permitir a remarcação ou cobrar uma taxa).
- **Cancelar se o paciente desistir.**
  - **RN**: O cancelamento deve ser possível até um período mínimo antes da consulta (ex: 24 horas de antecedência) para evitar perda de tempo do profissional.
  - **RN**: É necessário registrar a razão do cancelamento.
  - **RN**: Ao cancelar a consulta, o horário na agenda do médico deve ser liberado automaticamente.
  - **RN**: O sistema deve enviar uma notificação de cancelamento para o paciente e para o médico.
  - **RN**: Caso o paciente cancele com frequência, o sistema pode aplicar uma penalidade ou restringir futuras marcações.
- **Consultar agenda (diária, semanal, mensal).**
  - **RN**: A agenda deve exibir somente os horários ocupados e disponíveis, não mostrando informações de outros pacientes para manter a privacidade.
  - **RN**: A visualização da agenda deve ser possível por diferentes períodos de tempo (diária, semanal, mensal).
  - **RN**: A consulta de agenda do médico só pode ser acessada por ele mesmo ou por um administrador do sistema.
  - **RN**: O sistema deve permitir filtrar a agenda por médico, especialidade ou status da consulta (confirmada, remarcada, cancelada).
  - **RN**: As informações exibidas devem incluir o nome do paciente, horário da consulta, e a especialidade do atendimento.

### Gerenciamento de prontuários eletrônicos (histórico clínico do paciente) - Paulo

- **Registrar histórico clínico de um paciente (sintomas, diagnóstico, tratamento).**
  - **RN**: O registro do histórico clínico deve conter, no mínimo, os seguintes dados: sintomas, diagnóstico e conduta/tratamento.
  - **RN**: O registro do histórico clínico deve conter, no mínimo, os seguintes dados: sintomas, diagnóstico e conduta/tratamento.
- **Atualizar informações a cada atendimento, inativando quando o atendimento finalizar.**
  - **RN**: Cada atualização do prontuário deve estar vinculada a um atendimento, e o registro deve ser inativado quando o atendimento for finalizado.
  - **RN**: O sistema deve garantir que o prontuário seja imutável em suas versões anteriores, mantendo histórico de alterações e evoluções.
- **Consultar histórico completo do paciente.**
  - **RN**: A consulta ao prontuário deve exibir todo o histórico clínico do paciente de forma cronológica e completa.
  - **RN**: O acesso ao prontuário deve ser restrito a profissionais autorizados (médicos, enfermeiros, administradores clínicos).
- **Possibilidade de exclusão ou arquivamento quando necessário.**
  - **RN**: A exclusão ou arquivamento de um prontuário só pode ser feita por usuários com permissão administrativa, devendo ser registrado log com data, hora e responsável pela ação.
- **Auditlogs:**
  - **RN**: O sistema deve manter trilha de auditoria para todas as operações realizadas no prontuário (criação, atualização, consulta, arquivamento/exclusão).


## Recursos complementares

### Gerenciamento de convênios/planos de saúde aceitos pela clínica - Maju

- **Cadastrar planos aceitos.**
  - **RN**: O cadastro de convênios/planos deve conter nome do convênio, código de identificação e status (ativo/inativo).
  - **RN**: Não deve ser permitido cadastrar identificações de convênios duplicados (código já existente).
  - **RN**: O convênio recém-cadastrado deve, por padrão, iniciar com status ativo.
- **Alterar ou remover convênios inativos.**
  - **RN**: A alteração de dados de convênio só pode ser feita por usuários com permissão administrativa.
  - **RN**: A exclusão definitiva de convênio só pode ocorrer se este estiver marcado como inativo.
  - **RN**: O sistema deve manter histórico de alterações e remoções, com registro de data, hora e responsável.
- **Consultar quais pacientes usam determinado plano.**
  - **RN**: A consulta deve permitir filtrar pacientes vinculados a um convênio específico.
  - **RN**: O sistema deve exibir as informações principais do paciente (nome, número de registro, contato).
  - **RN**: O acesso a essas informações deve ser restrito a profissionais autorizados, conforme regras de confidencialidade.

### Gerenciamento de exames - Thaís

- **Agendar exame vinculado ao paciente, médico e tipo de exame.**
  - **RN**: O agendamento de exame só pode ser realizado se o paciente e o médico já estiverem previamente cadastrados no sistema.
  - **RN**: O tipo de exame deve estar previamente cadastrado no sistema (ex.: raio-x, sangue, ultrassonografia).
  - **RN**: A data e o horário do exame são obrigatórios.
  - **RN**: Não é permitido agendar dois exames para o mesmo paciente no mesmo horário.
  - **RN**: O médico vinculado ao exame deve estar ativo no sistema.
  - **RN**: Não é permitido agendar exame em horário de indisponibilidade do médico.
  - **RN**: O exame deve receber um status inicial “Agendado”.
- **Atualizar agendamento de exame.**
  - **RN**: Só podem ser alterados a data, o horário, o tipo de exame e o médico responsável (desde que o novo médico esteja ativo).
  - **RN**: O paciente vinculado não pode ser alterado após a criação do agendamento.
  - **RN**: A alteração só será válida se não gerar conflito de horário para o paciente ou para o médico.
  - **RN**: O histórico de alterações de data/hora do exame deve ser registrado no sistema.
- **Excluir exames agendados.**
  - **RN**: Não é permitido excluir exames já realizados ou em andamento (somente os com status “Agendado”).
  - **RN**: Caso o exame já esteja vinculado a um laudo, não pode ser excluído; deve ser apenas marcado como “Cancelado”.
  - **RN**: A exclusão só é permitida se o exame ainda não estiver associado a nenhum registro clínico no prontuário do paciente.
  - **RN**: O cancelamento de exame deve registrar a data e o motivo do cancelamento.
- **Consultar exames agendados.**
  - **RN**: A consulta deve permitir filtrar por paciente, médico, tipo de exame, status ou intervalo de datas.
  - **RN**: A pesquisa por paciente deve validar o CPF (11 dígitos).
  - **RN**: O resultado da consulta deve exibir, no mínimo: paciente, médico responsável, tipo de exame, data/hora e status.
  - **RN**: Exames cancelados ou realizados devem aparecer apenas em consultas históricas, não na listagem padrão de agendamentos futuros.

### Gerenciamento de medicamentos (cadastro de medicamentos usados em prescrições) - Velame

- **Cadastrar medicamentos informando nome, uso e contraindicações.**
  - **RN**: O cadastro de um medicamento deve ser feito com no mínimo as seguintes informações: nome, uso principal, e contraindicações (se existirem)
  - **RN**: O nome do medicamento deve ser único no sistema para evitar duplicidade e confusão.
  - **RN**: A entrada de dados de uso e contraindicações deve suportar texto livre, mas é ideal que haja campos específicos para informações padronizadas (ex: "Uso: analgésico", "Uso: anti-inflamatório").
  - **RN**: O sistema deve validar a unicidade do nome do medicamento no momento do cadastro para evitar que informações repetidas sejam inseridas.
- **Consultar a lista de medicamentos já cadastrados no sistema.**
  - **RN**: A consulta deve permitir a busca por nome ou por tipo de uso do medicamento.
  - **RN**: A lista deve exibir, de forma clara, o nome, uso, e contraindicações de cada medicamento.
  - **RN**: O sistema deve permitir a ordenação da lista, por exemplo, em ordem alfabética pelo nome do medicamento.
  - **RN**: A consulta deve ser acessível apenas por usuários autorizados, como médicos e farmacêuticos.
- **Atualizar as informações de um medicamento.**
  - **RN**: A alteração das informações de um medicamento deve ser restrita a usuários com permissão (ex: médicos ou administradores).
  - **RN**: O sistema deve manter um histórico de alterações, registrando a data da mudança e quem a realizou.
  - **RN**: A atualização de informações críticas, como a contraindicação, deve ser revisada por um responsável antes de ser aplicada.
- **Remover ou arquivar medicamentos que não são mais utilizados.**
  - **RN**: A remoção de um medicamento do sistema só deve ser permitida se ele não estiver vinculado a nenhuma prescrição ativa para evitar erros de integridade.
  - **RN**: É preferível a ação de arquivar um medicamento, ao invés de excluí-lo, para manter um registro de seu histórico.
  - **RN**: Medicamentos que foram arquivados não devem aparecer nas listas de consulta padrão, mas devem ser acessíveis através de filtros específicos (ex: "Mostrar medicamentos arquivados").
  - **RN**: A remoção ou arquivamento deve ser restrita a perfis de usuário com autoridade máxima no sistema.


## Financeiro e gestão

### Controle de faturamentos (consultas, exames, serviços) - Paulo

- **Registrar valores de consultas e exames.**
- **Alterar informações de cobrança (status: pago, pendente).**
- **Consultar histórico financeiro.**
- **Excluir registros inválidos.**
  - **RN**: O registro de um faturamento deve conter obrigatoriamente: identificação do paciente, procedimento associado (consulta ou exame), valor e método de pagamento.
  - **RN**: O valor do faturamento deve ser positivo e compatível com os valores cadastrados para o procedimento.
  - **RN**: O sistema deve permitir alterar o status da cobrança apenas para valores já registrados, com as opções: Pago, Pendente, Cancelado.
  - **RN**: A alteração de status deve ser restrita a usuários com permissão administrativa (ex.: setor financeiro ou administrador do sistema).
  - **RN**: A consulta ao histórico financeiro deve permitir filtros por período, paciente, procedimento ou status da cobrança.
  - **RN**: O histórico deve exibir claramente: paciente, procedimento, valor, método de pagamento e status atual.
  - **RN**: O sistema deve permitir a ordenação da lista de faturamentos (por data, valor ou status).
  - **RN**: A exclusão de registros deve ser restrita a administradores e só pode ocorrer se o faturamento estiver com status Pendente ou marcado como inválido.
  - **RN**: O sistema deve manter um log de alterações e exclusões, registrando usuário responsável, data e ação realizada.


### Controle de pagamentos de funcionários (salários, benefícios) - Lisa

- **Registrar folha de pagamento.**
  - **RN**: Para registrar uma folha de pagamento, é obrigatório vinculá-la a um funcionário ativo no sistema. Não é possível gerar pagamentos para funcionários com status "inativo".
  - **RN**: O sistema deve impedir o registro de mais de uma folha de pagamento para o mesmo funcionário dentro do mesmo período de referência (mês/ano) para evitar duplicidade.
  - **RN**: Ao ser registrada, toda nova folha de pagamento deve receber o status padrão de "Pendente"
- **Atualizar valores ou status de pagamento.**
  - **RN**: Os valores de uma folha de pagamento só podem ser atualizados enquanto o status do pagamento for "Pendente". Após ser marcado como "Pago", os valores não podem ser alterados.
  - **RN**: O status de um pagamento pode ser alterado de "Pendente" para "Pago". Uma vez alterado para "Pago", o status não pode ser revertido sem uma permissão de administrador para garantir a integridade do histórico financeiro.
- **Consultar histórico de pagamentos.**
  - **RN**: A consulta ao histórico de pagamentos deve permitir a filtragem por funcionário e por período (mês e ano) para facilitar a busca.
  - **RN**: O histórico deve exibir claramente os valores, o status ("Pago", "Pendente") e a data de referência de cada pagamento registrado.
- **Remover registros antigos/inválidos.**
  - **RN**: Um registro de pagamento jamais pode ser removido se o seu status for "Pago".
  - **RN**: A remoção de registros só é permitida para pagamentos "inválidos" , como um lançamento duplicado ou incorreto, e somente se o status for "Pendente".

### Gerenciamento de tipos de exames - Luziane

- **Cadastrar tipo de exame (código, descrição,  especialidade, valor, status).**
  - **RN**: não é possível cadastrar um tipo de exame sem descrição.
  - **RN**: não é possível cadastrar um tipo de exames sem especialidade.
  - **RN**: não é possível cadastrar um tipo de exame sem valor.
  - **RN**: não é possível cadastrar um tipo de exame com valor inferior a 0.
  - **RN**: não é possível cadastrar um tipo de exame com status inativo.
  - **RN**: o código do um tipo de exame deve ser único.
- **Atualizar dados do tipo de exame .**
  - **RN**: não é possível alterar o código do tipo de exame .
  - **RN**: não é possível atualizar um tipo de exame inserindo um valor inferior a 0.
  - **RN**: não é possível alterar um tipo de exame se houver agendamentos vinculados.
- **Consultar um tipo de exame.**
  - **RN**: não é possível consultar um tipo de exame sem informar código ou descrição.
  - **RN**: a busca por código deve retornar no máximo 1 registro.
  - **RN**: a busca por código deve retornar o tipo de exame do código informado.
- **Excluir ou inativar um tipo de exame.**
  - **RN**: não é possível excluir um tipo de exame com agendamento vinculado (histórico ou futuro).
  - **RN**: não é possível excluir um tipo de exame inexistente.
  - **RN**: não é possível inativar um tipo de exame inexistente.

---
