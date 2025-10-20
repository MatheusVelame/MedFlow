package com.medflow.dominio.prontuario;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/medflow/dominio/prontuario")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/com/medflow/dominio/prontuario")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.medflow.dominio.prontuario")
public class RunCucumberTest {
}
