package br.com.medflow.infraestrutura.persistencia.jpa;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaMapeador extends ModelMapper {

	public JpaMapeador() {
		super();
	}
}