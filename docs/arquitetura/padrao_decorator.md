# Padrão Decorator na Implementação de Prontuário e Faturamento

## Introdução

Este documento explica como o **Padrão Decorator** foi aplicado na implementação dos módulos de Prontuário e Faturamento do sistema MedFlow, seguindo os princípios de Clean Architecture e DDD (Domain-Driven Design).

## O que é o Padrão Decorator?

O Padrão Decorator é um padrão de projeto estrutural que permite adicionar novos comportamentos a objetos dinamicamente, envolvendo-os em objetos decoradores. Isso oferece uma alternativa flexível à herança para estender funcionalidades.

### Princípios do Decorator

1. **Composição sobre Herança**: Usa composição para adicionar funcionalidades
2. **Abertura/Fechamento**: Aberto para extensão, fechado para modificação
3. **Responsabilidade Única**: Cada decorator adiciona uma responsabilidade específica
4. **Encadeamento**: Decorators podem ser combinados em cascata

## Aplicação no Projeto MedFlow

### 1. Decorator em Repositórios

#### 1.1. Estrutura Base

Primeiro, criamos a **implementação base** que contém a lógica essencial de persistência:

```java
// ProntuarioRepositorioBase.java
public class ProntuarioRepositorioBase implements ProntuarioRepositorio {
    protected final ProntuarioJpaRepository jpaRepository;
    protected final HistoricoClinicoJpaRepository historicoClinicoJpaRepository;
    
    // Implementação base sem funcionalidades extras
    @Override
    public void salvar(Prontuario prontuario) {
        ProntuarioJpa jpa = toJpa(prontuario);
        jpaRepository.save(jpa);
        // ... lógica de persistência
    }
}
```

#### 1.2. Decorator Abstrato

O decorator abstrato define a estrutura comum para todos os decorators:

```java
// ProntuarioRepositorioDecorator.java
public abstract class ProntuarioRepositorioDecorator implements ProntuarioRepositorio {
    protected final ProntuarioRepositorio repositorio;

    public ProntuarioRepositorioDecorator(ProntuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void salvar(Prontuario prontuario) {
        repositorio.salvar(prontuario); // Delegação
    }
    
    // Outros métodos delegam para o repositório decorado
}
```

#### 1.3. Decorators Concretos

**Decorator de Logging (Prontuário):**

```java
// ProntuarioRepositorioLoggingDecorator.java
public class ProntuarioRepositorioLoggingDecorator 
    extends ProntuarioRepositorioDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(...);

    public ProntuarioRepositorioLoggingDecorator(ProntuarioRepositorioBase repositorio) {
        super(repositorio);
    }

    @Override
    public void salvar(Prontuario prontuario) {
        logger.info("Salvando prontuário: id={}, pacienteId={}", 
                   prontuario.getId(), prontuario.getPacienteId());
        try {
            repositorio.salvar(prontuario);
            logger.info("Prontuário salvo com sucesso: id={}", prontuario.getId());
        } catch (Exception e) {
            logger.error("Erro ao salvar prontuário: id={}", 
                        prontuario.getId(), e);
            throw e;
        }
    }
}
```

**Decorator de Auditoria (Faturamento):**

```java
// FaturamentoRepositorioAuditoriaDecorator.java
public class FaturamentoRepositorioAuditoriaDecorator 
    extends FaturamentoRepositorioDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(...);

    @Override
    public void salvar(Faturamento faturamento) {
        logger.info("AUDITORIA: Salvando faturamento - id={}, pacienteId={}, " +
                   "status={}, valor={}", 
                   faturamento.getId() != null ? faturamento.getId().getValor() : "NOVO",
                   faturamento.getPacienteId().getValor(),
                   faturamento.getStatus(),
                   faturamento.getValor().getValor());
        try {
            repositorio.salvar(faturamento);
            logger.info("AUDITORIA: Faturamento salvo com sucesso");
        } catch (Exception e) {
            logger.error("AUDITORIA: Erro ao salvar faturamento", e);
            throw e;
        }
    }
}
```

#### 1.4. Implementação Final (Composição)

A implementação final compõe os decorators em cascata:

