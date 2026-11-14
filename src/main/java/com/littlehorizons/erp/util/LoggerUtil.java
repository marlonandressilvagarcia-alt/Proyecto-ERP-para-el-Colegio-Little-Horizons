package com.littlehorizons.erp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe logging utility for Little Horizons ERP.
 * Persists error and audit messages to 'logs/error_log.txt' and 'error_log.txt'.
 */
public class LoggerUtil {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "logs/error_log.txt";
    private static final String ROOT_LOG_FILE = "error_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized void info(String message) {
        log("INFO", message, null);
    }

    public static synchronized void warn(String message) {
        log("WARN", message, null);
    }

    public static synchronized void error(String message) {
        log("ERROR", message, null);
    }

    public static synchronized void error(String message, Throwable throwable) {
        log("ERROR", message, throwable);
    }

    private static void log(String level, String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Write to both log locations
        writeLog(LOG_FILE, formattedMessage, throwable);
        writeLog(ROOT_LOG_FILE, formattedMessage, throwable);

        if ("ERROR".equalsIgnoreCase(level)) {
            System.err.println("[LOG-" + level + "] " + message);
            if (throwable != null) {
                System.err.println("  Cause: " + throwable.getMessage());
            }
        }
    }

    private static void writeLog(String filePath, String formattedMessage, Throwable throwable) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(formattedMessage);
            if (throwable != null) {
                throwable.printStackTrace(pw);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + filePath + " (" + e.getMessage() + ")");
        }
    }
}
