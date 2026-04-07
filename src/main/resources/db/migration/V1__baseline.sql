CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS academic_terms (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL,
    academic_year VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS school_classes (
    id VARCHAR(64) PRIMARY KEY,
    grade VARCHAR(20) NOT NULL,
    section VARCHAR(10) NOT NULL,
    room_no VARCHAR(20),
    academic_term_id VARCHAR(64) NOT NULL REFERENCES academic_terms(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS teacher_mappings (
    id VARCHAR(64) PRIMARY KEY,
    teacher_user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    subject_id VARCHAR(64) NOT NULL REFERENCES subjects(id),
    academic_term_id VARCHAR(64) NOT NULL REFERENCES academic_terms(id),
    class_teacher BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS timetable_slots (
    id VARCHAR(64) PRIMARY KEY,
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    subject_id VARCHAR(64) NOT NULL REFERENCES subjects(id),
    teacher_id VARCHAR(64) NOT NULL REFERENCES users(id),
    week_day VARCHAR(20) NOT NULL,
    period_no INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'admin-1', 'System Admin', '9000000001', 'admin@tnschool.local', '{noop}Admin@123456', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'admin-1');

INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'teacher-1', 'Mrs. Revathi', '9000000002', 'revathi@tnschool.local', '{noop}Teacher@123', 'TEACHER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'teacher-1');

INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'parent-1', 'Mr. Kumar', '9111000001', 'kumar@tnschool.local', '{noop}Parent@123', 'PARENT', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'parent-1');

INSERT INTO academic_terms (id, name, type, academic_year, start_date, end_date, active)
SELECT 'term-q1', 'Quarterly Term', 'QUARTERLY', '2026-2027', DATE '2026-06-10', DATE '2026-09-20', TRUE
WHERE NOT EXISTS (SELECT 1 FROM academic_terms WHERE id = 'term-q1');

INSERT INTO academic_terms (id, name, type, academic_year, start_date, end_date, active)
SELECT 'term-h1', 'Half-Yearly Term', 'HALF_YEARLY', '2026-2027', DATE '2026-09-21', DATE '2026-12-20', FALSE
WHERE NOT EXISTS (SELECT 1 FROM academic_terms WHERE id = 'term-h1');

INSERT INTO school_classes (id, grade, section, room_no, academic_term_id)
SELECT 'class-5-a', '5', 'A', '12', 'term-q1'
WHERE NOT EXISTS (SELECT 1 FROM school_classes WHERE id = 'class-5-a');

INSERT INTO school_classes (id, grade, section, room_no, academic_term_id)
SELECT 'class-6-b', '6', 'B', '18', 'term-q1'
WHERE NOT EXISTS (SELECT 1 FROM school_classes WHERE id = 'class-6-b');

INSERT INTO subjects (id, name, code)
SELECT 'subject-tam', 'Tamil', 'TAM'
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 'subject-tam');

INSERT INTO subjects (id, name, code)
SELECT 'subject-mat', 'Mathematics', 'MAT'
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 'subject-mat');

INSERT INTO subjects (id, name, code)
SELECT 'subject-sci', 'Science', 'SCI'
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 'subject-sci');

INSERT INTO teacher_mappings (id, teacher_user_id, class_id, subject_id, academic_term_id, class_teacher)
SELECT 'mapping-1', 'teacher-1', 'class-5-a', 'subject-mat', 'term-q1', TRUE
WHERE NOT EXISTS (SELECT 1 FROM teacher_mappings WHERE id = 'mapping-1');

INSERT INTO timetable_slots (id, class_id, subject_id, teacher_id, week_day, period_no, start_time, end_time)
SELECT 'slot-1', 'class-5-a', 'subject-mat', 'teacher-1', 'MONDAY', 1, TIME '09:00:00', TIME '09:45:00'
WHERE NOT EXISTS (SELECT 1 FROM timetable_slots WHERE id = 'slot-1');

INSERT INTO timetable_slots (id, class_id, subject_id, teacher_id, week_day, period_no, start_time, end_time)
SELECT 'slot-2', 'class-5-a', 'subject-sci', 'teacher-1', 'TUESDAY', 3, TIME '11:00:00', TIME '11:45:00'
WHERE NOT EXISTS (SELECT 1 FROM timetable_slots WHERE id = 'slot-2');
