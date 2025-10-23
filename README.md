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

## Links Relevantes

[Apresentação](https://www.canva.com/design/DAG2SOGRfNA/2dYa-JtEtf6IzFEaxkcMcg/edit?utm_content=DAG2SOGRfNA&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

[Mapa de História do Usuário](https://docs.google.com/spreadsheets/d/1kf9p6PZdcArcFrWaBAwNw4lFPddTfPL4PkI4kUyHw2Q/edit?usp=sharing)

[Protótipo de Alta](https://lovable.dev/projects/83b3df5b-93cc-497f-9b20-9732f14e1075)

[Divisão de Funcionalidades](https://docs.google.com/spreadsheets/d/1t92giffg4_3fWzn12EcefGPzbG3hpNor4TrYLyCI4VA/edit?gid=646963064#gid=646963064)

## Como Contribuir

1. Faça fork do repositório
2. Crie uma branch com sua feature (`git checkout -b feature/nome-da-feature`)
3. Commit suas alterações (`git commit -m 'Descrição da feature'`)
4. Push para a branch (`git push origin feature/nome-da-feature`)
5. Abra um Pull Request

## Licença

[MIT](LICENSE)

---
