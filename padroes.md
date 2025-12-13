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

**FUNCIONALIDADES — Exames e Especialidades**
*Domínios - Referência e Atendimento*

O padrão de projeto Proxy é aplicado no backend do sistema para as funcionalidades de Exames e de Especialidades. Abaixo, descreve-se onde proxies são instanciados, qual é o contrato (interfaces), responsabilidades do Proxy e do RealSubject, vantagens, cuidados (edge cases) e sugestões de melhoria e testes.

Em síntese, o padrão Proxy é usado de duas formas:

1. **Proxy Estático:** Para adicionar auditoria e logging aos serviços de domínio (Especialidades e Exames) sem modificar a implementação real.
2. **Proxy Dinâmico:** Para criar DTOs dinâmicos que mapeiam automaticamente chamadas de métodos para propriedades de entidades JPA, evitando a necessidade de criar classes DTO explícitas.

Arquivos relevantes (localizações no repositório)

**EXAMES - Domínio `atendimento`**

- Interface (contrato): `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/IExameServico.java`
- RealSubject (implementação): `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/ExameServicoImpl.java`
- Proxy concreto: `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/exames/ExameServicoProxy.java`
- Registro do bean (onde o proxy é criado e exposto): `apresentacao-backend/src/main/java/br/com/medflow/apresentacao/config/AtendimentoConfig.java`

**ESPECIALIDADES - domínio `referencia`**

- Interface (contrato): `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/IEspecialidadeServico.java`
- RealSubject (implementação): `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/EspecialidadeServicoImpl.java` (implementação real — usada nos testes/instanciação)
- Proxy concreto: `dominio-referencia/src/main/java/br/com/medflow/dominio/referencia/especialidades/EspecialidadeServicoProxy.java`
- Registro do bean: `apresentacao-backend/src/main/java/br/com/medflow/apresentacao/config/ReferenciaConfig.java`
- Exemplo de testes que usam o Proxy: `dominio-referencia/src/test/.../EspecialidadesFuncionalidadeBase.java`

**Contexto e objetivo do Proxy no projeto**

O padrão Proxy é usado aqui como um "wrapper" em torno dos serviços de domínio (Exames e Especialidades) para adicionar comportamento transversais (cross-cutting concerns), principalmente:

- Auditoria / logging (prints no console atualmente)
- Ponto único para eventualmente checar permissões, métricas ou validação antes de delegar ao serviço real

Arquiteturalmente:

- Tanto o Proxy quanto o RealSubject implementam a mesma interface (ex.: `IExameServico` e `IEspecialidadeServico`), permitindo que o bean exposto para a aplicação seja o proxy.
- O proxy é construído programaticamente nas classes de configuração Spring (`AtendimentoConfig` e `ReferenciaConfig`) e devolvido como bean. Assim, o restante da aplicação só vê a interface.

Como o Proxy é instanciado (ex.: Exames)

No arquivo `AtendimentoConfig.java`:

1. É criada a implementação real: Exemplo: `ExameServicoImpl servicoReal = new ExameServicoImpl(repositorio, verificadorExterno, eventoBarramento);`
2. O bean retornado pelo método é uma nova instância de `ExameServicoProxy` que envolve `servicoReal`: Exemplo: `return new ExameServicoProxy(servicoReal);`

**Fluxo de execução (sequência típica)**

1. Chamador (controller, outro serviço, testes) injeta a interface `IExameServico`.
2. Ao chamar, por exemplo, `agendarExame(...)`, a instância real será um `ExameServicoProxy`.
3. `ExameServicoProxy.agendarExame(...)` executa lógica de proxy (no código atual, chama log(...)) — ponto onde se pode inserir checagens adicionais.
4. Proxy delega a chamada para `servicoReal.agendarExame(...)` (o `ExameServicoImpl`), que contém toda a lógica de domínio (RNs — regras de negócio), persistência via repositório e publicação de eventos.
5. Resultado (ou exceção) é retornado ao chamador; o proxy poderia também aplicar lógica após a chamada (post-processing, transações, métricas).

**Contrato (inputs, outputs, erros)**

- `IExameServico`:
- Métodos principais: `agendarExame`, `atualizarAgendamento`, `tentarExcluirAgendamento`, `cancelarAgendamento`
- Inputs: IDs (paciente, médico), tipo de exame (`String`), datas (`LocalDateTime`), `UsuarioResponsavelId`.
- Outputs: Exame salvo ou void.
- Erros esperados (lançados pelo `RealSubject`): `ExcecaoDominio` quando regras de negócio são violadas (datas passadas, médico inativo, conflito de horário etc.).
- `IEspecialidadeServico`:
- Comandos de domínio (cadastrar, alterar, excluir e variações), consultas (listar, `buscarPorNome/id`).
- Outputs: `Especialidade`, `List<Especialidade>` ou void.
- Erros esperados: `RegraNegocioException` / ExcecaoDominio conforme regras do domínio (por exemplo, não permitir exclusão quando houver vínculo).

**Responsabilidades: Proxy vs `RealSubject`**

