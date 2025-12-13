# Padrões de Projeto Implementados no MedFlow

Este documento lista os padrões de projeto (Design Patterns) que foram implementados no sistema MedFlow, identificando as classes criadas e/ou alteradas por conta de sua adoção.

---

## 1. Decorator

**Descrição:** O padrão Decorator permite adicionar novos comportamentos a objetos dinamicamente, envolvendo-os em objetos decoradores. Isso oferece uma alternativa flexível à herança para estender funcionalidades.

**Classes Criadas/Modificadas:**

### Módulo de Prontuário:
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioRepositorioBase.java`
  - Implementação base que contém a lógica essencial de persistência sem funcionalidades extras
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioRepositorioDecorator.java`
  - Decorator abstrato que define a estrutura comum para todos os decorators
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioRepositorioLoggingDecorator.java`
  - Decorator concreto que adiciona funcionalidade de logging às operações do repositório
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioRepositorioImpl.java`
  - Implementação final que compõe os decorators em cascata sobre a implementação base
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioMapeadorBase.java`
  - Mapeador base para conversão entre entidades de domínio e JPA
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioMapeadorDecorator.java`
  - Decorator abstrato para mapeadores
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/prontuario/ProntuarioMapeadorValidacaoDecorator.java`
  - Decorator concreto que adiciona validação ao mapeamento

### Módulo de Faturamento:
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/FaturamentoRepositorioBase.java`
  - Implementação base para repositório de faturamento
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/FaturamentoRepositorioDecorator.java`
  - Decorator abstrato para repositório de faturamento
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/FaturamentoRepositorioAuditoriaDecorator.java`
  - Decorator concreto que adiciona funcionalidade de auditoria às operações do repositório
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/FaturamentoRepositorioImpl.java`
  - Implementação final que compõe os decorators

**Como está sendo usado:**
O padrão Decorator é usado para adicionar funcionalidades transversais (cross-cutting concerns) como logging e auditoria aos repositórios, sem modificar a implementação base. Isso permite combinar múltiplos decorators em cascata, mantendo o código limpo e seguindo o princípio Open/Closed.

---

## 2. Observer

**Descrição:** O padrão Observer define uma dependência um-para-muitos entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

**Classes Criadas/Modificadas:**

### Interfaces de Domínio:
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/evento/EventoObservador.java`
  - Interface genérica que define o contrato para observadores de eventos
  
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/evento/EventoBarramento.java`
  - Interface que define o contrato para o barramento de eventos (Subject)
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/evento/EventoObservador.java`
  - Interface de observador para eventos de atendimento
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/evento/EventoBarramento.java`
  - Interface de barramento para eventos de atendimento
  
- `dominio-administracao/src/main/java/br/com/medflow/dominio/administracao/evento/EventoObservador.java`
  - Interface de observador para eventos de administração
  
- `dominio-administracao/src/main/java/br/com/medflow/dominio/administracao/evento/EventoBarramento.java`
  - Interface de barramento para eventos de administração
  
- `dominio-catalogo/src/main/java/br/com/medflow/dominio/evento/EventoObservador.java`
  - Interface de observador para eventos de catálogo
  
- `dominio-catalogo/src/main/java/br/com/medflow/dominio/evento/EventoBarramento.java`
  - Interface de barramento para eventos de catálogo
  
- `dominio-referencia/src/main/java/br/com/medflow/dominio/evento/EventoObservador.java`
  - Interface de observador para eventos de referência
  
- `dominio-referencia/src/main/java/br/com/medflow/dominio/evento/EventoBarramento.java`
  - Interface de barramento para eventos de referência

### Implementação:
- `apresentacao-backend/src/main/java/br/com/medflow/infraestrutura/evento/EventoBarramentoImpl.java`
  - Implementação concreta do barramento de eventos que gerencia a lista de observadores e distribui eventos para eles

### Observadores Concretos:
- `aplicacao/src/main/java/br/com/medflow/aplicacao/financeiro/convenios/ConvenioAuditoriaObservador.java`
  - Observador concreto que reage a eventos de Convenio para registrar ações de auditoria

### Configuração:
- `apresentacao-backend/src/main/java/br/com/medflow/BackendAplicacao.java`
  - Configuração do barramento de eventos e registro de observadores

**Como está sendo usado:**
O padrão Observer é usado para implementar um sistema de eventos de domínio (Domain Events), permitindo que diferentes partes do sistema reajam a mudanças de estado sem acoplamento direto. Quando um evento é publicado no barramento, todos os observadores registrados são notificados automaticamente.

---

## 3. Proxy

**Descrição:** O padrão Proxy fornece um substituto ou marcador de lugar para outro objeto para controlar o acesso a ele. Pode ser usado para adicionar funcionalidades como logging, auditoria, cache ou controle de acesso.

**Classes Criadas/Modificadas:**

### Proxy Estático:
- `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/EspecialidadeServicoProxy.java`
  - Proxy estático que intercepta chamadas ao serviço de especialidades para adicionar auditoria/log antes de repassar para a implementação real
  
- `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/IEspecialidadeServico.java`
  - Interface comum implementada tanto pelo serviço real quanto pelo proxy
  
- `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/EspecialidadeServicoImpl.java`
  - Implementação real do serviço (RealSubject)

- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/ExameServicoProxy.java`
  - Proxy estático que intercepta chamadas ao serviço de exames para adicionar auditoria/log
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/IExameServico.java`
  - Interface comum para o serviço de exames
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/ExameServicoImpl.java`
  - Implementação real do serviço de exames

