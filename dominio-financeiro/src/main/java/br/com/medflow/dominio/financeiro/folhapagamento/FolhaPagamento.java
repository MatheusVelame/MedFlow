package br.com.medflow.dominio.financeiro.folhapagamento;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;

import java.math.BigDecimal;

/**
 * Classe de Domínio que representa a Folha de Pagamento e implementa as Regras de Negócio.
 */
public class FolhaPagamento {
    private FolhaPagamentoId id;
    private int funcionarioId;
    private String periodoReferencia; // formato: MM/AAAA
    private TipoRegistro tipoRegistro;
    private BigDecimal salarioBase;
    private BigDecimal beneficios;
    private String metodoPagamento;
    private TipoVinculo tipoVinculo;
    private StatusFolha status;

    public FolhaPagamento(int funcionarioId, String periodoReferencia, TipoRegistro tipoRegistro,
                          BigDecimal salarioBase, BigDecimal beneficios, String metodoPagamento,
                          TipoVinculo tipoVinculo, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela criação não pode ser nulo.");
        validarFuncionarioAtivo(funcionarioId);

        this.funcionarioId = funcionarioId;
        setPeriodoReferencia(periodoReferencia);
        setTipoRegistro(tipoRegistro);
        setSalarioBase(salarioBase);
        setBeneficios(beneficios);
        setMetodoPagamento(metodoPagamento);
        setTipoVinculo(tipoVinculo);
        this.status = StatusFolha.PENDENTE; // RN: Status padrão sempre é PENDENTE
    }

    public FolhaPagamento(FolhaPagamentoId id, int funcionarioId, String periodoReferencia,
                          TipoRegistro tipoRegistro, BigDecimal salarioBase, BigDecimal beneficios,
                          String metodoPagamento, TipoVinculo tipoVinculo, StatusFolha status) {
        notNull(id, "O ID da folha de pagamento não pode ser nulo na reconstrução.");
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.periodoReferencia = periodoReferencia;
        this.tipoRegistro = tipoRegistro;
        this.salarioBase = salarioBase;
        this.beneficios = beneficios;
        this.metodoPagamento = metodoPagamento;
        this.tipoVinculo = tipoVinculo;
        this.status = status;
    }

    private void validarFuncionarioAtivo(int funcionarioId) {
        // Esta validação seria feita pelo serviço verificando no repositório de funcionários
        isTrue(funcionarioId > 0, "O funcionário deve ser válido.");
    }

    public void setPeriodoReferencia(String periodoReferencia) {
        notBlank(periodoReferencia, "O período de referência é obrigatório.");
        validarFormatoPeriodo(periodoReferencia);
        this.periodoReferencia = periodoReferencia.trim();
    }

    private void validarFormatoPeriodo(String periodo) {
        if (!periodo.matches("^(0[1-9]|1[0-2])/\\d{4}$")) {
            throw new IllegalArgumentException("O período deve estar no formato MM/AAAA.");
        }
    }

    public void setTipoRegistro(TipoRegistro tipoRegistro) {
        notNull(tipoRegistro, "O tipo de registro é obrigatório.");
        this.tipoRegistro = tipoRegistro;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        notNull(salarioBase, "O salário base é obrigatório.");
        isTrue(salarioBase.compareTo(BigDecimal.ZERO) > 0, "O salário base deve ser maior que zero.");
        this.salarioBase = salarioBase;
    }

    public void setBeneficios(BigDecimal beneficios) {
        notNull(beneficios, "Os benefícios são obrigatórios.");
        isTrue(beneficios.compareTo(BigDecimal.ZERO) >= 0, "Os benefícios não podem ser negativos.");
        this.beneficios = beneficios;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        notBlank(metodoPagamento, "O método de pagamento é obrigatório.");
        this.metodoPagamento = metodoPagamento.trim();
    }

    public void setTipoVinculo(TipoVinculo tipoVinculo) {
        notNull(tipoVinculo, "O tipo de vínculo é obrigatório.");
        this.tipoVinculo = tipoVinculo;
    }

    public void atualizarValores(BigDecimal novoSalarioBase, BigDecimal novosBeneficios,
                                 UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");

        if (this.status != StatusFolha.PENDENTE) {
            throw new IllegalStateException("Apenas registros com status 'Pendente' podem ter valores alterados.");
        }

        setSalarioBase(novoSalarioBase);
        setBeneficios(novosBeneficios);
    }

    public void alterarStatus(StatusFolha novoStatus, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");
        notNull(novoStatus, "O novo status não pode ser nulo.");

        if (this.status == novoStatus) {
            throw new IllegalStateException("O status já está definido como " + novoStatus + ".");
        }

        // RN: Não é possível reverter status Cancelado
        if (this.status == StatusFolha.CANCELADO) {
            throw new IllegalStateException("Status 'Cancelado' não pode ser revertido.");
        }

        // RN: Não é possível reverter status Pago
        if (this.status == StatusFolha.PAGO) {
            throw new IllegalStateException("Status 'Pago' não pode ser revertido.");
        }

        // RN: Apenas de Pendente para Pago ou Cancelado
        if (this.status == StatusFolha.PENDENTE &&
                (novoStatus == StatusFolha.PAGO || novoStatus == StatusFolha.CANCELADO)) {
            this.status = novoStatus;
        } else {
            throw new IllegalStateException("Transição de status inválida.");
        }
    }

    public void validarCamposImutaveis(String novoPeriodo, int novoFuncionarioId, TipoRegistro novoTipo) {
        if (!this.periodoReferencia.equals(novoPeriodo)) {
            throw new IllegalStateException("Para corrigir o funcionário ou o período, cancele este registro e crie um novo.");
        }
        if (this.funcionarioId != novoFuncionarioId) {
            throw new IllegalStateException("Para corrigir o funcionário ou o período, cancele este registro e crie um novo.");
        }
        if (this.tipoRegistro != novoTipo) {
            throw new IllegalStateException("O tipo de registro não pode ser alterado.");
        }
    }

    public boolean podeSerRemovido() {
        return this.status == StatusFolha.PENDENTE;
    }

    // ===== GETTERS/SETTER ID (Para o Repositório) =====

    public FolhaPagamentoId getId() { return id; }
    public void setId(FolhaPagamentoId id) { this.id = id; }

    public int getFuncionarioId() { return funcionarioId; }
    public String getPeriodoReferencia() { return periodoReferencia; }
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public BigDecimal getSalarioBase() { return salarioBase; }
    public BigDecimal getBeneficios() { return beneficios; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public TipoVinculo getTipoVinculo() { return tipoVinculo; }
    public StatusFolha getStatus() { return status; }
}