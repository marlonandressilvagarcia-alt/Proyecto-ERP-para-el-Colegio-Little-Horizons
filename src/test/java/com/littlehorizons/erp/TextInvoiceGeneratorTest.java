package com.littlehorizons.erp;

import com.littlehorizons.erp.util.CufeGenerator;
import com.littlehorizons.erp.util.TextInvoiceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TextInvoiceGeneratorTest {

    @Test
    @DisplayName("Should generate formatted TXT electronic invoice file on disk")
    void testTxtInvoiceGeneration() throws IOException {
        String invoiceNumber = "FAC-LH-TEST01";
        String schoolName = "Colegio Internacional Little Horizons S.A.S.";
        String schoolNit = "901.458.789-3";
        String schoolAddress = "Calle 127 # 15-45, Bogotá";
        String schoolPhone = "(+57) 601 745 8900";
        String schoolEmail = "contacto@littlehorizons.edu.co";
        String dianResolution = "18760000001";
        String guardianName = "Carlos Eduardo Gómez";
        String guardianDoc = "52123401";
        String guardianAddress = "Calle 134 # 19-45, Bogotá";
        String studentName = "Santiago Andrés Gómez";
        String concept = "Pensión Mensual Febrero 2024";
        double amount = 450000.00;
        String paymentMethod = "Transferencia";
        String receiptNumber = "REC-2024-TEST01";
        LocalDate issueDate = LocalDate.of(2024, 2, 15);
        String cufe = CufeGenerator.generateCufe(invoiceNumber, schoolNit, issueDate.toString(), amount);

        String filePath = TextInvoiceGenerator.generateInvoiceTxt(
                invoiceNumber, schoolName, schoolNit, schoolAddress, schoolPhone, schoolEmail,
                dianResolution, guardianName, guardianDoc, guardianAddress, studentName,
                concept, amount, paymentMethod, receiptNumber, cufe, issueDate
        );

        assertNotNull(filePath, "Generated invoice file path should not be null");
        assertTrue(filePath.endsWith(".txt"), "Invoice file must have .txt extension as requested");

        File file = new File(filePath);
        assertTrue(file.exists(), "Invoice file must physically exist on filesystem");
        assertTrue(file.length() > 0, "Invoice file must not be empty");

        String content = Files.readString(Path.of(filePath));
        assertTrue(content.contains(invoiceNumber), "Content must include invoice number");
        assertTrue(content.contains(schoolNit), "Content must include school NIT");
        assertTrue(content.contains(guardianName), "Content must include guardian name");
        assertTrue(content.contains(studentName), "Content must include student name");
        assertTrue(content.contains(cufe), "Content must include simulated CUFE");
        assertTrue(content.contains("[QR CODE SIMULADO - LECTURA DIAN]"), "Content must include mock QR representation");
    }
}