```java
// ProntuarioRepositorioImpl.java
@Component
public class ProntuarioRepositorioImpl 
    implements ProntuarioRepositorio, ProntuarioRepositorioAplicacao {

    private final ProntuarioRepositorio repositorioDecorado;

    public ProntuarioRepositorioImpl(ProntuarioRepositorioBase repositorioBase) {
        // Aplica decorators em cascata: base -> logging
        this.repositorioDecorado = 
            new ProntuarioRepositorioLoggingDecorator(repositorioBase);
    }

    @Override
    public void salvar(Prontuario prontuario) {
        repositorioDecorado.salvar(prontuario);
    }
}
```

**Diagrama de Composição:**

```
ProntuarioRepositorioImpl
    └── ProntuarioRepositorioLoggingDecorator
            └── ProntuarioRepositorioBase
```

### 2. Decorator em Mapeadores

#### 2.1. Estrutura Base do Mapeador

```java
// ProntuarioMapeadorBase.java
public class ProntuarioMapeadorBase {
    
    public interface ProntuarioMapeador {
        ProntuarioJpa toJpa(Prontuario prontuario);
        Prontuario toDomain(ProntuarioJpa jpa);
        // ... outros métodos de mapeamento
    }

    public static class Impl implements ProntuarioMapeador {
        @Override
        public ProntuarioJpa toJpa(Prontuario prontuario) {
            // Mapeamento básico sem validações extras
            ProntuarioJpa jpa = new ProntuarioJpa();
            jpa.setId(prontuario.getId());
            jpa.setPacienteId(prontuario.getPacienteId());
            // ...
            return jpa;
        }
    }
}
```

#### 2.2. Decorator Abstrato do Mapeador

```java
// ProntuarioMapeadorDecorator.java
public abstract class ProntuarioMapeadorDecorator 
    implements ProntuarioMapeadorBase.ProntuarioMapeador {
    
    protected final ProntuarioMapeadorBase.ProntuarioMapeador mapeador;

    public ProntuarioMapeadorDecorator(
            ProntuarioMapeadorBase.ProntuarioMapeador mapeador) {
        this.mapeador = mapeador;
    }

    @Override
    public ProntuarioJpa toJpa(Prontuario prontuario) {
        return mapeador.toJpa(prontuario); // Delegação
    }
}
```

#### 2.3. Decorator de Validação

```java
// ProntuarioMapeadorValidacaoDecorator.java
public class ProntuarioMapeadorValidacaoDecorator 
    extends ProntuarioMapeadorDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(...);

    public ProntuarioMapeadorValidacaoDecorator(
            ProntuarioMapeadorBase.Impl mapeadorBase) {
        super(mapeadorBase);
    }

    @Override
    public ProntuarioJpa toJpa(Prontuario prontuario) {
        // Validação antes do mapeamento
        validarProntuario(prontuario);
        
        // Executa o mapeamento base
        ProntuarioJpa jpa = mapeador.toJpa(prontuario);
        
        // Validação após o mapeamento
        validarProntuarioJpa(jpa);
        
        return jpa;
    }

    private void validarProntuario(Prontuario prontuario) {
        if (prontuario == null) {
            throw new IllegalArgumentException("Prontuário não pode ser nulo");
        }
        // ... outras validações
    }

    private void validarProntuarioJpa(ProntuarioJpa jpa) {
        if (jpa.getPacienteId() == null || jpa.getPacienteId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
        // ... outras validações
    }
}
```

## Fluxo de Execução

### Exemplo: Salvar Prontuário com Logging

```
1. Controller recebe requisição
   ↓
2. UseCase chama ProntuarioRepositorioImpl.salvar()
   ↓
3. ProntuarioRepositorioImpl delega para ProntuarioRepositorioLoggingDecorator
   ↓
4. LoggingDecorator registra log de entrada
   ↓
5. LoggingDecorator delega para ProntuarioRepositorioBase
   ↓
6. Base executa a persistência real
   ↓
7. LoggingDecorator registra log de sucesso/erro
   ↓
8. Retorno ao UseCase
```

### Exemplo: Mapear Prontuário com Validação

```
1. Repositório precisa mapear Prontuario -> ProntuarioJpa
   ↓
2. Chama ProntuarioMapeadorValidacaoDecorator.toJpa()
   ↓
3. ValidacaoDecorator valida o objeto de domínio
   ↓
4. ValidacaoDecorator delega para ProntuarioMapeadorBase.Impl
   ↓
5. Base executa o mapeamento
   ↓
6. ValidacaoDecorator valida o objeto JPA resultante
   ↓
7. Retorna ProntuarioJpa validado
```

