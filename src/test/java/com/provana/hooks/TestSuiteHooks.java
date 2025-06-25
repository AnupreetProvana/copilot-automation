package com.provana.hooks;

import com.provana.utils.ExtentManager;
import com.provana.utils.DriverFactory;
import io.cucumber.java.AfterAll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSuiteHooks {

    private static final Logger LOG = LogManager.getLogger(TestSuiteHooks.class);

    @AfterAll
    public static void flushReportAndQuit() {

        try {
            ExtentManager.getExtentReports().flush();
            LOG.info("Extent Report flushed successfully.");
        } catch (Exception e) {
            LOG.warn("Extent Report was not initialised – nothing to flush.");
        }

        DriverFactory.quitDriver();
        LOG.info("Driver quit (suite-level).");
    }
}
