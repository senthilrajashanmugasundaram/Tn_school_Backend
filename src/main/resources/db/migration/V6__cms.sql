CREATE TABLE IF NOT EXISTS cms_content (
    id VARCHAR(64) PRIMARY KEY,
    content_type VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body VARCHAR(4000) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO cms_content (id, content_type, title, body, published, published_at, expires_at)
SELECT
    'cms-1',
    'ANNOUNCEMENT',
    'Quarterly exams begin on September 15',
    'Parents are requested to ensure students carry all required stationery and arrive on time.',
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
WHERE NOT EXISTS (SELECT 1 FROM cms_content WHERE id = 'cms-1');

INSERT INTO cms_content (id, content_type, title, body, published, published_at, expires_at)
SELECT
    'cms-2',
    'ACHIEVEMENT',
    'Class 5 Science Fair Recognition',
    'Students of Class 5 received district-level appreciation for their environment models.',
    TRUE,
    CURRENT_TIMESTAMP,
    NULL
WHERE NOT EXISTS (SELECT 1 FROM cms_content WHERE id = 'cms-2');

INSERT INTO cms_content (id, content_type, title, body, published, published_at, expires_at)
SELECT
    'cms-3',
    'NOTICE',
    'Parent-Teacher Meeting',
    'The next parent-teacher meeting will be held on the first Saturday of next month.',
    FALSE,
    NULL,
    NULL
WHERE NOT EXISTS (SELECT 1 FROM cms_content WHERE id = 'cms-3');
