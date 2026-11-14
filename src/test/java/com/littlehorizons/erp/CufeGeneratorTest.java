package com.littlehorizons.erp;

import com.littlehorizons.erp.util.CufeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CufeGeneratorTest {

    @Test
    @DisplayName("Should generate valid 64-character SHA-256 hexadecimal CUFE")
    void testCufeGeneration() {
        String invoiceNumber = "FAC-LH-000001";
        String nit = "901.458.789-3";
        String date = "2024-02-15";
        double amount = 450000.00;

        String cufe = CufeGenerator.generateCufe(invoiceNumber, nit, date, amount);

        assertNotNull(cufe, "CUFE should not be null");
        assertEquals(64, cufe.length(), "SHA-256 CUFE must be exactly 64 hexadecimal characters long");
        assertTrue(cufe.matches("^[a-f0-9]{64}$"), "CUFE must consist solely of lowercase hex characters");
    }

    @Test
    @DisplayName("Different invoice inputs should produce different CUFE hashes")
    void testCufeUniqueness() {
        String cufe1 = CufeGenerator.generateCufe("FAC-LH-000001", "901.458.789-3", "2024-02-15", 450000.00);
        String cufe2 = CufeGenerator.generateCufe("FAC-LH-000002", "901.458.789-3", "2024-02-15", 450000.00);

        assertNotEquals(cufe1, cufe2, "Different invoices must have distinct CUFE signatures");
    }
}