### Proxy Dinâmico (Java Dynamic Proxy):
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/convenio/ConvenioRepositorioAplicacaoImpl.java`
  - Usa `Proxy.newProxyInstance()` e `InvocationHandler` para criar proxies dinâmicos de DTOs (`ConvenioResumo`, `ConvenioDetalhes`, `HistoricoEntradaResumo`) que mapeiam chamadas de métodos para propriedades de entidades JPA

### Configuração:
- `apresentacao-backend/src/main/java/br/com/medflow/apresentacao/config/ReferenciaConfig.java`
  - Configuração do bean que retorna o proxy em vez da implementação real para especialidades
  
- `apresentacao-backend/src/main/java/br/com/medflow/apresentacao/config/AtendimentoConfig.java`
  - Configuração do bean que retorna o proxy em vez da implementação real para exames

**Como está sendo usado:**
O padrão Proxy é usado de duas formas:
1. **Proxy Estático:** Para adicionar auditoria e logging aos serviços de domínio (Especialidades e Exames) sem modificar a implementação real.
2. **Proxy Dinâmico:** Para criar DTOs dinâmicos que mapeiam automaticamente chamadas de métodos para propriedades de entidades JPA, evitando a necessidade de criar classes DTO explícitas.

---

## 4. Strategy

**Descrição:** O padrão Strategy define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Strategy permite que o algoritmo varie independentemente dos clientes que o utilizam.

**Classes Criadas/Modificadas:**

### Módulo de Folha de Pagamento:
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/folhapagamento/CalculoFolhaStrategy.java`
  - Interface que define o contrato para diferentes estratégias de cálculo de folha de pagamento
  
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/folhapagamento/CalculoPagamentoStrategy.java`
  - Estratégia concreta para cálculo de folhas de PAGAMENTO (aplica descontos de INSS e IRRF)
  
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/folhapagamento/CalculoAjusteStrategy.java`
  - Estratégia concreta para cálculo de folhas de AJUSTE (não aplica descontos)
  
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/folhapagamento/CalculoFolhaStrategyFactory.java`
  - Factory responsável por criar a estratégia apropriada baseada no tipo de registro
  
- `dominio-financeiro/src/main/java/br/com/medflow/dominio/financeiro/folhapagamento/FolhaPagamento.java`
  - Classe que utiliza a estratégia para calcular o valor líquido

### Módulo de Médicos:
- `aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoConversaoStrategy.java`
  - Interface que define o contrato para estratégias de conversão de Médico em diferentes DTOs
  
- `aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoConversaoComConsultasStrategy.java`
  - Estratégia concreta que converte Médico incluindo informações de consultas
  
- `aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoServicoAplicacao.java`
  - Serviço que utiliza as estratégias de conversão

**Como está sendo usado:**
O padrão Strategy é usado para encapsular diferentes algoritmos de cálculo (folha de pagamento) e diferentes formas de conversão de dados (médicos), permitindo que o algoritmo seja selecionado em tempo de execução sem modificar o código cliente.

---

## 5. Template Method

**Descrição:** O padrão Template Method define o esqueleto de um algoritmo em uma operação, delegando alguns passos para subclasses. Em outras palavras, este padrão permite que subclasses redefinam certos passos de um algoritmo sem alterar sua estrutura geral, promovendo reuso de código e garantindo consistência na execução de operações similares.

**Classes Criadas/Modificadas:**

- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/referencia/RepositorioJpaTemplate.java`
  - Classe abstrata que define o template method para operações de repositório JPA
  - Métodos template: `buscarPorId()` e `buscarTodos()` que definem o algoritmo de busca e conversão
  - Métodos primitivos abstratos: `buscarEntidadeJpa()` e `buscarTodasEntidades()` implementados pelas subclasses

- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/referencia/TipoExameRepositorioJpaImpl.java`
  - Subclasse que estende `RepositorioJpaTemplate` e implementa os métodos primitivos para TipoExame
  - Usa conversão automática via ModelMapper nos métodos template herdados

- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/PacienteRepositorioJpaAdapter.java`
  - Subclasse que estende `RepositorioJpaTemplate` e implementa os métodos primitivos para Paciente
  - **Abordagem híbrida:** Implementa os métodos abstratos (mantendo a estrutura do padrão), mas usa mapeamento manual (`PacienteJpaMapeador`) nas operações finais devido à ausência de construtor vazio na entidade de domínio

**Como está sendo usado:**

O padrão Template Method é usado para padronizar o algoritmo de busca e conversão de entidades JPA para entidades de domínio. O template define os passos fixos (conversão usando ModelMapper), enquanto as subclasses implementam apenas os passos variáveis (busca das entidades JPA específicas).

**Benefícios:**

- **Reuso de código:** O algoritmo de conversão é definido uma única vez na classe abstrata
- **Consistência:** Todas as implementações seguem o mesmo padrão de busca e conversão
- **Flexibilidade:** Subclasses podem adaptar o comportamento quando necessário (como demonstrado em Pacientes)
- **Manutenibilidade:** Alterações no algoritmo de conversão afetam todas as implementações automaticamente
- **Alinhamento com SOLID:** Open-Closed (aberto para extensão via herança, fechado para modificação), Single Responsibility (classe abstrata cuida da conversão, subclasses cuidam da busca específica)

---

## 6. Adapter

**Descrição:** O padrão Adapter converte a interface de uma classe em outra interface esperada pelo cliente. Adapter permite que classes com interfaces incompatíveis trabalhem juntas.

**Classes Criadas/Modificadas:**

- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/PacienteRepositorioJpaAdapter.java`
  - Adapter que adapta a interface do repositório JPA (`PacienteJpaRepository`) para a interface de domínio (`PacienteRepositorio`)
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/atendimento/ConsultaRepositorioImpl.java`
  - Adapter que implementa tanto a porta de Escrita (Domain Repository) quanto a porta de Leitura (Application Repository)
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/MedicamentoRepositorioImpl.java`
  - Adapter que adapta a interface JPA para as interfaces de domínio e aplicação
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/FuncionarioRepositorioAplicacaoImpl.java`
  - Adapter que implementa a porta de leitura (Application Repository) para Funcionários
  
- `infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/financeiro/convenio/ConvenioRepositorioAplicacaoImpl.java`
  - Adapter que adapta entidades JPA para DTOs de aplicação

**Como está sendo usado:**
O padrão Adapter é usado extensivamente na camada de infraestrutura para adaptar interfaces de frameworks externos (como Spring Data JPA) para as interfaces definidas nas camadas de domínio e aplicação, seguindo os princípios da Clean Architecture.

---
