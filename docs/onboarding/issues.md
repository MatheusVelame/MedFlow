# Onboarding de Issues do Projeto MEDFLOW

Bem-vindo(a) ao projeto **MEDFLOW**!  
Este documento serve como guia para criar, organizar e gerenciar **issues** no repositório. Seguindo essas diretrizes, garantimos consistência, rastreabilidade e priorização clara das tarefas.

---

## 1. Estrutura de uma Issue

Todas as issues de histórias devem seguir o padrão abaixo:

**Título:** `[Epic/Área] Breve descrição da funcionalidade`  
- Exemplo: `[Pacientes] Cadastrar novo paciente`

**Labels:**  
- Tipo de tarefa (`frontend`, `backend`, `qa`, `documentacao`, `bug`, `improvement`)  
- Epic (`epic:pacientes`, `epic:consultas`, etc.)  
- Prioridade (`priority:alta`, `priority:media`, `priority:baixa`)  
- Status (`status:to-do`, `status:in-progress`, `status:review`, `status:done`)

**Descrição (template padrão):**


Como \[tipo de usuário], quero \[ação], para \[benefício].

Critérios de aceite:

* \[Critério 1]
* \[Critério 2]
* \[Critério 3]

Dados necessários / Campos:

* \[Campo 1]
* \[Campo 2]
* \[Campo 3]

Notas / Observações:

* \[Se houver integrações, dependências ou regras de negócio]


## 2. Epics (Áreas principais do projeto)

Cada issue deve ser vinculada a um **epic**, que representa a área ou jornada do usuário:

| Epic | Descrição |
|------|-----------|
| `epic:pacientes` | Gestão de pacientes (cadastro, atualização, consulta) |
| `epic:medicos` | Gestão de médicos e especialidades |
| `epic:consultas` | Agendamento, remarcação e histórico de consultas |
| `epic:prontuario` | Prontuário eletrônico, registro de atendimentos |
| `epic:exames` | Solicitação, registro e acompanhamento de exames |
| `epic:medicamentos` | Cadastro e consulta de medicamentos |
| `epic:faturamento` | Registro de pagamentos e controle financeiro |
| `epic:folha-pagamento` | Cálculo e registro da folha de pagamento |
| `epic:convenios` | Cadastro e vínculo de pacientes a convênios |
| `epic:agenda` | Agenda do médico, eventos e bloqueios |
| `epic:historico` | Histórico de alterações e auditoria |

> Dica: prefixar com `epic:` permite filtrar facilmente todas as issues de uma área.

## 3. Labels recomendadas

**Tipo de tarefa:**  
- `frontend` → Interface do usuário  
- `backend` → Regras de negócio e banco de dados  
- `qa` → Testes e validação  
- `documentacao` → Documentos e manuais  
- `bug` → Correções de erros  
- `improvement` → Melhorias em funcionalidades existentes  

**Prioridade e status:**  
- `priority:alta`  
- `priority:media`  
- `priority:baixa`  
- `status:to-do`  
- `status:in-progress`  
- `status:review`  
- `status:done`  

**Extras funcionais (opcional):**  
- `autenticacao` → login, roles e permissões  
- `relatorios` → geração de relatórios  
- `integracoes` → APIs externas ou laboratórios  

## 4. Boas práticas ao criar uma issue

1. **Sempre vincular a um epic.**  
2. **Definir prioridade e tipo de tarefa.**  
3. **Escrever título claro e objetivo.**  
4. **Preencher a descrição com história do usuário, critérios de aceite e campos necessários.**  
5. **Evitar criar issues genéricas ou sem contexto.**  
6. **Se a história for grande, quebrar em sub-tarefas ou issues vinculadas.**  

## 5. Exemplo completo de issue

```
**Título:** `[Pacientes] Cadastrar novo paciente`  
**Labels:** `frontend`, `backend`, `epic:pacientes`, `priority:alta`, `status:to-do`  

**Descrição:**

Como funcionário da clínica, quero cadastrar um novo paciente, para que ele possa ser atendido.

Critérios de aceite:

* Formulário com Nome, CPF, Telefone, Endereço, Data de Nascimento, Status
* Validação de CPF único
* Registro ativo salvo no banco de dados

Dados necessários / Campos:

* Nome
* CPF
* Telefone
* Endereço
* Data de Nascimento
* Status
```

## 6. Recursos úteis

- [Guia de Markdown para GitHub](https://guides.github.com/features/mastering-markdown/)  
- [Boards e milestones no GitHub](https://docs.github.com/en/issues/organizing-your-work-with-project-boards/about-project-boards)  

**Seguindo essas diretrizes, todas as issues do MEDFLOW terão consistência, clareza e rastreabilidade, facilitando delegação e acompanhamento do progresso do projeto.**

---
