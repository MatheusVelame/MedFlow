# Fluxos e Processos Chave do Domínio

## 1. Processo de Agendamento de Consulta

- Passo 1: O paciente seleciona um médico e especialidade para o agendamento e comunica à atendente.
- Passo 2: O sistema verifica a disponibilidade de horários do médico.
- Passo 3: O paciente escolhe a data e hora da consulta.
- Passo 4: A consulta é confirmada e registrada no sistema.

## 2. Processo de Agendamento de Exames

- Passo 1: A atendente inicia o agendamento do exame, selecionando: tipo de exame, médico, data e horário.
- Passo 2: O sistema verifica a disponibilidade de horários do médico e do paciente, além da compatibilidade entre o tipo de exame e a especialidade do médico.
- Passo 3: Estando válido, o exame é confirmado e registrado no sistema.

## 3. Processo de Gerenciamento de Prontuários Eletrônicos

**3.1. Registro do Histórico Clínico do Paciente**

- Passo 1: O profissional de saúde autorizado inicia o registro do prontuário eletrônico.
- Passo 2: São inseridos, no mínimo, os dados obrigatórios: sintomas, diagnóstico e conduta/tratamento.
- Passo 3: O sistema valida os dados e salva o registro no prontuário do paciente.
- Passo 4: O sistema mantém a trilha de auditoria, registrando data, hora e profissional responsável pela criação. 

**3.2. Atualização do Prontuário Durante Atendimento**

- Passo 1: Durante um novo atendimento, o profissional autorizado acessa o prontuário do paciente.  
- Passo 2: O profissional insere informações adicionais ou complementares relacionadas ao atendimento.
- Passo 3: O sistema cria uma nova versão vinculada ao atendimento em andamento, preservando as versões anteriores.
- Passo 4: Ao finalizar o atendimento, o prontuário é marcado como inativado para aquele episódio.
- Passo 5: O sistema atualiza a trilha de auditoria com todas as alterações realizadas. 

**3.3. Consulta ao Prontuário**

- Passo 1: Um profissional autorizado solicita a consulta ao prontuário do paciente.
- Passo 2: O sistema verifica as permissões de acesso (médico, enfermeiro, administrador clínico).
- Passo 3: O sistema exibe todo o histórico clínico do paciente em ordem cronológica, sem permitir alterações retroativas.  
- Passo 4: A consulta é registrada na trilha de auditoria, incluindo usuário, data e hora.  

**3.4. Exclusão ou Arquivamento do Prontuário**

- Passo 1: Um usuário com permissão administrativa solicita a exclusão ou arquivamento do prontuário.
- Passo 2: O sistema valida a permissão e registra o motivo da ação.
- Passo 3: O prontuário é marcado como excluído ou arquivado, permanecendo inacessível para consultas normais.
- Passo 4: O sistema gera log detalhado com data, hora e responsável pela ação.

## 4. Processo de Controle de Faturamentos

**4.1. Registro de Faturamento**

- Passo 1: O usuário autorizado (setor financeiro ou atendente) inicia o registro do faturamento.
- Passo 2: São informados obrigatoriamente: identificação do paciente, procedimento associado (consulta ou exame), valor e método de pagamento.
- Passo 3: O sistema valida se o valor é positivo e compatível com a tabela de valores cadastrada para o procedimento.
- Passo 4: O registro é salvo no sistema e vinculado ao paciente.  
- Passo 5: O sistema atualiza o log com a criação do faturamento (usuário, data, hora e dados registrados).  

**4.2. Alteração de Status da Cobrança**

- Passo 1: Um usuário com permissão administrativa acessa um faturamento registrado.
- Passo 2: O sistema exibe as opções de status disponíveis: Pago, Pendente, Cancelado.
- Passo 3: O usuário seleciona o novo status e confirma a alteração.  
- Passo 4: O sistema registra a atualização, mantendo o histórico no log (usuário, data, hora e alteração realizada).

**4.3. Consulta ao Histórico Financeiro**

