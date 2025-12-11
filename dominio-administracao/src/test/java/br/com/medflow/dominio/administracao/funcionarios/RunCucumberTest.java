package br.com.medflow.dominio.administracao.funcionarios;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
// Seleciona a pasta onde estão as classes de Steps e o arquivo .feature
@SelectPackages("br.com.medflow.dominio.administracao.funcionarios")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.medflow.dominio.administracao.funcionarios")
public class RunCucumberTest {
}