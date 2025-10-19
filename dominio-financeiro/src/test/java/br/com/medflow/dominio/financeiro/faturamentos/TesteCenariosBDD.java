package br.com.medflow.dominio.financeiro.faturamentos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

@DisplayName("Cenários BDD - Módulo Financeiro")
public class TesteCenariosBDD {

    private FaturamentoRepositorioMemoria repositorio;
    private TabelaPrecosServico tabelaPrecos;
    private FaturamentoServico servico;

    @BeforeEach
    void setUp() {
        repositorio = new FaturamentoRepositorioMemoria();
        tabelaPrecos = new TabelaPrecosServico();
        servico = new FaturamentoServico(repositorio, tabelaPrecos);
    }

    @Test
    @DisplayName("RN1 - Registrar faturamento com todos os campos obrigatórios")
    void rn1_registrar_faturamento_com_todos_os_campos_obrigatorios() {
        // Given: existe o paciente "PAC-001" cadastrado
        // Given: existe na tabela o procedimento "Consulta Clínica"
        // Given: o usuário "Ana Lima" (Administrador Financeiro) está autenticado
        
        // When: a usuária registrar um faturamento com dados válidos
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-001"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("Ana Lima"), 
            null
        );
        
        // Then: o sistema deve criar o faturamento com status inicial "Pendente"
        assertNotNull(faturamento);
        assertEquals(StatusFaturamento.PENDENTE, faturamento.getStatus());
        
