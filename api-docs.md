openapi: 3.1.0
info:
  title: OpenAPI definition
  version: v0
servers:
- url: http://localhost:8080
  description: Generated server url
tags:
- name: Faturamentos
  description: API para gerenciamento de faturamentos médicos
- name: Prontuários
  description: API para gerenciamento de prontuários médicos
- name: Exames
  description: Operações para gerenciamento de agendamentos de exames
paths:
  /exames/{id}:
    get:
      tags:
      - Exames
      operationId: buscar
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int64
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ExameDetalheResponse"
    put:
      tags:
      - Exames
      summary: Atualizar agendamento de exame
      description: "Permite alterar data/hora, médico (somente se ativo) e tipo de\
        \ exame. Registra histórico de alterações."
      operationId: atualizar
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int64
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizacaoExameRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Agendamento atualizado com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ExameResponse"
    delete:
      tags:
      - Exames
      operationId: tentarExcluir
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int64
      - name: responsavelId
        in: query
        required: true
        schema:
          type: integer
          format: int64
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
  /backend/tipos-exames/{id}/inativar:
    put:
      tags:
      - tipo-exame-controlador
      operationId: inativarTipoExame
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temAgendamentosFuturos
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/medicamentos/{id}/status/{novoStatus}:
    put:
      tags:
      - medicamento-controlador
      operationId: mudarStatus
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: novoStatus
        in: path
        required: true
        schema:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - ARQUIVADO
      - name: temPrescricaoAtiva
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/medicamentos/{id}/revisao/solicitar:
    put:
      tags:
      - medicamento-controlador
      operationId: solicitarRevisao
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/RevisaoFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/medicamentos/{id}/revisao/rejeitar:
    put:
      tags:
      - medicamento-controlador
      operationId: rejeitarRevisao
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/medicamentos/{id}/revisao/aprovar:
    put:
      tags:
      - medicamento-controlador
      operationId: aprovarRevisao
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/medicamentos/{id}/arquivar:
    put:
      tags:
      - medicamento-controlador
      operationId: arquivarMedicamento
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temPrescricaoAtiva
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/funcionarios/{id}:
    get:
      tags:
      - funcionario-controlador
      operationId: obterDetalhes
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
    put:
      tags:
      - funcionario-controlador
      operationId: atualizarCompleto
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temVinculosAtivosFuncao
        in: query
        required: false
        schema:
          type: boolean
          default: false
      - name: temAtividadesFuturas
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/FuncionarioAtualizacaoCompletaFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
    delete:
      tags:
      - funcionario-controlador
      operationId: excluirFuncionario
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: possuiHistorico
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
    patch:
      tags:
      - funcionario-controlador
      operationId: atualizarDadosCadastrais
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temVinculosAtivosFuncao
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/FuncionarioAtualizacaoFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/funcionarios/{id}/status/{novoStatus}:
    put:
      tags:
      - funcionario-controlador
      operationId: mudarStatus_1
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: novoStatus
        in: path
        required: true
        schema:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
      - name: temAtividadesFuturas
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/faturamentos/{id}/pago:
    put:
      tags:
      - Faturamentos
      summary: Marcar faturamento como pago
      description: Atualiza o status de um faturamento para PAGO
      operationId: marcarComoPago
      parameters:
      - name: id
        in: path
        description: ID do faturamento
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      requestBody:
        description: Dados para marcar como pago
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/MarcarComoPagoRequest"
        required: true
      responses:
        "404":
          description: Faturamento não encontrado
          content: {}
        "400":
          description: Dados inválidos
          content: {}
        "200":
          description: Faturamento marcado como pago com sucesso
  /backend/faturamentos/{id}/cancelar:
    put:
      tags:
      - Faturamentos
      summary: Cancelar faturamento
      description: "Cancela um faturamento, atualizando seu status para CANCELADO\
        \ e registrando o motivo"
      operationId: cancelarFaturamento
      parameters:
      - name: id
        in: path
        description: ID do faturamento
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      requestBody:
        description: Dados para cancelamento
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CancelarFaturamentoRequest"
        required: true
      responses:
        "404":
          description: Faturamento não encontrado
          content: {}
        "400":
          description: Dados inválidos
          content: {}
        "200":
          description: Faturamento cancelado com sucesso
  /backend/convenios/{id}:
    get:
      tags:
      - convenio-controlador
      operationId: obterDetalhes_1
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
    put:
      tags:
      - convenio-controlador
      operationId: mudarStatus_2
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temProcedimentoAtivo
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/StatusFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/consultas/{id}/status:
    put:
      tags:
      - consulta-controlador
      operationId: mudarStatus_3
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/StatusFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /api/pacientes/{id}:
    get:
      tags:
      - paciente-controller
      operationId: buscarPorId
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/PacienteDetalhes"
    put:
      tags:
      - paciente-controller
      operationId: atualizar_1
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarPacienteRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/PacienteDetalhes"
    delete:
      tags:
      - paciente-controller
      operationId: remover
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: responsavelId
        in: query
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
  /api/medicos/{id}:
    get:
      tags:
      - medico-controller
      operationId: buscarPorId_1
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/MedicoDetalhes"
    put:
      tags:
      - medico-controller
      operationId: atualizar_2
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/MedicoAtualizacaoRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/MedicoDetalhes"
    delete:
      tags:
      - medico-controller
      operationId: remover_1
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
  /api/folhas-pagamento/{id}/valores:
    put:
      tags:
      - folha-pagamento-controlador
      operationId: atualizarValores
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarValoresFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FolhaPagamentoDetalhes"
  /api/folhas-pagamento/{id}/status:
    put:
      tags:
      - folha-pagamento-controlador
      operationId: alterarStatus
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AlterarStatusFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FolhaPagamentoDetalhes"
  /exames:
    get:
      tags:
      - Exames
      operationId: listar
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ExameResponse"
    post:
      tags:
      - Exames
      operationId: agendar
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AgendamentoExameRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ExameResponse"
  /backend/tipos-exames:
    get:
      tags:
      - tipo-exame-controlador
      operationId: pesquisarTiposExames
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/TipoExameResumo"
    post:
      tags:
      - tipo-exame-controlador
      operationId: cadastrarTipoExame
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/TipoExameFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "201":
          description: Created
  /backend/prontuarios:
    get:
      tags:
      - Prontuários
      summary: Listar todos os prontuários
      description: Retorna uma lista com todos os prontuários cadastrados no sistema
      operationId: listarProntuarios
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Lista de prontuários retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ProntuarioResumo"
    post:
      tags:
      - Prontuários
      summary: Criar novo prontuário
      description: Cria um novo prontuário independente para um paciente. Um paciente
        pode ter múltiplos prontuários para diferentes motivos/atendimentos.
      operationId: criarProntuario
      requestBody:
        description: Dados do prontuário
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CriarProntuarioRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Dados inválidos ou prontuário ativo já existe
          content: {}
        "201":
          description: Prontuário criado com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ProntuarioResponse"
  /backend/prontuarios/{id}/historico:
    get:
      tags:
      - Prontuários
      summary: Listar histórico clínico do prontuário
      description: Retorna a lista completa de registros de histórico clínico de um
        prontuário específico
      operationId: listarHistorico
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Lista de histórico clínico retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/HistoricoItemResponse"
    post:
      tags:
      - Prontuários
      summary: Adicionar histórico clínico ao prontuário
      description: "Adiciona um novo registro de histórico clínico (sintomas, diagnó\
        stico, conduta) ao prontuário especificado"
      operationId: adicionarHistorico
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      requestBody:
        description: Dados do histórico clínico
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AdicionarHistoricoRequest"
        required: true
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Dados inválidos
          content: {}
        "201":
          description: Histórico clínico adicionado com sucesso
  /backend/medicamentos:
    get:
      tags:
      - medicamento-controlador
      operationId: pesquisarMedicamentos
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicamentoResumo"
    post:
      tags:
      - medicamento-controlador
      operationId: cadastrarMedicamento
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/MedicamentoFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "201":
          description: Created
  /backend/funcionarios:
    get:
      tags:
      - funcionario-controlador
      operationId: pesquisarFuncionarios
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FuncionarioResumo"
    post:
      tags:
      - funcionario-controlador
      operationId: cadastrarFuncionario
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/FuncionarioFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "201":
          description: Created
  /backend/faturamentos:
    get:
      tags:
      - Faturamentos
      summary: Listar todos os faturamentos
      description: Retorna uma lista com todos os faturamentos (excluindo os removidos)
      operationId: listarFaturamentos
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Lista de faturamentos retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FaturamentoResumo"
    post:
      tags:
      - Faturamentos
      summary: Registrar novo faturamento
      description: "Cria um novo faturamento para um paciente. Tipos de procedimento:\
        \ CONSULTA ou EXAME"
      operationId: registrarFaturamento
      requestBody:
        description: Dados do faturamento
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/RegistrarFaturamentoRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Dados inválidos
          content: {}
        "201":
          description: Faturamento criado com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FaturamentoResumo"
  /backend/convenios:
    get:
      tags:
      - convenio-controlador
      operationId: pesquisarConvenios
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ConvenioResumo"
    post:
      tags:
      - convenio-controlador
      operationId: cadastrarConvenio
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/ConvenioFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "201":
          description: Created
  /backend/consultas:
    get:
      tags:
      - consulta-controlador
      operationId: pesquisarConsultas
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ConsultaResumo"
    post:
      tags:
      - consulta-controlador
      operationId: agendarConsulta
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AgendamentoFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "201":
          description: Created
  /api/referencia/especialidades:
    get:
      tags:
      - especialidade-controlador
      operationId: listar_1
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/EspecialidadeResumo"
    post:
      tags:
      - especialidade-controlador
      operationId: criar
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/EspecialidadeFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /api/pacientes:
    get:
      tags:
      - paciente-controller
      operationId: listarTodos
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/PacienteResumo"
    post:
      tags:
      - paciente-controller
      operationId: cadastrar
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CadastrarPacienteRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/PacienteDetalhes"
  /api/medicos:
    get:
      tags:
      - medico-controller
      operationId: listarTodos_1
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicoResumo"
    post:
      tags:
      - medico-controller
      operationId: cadastrar_1
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/MedicoCadastroRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/MedicoDetalhes"
  /api/folhas-pagamento:
    get:
      tags:
      - folha-pagamento-controlador
      operationId: listarTodas
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FolhaPagamentoResumo"
    post:
      tags:
      - folha-pagamento-controlador
      operationId: registrar
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/RegistrarFolhaFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FolhaPagamentoDetalhes"
  /exames/{id}/cancelamento:
    patch:
      tags:
      - Exames
      operationId: cancelar
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int64
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CancelamentoExameRequest"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ExameResponse"
  /backend/tipos-exames/{id}/valor:
    patch:
      tags:
      - tipo-exame-controlador
      operationId: atualizarValor
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temAgendamentos
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarValorFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/tipos-exames/{id}/especialidade:
    patch:
      tags:
      - tipo-exame-controlador
      operationId: atualizarEspecialidade
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temAgendamentos
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarEspecialidadeFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/tipos-exames/{id}/descricao:
    patch:
      tags:
      - tipo-exame-controlador
      operationId: atualizarDescricao
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temAgendamentos
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarDescricaoFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/prontuarios/{id}/inativar:
    patch:
      tags:
      - Prontuários
      summary: Inativar prontuário
      description: Inativa um prontuário. Médicos e gestores podem realizar esta operação.
      operationId: inativarProntuario
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      - name: profissionalResponsavel
        in: query
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Dados inválidos
          content: {}
        "204":
          description: Prontuário inativado com sucesso
  /backend/medicamentos/{id}/uso-principal:
    patch:
      tags:
      - medicamento-controlador
      operationId: atualizarUsoPrincipal
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsoPrincipalFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/convenios/{id}/nome:
    patch:
      tags:
      - convenio-controlador
      operationId: alterarNome
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/NomeFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /api/referencia/especialidades/{id}:
    get:
      tags:
      - especialidade-controlador
      operationId: buscarPorId_2
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/EspecialidadeDetalhes"
    delete:
      tags:
      - especialidade-controlador
      operationId: excluir
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
    patch:
      tags:
      - especialidade-controlador
      operationId: atualizar_3
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/AtualizarEspecialidadeFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /backend/tipos-exames/{id}:
    get:
      tags:
      - tipo-exame-controlador
      operationId: obterDetalhes_2
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
    delete:
      tags:
      - tipo-exame-controlador
      operationId: excluirTipoExame
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: temAgendamentos
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
  /backend/tipos-exames/inativos:
    get:
      tags:
      - tipo-exame-controlador
      operationId: pesquisarTiposExamesInativos
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/TipoExameResumo"
  /backend/prontuarios/{id}:
    get:
      tags:
      - Prontuários
      summary: Obter prontuário por ID
      description: "Retorna os detalhes completos de um prontuário, incluindo histó\
        rico clínico e atualizações"
      operationId: obterProntuario
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Prontuário encontrado
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ProntuarioResponse"
    delete:
      tags:
      - Prontuários
      summary: Excluir prontuário logicamente
      description: Exclui logicamente um prontuário. Médicos e gestores podem realizar
        esta operação.
      operationId: excluirProntuario
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      - name: profissionalResponsavel
        in: query
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Dados inválidos
          content: {}
        "204":
          description: Prontuário excluído com sucesso
  /backend/prontuarios/{id}/atualizacoes:
    get:
      tags:
      - Prontuários
      summary: Listar histórico de atualizações do prontuário
      description: Retorna a lista completa de registros de atualizações de um prontuário
        específico
      operationId: listarHistoricoAtualizacoes
      parameters:
      - name: id
        in: path
        description: ID do prontuário
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      responses:
        "404":
          description: Prontuário não encontrado
          content: {}
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Lista de histórico de atualizações retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/AtualizacaoItemResponse"
  /backend/prontuarios/paciente/{pacienteId}:
    get:
      tags:
      - Prontuários
      summary: Buscar prontuários por paciente
      description: Retorna uma lista de prontuários de um paciente específico
      operationId: buscarPorPaciente
      parameters:
      - name: pacienteId
        in: path
        description: ID do paciente
        required: true
        schema:
          type: string
        example: 1
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Lista de prontuários retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ProntuarioResumo"
  /backend/medicamentos/{id}:
    get:
      tags:
      - medicamento-controlador
      operationId: obterDetalhes_3
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /backend/medicamentos/revisao-pendente:
    get:
      tags:
      - medicamento-controlador
      operationId: pesquisarMedicamentosComRevisaoPendente
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicamentoResumo"
  /backend/funcionarios/status/{status}:
    get:
      tags:
      - funcionario-controlador
      operationId: pesquisarPorStatus
      parameters:
      - name: status
        in: path
        required: true
        schema:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FuncionarioResumo"
  /backend/funcionarios/funcao/{funcao}:
    get:
      tags:
      - funcionario-controlador
      operationId: pesquisarPorFuncao
      parameters:
      - name: funcao
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FuncionarioResumo"
  /backend/faturamentos/{id}:
    get:
      tags:
      - Faturamentos
      summary: Obter faturamento por ID
      description: "Retorna os detalhes completos de um faturamento, incluindo histó\
        rico de alterações"
      operationId: obterFaturamento
      parameters:
      - name: id
        in: path
        description: ID do faturamento
        required: true
        schema:
          type: string
        example: 123e4567-e89b-12d3-a456-426614174000
      responses:
        "404":
          description: Faturamento não encontrado
          content: {}
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: Faturamento encontrado
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FaturamentoDetalhes"
  /backend/faturamentos/status/{status}:
    get:
      tags:
      - Faturamentos
      summary: Pesquisar faturamentos por status
      description: "Retorna uma lista de faturamentos filtrados por status (PENDENTE,\
        \ PAGO, CANCELADO, INVALIDO, REMOVIDO)"
      operationId: pesquisarPorStatus_1
      parameters:
      - name: status
        in: path
        description: "Status do faturamento (PENDENTE, PAGO, CANCELADO, INVALIDO,\
          \ REMOVIDO)"
        required: true
        schema:
          type: string
        example: PENDENTE
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Status inválido
          content: {}
        "200":
          description: Lista de faturamentos retornada com sucesso
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FaturamentoResumo"
  /backend/convenios/status/{status}:
    get:
      tags:
      - convenio-controlador
      operationId: pesquisarPorStatus_2
      parameters:
      - name: status
        in: path
        required: true
        schema:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - ARQUIVADO
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ConvenioResumo"
  /backend/convenios/codigo/{codigoIdentificacao}:
    get:
      tags:
      - convenio-controlador
      operationId: pesquisarPorCodigo
      parameters:
      - name: codigoIdentificacao
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ConvenioResumo"
  /backend/consultas/{id}:
    get:
      tags:
      - consulta-controlador
      operationId: obterDetalhes_4
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /backend/consultas/agendadas:
    get:
      tags:
      - consulta-controlador
      operationId: listarAgendadas
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/ConsultaResumo"
  /api/pacientes/cpf/{cpf}:
    get:
      tags:
      - paciente-controller
      operationId: buscarPorCpf
      parameters:
      - name: cpf
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/PacienteResumo"
  /api/medicos/status/{status}:
    get:
      tags:
      - medico-controller
      operationId: listarPorStatus
      parameters:
      - name: status
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicoResumo"
  /api/medicos/health:
    get:
      tags:
      - medico-controller
      operationId: health
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: string
  /api/medicos/especialidade/{especialidadeId}:
    get:
      tags:
      - medico-controller
      operationId: listarPorEspecialidade
      parameters:
      - name: especialidadeId
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicoResumo"
  /api/medicos/crm/{crm}:
    get:
      tags:
      - medico-controller
      operationId: buscarPorCrm
      parameters:
      - name: crm
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/MedicoDetalhes"
  /api/medicos/buscar:
    get:
      tags:
      - medico-controller
      operationId: buscarGeral
      parameters:
      - name: termo
        in: query
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/MedicoResumo"
  /api/folhas-pagamento/{id}:
    get:
      tags:
      - folha-pagamento-controlador
      operationId: obter
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/FolhaPagamentoDetalhes"
    delete:
      tags:
      - folha-pagamento-controlador
      operationId: remover_2
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: integer
          format: int32
      - name: usuarioResponsavelId
        in: query
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
  /api/folhas-pagamento/status/{status}:
    get:
      tags:
      - folha-pagamento-controlador
      operationId: listarPorStatus_1
      parameters:
      - name: status
        in: path
        required: true
        schema:
          type: string
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FolhaPagamentoResumo"
  /api/folhas-pagamento/funcionario/{funcionarioId}:
    get:
      tags:
      - folha-pagamento-controlador
      operationId: listarPorFuncionario
      parameters:
      - name: funcionarioId
        in: path
        required: true
        schema:
          type: integer
          format: int32
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/FolhaPagamentoResumo"
  /backend/convenios/{codigoIdentificacao}:
    delete:
      tags:
      - convenio-controlador
      operationId: excluirConvenio
      parameters:
      - name: codigoIdentificacao
        in: path
        required: true
        schema:
          type: string
      - name: temProcedimentoAtivo
        in: query
        required: false
        schema:
          type: boolean
          default: false
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/UsuarioResponsavelFormulario"
        required: true
      responses:
        "404":
          description: Not Found
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "400":
          description: Bad Request
          content:
            '*/*':
              schema:
                $ref: "#/components/schemas/ErroResposta"
        "204":
          description: No Content
