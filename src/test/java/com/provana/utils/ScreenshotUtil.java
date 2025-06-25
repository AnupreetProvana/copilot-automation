package com.provana.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

public class ScreenshotUtil {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);
    private static final Properties cfg = DriverFactory.getProperties();

    /**
     * Captures screenshot and returns as Base64 string.
     * Respects the 'screenshots.enabled' toggle from config.properties.
     */
    public static String captureScreenshotBase64(WebDriver driver) {
        if (!Boolean.parseBoolean(cfg.getProperty("screenshots.enabled", "true"))) {
            logger.info("Screenshot capture is disabled via config.");
            return "";
        }

        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Generates a clean and timestamped screenshot file name.
     */
    public static String generateScreenshotName(String scenarioName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String cleanName = scenarioName.replaceAll("[^a-zA-Z0-9-_]", "_");
        return cleanName + "_" + timestamp + ".png";
    }

    private ScreenshotUtil() {
        // utility class, prevent instantiation
    }
}
