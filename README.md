# MedFlow - Sistema de Gestão de Clínicas Médicas

MedFlow é um sistema de gestão de clínicas médicas, incluindo funcionalidades de agendamento de consultas, gerenciamento de exames, prontuários eletrônicos, controle financeiro e gerenciamento de colaboradores.

## Funcionalidades Principais

- Gerenciamento de pacientes, médicos e funcionários
- Agendamento, remarcação e cancelamento de consultas
- Gerenciamento de exames e tipos de exames
- Registro e consulta de prontuários eletrônicos
- Controle financeiro: faturamento, pagamentos de funcionários
- Gerenciamento de convênios e planos de saúde
- Registro de medicamentos e prescrições

## Estrutura do Projeto

```
MedFlow/
├── README.md
├── docs/                 # Documentação detalhada do sistema
│   ├── dominio/
│   │   ├── glossario.md
│   │   ├── contexto_delimitado.md
│   │   ├── fluxos_processos_chave.md
│   │   ├── modelos_dominio.md
│   ├── requisitos/       # Funcionalidades, RN, regras específicas
│   │   ├── funcionalidades.md
│   │   ├── regras_negocio.md
│   │   └── RN_exames.md
│   ├── diagramas/        # diagramas visuais, PlantUML, imagens
│   │   ├── ERD.puml
│   │   ├── ERD.png
│   │   └── outros_diagramas.png
├── src/                  # Código-fonte do sistema
│   ├── main/
│   ├── tests/
│   └── ...
├── .gitignore
└── LICENSE
```

## Documentação

Toda a documentação do sistema está na pasta `docs/`:

- `docs/dominio/` → Glossário, Contextos Delimitados, ERD, Fluxos e Processos Chave
- `docs/requisitos/` → Funcionalidades detalhadas e Regras de Negócio
- `docs/diagramas/` → Diagramas ERD e outros diagramas visuais

## Como Contribuir

1. Faça fork do repositório
2. Crie uma branch com sua feature (`git checkout -b feature/nome-da-feature`)
3. Commit suas alterações (`git commit -m 'Descrição da feature'`)
4. Push para a branch (`git push origin feature/nome-da-feature`)
5. Abra um Pull Request

## Licença

[MIT](LICENSE)

---
