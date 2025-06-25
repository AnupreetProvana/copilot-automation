package com.provana.hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.provana.utils.*;

import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class Hooks {

    /* ───────────── constants & state ───────────── */
    private static final Logger     LOG = LogManager.getLogger(Hooks.class);
    private static final Properties CFG = DriverFactory.getProperties();

    private static String  quitMode      = "perScenario";
    private static String  featureSeen   = "";
    private static boolean videosWiped   = false;

    /** one single ExtentReports instance for the whole JVM */
    private static ExtentReports extent;

    /* ───────── static init – create Extent once ───────── */
    static {
        extent = ExtentManager.getExtentReports();   // may throw
    }

    /* ───────────── @BeforeAll ───────────── */
    @BeforeAll
    public static void beforeAll() {
        quitMode = CFG.getProperty("driver.quit.mode", "perScenario");
        LOG.info("driver.quit.mode = {}", quitMode);

        if (!videosWiped && VideoConfigManager.isCleanupEnabled()) {
            VideoConfigManager.cleanupOldVideos();
            videosWiped = true;
        }
    }

    /* ───────────── @Before (each Scenario) ───────────── */
    @Before
    public void setUp(Scenario scenario) {

        String currentFeature = extractFeatureName(scenario.getUri().toString());

        if ("perFeature".equalsIgnoreCase(quitMode) && !currentFeature.equals(featureSeen)) {
            LOG.info("New feature detected – restarting driver ({} → {})", featureSeen, currentFeature);
            DriverFactory.quitDriver();
            featureSeen = currentFeature;
        }

        DriverFactory.getDriver();   // idempotent

        if (VideoConfigManager.isRecordingEnabled()) {
            TestVideoRecorder.startRecording(scenario.getName());
        }

        if (extent != null) {                       // only if Extent is alive
            ExtentTest node = extent.createTest(scenario.getName());
            ExtentTestManager.setTest(node);
        }
    }

    /* ───────────── @After (each Scenario) ───────────── */
    @After
    public void tearDown(Scenario scenario) {

        TestVideoRecorder.stopRecording();

        ExtentTest node = ExtentTestManager.getTest();
        if (node != null) {                         // null if Extent disabled
            if (scenario.isFailed()) {
                LOG.error("Scenario failed: {}", scenario.getName());
                if (ExtentManager.isScreenshotsEnabled()) {
                    String base64 = ScreenshotUtil.captureScreenshotBase64(DriverFactory.getDriver());
                    node.fail("Scenario failed",
                              MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                } else {
                    node.fail("Scenario failed (screenshots disabled)");
                }
            } else {
                node.pass("Scenario passed");
            }
        }

        boolean isolated = scenario.getSourceTagNames().contains("@isolated");
        if (isolated || "perScenario".equalsIgnoreCase(quitMode)) {
            LOG.info("Closing driver ({}). Scenario: {}", isolated ? "@isolated" : quitMode, scenario.getName());
            DriverFactory.quitDriver();
        }

        ExtentTestManager.removeTest();
    }

    /* ───────────── helpers ───────────── */
    private static String extractFeatureName(String uri) {
        if (uri == null) return "";
        int idx = uri.lastIndexOf('/');
        return (idx != -1 && idx < uri.length() - 1) ? uri.substring(idx + 1) : uri;
    }
}
