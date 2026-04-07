package com.tnschool.sms.modules.homework.model;

import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.model.SubjectEntity;
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
@Table(name = "homework")
public class HomeworkEntity extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClassEntity schoolClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private UserEntity teacherUser;

    @Column(nullable = false)
    private LocalDate homeworkDate;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean smsSent;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public SchoolClassEntity getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClassEntity schoolClass) {
        this.schoolClass = schoolClass;
    }

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }

    public UserEntity getTeacherUser() {
        return teacherUser;
    }

    public void setTeacherUser(UserEntity teacherUser) {
        this.teacherUser = teacherUser;
    }

    public LocalDate getHomeworkDate() {
        return homeworkDate;
    }

    public void setHomeworkDate(LocalDate homeworkDate) {
        this.homeworkDate = homeworkDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSmsSent() {
        return smsSent;
    }

    public void setSmsSent(boolean smsSent) {
        this.smsSent = smsSent;
    }
}
