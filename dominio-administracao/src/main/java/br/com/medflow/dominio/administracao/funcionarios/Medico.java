package br.com.medflow.dominio.administracao.funcionarios;

import java.util.List;

public class Medico extends Funcionario {

    private final CRM crm;
    private final EspecialidadeId especialidade;

    // Construtor principal para criação
    public Medico(
            FuncionarioId id,
            String nome,
            String funcao,
            String contato,
            CRM crm,
            EspecialidadeId especialidade,
            UsuarioResponsavelId responsavelId
    ) {
        super(nome, funcao, contato, responsavelId);
        if (id != null) {
            this.setId(id);
        }
        this.crm = crm;
        this.especialidade = especialidade;
    }

    // Construtor para reconstrução (usado pelo repositório)
    public Medico(
            FuncionarioId id,
            String nome,
            String funcao,
            String contato,
            String status,
            List<HistoricoEntrada> historico,
            CRM crm,
            EspecialidadeId especialidade
    ) {
        super(id, nome, funcao, contato, StatusFuncionario.valueOf(status), historico);
        this.crm = crm;
        this.especialidade = especialidade;
    }

    public CRM getCrm() { return crm; }
    public EspecialidadeId getEspecialidade() { return especialidade; }

    // Classe interna para EspecialidadeId
    public static class EspecialidadeId {
        private final int id;

        public EspecialidadeId(int id) {
            this.id = id;
        }

        public int getId() { return id; }
    }
}