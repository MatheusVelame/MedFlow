package br.com.medflow.dominio.referencia.tiposExames;

   import java.time.LocalDateTime;

   public class HistoricoEntrada {
       private final AcaoHistorico acao;
       private final String descricao;
       private final UsuarioResponsavelId responsavel;
       private final LocalDateTime dataHora;

       public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel) {
           this.acao = acao;
           this.descricao = descricao;
           this.responsavel = responsavel;
           this.dataHora = LocalDateTime.now();
       }
       
       public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel, LocalDateTime dataHora) {
           this.acao = acao;
           this.descricao = descricao;
           this.responsavel = responsavel;
           this.dataHora = dataHora;
       }
       
       public AcaoHistorico getAcao() { return acao; }
       public String getDescricao() { return descricao; }
       public UsuarioResponsavelId getResponsavel() { return responsavel; }
       public LocalDateTime getDataHora() { return dataHora; }
   }