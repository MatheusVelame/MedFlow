# Configuração Java 17 - MedFlow

## ✅ Configurações Aplicadas

O projeto foi configurado para usar **Java 17** em todas as operações.

### Mudanças Realizadas:

1. **`pai/pom.xml`**: Adicionadas propriedades para forçar Java 17
   - `java.version=17`
   - `maven.compiler.source=17`
   - `maven.compiler.target=17`

2. **`apresentacao-backend/pom.xml`**: Configurado plugin Spring Boot
   - Adicionado `-Djava.awt.headless=true` para evitar problemas com JavaFX

3. **`.mvn/jvm.config`**: Criado arquivo de configuração do Maven
   - Adicionado flag `-Djava.awt.headless=true`

4. **`run.sh`**: Script criado para facilitar execução
   - Configura automaticamente JAVA_HOME para Java 17
   - Executa a aplicação com as configurações corretas

## 🚀 Como Usar

### Opção 1: Usar o script (Recomendado)

```bash
./run.sh
```

### Opção 2: Configurar manualmente

```bash
# Configurar JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Executar aplicação
mvn spring-boot:run -pl apresentacao-backend
```

### Opção 3: Executar JAR diretamente

```bash
# Gerar JAR
mvn package -DskipTests

# Executar
java -jar apresentacao-backend/target/medflow-apresentacao-backend-0.0.1-SNAPSHOT.jar
```

## ✅ Verificação

Para verificar se está usando Java 17:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn -version
```

Deve mostrar: `Java version: 17.0.15`

## 📝 Notas

- O problema do JavaFX foi resolvido com a flag `-Djava.awt.headless=true`
- Todas as compilações agora usam Java 17 por padrão
- O script `run.sh` facilita a execução sem precisar configurar manualmente
