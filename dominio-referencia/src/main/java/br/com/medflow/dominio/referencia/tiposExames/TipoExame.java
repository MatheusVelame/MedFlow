package br.com.medflow.dominio.referencia.tiposExames;

   import static org.apache.commons.lang3.Validate.notBlank;
   import static org.apache.commons.lang3.Validate.notNull;

   import java.util.ArrayList;
   import java.util.List;

   public class TipoExame {
       private TipoExameId id;
       private String codigo;
       private String descricao;
       private String especialidade;
       private Double valor;
       private StatusTipoExame status;
       private List<HistoricoEntrada> historico = new ArrayList<>();

       // Construtor para novo cadastro
       public TipoExame(String codigo, String descricao, String especialidade, Double valor, UsuarioResponsavelId responsavelId) {
           this.id = null; // Será atribuído pelo repositório
           setCodigo(codigo);
           setDescricao(descricao);
           setEspecialidade(especialidade);
           setValor(valor);
           this.status = StatusTipoExame.ATIVO;
           
           adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Tipo de exame criado com status Ativo", responsavelId);
       }
       
       // Construtor para reconstrução de objeto existente
       public TipoExame(TipoExameId id, String codigo, String descricao, String especialidade, 
                       Double valor, StatusTipoExame status, List<HistoricoEntrada> historico) {
           this.id = id;
           this.codigo = codigo;
           this.descricao = descricao;
           this.especialidade = especialidade;
           this.valor = valor;
           this.status = status;
           if (historico != null) {
               this.historico.addAll(historico);
           }
       }

       private void setCodigo(String codigo) {
           notBlank(codigo, "O código do tipo de exame é obrigatório");
           this.codigo = codigo;
       }

       private void setDescricao(String descricao) {
           notBlank(descricao, "A descrição do tipo de exame é obrigatória");
           this.descricao = descricao;
       }

       private void setEspecialidade(String especialidade) {
           notBlank(especialidade, "A especialidade do tipo de exame é obrigatória");
           this.especialidade = especialidade;
       }

       private void setValor(Double valor) {
           notNull(valor, "O valor do tipo de exame é obrigatório");
           if (valor < 0) {
               throw new IllegalArgumentException("O valor deve ser maior ou igual a zero");
           }
           this.valor = valor;
       }

       public void atualizarDescricao(String novaDescricao, UsuarioResponsavelId responsavelId) {
           notBlank(novaDescricao, "A descrição não pode estar em branco");
           
           if (this.descricao.equals(novaDescricao)) {
               return;
           }
           
           this.descricao = novaDescricao;
           adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, 
                   "Descrição alterada para: " + novaDescricao, responsavelId);
       }

       public void atualizarEspecialidade(String novaEspecialidade, UsuarioResponsavelId responsavelId) {
           notBlank(novaEspecialidade, "A especialidade não pode estar em branco");
           
           if (this.especialidade.equals(novaEspecialidade)) {
               return;
           }
           
           this.especialidade = novaEspecialidade;
           adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, 
                   "Especialidade alterada para: " + novaEspecialidade, responsavelId);
       }

       public void atualizarValor(Double novoValor, UsuarioResponsavelId responsavelId) {
           notNull(novoValor, "O valor não pode ser nulo");
           if (novoValor < 0) {
               throw new IllegalArgumentException("O valor deve ser maior ou igual a zero");
           }
           
           if (this.valor.equals(novoValor)) {
               return;
           }
           
           this.valor = novoValor;
           adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, 
                   "Valor alterado para: " + novoValor, responsavelId);
       }

       public void inativar(UsuarioResponsavelId responsavelId) {
           if (this.status == StatusTipoExame.INATIVO) {
               return;
           }
           
           this.status = StatusTipoExame.INATIVO;
           adicionarEntradaHistorico(AcaoHistorico.INATIVACAO, 
                   "Tipo de exame inativado", responsavelId);
       }

       private void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
           HistoricoEntrada entrada = new HistoricoEntrada(acao, descricao, responsavelId);
           this.historico.add(entrada);
       }

       // Getters
       public TipoExameId getId() { return id; }
       public String getCodigo() { return codigo; }
       public String getDescricao() { return descricao; }
       public String getEspecialidade() { return especialidade; }
       public Double getValor() { return valor; }
       public StatusTipoExame getStatus() { return status; }
       public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }
   }