package com.littlehorizons.erp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility to generate simulated CUFE (Código Único de Factura Electrónica)
 * according to DIAN electronic invoicing simulation standards.
 */
public class CufeGenerator {

    public static String generateCufe(String invoiceNumber, String nit, String date, double amount) {
        String rawData = String.format("%s|%s|%s|%.2f|LITTLE_HORIZONS_ERP_KEY", invoiceNumber, nit, date, amount);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.error("Failed to generate SHA-256 CUFE", e);
            return "CUFE-SIMULATED-" + Math.abs(rawData.hashCode());
        }
    }
}
