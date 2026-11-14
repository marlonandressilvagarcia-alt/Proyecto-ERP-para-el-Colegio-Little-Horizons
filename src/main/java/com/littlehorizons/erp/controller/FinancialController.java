package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.service.FinancialService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.FinancialView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FinancialController {

    private final FinancialService service;
    private final FinancialView view;

    public FinancialController(FinancialService service, FinancialView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = view.displayMenu();
            switch (option) {
                case 1 -> listConcepts();
                case 2 -> processPaymentAndInvoice();
                case 3 -> paymentsByStudent();
                case 4 -> paymentsByGuardian();
                case 5 -> listInvoices();
                case 6 -> reportDefaulters();
                case 7 -> back = true;
            }
        }
    }

    private void listConcepts() {
        List<PaymentConcept> concepts = service.getAllConcepts();
        view.displayConcepts(concepts);
        InputValidator.pressEnterToContinue();
    }

    private void processPaymentAndInvoice() {
        ConsoleFormatter.printSubHeader("CAJA Y GENERACIÓN DE FACTURA ELECTRÓNICA TXT");
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int guardianInput = InputValidator.readInt("ID del Acudiente pagador (0 si no aplica): ", 0, 999999);
        Integer guardianId = (guardianInput > 0) ? guardianInput : null;

        listConcepts();
        int conceptId = InputValidator.readInt("Seleccione ID del Concepto a pagar: ", 1, 999999);
        Optional<PaymentConcept> conceptOpt = service.getConceptById(conceptId);
        double defaultAmount = conceptOpt.map(PaymentConcept::getBaseAmount).orElse(100000.0);

        System.out.printf("Monto sugerido según catálogo: $%,.2f COP%n", defaultAmount);
        double amountPaid = InputValidator.readDouble("Monto real a pagar (COP): ", 1000.0, 50000000.0);
        LocalDate paymentDate = InputValidator.readDate("Fecha del pago (YYYY-MM-DD)", true);
        String paymentMethod = InputValidator.readOption("Método de pago (Efectivo, Transferencia, Tarjeta Débito, Tarjeta Crédito, Cheque): ",
                new String[]{"Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito", "Cheque"});
        int year = InputValidator.readInt("Año Académico (ej: 2024): ", 2020, 2030);
        String period = InputValidator.readOption("Período (I, II, III, IV, Anual): ", new String[]{"I", "II", "III", "IV", "Anual"});
        String notes = InputValidator.readString("Notas adicionales: ", false);

        Optional<Invoice> invoiceOpt = service.recordPaymentAndGenerateInvoice(
                studentId, guardianId, conceptId, amountPaid, paymentDate, paymentMethod, year, period, notes
        );

        if (invoiceOpt.isPresent()) {
            view.displayInvoiceGeneratedSuccess(invoiceOpt.get());
        } else {
            ConsoleFormatter.printError("No se pudo procesar el pago o generar la factura.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void paymentsByStudent() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        List<Payment> payments = service.getPaymentsByStudent(studentId);
        view.displayPaymentList(payments);
        InputValidator.pressEnterToContinue();
    }

    private void paymentsByGuardian() {
        int guardianId = InputValidator.readInt("ID del Acudiente: ", 1, 999999);
        List<Payment> payments = service.getPaymentsByGuardian(guardianId);
        view.displayPaymentList(payments);
        InputValidator.pressEnterToContinue();
    }

    private void listInvoices() {
        List<Invoice> invoices = service.getAllInvoices();
        view.displayInvoices(invoices);
        InputValidator.pressEnterToContinue();
    }

    private void reportDefaulters() {
        List<Integer> defaulterIds = service.getDefaulterStudentIds();
        ConsoleFormatter.printSubHeader("REPORTE DE ESTUDIANTES EN MORA (PAGOS PENDIENTES > 30-60 DÍAS)");
        if (defaulterIds.isEmpty()) {
            ConsoleFormatter.printSuccess("¡Excelente! No se registran estudiantes morosos en la base de datos.");
        } else {
            ConsoleFormatter.printWarning(String.format("Se identificaron %d estudiantes con mora en pagos:", defaulterIds.size()));
            for (Integer id : defaulterIds) {
                System.out.printf("  - Estudiante ID #%d (Sin pagos recientes registrados)%n", id);
            }
        }
        InputValidator.pressEnterToContinue();
    }
}
