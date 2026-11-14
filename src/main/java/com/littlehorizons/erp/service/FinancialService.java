package com.littlehorizons.erp.service;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.GuardianDAO;
import com.littlehorizons.erp.dao.InvoiceDAO;
import com.littlehorizons.erp.dao.PaymentDAO;
import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.dao.impl.GuardianDAOImpl;
import com.littlehorizons.erp.dao.impl.InvoiceDAOImpl;
import com.littlehorizons.erp.dao.impl.PaymentDAOImpl;
import com.littlehorizons.erp.dao.impl.StudentDAOImpl;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.CufeGenerator;
import com.littlehorizons.erp.util.LoggerUtil;
import com.littlehorizons.erp.util.TextInvoiceGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service managing financial transactions, payments, fee concepts, defaulters,
 * and automatic TXT Electronic Invoice generation.
 */
public class FinancialService {

    private final PaymentDAO paymentDAO;
    private final InvoiceDAO invoiceDAO;
    private final StudentDAO studentDAO;
    private final GuardianDAO guardianDAO;
    private final DatabaseConfig config = DatabaseConfig.getInstance();

    public FinancialService() {
        this.paymentDAO = new PaymentDAOImpl();
        this.invoiceDAO = new InvoiceDAOImpl();
        this.studentDAO = new StudentDAOImpl();
        this.guardianDAO = new GuardianDAOImpl();
    }

    public FinancialService(PaymentDAO paymentDAO, InvoiceDAO invoiceDAO, StudentDAO studentDAO, GuardianDAO guardianDAO) {
        this.paymentDAO = paymentDAO;
        this.invoiceDAO = invoiceDAO;
        this.studentDAO = studentDAO;
        this.guardianDAO = guardianDAO;
    }

    public List<PaymentConcept> getAllConcepts() {
        return paymentDAO.findAllConcepts();
    }

    public Optional<PaymentConcept> getConceptById(int id) {
        return paymentDAO.findConceptById(id);
    }

    /**
     * Registers a payment transaction and automatically generates a corresponding TXT electronic invoice.
     */
    public Optional<Invoice> recordPaymentAndGenerateInvoice(
            int studentId,
            Integer guardianId,
            int conceptId,
            double amountPaid,
            LocalDate paymentDate,
            String paymentMethod,
            Integer academicYear,
            String academicPeriod,
            String notes
    ) {
        String receiptNumber = "REC-" + paymentDate.getYear() + "-" + System.currentTimeMillis() % 100000;
        Payment payment = new Payment(0, studentId, guardianId, conceptId, amountPaid, paymentDate,
                paymentMethod, receiptNumber, academicYear, academicPeriod, notes);

        boolean success = paymentDAO.create(payment);
        if (!success) {
            LoggerUtil.error("Failed to register payment for student ID: " + studentId);
            return Optional.empty();
        }

        // Fetch associated entities for invoice generation
        Optional<Student> studentOpt = studentDAO.findById(studentId);
        String studentName = studentOpt.map(Student::getFullName).orElse("Estudiante #" + studentId);

        String guardianName = "Responsable de Familia";
        String guardianDoc = "N/A";
        String guardianAddress = "Bogotá, Colombia";

        if (guardianId != null) {
            Optional<Guardian> guardianOpt = guardianDAO.findById(guardianId);
            if (guardianOpt.isPresent()) {
                Guardian g = guardianOpt.get();
                guardianName = g.getFullName();
                guardianDoc = g.getDocNumber();
                guardianAddress = g.getAddress();
            }
        } else {
            // Check if student has linked guardians
            List<Guardian> guardians = studentDAO.findGuardiansByStudentId(studentId);
            if (!guardians.isEmpty()) {
                Guardian g = guardians.getFirst();
                guardianName = g.getFullName();
                guardianDoc = g.getDocNumber();
                guardianAddress = g.getAddress();
            }
        }

        Optional<PaymentConcept> conceptOpt = paymentDAO.findConceptById(conceptId);
        String conceptDescription = conceptOpt.map(PaymentConcept::getName).orElse("Concepto de Pago Escolar");

        // Generate consecutive invoice number
        String invoiceNumber = invoiceDAO.getNextInvoiceNumber();

        // Generate simulated CUFE
        String cufe = CufeGenerator.generateCufe(invoiceNumber, config.getSchoolNit(), paymentDate.toString(), amountPaid);

        // Generate TXT invoice file
        String filePath = TextInvoiceGenerator.generateInvoiceTxt(
                invoiceNumber,
                config.getSchoolName(),
                config.getSchoolNit(),
                config.getSchoolAddress(),
                config.getSchoolPhone(),
                config.getSchoolEmail(),
                config.getDianResolution(),
                guardianName,
                guardianDoc,
                guardianAddress,
                studentName,
                conceptDescription,
                amountPaid,
                paymentMethod,
                receiptNumber,
                cufe,
                paymentDate
        );

        String txtFileName = (filePath != null) ? new File(filePath).getName() : "factura_" + invoiceNumber + ".txt";

        Invoice invoice = new Invoice(0, invoiceNumber, payment.getId(), paymentDate, cufe, txtFileName, filePath);
        invoiceDAO.create(invoice);

        LoggerUtil.info("Electronic invoice generated successfully: " + invoiceNumber + " -> " + filePath);
        return Optional.of(invoice);
    }

    public List<Payment> getPaymentsByStudent(int studentId) {
        return paymentDAO.findByStudent(studentId);
    }

    public List<Payment> getPaymentsByGuardian(int guardianId) {
        return paymentDAO.findByGuardian(guardianId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }

    public Optional<Invoice> getInvoiceByNumber(String invoiceNumber) {
        return invoiceDAO.findByInvoiceNumber(invoiceNumber);
    }

    public List<Integer> getDefaulterStudentIds() {
        return paymentDAO.findDefaulterStudentIds();
    }
}
