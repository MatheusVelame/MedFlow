package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameDetalhes;
import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameRepositorioAplicacao;
import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameResumo;
import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

@Component
public class TipoExameRepositorioAplicacaoImpl implements TipoExameRepositorioAplicacao {

    private final TipoExameJpaRepository jpaRepository;

    public TipoExameRepositorioAplicacaoImpl(TipoExameJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoExameResumo> pesquisarResumos() {
        // Retorna todos EXCETO os INATIVOS, ordenados por código
        return jpaRepository.findByStatusNot(StatusTipoExame.INATIVO, Sort.by("codigo"))
                .stream()
                .map(this::mapearParaResumo)
                .toList();
    }

    @Override
    public Optional<TipoExameDetalhes> obterDetalhesPorId(Integer id) {
        return jpaRepository.findById(id)
                .map(this::mapearParaDetalhes);
    }

    @Override
    public List<TipoExameResumo> findByStatus(StatusTipoExame status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::mapearParaResumo)
                .toList();
    }

    @Override
    public List<TipoExameResumo> pesquisarTiposExamesInativos() {
        return jpaRepository.findByStatus(StatusTipoExame.INATIVO).stream()
                .map(this::mapearParaResumo)
                .toList();
    }

    // ===== MAPEADORES =====

    private TipoExameResumo mapearParaResumo(TipoExameJpa jpa) {
        return new TipoExameResumo() {
            @Override
            public Integer getId() {
                return jpa.getId();
            }

            @Override
            public String getCodigo() {
                return jpa.getCodigo();
            }

            @Override
            public String getDescricao() {
                return jpa.getDescricao();
            }

            @Override
            public String getEspecialidade() {
                return jpa.getEspecialidade();
            }

            @Override
            public Double getValor() {
                return jpa.getValor();
            }

            @Override
            public StatusTipoExame getStatus() {
                return jpa.getStatus();
            }
        };
    }

    private TipoExameDetalhes mapearParaDetalhes(TipoExameJpa jpa) {
        return new TipoExameDetalhes() {
            @Override public Integer getId() { return jpa.getId(); }
            @Override public String getCodigo() { return jpa.getCodigo(); }
            @Override public String getDescricao() { return jpa.getDescricao(); }
            @Override public String getEspecialidade() { return jpa.getEspecialidade(); }
            @Override public Double getValor() { return jpa.getValor(); }
            @Override public StatusTipoExame getStatus() { return jpa.getStatus(); }
        };
    }

}