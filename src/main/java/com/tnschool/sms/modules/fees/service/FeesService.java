package com.tnschool.sms.modules.fees.service;

import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.repository.AcademicTermRepository;
import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.fees.dto.CreateFeeStructureRequest;
import com.tnschool.sms.modules.fees.dto.FeePaymentResponse;
import com.tnschool.sms.modules.fees.dto.FeeStructureResponse;
import com.tnschool.sms.modules.fees.dto.RecordFeePaymentRequest;
import com.tnschool.sms.modules.fees.dto.StudentFeeSummaryResponse;
import com.tnschool.sms.modules.fees.model.FeePaymentEntity;
import com.tnschool.sms.modules.fees.model.FeePaymentStatus;
import com.tnschool.sms.modules.fees.model.FeeStructureEntity;
import com.tnschool.sms.modules.fees.repository.FeePaymentRepository;
import com.tnschool.sms.modules.fees.repository.FeeStructureRepository;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeesService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AcademicTermRepository academicTermRepository;
    private final StudentRepository studentRepository;

    public FeesService(
            FeeStructureRepository feeStructureRepository,
            FeePaymentRepository feePaymentRepository,
            SchoolClassRepository schoolClassRepository,
            AcademicTermRepository academicTermRepository,
            StudentRepository studentRepository
    ) {
        this.feeStructureRepository = feeStructureRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.academicTermRepository = academicTermRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getStructures(String classId, String termId) {
        List<FeeStructureEntity> items;
        if (classId != null && !classId.isBlank() && termId != null && !termId.isBlank()) {
            items = feeStructureRepository.findAllBySchoolClass_IdAndAcademicTerm_IdOrderByFeeTypeAsc(classId, termId);
        } else if (classId != null && !classId.isBlank()) {
            items = feeStructureRepository.findAllBySchoolClass_IdOrderByFeeTypeAsc(classId);
        } else {
            items = feeStructureRepository.findAll();
        }
        return items.stream().map(this::toStructureResponse).toList();
    }

    @Transactional
    public FeeStructureResponse createStructure(CreateFeeStructureRequest request) {
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        AcademicTermEntity academicTerm = academicTermRepository.findById(request.academicTermId())
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));

        FeeStructureEntity structure = new FeeStructureEntity();
        structure.setSchoolClass(schoolClass);
        structure.setAcademicTerm(academicTerm);
        structure.setFeeType(request.feeType());
        structure.setAmount(request.amount());
        structure.setDueDate(request.dueDate());
        return toStructureResponse(feeStructureRepository.save(structure));
    }

    @Transactional
    public FeePaymentResponse recordPayment(RecordFeePaymentRequest request) {
        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        FeeStructureEntity structure = feeStructureRepository.findById(request.feeStructureId())
                .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));

        FeePaymentEntity payment = new FeePaymentEntity();
        payment.setStudent(student);
        payment.setFeeStructure(structure);
        payment.setPaidAmount(request.paidAmount());
        payment.setPaidDate(request.paidDate());
        payment.setStatus(request.status());
        payment.setReceiptNo(request.receiptNo());
        payment.setPaymentMode(request.paymentMode());
        payment.setRemarks(request.remarks());
        return toPaymentResponse(feePaymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public StudentFeeSummaryResponse getStudentFees(String studentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        List<FeePaymentEntity> payments = feePaymentRepository.findAllByStudent_IdOrderByFeeStructure_FeeTypeAsc(studentId);

        List<StudentFeeSummaryResponse.FeeLineItem> summary = payments.stream()
                .map(payment -> new StudentFeeSummaryResponse.FeeLineItem(
                        payment.getFeeStructure().getFeeType(),
                        payment.getFeeStructure().getAmount(),
                        payment.getPaidAmount(),
                        payment.getStatus().name(),
                        payment.getFeeStructure().getDueDate() == null ? null : payment.getFeeStructure().getDueDate().toString()
                ))
                .toList();

        double totalAmount = summary.stream().mapToDouble(StudentFeeSummaryResponse.FeeLineItem::amount).sum();
        double totalPaid = summary.stream().mapToDouble(StudentFeeSummaryResponse.FeeLineItem::paidAmount).sum();

        return new StudentFeeSummaryResponse(
                new StudentFeeSummaryResponse.StudentSummary(student.getName(), student.getAdmissionNo()),
                summary,
                new StudentFeeSummaryResponse.Totals(totalAmount, totalPaid, totalAmount - totalPaid)
        );
    }

    @Transactional(readOnly = true)
    public List<FeePaymentResponse> getPendingFees() {
        return feePaymentRepository.findAllByStatusInOrderByStudent_NameAscFeeStructure_FeeTypeAsc(
                        List.of(FeePaymentStatus.PENDING, FeePaymentStatus.PARTIALLY_PAID))
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    private FeeStructureResponse toStructureResponse(FeeStructureEntity entity) {
        return new FeeStructureResponse(
                entity.getId(),
                entity.getSchoolClass().getId(),
                entity.getAcademicTerm().getId(),
                entity.getFeeType(),
                entity.getAmount(),
                entity.getDueDate() == null ? null : entity.getDueDate().toString(),
                new FeeStructureResponse.ClassSummary(entity.getSchoolClass().getGrade(), entity.getSchoolClass().getSection())
        );
    }

    private FeePaymentResponse toPaymentResponse(FeePaymentEntity entity) {
        return new FeePaymentResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getFeeStructure().getId(),
                entity.getPaidAmount(),
                entity.getPaidDate() == null ? null : entity.getPaidDate().toString(),
                entity.getStatus().name(),
                entity.getReceiptNo(),
                entity.getPaymentMode(),
                entity.getRemarks(),
                new FeePaymentResponse.StudentSummary(
                        entity.getStudent().getName(),
                        entity.getStudent().getAdmissionNo(),
                        new FeePaymentResponse.ClassSummary(
                                entity.getStudent().getSchoolClass().getGrade(),
                                entity.getStudent().getSchoolClass().getSection()
                        ),
                        new FeePaymentResponse.ParentSummary(
                                entity.getStudent().getParentUser().getName(),
                                entity.getStudent().getParentUser().getPhone()
                        )
                ),
                new FeePaymentResponse.FeeStructureSummary(
                        entity.getFeeStructure().getFeeType(),
                        entity.getFeeStructure().getAmount()
                )
        );
    }
}