- Passo 1: O usuário autorizado solicita a consulta de faturamentos no sistema.  
- Passo 2: O sistema disponibiliza filtros por período, paciente, procedimento ou status da cobrança.
- Passo 3: A lista de faturamentos é exibida com as seguintes informações: paciente, procedimento, valor, método de pagamento e status atual.
- Passo 4: O sistema permite ordenar os resultados por data, valor ou status.
- Passo 5: A consulta realizada é registrada na trilha de auditoria.  

**4.4. Exclusão de Registros Inválidos**

- Passo 1: Um administrador acessa o faturamento que se deseja excluir.  
- Passo 2: O sistema valida se o registro está com status Pendente ou marcado como inválido.
- Passo 3: Caso válido, o faturamento é excluído do acesso regular do sistema.
- Passo 4: O sistema gera log de exclusão com data, hora, usuário responsável e motivo da exclusão.

## 5. Processo de Gerenciamento de Especialidades Médicas

**Fluxo Principal (Cadastro de Especialidade):**

1. O administrador da clínica solicita o cadastro de uma nova especialidade.
2. O sistema solicita o preenchimento dos campos obrigatórios (nome).
3. O sistema valida se o nome contém apenas caracteres alfabéticos e se não existe outra especialidade com o mesmo nome.
4. Opcionalmente, é preenchida a descrição (até 255 caracteres).
5. O sistema cria a especialidade com status inicial “Ativa”.  

**Fluxo Alternativo (Alteração de Especialidade):**

1. O administrador solicita a alteração de nome ou descrição de uma especialidade existente.
2. O sistema valida se a alteração mantém a unicidade do nome.
3. Caso a especialidade esteja vinculada a médicos ativos, o sistema exige tratamento da vinculação antes da alteração.
4. O sistema salva as mudanças e atualiza a especialidade.  

**Fluxo Alternativo (Exclusão/Inativação):**

1. O administrador solicita a exclusão de uma especialidade.
2. O sistema verifica se há médicos vinculados:
	- Se não houver histórico → exclusão física.
	- Se houver histórico → marca como “Inativa”.
3. Especialidades inativas não podem ser atribuídas a novos médicos. 

**Fluxo de Consulta/Listagem:**

1. O usuário acessa a listagem de especialidades.
2. O sistema exibe, por padrão, apenas as especialidades “Ativas” com nome e status.
3. O usuário pode aplicar filtros por status ou ordenar alfabeticamente.  

## 6. Processo de Gerenciamento de Exames

**Fluxo Principal (Agendamento de Exame):**

1. O atendente seleciona o paciente, médico e tipo de exame.
2. O sistema valida se paciente, médico e exame estão previamente cadastrados e ativos.
3. O atendente informa data e horário do exame.
4. O sistema verifica se não há conflito de horários para paciente e médico.
5. O sistema registra o agendamento com status inicial “Agendado”.  

**Fluxo Alternativo (Atualização de Exame):**

1. O atendente solicita a alteração de data, horário, médico ou tipo de exame.
2. O sistema valida se o novo médico está ativo e se não há conflito de horário.
3. O paciente vinculado não pode ser alterado.
4. O sistema salva as alterações e registra histórico da mudança.  

**Fluxo Alternativo (Cancelamento/Exclusão de Exame):**

1. O atendente solicita o cancelamento ou exclusão de exame.
2. O sistema verifica o status:
	- Se “Agendado” → pode excluir, desde que não esteja vinculado a laudo ou prontuário.
	- Se já realizado ou vinculado a laudo → não pode excluir, apenas marcar como “Cancelado”.
3. O sistema registra motivo e data do cancelamento.  

**Fluxo de Consulta de Exames:**

1. O usuário acessa a tela de pesquisa de exames.
2. O sistema permite filtrar por paciente (CPF validado), médico, tipo de exame, status ou intervalo de datas.
3. O sistema exibe, no mínimo: paciente, médico responsável, tipo, data/hora e status.
4. Exames cancelados/realizados aparecem apenas em consultas históricas, não na agenda padrão.

## 7. Processo de Gerenciamento de Pacientes

**Fluxo Principal (Cadastro de Paciente):**