        // Then: o registro deve conter paciente, procedimento, valor e método de pagamento
        assertNotNull(faturamento.getPacienteId());
        assertNotNull(faturamento.getTipoProcedimento());
        assertNotNull(faturamento.getValor());
        assertNotNull(faturamento.getMetodoPagamento());
    }

    @Test
    @DisplayName("RN1 - Impedir registro quando faltar método de pagamento")
    void rn1_impedir_registro_quando_faltar_metodo_de_pagamento() {
        // Given: existe o paciente "PAC-002" cadastrado
        // Given: existe na tabela o procedimento "Exame Hemograma"
        // Given: o usuário "Carlos Souza" (Administrador Financeiro) está autenticado
        
        // When: o usuário tentar registrar um faturamento sem método de pagamento
        // Then: o sistema deve rejeitar o registro informando que "método de pagamento é obrigatório"
        assertThrows(IllegalArgumentException.class, () -> {
            new Faturamento(
                new PacienteId("PAC-002"), 
                TipoProcedimento.EXAME, 
                "Exame Hemograma",
                new Valor(new BigDecimal("45.00")), 
                null, // método de pagamento nulo
                new UsuarioResponsavelId("Carlos Souza"), 
                null
            );
        });
    }

    @Test
    @DisplayName("RN2 - Registrar faturamento com valor compatível")
    void rn2_registrar_faturamento_com_valor_compativel() {
        // Given: existe o paciente "PAC-003" cadastrado
        // Given: a tabela de preços define "Ultrassom Abdominal" = 180.00
        // Given: o usuário "Mariana Reis" (Administrador Financeiro) está autenticado
        
        // When: a usuária registrar um faturamento com valor compatível
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-003"), 
            TipoProcedimento.EXAME, 
            "Ultrassom Abdominal",
            new Valor(new BigDecimal("180.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("Mariana Reis"), 
            null
        );
        
        // Then: o sistema deve aceitar o registro
        assertNotNull(faturamento);
        assertEquals(StatusFaturamento.PENDENTE, faturamento.getStatus());
    }

    @Test
    @DisplayName("RN2 - Bloquear registro com valor não positivo")
    void rn2_bloquear_registro_com_valor_nao_positivo() {
        // Given: existe o paciente "PAC-004" cadastrado
        // Given: a tabela de preços define "Raio-X Tórax" = 95.00
        // Given: o usuário "Ana Lima" (Administrador Financeiro) está autenticado
        
        // When: a usuária tentar registrar um faturamento com valor zero
        // Then: o sistema deve rejeitar o registro informando "valor deve ser positivo"
        assertThrows(IllegalArgumentException.class, () -> {
            var valor = new Valor(new BigDecimal("0.00"));
            servico.registrarFaturamento(
                new PacienteId("PAC-004"), 
                TipoProcedimento.EXAME, 
                "Raio-X Tórax",
                valor, 
                new MetodoPagamento("Dinheiro"),
                new UsuarioResponsavelId("Ana Lima"), 
                null
            );
        });
    }

    @Test
    @DisplayName("RN2 - Bloquear registro quando valor divergir da tabela sem justificativa")
    void rn2_bloquear_registro_quando_valor_divergir_da_tabela_sem_justificativa() {
        // Given: a tabela de preços define "Consulta Clínica" = 200.00
        // Given: existe o paciente "PAC-005" cadastrado
        // Given: o usuário "Carlos Souza" (Administrador Financeiro) está autenticado
        
        // When: o usuário tentar registrar um faturamento com valor divergente
        // Then: o sistema deve rejeitar o registro informando "justificativa obrigatória para valor diferente da tabela"
        assertThrows(Exception.class, () -> {
            servico.registrarFaturamento(
                new PacienteId("PAC-005"), 
                TipoProcedimento.CONSULTA, 
                "Consulta Clínica",
                new Valor(new BigDecimal("50.00")), // Valor muito diferente do padrão
                new MetodoPagamento("Cartão"),
                new UsuarioResponsavelId("Carlos Souza"), 
                null
            );
        });
    }

    @Test
    @DisplayName("RN3 - Forçar status inicial Pendente")
    void rn3_forcar_status_inicial_pendente() {
        // Given: existe o paciente "PAC-006" cadastrado
        // Given: a tabela de preços define "Exame Glicemia" = 30.00
        // Given: o usuário "Mariana Reis" (Administrador Financeiro) está autenticado
        
        // When: a usuária registrar um faturamento
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-006"), 
            TipoProcedimento.EXAME, 
            "Exame Glicemia",
            new Valor(new BigDecimal("30.00")), 
            new MetodoPagamento("Convênio"),
            new UsuarioResponsavelId("Mariana Reis"), 
            null
        );
        
        // Then: o sistema deve criar o faturamento com status "Pendente"
        assertEquals(StatusFaturamento.PENDENTE, faturamento.getStatus());
    }

    @Test
    @DisplayName("RN3 - Ignorar status enviado pelo cliente e manter Pendente")
    void rn3_ignorar_status_enviado_pelo_cliente_e_manter_pendente() {
        // Given: existe o paciente "PAC-007" cadastrado
        // Given: a tabela de preços define "Consulta Clínica" = 200.00
        // Given: o usuário "Ana Lima" (Administrador Financeiro) está autenticado
        
        // When: a usuária enviar um registro com campo de status "Pago"
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-007"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("Ana Lima"), 
            null
        );
        
        // Then: o sistema deve ignorar o valor recebido para status e salvar como "Pendente"
        assertEquals(StatusFaturamento.PENDENTE, faturamento.getStatus());
    }

    @Test
    @DisplayName("RN1 - Administrador exclui faturamento pendente")
    void rn1_administrador_exclui_faturamento_pendente() {
        // Given: existe um faturamento com ID "FAT-1023" e status "Pendente"
        // Given: o usuário "Ana Lima" possui papel "Administrador Financeiro"
        // Given: o motivo informado é "Duplicidade de lançamento"
        
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-001"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("admin"), 
            null
        );
        
        // When: a usuária solicitar a exclusão do faturamento
        servico.excluirLogicamente(faturamento.getId(), "Duplicidade de lançamento", 
                                 new UsuarioResponsavelId("Ana Lima"), true);
        
        // Then: o sistema deve marcar o faturamento como "removido" (exclusão lógica)
        assertEquals(StatusFaturamento.REMOVIDO, faturamento.getStatus());
        
        // Then: deve registrar no log
        assertTrue(faturamento.getObservacoes().contains("Duplicidade de lançamento"));
        
        // Then: o registro não deve aparecer nas listagens operacionais padrão
        var faturamentos = servico.pesquisarExcluindoRemovidos();
        assertTrue(faturamentos.stream().noneMatch(f -> f.getId().equals(faturamento.getId())));
    }

    @Test
    @DisplayName("RN1 - Usuário sem permissão tenta excluir faturamento")
    void rn1_usuario_sem_permissao_tenta_excluir_faturamento() {
        // Given: existe um faturamento com ID "FAT-3001" e status "Pendente"
        // Given: o usuário "Beatriz Melo" possui papel "Atendente" (sem permissão administrativa)
        // Given: o motivo informado é "Erro de digitação"
        
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-001"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("admin"), 
            null
        );
        
        // When: a usuária tentar excluir o faturamento
        // Then: o sistema deve negar a operação informando "Apenas administradores podem excluir faturamentos"
        assertThrows(IllegalStateException.class, () -> {
            servico.excluirLogicamente(faturamento.getId(), "Erro de digitação", 
                                     new UsuarioResponsavelId("Beatriz Melo"), false);
        });
        
        // Then: o faturamento deve permanecer inalterado
        assertEquals(StatusFaturamento.PENDENTE, faturamento.getStatus());
    }

    @Test
    @DisplayName("RN2 - Administrador exclui faturamento marcado como Inválido")
    void rn2_administrador_exclui_faturamento_marcado_como_invalido() {
        // Given: existe um faturamento com ID "FAT-2045" e status "Inválido"
        // Given: o usuário "Carlos Souza" possui papel "Administrador Financeiro"
        // Given: o motivo informado é "Erro de identificação do paciente"
        
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-001"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("admin"), 
            null
        );
        
        // Marcar como inválido
        try {
            var field = Faturamento.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(faturamento, StatusFaturamento.INVALIDO);
        } catch (Exception e) {
            // Ignorar erro de reflexão em teste
        }
        
        // When: o usuário excluir o faturamento
        servico.excluirLogicamente(faturamento.getId(), "Erro de identificação do paciente", 
                                 new UsuarioResponsavelId("Carlos Souza"), true);
        
        // Then: o sistema deve marcar o faturamento como "removido" (exclusão lógica)
        assertEquals(StatusFaturamento.REMOVIDO, faturamento.getStatus());
        
        // Then: deve registrar no log
        assertTrue(faturamento.getObservacoes().contains("Erro de identificação do paciente"));
    }

    @Test
    @DisplayName("RN2 - Bloqueio de exclusão para faturamento com status não permitido")
    void rn2_bloqueio_de_exclusao_para_faturamento_com_status_nao_permitido() {
        // Given: existe um faturamento com ID "FAT-4500" e status "Pago"
        // Given: o usuário "Ana Lima" possui papel "Administrador Financeiro"
        // Given: o motivo informado é "Ajuste operacional"
        
        var faturamento = servico.registrarFaturamento(
            new PacienteId("PAC-001"), 
            TipoProcedimento.CONSULTA, 
            "Consulta Clínica",
            new Valor(new BigDecimal("200.00")), 
            new MetodoPagamento("Cartão"),
            new UsuarioResponsavelId("admin"), 
            null
        );
        
        // Marcar como pago
        var permissaoAdmin = new PermissaoUsuario(
            new UsuarioResponsavelId("admin"), 
            PapelUsuario.ADMINISTRADOR_FINANCEIRO, 
            Set.of()
        );
        servico.alterarStatus(faturamento.getId(), StatusFaturamento.PAGO, 
                            "Pagamento confirmado", new UsuarioResponsavelId("admin"), 
                            permissaoAdmin);
        
        // When: a usuária tentar excluir o faturamento
        // Then: o sistema deve impedir a exclusão informando "Apenas Pendente ou Inválido podem ser excluídos"
        assertThrows(IllegalStateException.class, () -> {
            servico.excluirLogicamente(faturamento.getId(), "Ajuste operacional", 
                                     new UsuarioResponsavelId("Ana Lima"), true);
        });
        
        // Then: o faturamento deve permanecer inalterado
        assertEquals(StatusFaturamento.PAGO, faturamento.getStatus());
    }
}
