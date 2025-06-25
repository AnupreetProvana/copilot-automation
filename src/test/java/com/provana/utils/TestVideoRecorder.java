package com.provana.utils;

import com.automation.remarks.video.recorder.VideoRecorder;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public final class TestVideoRecorder {

    private static final ThreadLocal<VideoRecorder> RECORDER = new ThreadLocal<>();
    private static final ThreadLocal<String> UNIQUE_ID = new ThreadLocal<>();

    public static void startRecording(String rawName) {
        if (!VideoConfigManager.isRecordingEnabled()) return;
        try {
            Files.createDirectories(VideoConfigManager.VIDEO_DIR);
            String base = rawName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String uid = UUID.randomUUID().toString().substring(0,6);
            String unique = String.join("_", base, stamp, uid);
            UNIQUE_ID.set(unique);

            System.setProperty("video.folder", VideoConfigManager.VIDEO_DIR.toString());
            System.setProperty("video.enabled", "true");

            VideoRecorder vr = new VideoRecorder(unique);
            vr.start();
            RECORDER.set(vr);
            System.out.println("[VideoRecorder] ▶ start: " + unique);
        } catch (Exception e) {
            System.out.println("[VideoRecorder] Cannot start: " + e.getMessage());
        }
    }

    public static void stopRecording() {
        VideoRecorder vr = RECORDER.get();
        String name = UNIQUE_ID.get();
        if (vr == null) return;
        try {
            vr.stop();
            System.out.println("[VideoRecorder] ■ stop : " + name);
        } catch (Exception e) {
            System.out.println("[VideoRecorder] Stop error: " + e.getMessage());
        } finally {
            RECORDER.remove();
            UNIQUE_ID.remove();
        }
    }

    private TestVideoRecorder() {}
}