- Proxy:
  - Interceptar chamadas e adicionar comportamento transversal:
    - Logging / auditoria (no código atual: `System.out.println("[AUDITORIA - PROXY] ...")`)
    - Local para adicionar checagens de autorização antes de delegar
    - Ponto para métricas (contadores, latências)
    - Eventualmente, tratamento de retries, circuit-breakers, caching superficial
  - Não deve conter a lógica de negócio (regras do domínio). Esta fica no RealSubject.
- **RealSubject** (`ExameServicoImpl` / `EspecialidadeServicoImpl`):
  - Implementa as regras de negócio (RNs) e interage com repositórios, serviços externos e barramento de eventos.
  - Lança exceções de domínio quando regras são violadas.
  - Responsável por persistir estado e emitir eventos de domínio.

**Vantagens desta abordagem**

- Separação de preocupações: lógica transversal desacoplada do core de domínio.
- Fácil de colocar / remover comportamentos sem alterar a implementação do domínio.
- Interface única visível para consumidores (inversão de controle via Spring DI).
- Simplicidade: implementação do Proxy no código atual é direta e explícita, fácil de entender.

Riscos e edge-cases (pontos de atenção)

1. Transações e boundaries:
2. Se a implementação do domínio usa transações declarativas (ex.: @Transactional em beans Spring), criar o Proxy manual pode interferir no comportamento das proxies do Spring (especialmente quando se usa proxies CGLIB/JDK).
3. Recomenda-se garantir que o RealSubject (ou métodos que precisam de transação) estejam anotados/expandidos de modo que o container gerencie a transação corretamente. Testar se o bean retornado (proxy personalizado) ainda respeita a configuração transacional desejada.
4. Tratamento de exceções:
5. Se o Proxy lê ou transforma exceções, garanta que exceções de domínio não sejam silenciadas. Atualmente o Proxy apenas faz log e delega.
6. Thread-safety / estado:
7. O Proxy atual não mantém estado mutável, então é seguro como singleton. Evitar adicionar campos mutáveis ao Proxy sem sincronização.
8. Performance:
9. Logging síncrono no console pode ser custo; para produção, preferir um logger assíncrono/estruturado (SLF4J + Logback/Log4J2) e/ ou um coletor de métricas.
10. Contract drift:
11. Se a interface (IExameServico / IEspecialidadeServico) mudar, o Proxy precisa ser atualizado para expor todos os métodos. Ou usar técnicas (dynamic proxies) para reduzir repetição.
12. Serialização / proxies dinâmicos:
13. Se o proxy precisar ser serializável ou passível de proxying via reflection para frameworks, considerar suporte adicional.

**Testes e validação**

- Testes unitários:
- Mockar o `servicoReal` e injetá-lo no `ServicoProxy`, verificar que:
  - O proxy chama o método de log (poder verificar chamando/espionando o logger ou verificando que o método do `servicoReal` foi chamado).
  - Em casos de exceção lançada pelo `servicoReal`, o proxy propaga a exceção.
- Exemplos de foco:
  - Happy path: proxy delega e retorna o mesmo resultado.
  - Exceção: proxy não mascara exceção do `RealSubject`.
  - Checagem de autorização (se implementada): quando negada, não deve delegar.
- Testes de integração:
- Testar o bean exposto na configuração Spring (`AtendimentoConfig` e `ReferenciaConfig`) para garantir que o proxy é o bean injetado e que todo o fluxo (proxy -> real -> repositório) funciona.
- Verificar interação com o barramento de eventos (no caso de Exame: publicação de `ExameAgendadoEvent`).

**Sugestões de implementação / melhorias**

1. Centralizar logging/auditoria:
2. Substituir `System.out.println` por logger (`org.slf4j.Logger`) com níveis e contexto estruturado (ex.: MDC com responsavel).
3. Registrar identificadores importantes (ID do exame, paciente, usuário que chamou) para rastreabilidade.
4. Usar Spring AOP (ou proxies dinâmicos) para reduzir código boilerplate:
5. Em vez de escrever um proxy por serviço, aplicar um advice que intercepta chamadas de métodos anotados (ex.: `@Auditavel`) e faz logging/metrics. Isso evita duplicação e garante aplicação uniforme.
6. Vantagens: menos código a manter; integrado ao container, respeitando aspectos como transação.
7. Métricas e monitoramento:
8. Adicionar contadores/latência (`Micrometer/Prometheus`) no proxy/advice para medir uso e latência de operações críticas (agendamento, cancelamento).
9. Authorization / Permission checks:
10. Se autorizado no escopo, adicionar verificação de permissões no Proxy (ou via AOP) antes de delegar ao `RealSubject`. Preferir extrair essa responsabilidade para componente dedicado de autorização se for complexo.
11. Tratamento de falhas externas:
12. Para chamadas que dependem de serviços externos (`verificadorExterno`), considerar padrões como circuit-breaker (`Resilience4j`) no nível do `RealSubject` ou em um decorator específico.
13. Evitar duplicação:
14. Se vários serviços precisam de comportamento semelhante, implementar um decorator genérico (ou usar AOP) para aplicar auditoria/métricas a múltiplos serviços.