1. O atendente solicita o cadastro de um novo paciente.
2. O sistema solicita o preenchimento dos campos obrigatórios: nome completo, CPF, telefone, e data de nascimento.
3. Opcionalmente, é preenchido o endereço.
4. O sistema valida os dados: formato correto, unicidade do CPF, data de nascimento válida.
5. O sistema cria o paciente com status inicial “Ativo”.

**Fluxo Alternativo (Alteração de Paciente):**

1. O atendente solicita a alteração de dados de um paciente existente.
2. O sistema valida os campos alterados (ex.: CPF não pode ser duplicado).
3. O sistema salva as mudanças e registra histórico da alteração.

**Fluxo Alternativo (Exclusão/Inativação de Paciente):**

1. O administrador solicita a exclusão ou inativação de um paciente.
2. O sistema verifica se há histórico associado (consultas, prontuário, exames, faturamentos):
	- Se não houver histórico → exclusão física.
	- Se houver histórico → marca como “Inativo”.
3. Pacientes inativos não podem ser usados em novos agendamentos.

**Fluxo de Consulta/Listagem:**

1. O usuário acessa a listagem de pacientes.
2. O sistema exibe os pacientes com nome, CPF, telefone e status.
3. O usuário pode aplicar filtros (nome, CPF, status) ou ordenar alfabeticamente.

## 8. Processo de Gerenciamento de Tipos de Exames

**Fluxo Principal (Cadastro de Tipo de Exame):**

1. O administrador solicita o cadastro de um novo tipo de exame.
2. O sistema solicita o preenchimento dos campos obrigatórios: código, descrição, especialidade vinculada e valor.
3. O sistema valida os dados: código único, valor positivo e especialidade ativa.
4. O sistema cria o tipo de exame com status inicial “Ativo”.

**Fluxo Alternativo (Alteração de Tipo de Exame):**

1. O administrador solicita a alteração de descrição, especialidade ou valor de um tipo de exame.
2. O sistema valida os novos dados (especialidade deve estar ativa, valor deve ser positivo).
3. O sistema salva as mudanças e registra histórico da alteração.

**Fluxo Alternativo (Exclusão/Inativação de Tipo de Exame):**

1. O administrador solicita a exclusão de um tipo de exame.
2. O sistema verifica se há histórico de agendamentos/faturamentos:
	- Se não houver histórico → exclusão física.
	- Se houver histórico → marca como “Inativo”.
3. Tipos de exame inativos não podem ser usados em novos agendamentos.

**Fluxo de Consulta/Listagem:**

1. O usuário acessa a listagem de médicos.
2. O sistema exibe, por padrão, apenas os médicos com status “Ativo”, mostrando colunas como: Nome Completo, CRM, Especialidade Principal e Status.
3. O sistema oferece um campo de busca principal onde o usuário pode digitar o Nome, CPF ou CRM para encontrar um médico específico rapidamente.
4. Além da busca, o usuário pode refinar a lista aplicando filtros por Especialidade (ex: "Cardiologia", "Clínico Geral") e por Status ("Ativos" ou "Inativos").

## 9. Processo de Gerenciamento de Médicos

**Fluxo Principal (Cadastro de Médico):**

1. O funcionário autorizado solicita o cadastro de um novo médico no sistema.
2. O sistema solicita o preenchimento dos campos obrigatórios: nome completo, CPF, CRM, data de nascimento, e-mail, número de contato e especialidade.
3. O sistema valida os dados inseridos:
	- Unicidade do CPF.
	- Unicidade do CRM.
	- Formato da data de nascimento (dd/mm/aaaa).
	- Valida se a especialidade selecionada está previamente cadastrada e ativa.
4. Opcionalmente, podem ser cadastrados endereço e observações adicionais.
5. Opcionalmente, é preenchida a disponibilidade de horários.
6. O sistema cria o médico com status inicial “Ativo” e salva as informações.

**Fluxo Alternativo (Alteração de Médico):**

