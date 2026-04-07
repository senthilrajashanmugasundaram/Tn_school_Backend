package com.tnschool.sms.modules.fees.model;

import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "fee_payments")
public class FeePaymentEntity extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructureEntity feeStructure;

    @Column(nullable = false)
    private Double paidAmount;

    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeePaymentStatus status;

    @Column(length = 50)
    private String receiptNo;

    @Column(length = 30)
    private String paymentMode;

    @Column(length = 255)
    private String remarks;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public FeeStructureEntity getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructureEntity feeStructure) {
        this.feeStructure = feeStructure;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public FeePaymentStatus getStatus() {
        return status;
    }

    public void setStatus(FeePaymentStatus status) {
        this.status = status;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
