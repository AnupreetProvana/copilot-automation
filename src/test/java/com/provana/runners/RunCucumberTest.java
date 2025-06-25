package com.provana.runners;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;
import com.provana.utils.ConfigReader;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/provana")

@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.provana.stepdefinitions,com.provana.hooks"
)

@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = """
                pretty,
                html:target/cucumber-report.html,
                json:target/cucumber-report.json,
                junit:target/cucumber-report.xml,
                rerun:target/rerun.txt
                """
)

// Dynamically inject tags only if they are present
public class RunCucumberTest {
    static {
        String tags = ConfigReader.get("cucumber.tags");
        if (!tags.isEmpty()) {
            System.setProperty(FILTER_TAGS_PROPERTY_NAME, tags);
        }
    }
}
