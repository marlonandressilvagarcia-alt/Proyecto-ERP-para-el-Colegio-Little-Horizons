package com.littlehorizons.erp;

import com.littlehorizons.erp.controller.MainController;
import com.littlehorizons.erp.util.LoggerUtil;

/**
 * Little Horizons ERP - Main Application Entry Point.
 * Centralized Enterprise Resource Planning System for Little Horizons International School.
 *
 * Architecture: Model-View-Controller (MVC) + DAO + Service layers
 * Language: Java 21 Standard Edition
 * Database: MySQL 8.0+
 */
public class Main {

    public static void main(String[] args) {
        LoggerUtil.info("Little Horizons ERP Application starting up...");
        try {
            MainController app = new MainController();
            app.start();
        } catch (Exception e) {
            LoggerUtil.error("Fatal uncaught exception in ERP main loop", e);
            System.err.println("Unrecoverable error occurred. Please consult error_log.txt for details.");
        } finally {
            LoggerUtil.info("Little Horizons ERP Application shut down cleanly.");
        }
    }
}
