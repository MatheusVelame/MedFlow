package br.com.medflow.dominio.referencia.tiposExames.test;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/tipos_exame.feature") // Aponta diretamente para o arquivo específico
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.medflow.dominio.referencia.tiposExames.test")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTiposExamesTest {
    // Esta classe não precisa de implementação, serve apenas como configuração do Cucumber
    
    // Adicione este bloco para forçar a limpeza de propriedades
    static {
        System.clearProperty("cucumber.features");
        System.clearProperty("cucumber.glue");
    }
}