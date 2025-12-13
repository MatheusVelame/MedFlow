package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio;

import br.com.medflow.aplicacao.financeiro.convenios.ConvenioDetalhes;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioRepositorioAplicacao;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioResumo;
import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ConvenioRepositorioAplicacaoImpl implements ConvenioRepositorioAplicacao {

    private final ConvenioJpaRepository jpaRepository;

    public ConvenioRepositorioAplicacaoImpl(ConvenioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ConvenioResumo> pesquisarResumos() {
        return jpaRepository.findAll().stream()
                .filter(c -> c.getStatus() != StatusConvenio.ARQUIVADO)
                .map(this::criarConvenioResumo)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConvenioDetalhes> obterDetalhesPorId(Integer id) {
        return jpaRepository.findById(id)
                .map(this::criarConvenioDetalhes);
    }

    @Override
    public Optional<ConvenioDetalhes> obterDetalhesPorCodigoIdentificacao(String codigoIdentificacao) {
        return jpaRepository.findByCodigoIdentificacaoIgnoreCase(codigoIdentificacao)
                .map(this::criarConvenioDetalhes);
    }

    @Override
    public List<ConvenioResumo> pesquisarPorCodigoIdentificacao(String codigoIdentificacao) {
        return jpaRepository.findByCodigoIdentificacaoIgnoreCase(codigoIdentificacao)
                .map(c -> List.of(criarConvenioResumo(c)))
                .orElse(List.of());
    }

    @Override
    public List<ConvenioResumo> pesquisarPorStatus(StatusConvenio status) {
        return jpaRepository.findAll().stream()
                .filter(c -> c.getStatus() == status)
                .map(this::criarConvenioResumo)
                .collect(Collectors.toList());
    }

    private ConvenioResumo criarConvenioResumo(ConvenioJpa jpa) {
        return (ConvenioResumo) Proxy.newProxyInstance(
                ConvenioResumo.class.getClassLoader(),
                new Class[]{ConvenioResumo.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String methodName = method.getName();
                        if (methodName.equals("getId")) return jpa.getId();
                        if (methodName.equals("getNome")) return jpa.getNome();
                        if (methodName.equals("getCodigoIdentificacao")) return jpa.getCodigoIdentificacao();
                        if (methodName.equals("getStatus")) return jpa.getStatus();
                        if (methodName.equals("toString")) return "ConvenioResumo{id=" + jpa.getId() + ", nome=" + jpa.getNome() + "}";
                        if (methodName.equals("equals")) return proxy == args[0];
                        if (methodName.equals("hashCode")) return System.identityHashCode(proxy);
                        throw new UnsupportedOperationException("Método não suportado: " + methodName);
                    }
                }
        );
    }

    private ConvenioDetalhes criarConvenioDetalhes(ConvenioJpa jpa) {
        return (ConvenioDetalhes) Proxy.newProxyInstance(
                ConvenioDetalhes.class.getClassLoader(),
                new Class[]{ConvenioDetalhes.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String methodName = method.getName();
                        if (methodName.equals("getId")) return jpa.getId();
                        if (methodName.equals("getNome")) return jpa.getNome();
                        if (methodName.equals("getCodigoIdentificacao")) return jpa.getCodigoIdentificacao();
                        if (methodName.equals("getStatus")) return jpa.getStatus();
                        if (methodName.equals("getHistorico")) {
                            return jpa.getHistorico().stream()
                                    .map(ConvenioRepositorioAplicacaoImpl.this::criarHistoricoEntradaResumo)
                                    .collect(Collectors.toList());
                        }
                        if (methodName.equals("toString")) return "ConvenioDetalhes{id=" + jpa.getId() + ", nome=" + jpa.getNome() + "}";
                        if (methodName.equals("equals")) return proxy == args[0];
                        if (methodName.equals("hashCode")) return System.identityHashCode(proxy);
                        throw new UnsupportedOperationException("Método não suportado: " + methodName);
                    }
                }
        );
    }

    private Object criarHistoricoEntradaResumo(HistoricoConvenioJpa historicoJpa) {
        // Usa a interface HistoricoEntradaResumo que está no mesmo arquivo que ConvenioDetalhes
        // Como é package-private, tentamos diferentes abordagens
        Class<?> historicoInterface = null;
        try {
            // Tenta primeiro como classe interna (usando $)
            historicoInterface = Class.forName("br.com.medflow.aplicacao.financeiro.convenios.ConvenioDetalhes$HistoricoEntradaResumo");
        } catch (ClassNotFoundException e1) {
            try {
                // Tenta como classe do mesmo pacote
                historicoInterface = Class.forName("br.com.medflow.aplicacao.financeiro.convenios.HistoricoEntradaResumo");
            } catch (ClassNotFoundException e2) {
                // Última tentativa: busca nas classes internas declaradas
                Class<?>[] classesInternas = ConvenioDetalhes.class.getDeclaredClasses();
                if (classesInternas.length > 0) {
                    // Procura pela interface com o nome correto
                    for (Class<?> classe : classesInternas) {
                        if (classe.getSimpleName().equals("HistoricoEntradaResumo")) {
                            historicoInterface = classe;
                            break;
                        }
                    }
                    // Se não encontrou, usa a primeira
                    if (historicoInterface == null) {
                        historicoInterface = classesInternas[0];
                    }
                } else {
                    throw new IllegalStateException("Interface HistoricoEntradaResumo não encontrada. Verifique se está definida no mesmo arquivo que ConvenioDetalhes.", e2);
                }
            }
        }
        
        return Proxy.newProxyInstance(
                historicoInterface.getClassLoader(),
                new Class[]{historicoInterface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String methodName = method.getName();
                        if (methodName.equals("getAcao")) return historicoJpa.getAcao() != null ? historicoJpa.getAcao().name() : null;
                        if (methodName.equals("getDescricao")) return historicoJpa.getDescricao();
                        if (methodName.equals("getResponsavelNome")) {
                            // Retorna um placeholder baseado no ID do responsável
                            Integer responsavelId = historicoJpa.getResponsavelId();
                            return responsavelId != null ? "Usuário " + responsavelId : "N/A";
                        }
                        if (methodName.equals("getDataHora")) return historicoJpa.getDataHora();
                        if (methodName.equals("toString")) return "HistoricoEntradaResumo{acao=" + historicoJpa.getAcao() + "}";
                        if (methodName.equals("equals")) return proxy == args[0];
                        if (methodName.equals("hashCode")) return System.identityHashCode(proxy);
                        throw new UnsupportedOperationException("Método não suportado: " + methodName);
                    }
                }
        );
    }
}
