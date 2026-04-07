CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    admission_no VARCHAR(50) NOT NULL UNIQUE,
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    parent_user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    gender VARCHAR(20),
    date_of_birth DATE,
    blood_group VARCHAR(10),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attendance_records (
    id VARCHAR(64) PRIMARY KEY,
    student_id VARCHAR(64) NOT NULL REFERENCES students(id),
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks VARCHAR(255),
    marked_by_user_id VARCHAR(64) REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_student_date UNIQUE (student_id, attendance_date)
);

INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'parent-2', 'Mrs. Priya', '9111000002', 'priya@tnschool.local', '{noop}Parent2@123', 'PARENT', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'parent-2');

INSERT INTO students (id, name, admission_no, class_id, parent_user_id, gender, date_of_birth, blood_group, active)
SELECT 'stu-1', 'Vimal', 'TN2026001', 'class-5-a', 'parent-1', 'MALE', DATE '2015-07-14', 'B+', TRUE
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 'stu-1');

INSERT INTO students (id, name, admission_no, class_id, parent_user_id, gender, date_of_birth, blood_group, active)
SELECT 'stu-2', 'Ananya', 'TN2026002', 'class-5-a', 'parent-1', 'FEMALE', DATE '2015-09-02', 'O+', TRUE
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 'stu-2');

INSERT INTO students (id, name, admission_no, class_id, parent_user_id, gender, date_of_birth, blood_group, active)
SELECT 'stu-3', 'Harish', 'TN2026003', 'class-6-b', 'parent-2', 'MALE', DATE '2014-12-11', 'A+', TRUE
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 'stu-3');

INSERT INTO attendance_records (id, student_id, class_id, attendance_date, status, remarks, marked_by_user_id)
SELECT 'att-1', 'stu-1', 'class-5-a', DATE '2026-04-01', 'PRESENT', NULL, 'teacher-1'
WHERE NOT EXISTS (SELECT 1 FROM attendance_records WHERE id = 'att-1');

INSERT INTO attendance_records (id, student_id, class_id, attendance_date, status, remarks, marked_by_user_id)
SELECT 'att-2', 'stu-2', 'class-5-a', DATE '2026-04-01', 'ABSENT', 'Fever', 'teacher-1'
WHERE NOT EXISTS (SELECT 1 FROM attendance_records WHERE id = 'att-2');

INSERT INTO attendance_records (id, student_id, class_id, attendance_date, status, remarks, marked_by_user_id)
SELECT 'att-3', 'stu-1', 'class-5-a', DATE '2026-04-02', 'PRESENT', NULL, 'teacher-1'
WHERE NOT EXISTS (SELECT 1 FROM attendance_records WHERE id = 'att-3');

INSERT INTO attendance_records (id, student_id, class_id, attendance_date, status, remarks, marked_by_user_id)
SELECT 'att-4', 'stu-2', 'class-5-a', DATE '2026-04-02', 'LATE', 'Bus delay', 'teacher-1'
WHERE NOT EXISTS (SELECT 1 FROM attendance_records WHERE id = 'att-4');
