# Contextos Delimitados - Bounded Contexts

- Cada contexto delimitado abaixo segue a seguinte estrutura:
  - **Visão Geral** → explicação clara da fronteira de responsabilidade.
  - **Termos Específicos** → ligação direta com o glossário/language ubiquitous.

## 1. Gerenciamento de Pacientes

- **Visão Geral:** Administra o ciclo de vida do Paciente: cadastro, atualização, consulta, unificação de duplicidades, (des)ativação, consentimentos e exportabilidade de dados (LGPD). Serve de fonte única de verdade para dados cadastrais e identificadores.  
- **Termos Específicos:**
  - Paciente: pessoa atendida pela clínica, identificada por documento oficial e/ou identificador interno.
  - Prontuário Eletrônico: histórico clínico do paciente, referenciado por este contexto.
  - Contato: meios de comunicação do paciente (telefone, e-mail, responsável).
  - Endereço: localização residencial do paciente.
  - Situação Cadastral: Ativo/Inativo (controle administrativo, não clínico).

## 2. Gerenciamento de Médicos e Funcionários

- **Visão Geral:** Abarca o cadastro e gestão de médicos e funcionários da clínica, garantindo que as informações estejam sempre atualizadas.
- **Termos Específicos:**
  - Médico
  - Funcionário
  - Especialidade Médica  

## 3. Agendamento de Consultas

- **Visão Geral:** Responsável pela gestão de agendamentos de consultas, incluindo marcação, remarcação, cancelamento e consulta à agenda.
- **Termos Específicos:**
  - Consulta

## 4. Gerenciamento de Convênios e Exames

- **Visão Geral:** Engloba o gerenciamento dos convênios aceitos pela clínica e o controle de exames solicitados.
- **Termos Específicos:**
  - Convênio/Plano de Saúde
  - Exame

## 5. Gestão Financeira

- **Visão Geral:** O contexto de Financeiro e Gestão é responsável pelo controle de faturamentos, garantindo que todos os registros financeiros da clínica sejam precisos, atualizados e consistentes. Ele assegura tanto a sustentabilidade econômica da clínica quanto a transparência na relação com pacientes, convênios e colaboradores.
- **Termos Específicos:**
  - Faturamento: Registro financeiro que consolida os valores de consultas, exames e serviços prestados pela clínica.
  - Método de Pagamento: Forma como o faturamento é quitado, podendo ser cartão de crédito, débito, dinheiro, convênio ou reembolso.
  - Status de Cobrança: Situação atual do faturamento, indicando se está Pago, Pendente ou Cancelado.
  - Histórico Financeiro: Conjunto de registros de faturamentos e pagamentos, permitindo análise e rastreabilidade ao longo do tempo.

## 6. Gerenciamento de Especialidades Médicas

- **Visão Geral**
  - Responsável pelo cadastro, alteração, exclusão e listagem de especialidades médicas utilizadas na clínica. Este contexto garante que cada especialidade seja única e consistente, servindo de referência para o vínculo entre médicos e seus campos de atuação.
- **Termos Específicos**
- Especialidade Médica
- **Restrições/Regras de Negócio Essenciais**
  - O nome da especialidade é obrigatório, único e só pode conter caracteres alfabéticos (com acentos e espaços).
  - O status inicial de toda especialidade cadastrada é “Ativa”.
  - Não é permitido excluir ou renomear uma especialidade vinculada a médicos ativos sem tratamento específico dessa vinculação.
  - Especialidades inativas não podem ser atribuídas a novos médicos.

## 7. Gerenciamento de Exames

- **Visão Geral**
  - Responsável pelo agendamento, atualização, cancelamento/exclusão e consulta de exames vinculados a pacientes, médicos e tipos de exame. Esse contexto garante a consistência dos agendamentos e o correto vínculo com laudos e prontuários.
- **Termos Específicos**
  - Exame
  - Laudo
  - Tipo de Exame
  - Status
- **Restrições/Regras de Negócio Essenciais**
  - O agendamento de exame só é permitido se paciente, médico e tipo de exame já estiverem cadastrados e ativos.
  - Não pode haver conflito de horário para o mesmo paciente ou para o mesmo médico.
  - Exames só podem ser excluídos se estiverem no status “Agendado” e não vinculados a laudos ou prontuários.
  - O histórico de alterações (data/hora, médico, tipo) deve ser registrado.
  - Consultas a exames devem sempre exibir os dados mínimos: paciente, médico, tipo, data/hora e status.

---