*Exemplo de transformação (opção leve)*

- Se quiser manter controle manual, mas reduzir repetição:
- Criar uma classe base abstrata `AbstractServicoProxy<TInterface>` com utilitários de log/metrics e estender nos proxies concretos.
- Ou: migrar para um advice AOP:
- Criar um `@Aspect` que aplica around-advice em beans que implementam `IExameServico` e `IEspecialidadeServico` ou que são anotados com uma anotação específica.

**Checklist de qualidade / testes a executar**

- [x] Verificar que o bean que é injetado é realmente o proxy (tests de integração ou debug).
- [x] Cobrir unidade: proxy delega e propaga exceções.
- [x] Cobrir integração: fluxo de agendamento publica evento (Exame) após persistência.
- [ ] Confirmar comportamento transacional ao introduzir mudanças no proxy (se for necessário `@Transactional`, testar o cenário de rollback).

**Exemplos de verificações rápidas (conceitual)**

- Unit:
- Criar `ExameServicoProxy` com um `servicoReal` mock e assertar que `servicoReal.agendarExame(...)` foi chamado.
- Integration:
- Iniciar contexto Spring que configura `exameServico(...)` em `AtendimentoConfig` e verificar que o bean obtido por `context.getBean(IExameServico.class)` é instância de `ExameServicoProxy` e que o fluxo completo salva e publica evento.

**Observações finais e recomendações práticas**

- A implementação atual é clara e funcional: proxies explícitos dão visibilidade e controle.
- Para produção, trocar prints por logs estruturados e avaliar migração para AOP quando o comportamento transversal se multiplicar.
- Verificar interações com transações e proxies do Spring (testes de integração) antes de adicionar lógica dependente de transação no Proxy.
- Documentar se a intenção do Proxy é somente auditoria (simples) ou se servirá também para autorização, métricas, caching, retries etc. A escolha afeta a implementação (proxy manual vs AOP vs dynamic proxy vs middleware).

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

## 6. Iterator (Matheus Velame)

**Descrição:** O padrão Iterator (ou Iterador) tem o propósito de "Prover uma maneira de acessar os elementos de um objeto agregado sequencialmente sem expor sua representação subjacente". No MedFlow, este padrão é fundamental para percorrer coleções internas de entidades de domínio (o Histórico), garantindo que o código cliente não dependa da estrutura interna (como uma List ou Map) do objeto agregado.

**Classes Criadas/Modificadas:**

***Módulo de Atendimento (Consultas):***

- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/IteradorHistorico.java`
  - Interface do Iterator (Iterador): Define o contrato para a navegação sequencial, com temProximo() (análogo a hasNext()) e proximo() (análogo a next()).
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/ColecaoHistorico.java`
  - Interface do Aggregate (Coleção): Define o método de fábrica para criar uma instância do iterador (criarIterador()).
  
- `dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/ConsultaHistoricoIterator.java`
  - Iterator Concreto: Implementa a lógica de iteração sobre o List<HistoricoConsultaEntrada> interno da Consulta, rastreando a posição atual com o campo posicao.
 

***Módulo de Catálogo (Medicamentos):***
  
- `dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/IteradorHistorico.java`
  - Interface do Iterator (Iterador): Contrato para navegação, permitindo percorrimento sem expor a estrutura.
  
- `dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/ColecaoHistorico.java`
  - Interface do Aggregate (Coleção): Define o método criarIterador().
 
- `dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/MedicamentoHistoricoIterator.java`
  - Iterator Concreto: Implementa a lógica para iterar o histórico do Medicamento, com checagem de robustez para lançar IndexOutOfBoundsException.

**Como está sendo usado:**
O padrão Iterator é empregado nos Agregados de Domínio (Consulta e Medicamento) para gerenciar a travessia de seus históricos internos.

1. **Encapsulamento e Estado:** As classes de Iterador Concreto recebem a coleção interna (List) no construtor e gerenciam o estado de iteração através do campo posicao, garantindo que o cliente não precise manipular a coleção diretamente.
2. **Lógica de Navegação:** Os métodos de interface (temProximo(), proximo()) encapsulam a lógica de avanço (posicao++), abstraindo a complexidade de percorrimento do código que utiliza o iterador.
3. **Princípio da Responsabilidade Única (SOLID):** A responsabilidade de armazenar os dados pertence à entidade Agregada (ex: Consulta), e a responsabilidade de percorrer os dados pertence à classe do Iterador Concreto (ex: ConsultaHistoricoIterator), promovendo baixo acoplamento.

**Benefícios:**
- **Baixo Acoplamento:** O cliente interage com a interface IteradorHistorico, sendo imune a mudanças na estrutura interna da coleção subjacente (ex: trocar List por outra estrutura de dados), fortalecendo o limite do Agregado.
- **Transparência:** O cliente obtém os dados do histórico sequencialmente sem saber como eles são armazenados.
- **Reuso:** O mesmo padrão de iteração (sequencial) é aplicado em diferentes agregados de domínio (Consulta e Medicamento) que precisam expor seus históricos de forma segura e padronizada.

---
