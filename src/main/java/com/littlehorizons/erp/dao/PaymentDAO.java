package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;

import java.util.List;
import java.util.Optional;

public interface PaymentDAO {
    boolean create(Payment payment);
    Optional<Payment> findById(int id);
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    List<Payment> findAll();
    List<Payment> findByStudent(int studentId);
    List<Payment> findByGuardian(int guardianId);
    List<PaymentConcept> findAllConcepts();
    Optional<PaymentConcept> findConceptById(int id);
    List<Integer> findDefaulterStudentIds();
}
