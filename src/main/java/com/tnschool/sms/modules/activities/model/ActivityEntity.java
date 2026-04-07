package com.tnschool.sms.modules.activities.model;

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
@Table(name = "activities")
public class ActivityEntity extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_term_id")
    private AcademicTermEntity academicTerm;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDate activityDate;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public AcademicTermEntity getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(AcademicTermEntity academicTerm) {
        this.academicTerm = academicTerm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }
}
