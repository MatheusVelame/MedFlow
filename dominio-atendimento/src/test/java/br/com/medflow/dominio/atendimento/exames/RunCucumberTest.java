package br.com.medflow.dominio.atendimento.exames;

import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
// Esta linha indica ao Cucumber onde procurar as classes de testes (.feature e Step Definitions)
@SelectPackages("br.com.medflow.dominio.atendimento.exames") 
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {
}