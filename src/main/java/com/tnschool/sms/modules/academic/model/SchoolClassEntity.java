package com.tnschool.sms.modules.academic.model;

import com.tnschool.sms.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "school_classes")
public class SchoolClassEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 20)
    private String grade;

    @Column(nullable = false, length = 10)
    private String section;

    @Column(length = 20)
    private String roomNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_term_id", nullable = false)
    private AcademicTermEntity academicTerm;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public AcademicTermEntity getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(AcademicTermEntity academicTerm) {
        this.academicTerm = academicTerm;
    }
}
