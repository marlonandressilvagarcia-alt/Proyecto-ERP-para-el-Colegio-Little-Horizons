package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;

/**
 * Console view for financial transactions and electronic invoices (TXT).
 */
public class FinancialView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO FINANCIERO Y FACTURACIÓN ELECTRÓNICA (TXT)");
        System.out.println("  1. Listar catálogo de conceptos de pago y tarifas");
        System.out.println("  2. Registrar pago y emitir Factura Electrónica TXT");
        System.out.println("  3. Consultar historial de pagos por estudiante");
        System.out.println("  4. Consultar historial de pagos por acudiente");
        System.out.println("  5. Listar facturas electrónicas emitidas");
        System.out.println("  6. Generar reporte de estudiantes en mora (Defaulters)");
        System.out.println("  7. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-7): ", 1, 7);
    }

    public void displayConcepts(List<PaymentConcept> concepts) {
        ConsoleFormatter.printSubHeader("CATÁLOGO DE CONCEPTOS DE COBRO");
        System.out.printf("%-4s %-12s %-32s %-15s %-12s%n", "ID", "CÓDIGO", "NOMBRE DEL CONCEPTO", "VALOR BASE", "RECURRENTE");
        ConsoleFormatter.printDivider();
        for (PaymentConcept c : concepts) {
            System.out.printf("%-4d %-12s %-32s $%,14.2f %-12s%n",
                    c.getId(), c.getCode(), c.getName(), c.getBaseAmount(),
                    c.isRecurring() ? "SÍ (Mensual)" : "NO (Único)");
        }
    }

    public void displayPaymentList(List<Payment> payments) {
        ConsoleFormatter.printSubHeader("HISTORIAL DE PAGOS REGISTRADOS (" + payments.size() + " PAGOS)");
        if (payments.isEmpty()) {
            System.out.println("  No hay pagos registrados.");
            return;
        }
        System.out.printf("%-4s %-16s %-10s %-12s %-15s %-15s%n", "ID", "RECIBO", "ESTUDIANTE", "FECHA PAGO", "MONTO PAGADO", "MÉTODO");
        ConsoleFormatter.printDivider();
        for (Payment p : payments) {
            System.out.printf("%-4d %-16s %-10d %-12s $%,14.2f %-15s%n",
                    p.getId(), p.getReceiptNumber(), p.getStudentId(),
                    p.getPaymentDate().toString(), p.getAmountPaid(), p.getPaymentMethod());
        }
    }

    public void displayInvoices(List<Invoice> invoices) {
        ConsoleFormatter.printSubHeader("FACTURAS ELECTRÓNICAS GENERADAS (FORMATO TXT)");
        if (invoices.isEmpty()) {
            System.out.println("  No se han emitido facturas aún.");
            return;
        }
        System.out.printf("%-4s %-16s %-12s %-35s %-25s%n", "ID", "NÚMERO FACTURA", "FECHA", "ARCHIVO TXT", "RUTA");
        ConsoleFormatter.printDivider();
        for (Invoice inv : invoices) {
            System.out.printf("%-4d %-16s %-12s %-35s %-25s%n",
                    inv.getId(), inv.getInvoiceNumber(), inv.getIssueDate().toString(),
                    inv.getTxtFileName(), inv.getFilePath());
        }
    }

    public void displayInvoiceGeneratedSuccess(Invoice invoice) {
        ConsoleFormatter.printSuccess("=================================================================");
        ConsoleFormatter.printSuccess("¡PAGO CONFIRMADO Y FACTURA ELECTRÓNICA TXT GENERADA CON ÉXITO!");
        System.out.println("  Factura Número:   " + invoice.getInvoiceNumber());
        System.out.println("  Archivo TXT:      " + invoice.getTxtFileName());
        System.out.println("  Ruta del Archivo: " + invoice.getFilePath());
        System.out.println("  CUFE (Simulado):  " + invoice.getCufe());
        ConsoleFormatter.printSuccess("=================================================================");
    }
}
