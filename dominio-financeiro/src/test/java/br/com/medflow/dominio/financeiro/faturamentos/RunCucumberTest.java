package br.com.medflow.dominio.financeiro.faturamentos;

import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
// Esta linha indica ao Cucumber onde procurar as classes de testes (.feature e Step Definitions)
@SelectPackages("br.com.medflow.dominio.financeiro.faturamentos") 
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {
}
