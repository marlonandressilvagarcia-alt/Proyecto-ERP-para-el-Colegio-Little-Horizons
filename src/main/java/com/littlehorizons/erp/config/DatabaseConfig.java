package com.littlehorizons.erp.config;

import com.littlehorizons.erp.util.LoggerUtil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton configuration and connection manager for MySQL database.
 */
public class DatabaseConfig {

    private static DatabaseConfig instance;
    private final Properties properties = new Properties();

    private DatabaseConfig() {
        loadProperties();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                properties.load(is);
                LoggerUtil.info("Database properties loaded successfully from db.properties.");
            } else {
                LoggerUtil.warn("db.properties file not found on classpath. Loading default settings.");
                setDefaultProperties();
            }
        } catch (Exception e) {
            LoggerUtil.error("Error reading db.properties. Falling back to defaults.", e);
            setDefaultProperties();
        }

        // Register MySQL JDBC Driver
        try {
            Class.forName(getDriver());
        } catch (ClassNotFoundException e) {
            LoggerUtil.error("MySQL JDBC Driver not found in classpath.", e);
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/erp_little_horizons?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("school.name", "Colegio Internacional Little Horizons S.A.S.");
        properties.setProperty("school.nit", "901.458.789-3");
        properties.setProperty("school.address", "Calle 127 # 15-45, Bogotá, Colombia");
        properties.setProperty("school.phone", "(+57) 601 745 8900");
        properties.setProperty("school.email", "contacto@littlehorizons.edu.co");
        properties.setProperty("school.dian.resolution", "18760000001");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LoggerUtil.warn("Database connection check failed: " + e.getMessage());
            return false;
        }
    }

    public String getUrl() {
        return properties.getProperty("db.url", "jdbc:mysql://localhost:3306/erp_little_horizons");
    }

    public String getUser() {
        return properties.getProperty("db.user", "root");
    }

    public String getPassword() {
        return properties.getProperty("db.password", "");
    }

    public String getDriver() {
        return properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public String getSchoolName() {
        return properties.getProperty("school.name", "Colegio Internacional Little Horizons S.A.S.");
    }

    public String getSchoolNit() {
        return properties.getProperty("school.nit", "901.458.789-3");
    }

    public String getSchoolAddress() {
        return properties.getProperty("school.address", "Calle 127 # 15-45, Bogotá, Colombia");
    }

    public String getSchoolPhone() {
        return properties.getProperty("school.phone", "(+57) 601 745 8900");
    }

    public String getSchoolEmail() {
        return properties.getProperty("school.email", "contacto@littlehorizons.edu.co");
    }

    public String getDianResolution() {
        return properties.getProperty("school.dian.resolution", "18760000001");
    }
}