components:
  schemas:
    ErroResposta:
      type: object
      properties:
        status:
          type: integer
          format: int32
        mensagem:
          type: string
    AtualizacaoExameRequest:
      type: object
      properties:
        medicoId:
          type: integer
          format: int64
          description: ID do médico responsável (novo)
        tipoExame:
          type: string
          description: Tipo do exame (novo)
          minLength: 1
        dataHora:
          type: string
          format: date-time
          description: Nova data e hora do exame (ISO-8601)
        responsavelId:
          type: integer
          format: int64
          description: ID do usuário/responsável que está realizando a alteração
        observacoes:
          type: string
          description: Observações/Notas sobre a alteração (opcional)
      required:
      - dataHora
      - medicoId
      - responsavelId
    ExameResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        pacienteId:
          type: integer
          format: int64
        medicoId:
          type: integer
          format: int64
        tipoExame:
          type: string
        dataHora:
          type: string
          format: date-time
        status:
          type: string
    UsuarioResponsavelFormulario:
      type: object
      properties:
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    RevisaoFormulario:
      type: object
      properties:
        novaContraindicacao:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    FuncionarioAtualizacaoCompletaFormulario:
      type: object
      properties:
        nome:
          type: string
          minLength: 1
        funcao:
          type: string
          minLength: 1
        contato:
          type: string
          minLength: 1
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
      - status
    MarcarComoPagoRequest:
      type: object
      description: Dados para marcar faturamento como pago
      properties:
        usuarioResponsavel:
          type: string
          description: ID do usuário responsável
          example: user-123
      required:
      - usuarioResponsavel
    CancelarFaturamentoRequest:
      type: object
      description: Dados para cancelamento de faturamento
      properties:
        motivo:
          type: string
          description: Motivo do cancelamento
          example: Paciente desistiu do procedimento
        usuarioResponsavel:
          type: string
          description: ID do usuário responsável
          example: user-123
      required:
      - motivo
      - usuarioResponsavel
    StatusFormulario:
      type: object
      properties:
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - ARQUIVADO
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
      - status
    AtualizarPacienteRequest:
      type: object
      properties:
        nome:
          type: string
        cpf:
          type: string
        dataNascimento:
          type: string
        telefone:
          type: string
        endereco:
          type: string
        responsavelId:
          type: integer
          format: int32
    PacienteDetalhes:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        dataNascimento:
          type: string
        telefone:
          type: string
        endereco:
          type: string
        cpf:
          type: string
    DisponibilidadeRequest:
      type: object
      properties:
        diaSemana:
          type: string
        horaInicio:
          type: string
        horaFim:
          type: string
    MedicoAtualizacaoRequest:
      type: object
      properties:
        nome:
          type: string
        contato:
          type: string
        dataNascimento:
          type: string
          format: date
        disponibilidades:
          type: array
          items:
            $ref: "#/components/schemas/DisponibilidadeRequest"
    HistoricoDetalhes:
      type: object
      properties:
        acao:
          type: string
        descricao:
          type: string
        responsavel:
          type: string
        dataHora:
          type: string
          format: date-time
    HorarioDisponibilidade:
      type: object
      properties:
        diaSemana:
          type: string
        horaInicio:
          type: string
        horaFim:
          type: string
    MedicoDetalhes:
      type: object
      properties:
        id:
          type: string
        nome:
          type: string
        funcao:
          type: string
        contato:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
        historico:
          type: array
          items:
            $ref: "#/components/schemas/HistoricoDetalhes"
        crm:
          type: string
        especialidade:
          type: string
        dataNascimento:
          type: string
          format: date
        horariosDisponiveis:
          type: array
          items:
            $ref: "#/components/schemas/HorarioDisponibilidade"
    AtualizarValoresFormulario:
      type: object
      properties:
        novoSalarioBase:
          type: number
        novosBeneficios:
          type: number
        usuarioResponsavelId:
          type: integer
          format: int32
    FolhaPagamentoDetalhes:
      type: object
      properties:
        id:
          type: integer
          format: int32
        funcionarioId:
          type: integer
          format: int32
        periodoReferencia:
          type: string
        tipoRegistro:
          type: string
          enum:
          - PAGAMENTO
          - AJUSTE
        salarioBase:
          type: number
        beneficios:
          type: number
        metodoPagamento:
          type: string
        status:
          type: string
          enum:
          - PENDENTE
          - PAGO
          - CANCELADO
        valorLiquido:
          type: number
    AlterarStatusFormulario:
      type: object
      properties:
        novoStatus:
          type: string
          enum:
          - PENDENTE
          - PAGO
          - CANCELADO
        usuarioResponsavelId:
          type: integer
          format: int32
    AgendamentoExameRequest:
      type: object
      properties:
        pacienteId:
          type: integer
          format: int64
        medicoId:
          type: integer
          format: int64
        tipoExame:
          type: string
          minLength: 1
        dataHora:
          type: string
          format: date-time
        responsavelId:
          type: integer
          format: int64
      required:
      - dataHora
      - medicoId
      - pacienteId
      - responsavelId
    TipoExameFormulario:
      type: object
      properties:
        codigo:
          type: string
          minLength: 1
        descricao:
          type: string
          minLength: 1
        especialidade:
          type: string
          minLength: 1
        valor:
          type: number
          format: double
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
      - valor
    CriarProntuarioRequest:
      type: object
      properties:
        pacienteId:
          type: string
        atendimentoId:
          type: string
        profissionalResponsavel:
          type: string
        observacoesIniciais:
          type: string
    ProntuarioResponse:
      type: object
      properties:
        id:
          type: string
        pacienteId:
          type: string
        atendimentoId:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - INATIVADO
          - ARQUIVADO
          - EXCLUIDO
        dataHoraCriacao:
          type: string
          format: date-time
        profissionalResponsavel:
          type: string
        observacoesIniciais:
          type: string
    AdicionarHistoricoRequest:
      type: object
      properties:
        sintomas:
          type: string
        diagnostico:
          type: string
        conduta:
          type: string
        profissionalResponsavel:
          type: string
        anexosReferenciados:
          type: array
          items:
            type: string
    MedicamentoFormulario:
      type: object
      properties:
        nome:
          type: string
          minLength: 1
        usoPrincipal:
          type: string
          minLength: 1
        contraindicacoes:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    FuncionarioFormulario:
      type: object
      properties:
        nome:
          type: string
          minLength: 1
        funcao:
          type: string
          minLength: 1
        contato:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    RegistrarFaturamentoRequest:
      type: object
      description: Dados para registro de novo faturamento
      properties:
        pacienteId:
          type: string
          description: ID do paciente (INTEGER)
          example: 1
        tipoProcedimento:
          type: string
          description: Tipo de procedimento (CONSULTA ou EXAME)
          example: CONSULTA
        descricaoProcedimento:
          type: string
          description: Descrição do procedimento
          example: Consulta médica geral
        valor:
          type: number
          format: double
          description: Valor do faturamento
          example: 150.0
        metodoPagamento:
          type: string
          description: Método de pagamento
          example: CARTAO_CREDITO
        usuarioResponsavel:
          type: string
          description: ID do usuário responsável
          example: user-123
        observacoes:
          type: string
          description: Observações adicionais
          example: Paciente com desconto especial
      required:
      - descricaoProcedimento
      - metodoPagamento
      - pacienteId
      - tipoProcedimento
      - usuarioResponsavel
      - valor
    FaturamentoResumo:
      type: object
      properties:
        id:
          type: string
        pacienteId:
          type: string
        tipoProcedimento:
          type: string
          enum:
          - Consulta
          - Exame
        descricaoProcedimento:
          type: string
        valor:
          type: number
        metodoPagamento:
          type: string
        status:
          type: string
          enum:
          - Pendente
          - Pago
          - Cancelado
          - Inválido
          - Removido
        dataHoraFaturamento:
          type: string
          format: date-time
    ConvenioFormulario:
      type: object
      properties:
        nome:
          type: string
          minLength: 1
        codigoIdentificacao:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    AgendamentoFormulario:
      type: object
      properties:
        dataHora:
          type: string
          format: date-time
        descricao:
          type: string
          minLength: 1
        pacienteId:
          type: integer
          format: int32
        medicoId:
          type: integer
          format: int32
        usuarioId:
          type: integer
          format: int32
      required:
      - dataHora
      - medicoId
      - pacienteId
      - usuarioId
    EspecialidadeFormulario:
      type: object
      properties:
        nome:
          type: string
          maxLength: 100
          minLength: 3
        descricao:
          type: string
          maxLength: 255
          minLength: 0
    CadastrarPacienteRequest:
      type: object
      properties:
        nome:
          type: string
        cpf:
          type: string
        dataNascimento:
          type: string
        telefone:
          type: string
        endereco:
          type: string
        responsavelId:
          type: integer
          format: int32
    MedicoCadastroRequest:
      type: object
      properties:
        nome:
          type: string
        contato:
          type: string
        crmNumero:
          type: string
        crmUf:
          type: string
        especialidadeId:
          type: integer
          format: int32
        dataNascimento:
          type: string
          format: date
        disponibilidades:
          type: array
          items:
            $ref: "#/components/schemas/DisponibilidadeRequest"
    RegistrarFolhaFormulario:
      type: object
      properties:
        funcionarioId:
          type: integer
          format: int32
        periodoReferencia:
          type: string
        tipoRegistro:
          type: string
          enum:
          - PAGAMENTO
          - AJUSTE
        salarioBase:
          type: number
        beneficios:
          type: number
        metodoPagamento:
          type: string
        tipoVinculo:
          type: string
          enum:
          - CLT
          - ESTAGIARIO
          - PJ
        usuarioResponsavelId:
          type: integer
          format: int32
        funcionarioAtivo:
          type: boolean
    CancelamentoExameRequest:
      type: object
      properties:
        motivo:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int64
      required:
      - responsavelId
    AtualizarValorFormulario:
      type: object
      properties:
        novoValor:
          type: number
          format: double
        responsavelId:
          type: integer
          format: int32
      required:
      - novoValor
      - responsavelId
    AtualizarEspecialidadeFormulario:
      type: object
      properties:
        novaEspecialidade:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    AtualizarDescricaoFormulario:
      type: object
      properties:
        novaDescricao:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    UsoPrincipalFormulario:
      type: object
      properties:
        novoUsoPrincipal:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    FuncionarioAtualizacaoFormulario:
      type: object
      properties:
        novoNome:
          type: string
          minLength: 1
        novaFuncao:
          type: string
          minLength: 1
        novoContato:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    NomeFormulario:
      type: object
      properties:
        novoNome:
          type: string
          minLength: 1
        responsavelId:
          type: integer
          format: int32
      required:
      - responsavelId
    ExameDetalheResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        pacienteId:
          type: integer
          format: int64
        medicoId:
          type: integer
          format: int64
        tipoExame:
          type: string
        dataHora:
          type: string
          format: date-time
        status:
          type: string
        historico:
          type: array
          items:
            $ref: "#/components/schemas/HistoricoEntradaResponse"
    HistoricoEntradaResponse:
      type: object
      properties:
        dataHora:
          type: string
          format: date-time
        acao:
          type: string
        descricao:
          type: string
        responsavelId:
          type: integer
          format: int64
    TipoExameResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
        valor:
          type: number
          format: double
        descricao:
          type: string
        codigo:
          type: string
        especialidade:
          type: string
    ProntuarioResumo:
      type: object
      properties:
        id:
          type: string
        pacienteId:
          type: string
        atendimentoId:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - INATIVADO
          - ARQUIVADO
          - EXCLUIDO
        dataHoraCriacao:
          type: string
          format: date-time
        profissionalResponsavel:
          type: string
    HistoricoItemResponse:
      type: object
      properties:
        id:
          type: string
        sintomas:
          type: string
        diagnostico:
          type: string
        conduta:
          type: string
        dataHoraRegistro:
          type: string
          format: date-time
        profissionalResponsavel:
          type: string
        anexosReferenciados:
          type: array
          items:
            type: string
        prontuarioId:
          type: string
        pacienteId:
          type: string
    AtualizacaoItemResponse:
      type: object
      properties:
        id:
          type: string
        atendimentoId:
          type: string
        dataHoraAtualizacao:
          type: string
          format: date-time
        profissionalResponsavel:
          type: string
        observacoes:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - INATIVADO
          - ARQUIVADO
          - EXCLUIDO
        prontuarioId:
          type: string
        pacienteId:
          type: string
    MedicamentoResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        usoPrincipal:
          type: string
        contraindicacoes:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - ARQUIVADO
        possuiRevisaoPendente:
          type: boolean
    FuncionarioResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        funcao:
          type: string
        contato:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
    FaturamentoDetalhes:
      type: object
      properties:
        id:
          type: string
        pacienteId:
          type: string
        tipoProcedimento:
          type: string
          enum:
          - Consulta
          - Exame
        descricaoProcedimento:
          type: string
        valor:
          type: number
        metodoPagamento:
          type: string
        status:
          type: string
          enum:
          - Pendente
          - Pago
          - Cancelado
          - Inválido
          - Removido
        dataHoraFaturamento:
          type: string
          format: date-time
        usuarioResponsavel:
          type: string
        observacoes:
          type: string
        valorPadrao:
          type: number
        justificativaValorDiferente:
          type: string
        historico:
          type: array
          items:
            $ref: "#/components/schemas/HistoricoDetalhes"
    ConvenioResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - ARQUIVADO
        nome:
          type: string
        codigoIdentificacao:
          type: string
    ConsultaResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        dataHora:
          type: string
          format: date-time
        nomePaciente:
          type: string
        nomeMedico:
          type: string
        status:
          type: string
          enum:
          - AGENDADA
          - CANCELADA
          - EM_ANDAMENTO
          - REALIZADA
    EspecialidadeResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        descricao:
          type: string
        status:
          type: string
    EspecialidadeDetalhes:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        descricao:
          type: string
        status:
          type: string
        possuiVinculoHistorico:
          type: boolean
    PacienteResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        nome:
          type: string
        telefone:
          type: string
        cpf:
          type: string
    MedicoResumo:
      type: object
      properties:
        id:
          type: string
        nome:
          type: string
        funcao:
          type: string
        contato:
          type: string
        status:
          type: string
          enum:
          - ATIVO
          - INATIVO
          - FERIAS
          - AFASTADO
        crm:
          type: string
        especialidade:
          type: string
        consultasHoje:
          type: integer
          format: int32
        proximaConsulta:
          type: string
    FolhaPagamentoResumo:
      type: object
      properties:
        id:
          type: integer
          format: int32
        funcionarioId:
          type: integer
          format: int32
        periodoReferencia:
          type: string
        valorLiquido:
          type: number
        status:
          type: string
          enum:
          - PENDENTE
          - PAGO
          - CANCELADO
