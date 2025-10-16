package com.medflow.bdd.runner;

import io.cucumber.junit.platform.engine.Cucumber;

@Cucumber
@io.cucumber.junit.platform.engine.CucumberOptions(
    features = "src/test/resources/features",  // todas as .feature do módulo
    glue = "com.medflow.bdd.steps"             // package das classes de Steps
)
public class RunCucumberTest {
    // Nenhum código adicional é necessário - a anotação @Cucumber detecta todas as features
}
