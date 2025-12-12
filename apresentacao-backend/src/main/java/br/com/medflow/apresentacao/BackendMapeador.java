package br.com.medflow.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// Imports dos Value Objects do seu domínio
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.UsuarioResponsavelId;

// Imports de Value Objects de funcionários
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
// Não importar UsuarioResponsavelId de funcionários para evitar conflito - usar nome completo

// [Outros imports de Value Objects de outros domínios]

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

        // Converte Integer (vindo do Formulário) para o Value Object UsuarioResponsavelId (medicamentos)
		addConverter(new AbstractConverter<Integer, UsuarioResponsavelId>() {
			@Override
			protected UsuarioResponsavelId convert(Integer source) {
				return new UsuarioResponsavelId(source);
			}
		});
        
        // Converte Integer (vindo do Formulário) para o Value Object FuncionarioId
		addConverter(new AbstractConverter<Integer, FuncionarioId>() {
			@Override
			protected FuncionarioId convert(Integer source) {
				return new FuncionarioId(source); 
			}
		});

        // Converte Integer (vindo do Formulário) para o Value Object UsuarioResponsavelId (funcionários)
		addConverter(new AbstractConverter<Integer, br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId>() {
			@Override
			protected br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId convert(Integer source) {
				return new br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId(source);
			}
		});
        
        // [Outros conversores para Value Objects de outros domínios]
	}

    // Sobrescrita do método map para lidar com fontes nulas (padrão SGB)
	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}