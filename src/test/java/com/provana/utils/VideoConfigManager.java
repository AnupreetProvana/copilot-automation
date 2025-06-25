package com.provana.utils;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;

public final class VideoConfigManager {

    private static boolean recordingEnabled = true;
    private static boolean cleanupEnabled = true;
    private static boolean cleanedOnce = false;

    public static final Path VIDEO_DIR = Paths.get("target", "test-output", "videos").toAbsolutePath();

    static {
        try {
            Properties p = new Properties();
            InputStream input = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("com/provana/config/config.properties");
            if (input != null) {
                p.load(input);
            } else {
                System.out.println("[VideoConfig] config.properties not found on classpath!");
            }

            recordingEnabled = Boolean.parseBoolean(p.getProperty("video.recording.enabled", "true"));
            cleanupEnabled   = Boolean.parseBoolean(p.getProperty("video.cleanup.enabled", "true"));

            recordingEnabled = Boolean.parseBoolean(System.getProperty(
                    "video.recording.enabled", String.valueOf(recordingEnabled)));
            cleanupEnabled = Boolean.parseBoolean(System.getProperty(
                    "video.cleanup.enabled", String.valueOf(cleanupEnabled)));

            // debug
            System.out.println("[DEBUG] recordingEnabled = " + recordingEnabled);
            System.out.println("[DEBUG] cleanupEnabled   = " + cleanupEnabled);
            System.out.println("[DEBUG] config resource URL = " +
                VideoConfigManager.class.getClassLoader()
                    .getResource("com/provana/config/config.properties"));

        } catch (Exception e) {
            System.out.println("[VideoConfig] Could not load flags: " + e.getMessage());
        }
    }

    public static boolean isRecordingEnabled() { return recordingEnabled; }
    public static boolean isCleanupEnabled()   { return cleanupEnabled; }

    public static synchronized void cleanupOldVideos() {
        if (!cleanupEnabled || cleanedOnce) return;
        try {
            if (Files.exists(VIDEO_DIR)) {
                Files.list(VIDEO_DIR)
                     .filter(p -> p.toString().endsWith(".mp4") || p.toString().endsWith(".avi"))
                     .forEach(p -> { try { Files.deleteIfExists(p);} catch (Exception ignored){}});
                System.out.println("[VideoConfig] Old videos cleaned.");
            }
        } catch (Exception e) {
            System.out.println("[VideoConfig] Cleanup error: " + e.getMessage());
        }
        cleanedOnce = true;
    }

    private VideoConfigManager() {}
}