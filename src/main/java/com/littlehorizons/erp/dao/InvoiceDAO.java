package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceDAO {
    boolean create(Invoice invoice);
    Optional<Invoice> findById(int id);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findByPaymentId(int paymentId);
    List<Invoice> findAll();
    String getNextInvoiceNumber();
}
