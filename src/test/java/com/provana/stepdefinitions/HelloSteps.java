package com.provana.stepdefinitions;

import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloSteps {

    private static final Logger logger = LogManager.getLogger(HelloSteps.class);

    @Given("I have a working Cucumber JVM setup")
    public void i_have_a_working_cucumber_jvm_setup() {
        logger.info("Verified working Cucumber JVM setup.");
    }

    @When("I run my first step")
    public void i_run_my_first_step() {
        logger.info("Running first step.");
    }

    @Then("I should see Hello Cucumber in the output")
    public void i_should_see_hello_cucumber_in_the_output() {
        logger.info("Hello Cucumber");
        System.out.println("Hello Cucumber");
    }
}