## Benefícios da Aplicação do Decorator

### 1. **Separação de Responsabilidades**

Cada decorator tem uma responsabilidade única:
- `LoggingDecorator`: Apenas logging
- `AuditoriaDecorator`: Apenas auditoria
- `ValidacaoDecorator`: Apenas validação

### 2. **Flexibilidade e Extensibilidade**

Novos comportamentos podem ser adicionados sem modificar código existente:

```java
// Exemplo: Adicionar cache sem modificar código existente
public class ProntuarioRepositorioCacheDecorator 
    extends ProntuarioRepositorioDecorator {
    
    private final Cache cache;
    
    @Override
    public Optional<Prontuario> obterPorId(String id) {
        // Verifica cache primeiro
        Prontuario cached = cache.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // Se não estiver em cache, busca e armazena
        Optional<Prontuario> prontuario = repositorio.obterPorId(id);
        prontuario.ifPresent(p -> cache.put(id, p));
        return prontuario;
    }
}
```

### 3. **Composição Dinâmica**

Decorators podem ser combinados em diferentes ordens conforme necessário:

```java
// Ordem 1: Base -> Logging -> Cache
new ProntuarioRepositorioCacheDecorator(
    new ProntuarioRepositorioLoggingDecorator(
        new ProntuarioRepositorioBase(...)
    )
)

// Ordem 2: Base -> Cache -> Logging
new ProntuarioRepositorioLoggingDecorator(
    new ProntuarioRepositorioCacheDecorator(
        new ProntuarioRepositorioBase(...)
    )
)
```

### 4. **Testabilidade**

Cada decorator pode ser testado isoladamente:

```java
@Test
void testLoggingDecorator() {
    ProntuarioRepositorio base = mock(ProntuarioRepositorioBase.class);
    ProntuarioRepositorioLoggingDecorator decorator = 
        new ProntuarioRepositorioLoggingDecorator(base);
    
    Prontuario prontuario = criarProntuarioTeste();
    decorator.salvar(prontuario);
    
    verify(base).salvar(prontuario);
    // Verificar se logs foram gerados
}
```

### 5. **Conformidade com SOLID**

- **S**ingle Responsibility: Cada decorator tem uma responsabilidade
- **O**pen/Closed: Aberto para extensão, fechado para modificação
- **L**iskov Substitution: Decorators podem substituir a interface base
- **I**nterface Segregation: Interfaces específicas para cada contexto
- **D**ependency Inversion: Depende de abstrações, não de implementações

## Comparação: Com vs Sem Decorator

### Sem Decorator (Abordagem Tradicional)

```java
@Component
public class ProntuarioRepositorioImpl implements ProntuarioRepositorio {
    
    @Override
    public void salvar(Prontuario prontuario) {
        // Lógica de persistência
        // Lógica de logging (misturada)
        // Lógica de validação (misturada)
        // Lógica de cache (misturada)
        // Difícil de testar e manter
    }
}
```

**Problemas:**
- ❌ Violação do Single Responsibility Principle
- ❌ Difícil de testar funcionalidades isoladas
- ❌ Modificações requerem alterar código existente
- ❌ Acoplamento forte entre responsabilidades

### Com Decorator

```java
@Component
public class ProntuarioRepositorioImpl implements ProntuarioRepositorio {
    
    private final ProntuarioRepositorio repositorioDecorado;

    public ProntuarioRepositorioImpl(ProntuarioRepositorioBase base) {
        // Composição clara e flexível
        this.repositorioDecorado = 
            new ProntuarioRepositorioLoggingDecorator(base);
    }
}
```

**Vantagens:**
- ✅ Cada decorator tem responsabilidade única
- ✅ Fácil de testar isoladamente
- ✅ Extensível sem modificar código existente
- ✅ Baixo acoplamento entre responsabilidades

## Estrutura de Arquivos

