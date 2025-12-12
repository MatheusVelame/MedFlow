#!/bin/bash
# Script para executar a aplicação com Java 17

# Configura JAVA_HOME para Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Verifica se o Java 17 foi encontrado
if [ -z "$JAVA_HOME" ]; then
    echo "❌ Erro: Java 17 não encontrado!"
    echo "Instale o Java 17 com: brew install openjdk@17"
    exit 1
fi

echo "✅ Usando Java 17: $JAVA_HOME"
echo ""

# Executa a aplicação
mvn spring-boot:run -pl apresentacao-backend
