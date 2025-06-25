package com.provana.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public final class ExtentManager {

    private static final Path OUT_DIR = Path.of("target", "test-output");
    private static ExtentReports extent;

    public static synchronized ExtentReports getExtentReports() {
        if (extent != null)
            return extent;

        // Ensure output directory exists
        try {
            Files.createDirectories(OUT_DIR);
        } catch (Exception ignored) {}

        // Cleanup old reports if enabled
        if (isReportCleanupEnabled()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(OUT_DIR, "*.html")) {
                for (Path entry : stream) {
                    Files.deleteIfExists(entry);
                }
            } catch (Exception e) {
                System.err.println("Failed to clean old reports: " + e.getMessage());
            }
        }

        // Unique file name
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ssa").format(new Date());
        String reportFile = OUT_DIR.resolve("ExtentReport_" + timestamp + ".html").toString();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setTheme(Theme.STANDARD); // or Theme.DARK
        spark.config().setDocumentTitle("Provana Testing Team");
        spark.config().setReportName("Provana Automation Report");
        spark.config().setEncoding("utf-8");
        spark.config().setTimelineEnabled(true);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        return extent;
    }

    // Screenshot toggle
    public static boolean isScreenshotsEnabled() {
        return Boolean.parseBoolean(DriverFactory.getProperties().getProperty("screenshots.enabled", "true"));
    }

    // Report cleanup toggle
    private static boolean isReportCleanupEnabled() {
        return Boolean.parseBoolean(DriverFactory.getProperties().getProperty("reports.cleanup.enabled", "true"));
    }

    private ExtentManager() {
        // prevent instantiation
    }
}