1. O administrador seleciona um médico existente e solicita a alteração dos seus dados.
2. O sistema exibe os campos editáveis (nome, contato, endereço, especialidade, disponibilidade), mantendo o campo CRM bloqueado para alteração.
3. O sistema valida se a alteração na disponibilidade de horários não conflita com consultas futuras já agendadas. Caso haja conflito, uma mensagem de alerta é exibida.
4. O sistema salva as mudanças e registra um histórico da alteração (usuário, data, hora e dados modificados).

**Fluxo Alternativo (Exclusão/Inativação de Médico):**

1. O administrador solicita a exclusão ou inativação de um médico.
2. O sistema verifica se o médico possui histórico associado (consultas, prontuários, exames).
3. Se não houver histórico: O sistema permite a exclusão física do registro, após confirmação.
4. Se houver histórico: O sistema impede a exclusão e oferece a opção de alterar o status para “Inativo”.
5. Médicos com status “Inativo” não podem ser selecionados para novos agendamentos, mas seu histórico é preservado.

**Fluxo de Consulta/Listagem:**

1. O usuário acessa a funcionalidade de listagem de médicos.
2. O sistema exibe, por padrão, apenas os médicos com status “Ativo”, mostrando nome completo, CRM e especialidade.
3. O usuário pode aplicar filtros por especialidade e status.

## 10. Processo de Controle de Pagamentos de Funcionários

**Fluxo Principal (Registro de Folha de Pagamento):**

1. O usuário autorizado (setor financeiro ou administrador) inicia o registro do pagamento de um funcionário.
2. O sistema solicita os dados obrigatórios: identificação do funcionário, salario, benefícios, período de referência e método de pagamento.
3. O sistema valida os dados:
	- Verifica se o funcionário selecionado está com status “Ativo”.
	- Verifica se já não existe uma folha de pagamento para o mesmo funcionário no mesmo período de referência.
4. O sistema registra o pagamento com status inicial “Pendente”.
5. O sistema atualiza o log com a criação do registro (usuário, data, hora e dados registrados).

**Fluxo Alternativo (Atualização de Pagamento):**

1. Um usuário com permissão acessa uma folha de pagamento registrada.
O sistema valida o status do registro para determinar as ações permitidas:
	- Se o status for “Pendente”:
		- O sistema permite a alteração apenas dos campos de valores (salário base, benefícios).
		- Os campos “Funcionário” e “Período de Referência” são bloqueados e não podem ser alterados. Se foram preenchidos incorretamente, o registro deve ser cancelado e um novo deve ser criado.
		- O usuário pode alterar o status para “Pago” ou “Cancelado”.
	- Se o status for “Pago” ou “Cancelado”:
		- O sistema bloqueia a alteração de todos os campos. O registro é considerado finalizado e imutável para garantir a integridade do histórico financeiro.
		- O sistema registra qualquer atualização permitida, mantendo o histórico da alteração no log (usuário, data, hora e qual alteração foi realizada).
4. O sistema registra a atualização, mantendo o histórico da alteração no log (usuário, data, hora e qual alteração foi realizada).

**Fluxo Alternativo (Exclusão de Pagamento Inválido):**

1. Um administrador acessa a folha de pagamento que deseja remover.
2. O sistema valida o status do registro. A remoção só é permitida se o status for “Pendente”.
3. Registros com status “Pago” ou “Cancelado” não podem ser removidos, pois são considerados parte permanente do histórico financeiro.
4. Caso a remoção seja válida, o registro é excluído do sistema após a confirmação do motivo (ex: duplicidade, erro de lançamento).
5. O sistema gera um log de exclusão com data, hora, usuário responsável e o motivo da remoção.

**Fluxo de Consulta/Listagem de Pagamentos:**

1. O usuário autorizado solicita a consulta ao histórico de pagamentos.
2. O sistema disponibiliza filtros status do pagamento (“Pago”, “Pendente” ou “Cancelado”) e e período de referência (mês/ano).
3. O sistema disponibiliza busca por nome de funcionário e cpf de funcionário.
4. A lista de pagamentos é exibida com as seguintes informações: funcionário, tipo de registro, período, valores, status e data de registro.
5. O sistema ordena os resultados por data.
6. A consulta realizada é registrada na trilha de auditoria.
---
