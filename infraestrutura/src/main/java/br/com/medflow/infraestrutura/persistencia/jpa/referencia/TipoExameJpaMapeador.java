package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.tiposExames.HistoricoEntrada;
import br.com.medflow.dominio.referencia.tiposExames.TipoExame;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;

@Component
public class TipoExameJpaMapeador extends ModelMapper {
    
    public TipoExameJpaMapeador() {
        super();
        
        // Mapeamento de DOMÍNIO (TipoExame) para JPA (TipoExameJpa)
        createTypeMap(TipoExame.class, TipoExameJpa.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getId().getId(), TipoExameJpa::setId);
                mapper.skip(TipoExameJpa::setHistorico);
            });
        
        // Mapeamento de JPA (TipoExameJpa) para DOMÍNIO (TipoExame)
        createTypeMap(TipoExameJpa.class, TipoExame.class)
            .setProvider(request -> {
                TipoExameJpa jpa = (TipoExameJpa) request.getSource();
                
                return new TipoExame(
                    new TipoExameId(jpa.getId()),
                    jpa.getCodigo(),
                    jpa.getDescricao(),
                    jpa.getEspecialidade(),
                    jpa.getValor(),
                    jpa.getStatus(),
                    jpa.getHistorico().stream()
                        .map(h -> map(h, HistoricoEntrada.class))
                        .toList()
                );
            });
        
        // Mapeamento de DOMÍNIO (HistoricoEntrada) para JPA (HistoricoTipoExameJpa)
        createTypeMap(HistoricoEntrada.class, HistoricoTipoExameJpa.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getResponsavel().getId(), 
                    HistoricoTipoExameJpa::setResponsavelId);
            });
        
        // Mapeamento de JPA (HistoricoTipoExameJpa) para DOMÍNIO (HistoricoEntrada)
        createTypeMap(HistoricoTipoExameJpa.class, HistoricoEntrada.class)
            .setProvider(request -> {
                HistoricoTipoExameJpa jpa = (HistoricoTipoExameJpa) request.getSource();
                
                return new HistoricoEntrada(
                    jpa.getAcao(),
                    jpa.getDescricao(),
                    new br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId(
                        jpa.getResponsavelId()
                    ),
                    jpa.getDataHora()
                );
            });
    }
}