```
infraestrutura/src/main/java/.../prontuario/
├── ProntuarioRepositorioBase.java          # Implementação base
├── ProntuarioRepositorioDecorator.java     # Decorator abstrato
├── ProntuarioRepositorioLoggingDecorator.java  # Decorator concreto (logging)
└── ProntuarioRepositorioImpl.java          # Composição final

infraestrutura/src/main/java/.../prontuario/
├── ProntuarioMapeadorBase.java             # Mapeador base
├── ProntuarioMapeadorDecorator.java        # Decorator abstrato
└── ProntuarioMapeadorValidacaoDecorator.java  # Decorator concreto (validação)

infraestrutura/src/main/java/.../financeiro/
├── FaturamentoRepositorioBase.java
├── FaturamentoRepositorioDecorator.java
├── FaturamentoRepositorioAuditoriaDecorator.java  # Decorator concreto (auditoria)
└── FaturamentoRepositorioImpl.java
```

## Exemplo Completo: Fluxo de Salvamento

```java
// 1. Controller recebe requisição
@PostMapping("/{id}/historico")
public ResponseEntity<Void> adicionarHistorico(
    @PathVariable String id,
    @RequestBody AdicionarHistoricoRequest request
) {
    adicionarHistoricoUseCase.executar(id, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}

// 2. UseCase executa lógica de negócio
public void executar(String prontuarioId, AdicionarHistoricoRequest request) {
    Prontuario prontuario = repositorio.obterPorId(prontuarioId)
        .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado"));
    
    HistoricoClinico novoHistorico = new HistoricoClinico(...);
    prontuario.adicionarHistoricoClinico(novoHistorico);
    
    // 3. Chama repositório (que usa decorators)
    repositorio.salvar(prontuario);
}

// 4. Repositório com decorator
public void salvar(Prontuario prontuario) {
    // LoggingDecorator.salvar() é chamado
    //   → Log de entrada
    //   → Base.salvar() é chamado
    //   → Persistência real
    //   → Log de sucesso
    repositorioDecorado.salvar(prontuario);
}
```

## Extensões Futuras

O padrão permite adicionar facilmente novos decorators:

### Exemplo: Decorator de Cache

```java
public class ProntuarioRepositorioCacheDecorator 
    extends ProntuarioRepositorioDecorator {
    
    private final Cache<String, Prontuario> cache;

    @Override
    public Optional<Prontuario> obterPorId(String id) {
        Prontuario cached = cache.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        Optional<Prontuario> prontuario = repositorio.obterPorId(id);
        prontuario.ifPresent(p -> cache.put(id, p));
        return prontuario;
    }
}
```

### Exemplo: Decorator de Métricas

```java
public class ProntuarioRepositorioMetricasDecorator 
    extends ProntuarioRepositorioDecorator {
    
    private final MeterRegistry meterRegistry;

    @Override
    public void salvar(Prontuario prontuario) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            repositorio.salvar(prontuario);
            sample.stop(Timer.builder("prontuario.save")
                .tag("status", "success")
                .register(meterRegistry));
        } catch (Exception e) {
            sample.stop(Timer.builder("prontuario.save")
                .tag("status", "error")
                .register(meterRegistry));
            throw e;
        }
    }
}
```

## Configuração no Spring

Os decorators são configurados na classe principal:

```java
@SpringBootApplication
public class BackendAplicacao {
    
    @Bean
    public ProntuarioRepositorioBase prontuarioRepositorioBase(
            ProntuarioJpaRepository jpaRepository,
            HistoricoClinicoJpaRepository historicoClinicoJpaRepository,
            HistoricoAtualizacaoJpaRepository historicoAtualizacaoJpaRepository) {
        return new ProntuarioRepositorioBase(
            jpaRepository, 
            historicoClinicoJpaRepository, 
            historicoAtualizacaoJpaRepository
        );
    }
    
    // O ProntuarioRepositorioImpl já compõe os decorators
    // Não precisa de bean adicional
}
```

## Conclusão

O padrão Decorator foi aplicado com sucesso nos módulos de Prontuário e Faturamento, proporcionando:

1. **Modularidade**: Cada funcionalidade (logging, auditoria, validação) está isolada
2. **Manutenibilidade**: Fácil de modificar ou estender comportamentos
3. **Testabilidade**: Cada decorator pode ser testado independentemente
4. **Flexibilidade**: Novos decorators podem ser adicionados sem impacto no código existente
5. **Conformidade com SOLID**: Respeita os princípios de design orientado a objetos

Esta implementação demonstra como padrões de projeto bem aplicados podem melhorar significativamente a qualidade e manutenibilidade do código em uma arquitetura limpa.
