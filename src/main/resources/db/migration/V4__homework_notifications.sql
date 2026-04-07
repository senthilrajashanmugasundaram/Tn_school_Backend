CREATE TABLE IF NOT EXISTS homework (
    id VARCHAR(64) PRIMARY KEY,
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    subject_id VARCHAR(64) NOT NULL REFERENCES subjects(id),
    teacher_user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    homework_date DATE NOT NULL,
    description VARCHAR(1000) NOT NULL,
    sms_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS homework_sms_logs (
    id VARCHAR(64) PRIMARY KEY,
    student_id VARCHAR(64) NOT NULL REFERENCES students(id),
    homework_date DATE NOT NULL,
    sms_body VARCHAR(2000) NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE,
    provider_status VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_homework_sms_student_date UNIQUE (student_id, homework_date)
);

INSERT INTO teacher_mappings (id, teacher_user_id, class_id, subject_id, academic_term_id, class_teacher)
SELECT 'mapping-2', 'teacher-1', 'class-5-a', 'subject-sci', 'term-q1', FALSE
WHERE NOT EXISTS (SELECT 1 FROM teacher_mappings WHERE id = 'mapping-2');

INSERT INTO homework (id, class_id, subject_id, teacher_user_id, homework_date, description, sms_sent)
SELECT 'hw-1', 'class-5-a', 'subject-mat', 'teacher-1', DATE '2026-04-03', 'Page 23 sums 1 to 5', FALSE
WHERE NOT EXISTS (SELECT 1 FROM homework WHERE id = 'hw-1');

INSERT INTO homework (id, class_id, subject_id, teacher_user_id, homework_date, description, sms_sent)
SELECT 'hw-2', 'class-5-a', 'subject-sci', 'teacher-1', DATE '2026-04-03', 'Learn lesson 4 and revise notes', FALSE
WHERE NOT EXISTS (SELECT 1 FROM homework WHERE id = 'hw-2');
