package br.com.medflow.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// Imports dos Value Objects do domínio Medicamento
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.UsuarioResponsavelId;

// Imports dos Value Objects do domínio TipoExame
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;

@Component
public class BackendMapeador extends ModelMapper {

    public BackendMapeador() {
        
        // ===== CONVERSORES PARA MEDICAMENTO =====
        
        // Converte Integer (vindo do Formulário) para o Value Object MedicamentoId
        addConverter(new AbstractConverter<Integer, MedicamentoId>() {
            @Override
            protected MedicamentoId convert(Integer source) {
                return new MedicamentoId(source); 
            }
        });

        // Converte Integer (vindo do Formulário) para o Value Object UsuarioResponsavelId (Medicamento)
        addConverter(new AbstractConverter<Integer, UsuarioResponsavelId>() {
            @Override
            protected UsuarioResponsavelId convert(Integer source) {
                return new UsuarioResponsavelId(source);
            }
        });
        
        // ===== CONVERSORES PARA TIPO EXAME =====
        
        // Converte Integer para TipoExameId
        addConverter(new AbstractConverter<Integer, TipoExameId>() {
            @Override
            protected TipoExameId convert(Integer source) {
                return new TipoExameId(source);
            }
        });

        // Converte Integer para UsuarioResponsavelId (TipoExame)
        addConverter(new AbstractConverter<Integer, br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId>() {
            @Override
            protected br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId convert(Integer source) {
                return new br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId(source);
            }
        });
    }

    // Sobrescrita do método map para lidar com fontes nulas (padrão SGB)
    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return source != null ? super.map(source, destinationType) : null;
    }
}