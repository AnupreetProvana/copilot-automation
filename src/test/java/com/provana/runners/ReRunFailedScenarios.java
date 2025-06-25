package com.provana.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Executes only the scenarios listed in target/rerun.txt (i.e., those that
 * failed in the previous run). If the file is missing or empty, JUnit will
 * simply report “No tests discovered” – which is expected.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/provana") // feature root
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.provana.stepdefinitions,com.provana.hooks"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty," +
                " html:target/rerun-cucumber-report.html," +
                " json:target/rerun-cucumber-report.json"
)
// ✅ Use hardcoded string for rerun filter key (official property)
@ConfigurationParameter(
        key = "cucumber.filter.rerun",
        value = "@target/rerun.txt"
)
public class ReRunFailedScenarios {
    // nothing else needed – annotations drive execution
}
