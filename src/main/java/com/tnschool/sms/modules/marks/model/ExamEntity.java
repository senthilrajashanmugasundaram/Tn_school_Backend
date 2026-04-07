package com.tnschool.sms.modules.marks.model;

import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import com.tnschool.sms.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
public class ExamEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_term_id", nullable = false)
    private AcademicTermEntity academicTerm;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private Integer totalMarks;

    @Column(nullable = false)
    private boolean marksLocked;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcademicTermEntity getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(AcademicTermEntity academicTerm) {
        this.academicTerm = academicTerm;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public boolean isMarksLocked() {
        return marksLocked;
    }

    public void setMarksLocked(boolean marksLocked) {
        this.marksLocked = marksLocked;
    }
}
