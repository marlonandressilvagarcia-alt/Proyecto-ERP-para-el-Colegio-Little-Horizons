package com.littlehorizons.erp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates visual Electronic Invoices in TXT format according to Colombian educational regulations (DIAN simulation).
 */
public class TextInvoiceGenerator {

    private static final String INVOICE_DIR = "invoices";

    static {
        File dir = new File(INVOICE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String generateInvoiceTxt(
            String invoiceNumber,
            String schoolName,
            String schoolNit,
            String schoolAddress,
            String schoolPhone,
            String schoolEmail,
            String dianResolution,
            String guardianName,
            String guardianDoc,
            String guardianAddress,
            String studentName,
            String conceptDescription,
            double amountPaid,
            String paymentMethod,
            String receiptNumber,
            String cufe,
            LocalDate issueDate
    ) {
        String fileName = "factura_" + invoiceNumber.replace("/", "_").replace(" ", "_") + ".txt";
        File file = new File(INVOICE_DIR, fileName);

        double subtotal = amountPaid;
        double vat = 0.00; // Educational services in Colombia are generally VAT exempt (Art. 476 E.T.)
        double total = subtotal + vat;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = issueDate.format(dtf);

        try (FileWriter fw = new FileWriter(file, false);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("================================================================================");
            pw.printf("                    %s%n", schoolName.toUpperCase());
            pw.printf("                              NIT: %s%n", schoolNit);
            pw.printf("              Dirección: %s%n", schoolAddress);
            pw.printf("                 Tel: %s | Email: %s%n", schoolPhone, schoolEmail);
            pw.println("================================================================================");
            pw.println("                   FACTURA ELECTRÓNICA DE VENTA (TXT)");
            pw.printf("Factura No.:       %-30s Fecha Emisión: %s%n", invoiceNumber, formattedDate);
            pw.printf("Resolución DIAN:   No. %s de Habilitación para Factura Electrónica%n", dianResolution);
            pw.printf("Prefijo Autorizado: FAC-LH  |  Rango: 000001 al 100000%n");
            pw.println("--------------------------------------------------------------------------------");
            pw.println("DATOS DEL ADQUIRIENTE (ACUDIENTE / RESPONSABLE):");
            pw.printf("Nombre:            %s%n", guardianName);
            pw.printf("Identificación:    CC/Doc %s%n", guardianDoc);
            pw.printf("Dirección:         %s%n", guardianAddress != null ? guardianAddress : "No registrada");
            pw.printf("Estudiante:        %s%n", studentName);
            pw.println("--------------------------------------------------------------------------------");
            pw.println("DETALLE DE CONCEPTOS FACTURADOS:");
            pw.printf("%-6s %-42s %-6s %-12s %-10s%n", "ÍTEM", "DESCRIPCIÓN DEL CONCEPTO", "CANT", "VR. UNIT", "SUBTOTAL");
            pw.println("--------------------------------------------------------------------------------");
            pw.printf("%-6s %-42s %-6d $%-11.2f $%-9.2f%n", "01", conceptDescription, 1, amountPaid, amountPaid);
            pw.println("--------------------------------------------------------------------------------");
            pw.printf("%56s: $%,12.2f%n", "SUBTOTAL EXENTO DE IVA (ART. 476 E.T.)", subtotal);
            pw.printf("%56s: $%,12.2f%n", "IVA (0%)", vat);
            pw.printf("%56s: $%,12.2f%n", "VALOR TOTAL A PAGAR", total);
            pw.println("================================================================================");
            pw.println("INFORMACIÓN TRANSACCIONAL:");
            pw.printf("Método de Pago:    %-25s Recibo de Caja: %s%n", paymentMethod, receiptNumber);
            pw.println("--------------------------------------------------------------------------------");
            pw.println("CÓDIGO ÚNICO DE FACTURA ELECTRÓNICA (CUFE):");
            pw.println(cufe);
            pw.println("--------------------------------------------------------------------------------");
            pw.println("REPRESENTACIÓN GRÁFICA DEL CÓDIGO QR DE VALIDACIÓN DIAN:");
            pw.println("+------------------------------------------------------------------------------+");
            pw.println("| [QR CODE SIMULADO - LECTURA DIAN]                                            |");
            pw.printf("| NumFac:%-14s | FecFac:%-10s | NitFac:%-15s   |%n", invoiceNumber, formattedDate, schoolNit);
            pw.printf("| DocAdq:%-14s | ValFac:%-10.2f | ValIva:0.00                              |%n", guardianDoc, total);
            pw.printf("| CUFE: %-70s |%n", cufe.substring(0, Math.min(70, cufe.length())));
            pw.println("| https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + cufe.substring(0, 16) + "... |");
            pw.println("+------------------------------------------------------------------------------+");
            pw.println("Esta factura electrónica es un título valor simulado según las disposiciones");
            pw.println("legales vigentes del Decreto 2242 y la Resolución 000042 de la DIAN.");
            pw.println("Generado exitosamente por: Little Horizons ERP System (Módulo de Facturación)");
            pw.println("================================================================================");

            LoggerUtil.info("Generated TXT invoice file: " + file.getAbsolutePath());
            return file.getPath();
        } catch (IOException e) {
            LoggerUtil.error("Failed to generate TXT invoice: " + fileName, e);
            return null;
        }
    }
}
