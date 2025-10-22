package br.com.medflow.dominio.referencia.tiposExames.test;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;
import br.com.medflow.dominio.referencia.tiposExames.TipoExame;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameRepositorio;


import br.com.medflow.dominio.referencia.tiposExames.TipoExameRepositorio;

   public class TipoExameRepositorioMemoria implements TipoExameRepositorio {
       private Map<TipoExameId, TipoExame> tiposExames = new HashMap<>();
       private int sequenciaId = 0;

       @Override
       public void salvar(TipoExame tipoExame) {
           notNull(tipoExame, "O tipo de exame não pode ser nulo");
           
           // Simula a geração de ID (para novos agregados) ou atualização
           if (tipoExame.getId() == null) {
               sequenciaId++;
               TipoExameId novoId = new TipoExameId(sequenciaId);
               
               // Reconstrução forçada para atribuir o ID, mantendo a imutabilidade
               TipoExame novo = new TipoExame(
                   novoId, 
                   tipoExame.getCodigo(),
                   tipoExame.getDescricao(),
                   tipoExame.getEspecialidade(),
                   tipoExame.getValor(),
                   tipoExame.getStatus(),
                   tipoExame.getHistorico()
               );
               tiposExames.put(novoId, novo);
           } else {
               tiposExames.put(tipoExame.getId(), tipoExame);
           }
       }

       @Override
       public TipoExame obter(TipoExameId id) {
           notNull(id, "O id do tipo de exame não pode ser nulo");
           var tipoExame = tiposExames.get(id);
           return Optional.ofNullable(tipoExame)
                  .orElseThrow(() -> new IllegalArgumentException("Tipo de exame não encontrado"));
       }

       @Override
       public Optional<TipoExame> obterPorCodigo(String codigo) {
           return tiposExames.values().stream()
                   .filter(e -> e.getCodigo().equalsIgnoreCase(codigo))
                   .findFirst();
       }
       
       @Override
       public List<TipoExame> pesquisar() {
           // Retorna todos, exceto os INATIVOS (Lista Padrão)
           return tiposExames.values().stream()
                   .filter(e -> e.getStatus() != StatusTipoExame.INATIVO)
                   .toList();
       }

       @Override
       public List<TipoExame> pesquisarComFiltroInativo() {
           // Retorna todos, incluindo os INATIVOS
           return new ArrayList<>(tiposExames.values());
       }
       
       @Override
       public void excluir(TipoExameId id) {
           notNull(id, "O id do tipo de exame não pode ser nulo");
           tiposExames.remove(id);
       }
       
       /**
        * Limpa o repositório em memória. Essencial para isolamento de testes BDD.
        */
       public void clear() {
           tiposExames.clear();
           sequenciaId = 0;
       }
   }