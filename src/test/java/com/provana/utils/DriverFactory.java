package com.provana.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    private static WebDriver driver;
    private static Properties properties;

    private static final Path DOWNLOAD_DIR =
            Paths.get("target", "test-output", "downloads").toAbsolutePath();
    private static boolean downloadDirCleaned = false;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DriverFactory::quitDriver));
    }

    public static synchronized WebDriver getDriver() {
        if (driver != null) return driver;

        logger.info("Initializing WebDriver...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-save-password-bubble", "--disable-infobars");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("safebrowsing.enabled", false);

        boolean headless = Boolean.parseBoolean(cfg().getProperty("browser.headless", "false"));
        if (headless) {
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
        }

        if (Boolean.parseBoolean(cfg().getProperty("downloads.enabled", "true"))) {
            prepareDownloadDirectory();
            prefs.put("download.default_directory", DOWNLOAD_DIR.toString());
            prefs.put("download.prompt_for_download", false);
            prefs.put("download.directory_upgrade", true);
        }

        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        logger.info("WebDriver initialized successfully.");
        return driver;
    }

    private static void prepareDownloadDirectory() {
        boolean cleanup = Boolean.parseBoolean(cfg().getProperty("downloads.cleanup.enabled", "true"));
        try {
            Files.createDirectories(DOWNLOAD_DIR);
            if (cleanup && !downloadDirCleaned) {
                Files.list(DOWNLOAD_DIR).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                });
                downloadDirCleaned = true;
                logger.info("Download directory cleaned.");
            }
        } catch (Exception e) {
            logger.warn("Cannot prepare download folder: {}", e.getMessage());
        }
    }

    private static Properties cfg() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream in = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("com/provana/config/config.properties")) {
                if (in != null) {
                    properties.load(in);
                    logger.info("config.properties loaded successfully from path: com/provana/config/config.properties");
                } else {
                    logger.warn("config.properties not found on classpath!");
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not load config.properties", e);
            }
        }
        return properties;
    }

    public static synchronized void quitDriver() {
        if (driver != null) {
            logger.info("Quitting WebDriver …");
            driver.quit();
            driver = null;
        }
    }

    public static Properties getProperties() {
        return cfg();
    }

    private DriverFactory() {}
}