package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Electronic Invoice entity (TXT format).
 */
public class Invoice {
    private int id;
    private String invoiceNumber;
    private int paymentId;
    private LocalDate issueDate;
    private String cufe;
    private String txtFileName;
    private String filePath;

    public Invoice() {}

    public Invoice(int id, String invoiceNumber, int paymentId, LocalDate issueDate, String cufe, String txtFileName, String filePath) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.paymentId = paymentId;
        this.issueDate = issueDate;
        this.cufe = cufe;
        this.txtFileName = txtFileName;
        this.filePath = filePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getCufe() { return cufe; }
    public void setCufe(String cufe) { this.cufe = cufe; }

    public String getTxtFileName() { return txtFileName; }
    public void setTxtFileName(String txtFileName) { this.txtFileName = txtFileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    @Override
    public String toString() {
        return String.format("[%d] Factura: %s - Fecha: %s - Archivo: %s", id, invoiceNumber, issueDate, txtFileName);
    }
}
