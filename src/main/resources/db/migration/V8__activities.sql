CREATE TABLE IF NOT EXISTS activities (
    id VARCHAR(64) PRIMARY KEY,
    academic_term_id VARCHAR(64) REFERENCES academic_terms(id),
    category VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    activity_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS activity_participants (
    id VARCHAR(64) PRIMARY KEY,
    activity_id VARCHAR(64) NOT NULL REFERENCES activities(id),
    student_id VARCHAR(64) NOT NULL REFERENCES students(id),
    achievement VARCHAR(255),
    position VARCHAR(100),
    certificate BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO activities (id, academic_term_id, category, title, description, activity_date)
SELECT 'activity-1', 'term-q1', 'Sports', 'Inter-school Kho Kho', 'District-level kho kho tournament participation', DATE '2026-08-12'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE id = 'activity-1');

INSERT INTO activities (id, academic_term_id, category, title, description, activity_date)
SELECT 'activity-2', 'term-q1', 'Music', 'Tamil Music Fest', 'Students perform traditional and folk music pieces', DATE '2026-07-25'
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE id = 'activity-2');

INSERT INTO activity_participants (id, activity_id, student_id, achievement, position, certificate, notes)
SELECT 'activity-participant-1', 'activity-1', 'stu-1', 'Runner-up', '2', TRUE, 'Excellent teamwork'
WHERE NOT EXISTS (SELECT 1 FROM activity_participants WHERE id = 'activity-participant-1');

INSERT INTO activity_participants (id, activity_id, student_id, achievement, position, certificate, notes)
SELECT 'activity-participant-2', 'activity-2', 'stu-2', 'Participation', NULL, TRUE, 'Stage confidence improved'
WHERE NOT EXISTS (SELECT 1 FROM activity_participants WHERE id = 'activity-participant-2');
