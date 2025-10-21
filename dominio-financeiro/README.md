# Módulo Domínio Financeiro

Este módulo implementa o contexto delimitado "Gestão Financeira" do sistema MedFlow, responsável pelo controle de faturamentos de consultas e exames.

## Funcionalidades Implementadas

### 1. Registro de Faturamentos
- ✅ Criação de faturamentos para consultas e exames
- ✅ Validação de campos obrigatórios (paciente, procedimento, valor, método de pagamento)
- ✅ Validação de valores positivos e compatibilidade com tabela de preços
- ✅ Status inicial "Pendente" obrigatório
- ✅ Histórico completo de alterações

### 2. Exclusão Lógica de Faturamentos
- ✅ Exclusão lógica (soft delete) para manter rastreabilidade
- ✅ Controle de permissões (apenas administradores)
- ✅ Validação de status permitidos (Pendente ou Inválido)
- ✅ Log detalhado de todas as operações
- ✅ Auditoria de tentativas negadas

### 3. Gestão de Status
- ✅ **Pendente** - Status inicial
- ✅ **Pago** - Faturamento quitado
- ✅ **Cancelado** - Faturamento cancelado
- ✅ **Inválido** - Faturamento marcado como inválido
- ✅ **Removido** - Faturamento excluído logicamente

## Arquitetura

### Entidades
- **Faturamento** - Aggregate Root
- **FaturamentoId** - Value Object para identificação
- **PacienteId** - Value Object para identificação do paciente
- **Valor** - Value Object para valores monetários
- **MetodoPagamento** - Value Object para métodos de pagamento
- **UsuarioResponsavelId** - Value Object para identificação do usuário

### Enums
- **StatusFaturamento** - Estados do faturamento
- **TipoProcedimento** - Tipos de procedimento (Consulta/Exame)
- **AcaoHistoricoFaturamento** - Ações registradas no histórico

### Serviços
- **FaturamentoServico** - Lógica de negócio principal
- **TabelaPrecosServico** - Gestão de preços padrão
- **FaturamentoRepositorio** - Interface de persistência

## Regras de Negócio

### RN1 - Campos Obrigatórios
- ID do paciente
- Tipo e descrição do procedimento
- Valor (deve ser positivo)
- Método de pagamento

### RN2 - Validação de Valores
- Valor deve ser positivo
- Valor deve ser compatível com tabela de preços
- Justificativa obrigatória para valores divergentes

### RN3 - Status Inicial
- Todo faturamento inicia com status "Pendente"
- Status informado pelo cliente é ignorado

### RN4 - Exclusão Lógica
- Apenas administradores podem excluir
- Apenas status Pendente ou Inválido podem ser excluídos
- Exclusão é lógica (não física) para auditoria

## Testes

O módulo inclui testes BDD completos usando Cucumber:

- **gerenciamento_de_faturamentos.feature** - Testes de registro
- **exclusao_logica_de_faturamentos.feature** - Testes de exclusão
- Cobertura de 100% das regras de negócio
- Cenários de sucesso e falha

## Uso

```java
// Criar faturamento
var faturamento = faturamentoServico.registrarFaturamento(
    new PacienteId("PAC-001"),
    TipoProcedimento.CONSULTA,
    "Consulta Clínica",
    new Valor(new BigDecimal("200.00")),
    new MetodoPagamento("Cartão"),
    new UsuarioResponsavelId("admin"),
    "Observações"
);

// Excluir logicamente
faturamentoServico.excluirLogicamente(
    faturamento.getId(),
    "Duplicidade de lançamento",
    new UsuarioResponsavelId("admin"),
    true // é administrador
);
```

## Conformidade

- ✅ **LGPD** - Exclusão lógica preserva rastreabilidade
- ✅ **Auditoria** - Histórico completo de todas as operações
- ✅ **Segurança** - Controle de permissões
- ✅ **Integridade** - Validações rigorosas