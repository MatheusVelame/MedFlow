package br.com.medflow.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// Imports dos Value Objects do seu domínio
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.UsuarioResponsavelId;

// Imports de Value Objects de Convenios
import br.com.medflow.dominio.financeiro.convenios.ConvenioId;
//import br.com.medflow.dominio.financeiro.convenios.UsuarioResponsavelId;

@Component
public class BackendMapeador extends ModelMapper {

	public BackendMapeador() {
        
        // Converte Integer (vindo do Formulário) para o Value Object MedicamentoId
		addConverter(new AbstractConverter<Integer, MedicamentoId>() {
			@Override
			protected MedicamentoId convert(Integer source) {
				return new MedicamentoId(source); 
			}
		});

        // Converte Integer (vindo do Formulário) para o Value Object UsuarioResponsavelId
		addConverter(new AbstractConverter<Integer, UsuarioResponsavelId>() {
			@Override
			protected UsuarioResponsavelId convert(Integer source) {
				return new UsuarioResponsavelId(source);
			}
		});

		// Converte Integer (vindo do Formulário) para o Value Object ConvenioId
		addConverter(new AbstractConverter<Integer, ConvenioId>() {
			@Override
			protected ConvenioId convert(Integer source) {
				return new ConvenioId(source);
			}
		});
	}

    // Sobrescrita do método map para lidar com fontes nulas (padrão SGB)
	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}