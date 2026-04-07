CREATE TABLE IF NOT EXISTS exams (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    academic_term_id VARCHAR(64) NOT NULL REFERENCES academic_terms(id),
    exam_date DATE NOT NULL,
    total_marks INTEGER NOT NULL,
    marks_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS marks (
    id VARCHAR(64) PRIMARY KEY,
    exam_id VARCHAR(64) NOT NULL REFERENCES exams(id),
    student_id VARCHAR(64) NOT NULL REFERENCES students(id),
    subject_id VARCHAR(64) NOT NULL REFERENCES subjects(id),
    entered_by_user_id VARCHAR(64) REFERENCES users(id),
    marks_obtained DOUBLE PRECISION NOT NULL,
    max_marks DOUBLE PRECISION NOT NULL,
    grade VARCHAR(10),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_mark_exam_student_subject UNIQUE (exam_id, student_id, subject_id)
);

INSERT INTO exams (id, name, academic_term_id, exam_date, total_marks, marks_locked)
SELECT 'exam-q1', 'Quarterly Exam', 'term-q1', DATE '2026-09-15', 100, FALSE
WHERE NOT EXISTS (SELECT 1 FROM exams WHERE id = 'exam-q1');

INSERT INTO marks (id, exam_id, student_id, subject_id, entered_by_user_id, marks_obtained, max_marks, grade)
SELECT 'mark-1', 'exam-q1', 'stu-1', 'subject-mat', 'teacher-1', 92, 100, 'A+'
WHERE NOT EXISTS (SELECT 1 FROM marks WHERE id = 'mark-1');

INSERT INTO marks (id, exam_id, student_id, subject_id, entered_by_user_id, marks_obtained, max_marks, grade)
SELECT 'mark-2', 'exam-q1', 'stu-2', 'subject-mat', 'teacher-1', 78, 100, 'B+'
WHERE NOT EXISTS (SELECT 1 FROM marks WHERE id = 'mark-2');
