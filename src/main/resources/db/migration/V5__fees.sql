CREATE TABLE IF NOT EXISTS fee_structures (
    id VARCHAR(64) PRIMARY KEY,
    class_id VARCHAR(64) NOT NULL REFERENCES school_classes(id),
    academic_term_id VARCHAR(64) NOT NULL REFERENCES academic_terms(id),
    fee_type VARCHAR(100) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    due_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fee_payments (
    id VARCHAR(64) PRIMARY KEY,
    student_id VARCHAR(64) NOT NULL REFERENCES students(id),
    fee_structure_id VARCHAR(64) NOT NULL REFERENCES fee_structures(id),
    paid_amount DOUBLE PRECISION NOT NULL,
    paid_date DATE,
    status VARCHAR(20) NOT NULL,
    receipt_no VARCHAR(50),
    payment_mode VARCHAR(30),
    remarks VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO fee_structures (id, class_id, academic_term_id, fee_type, amount, due_date)
SELECT 'fee-structure-1', 'class-5-a', 'term-q1', 'Tuition', 12000, DATE '2026-06-20'
WHERE NOT EXISTS (SELECT 1 FROM fee_structures WHERE id = 'fee-structure-1');

INSERT INTO fee_structures (id, class_id, academic_term_id, fee_type, amount, due_date)
SELECT 'fee-structure-2', 'class-5-a', 'term-q1', 'Transport', 3000, DATE '2026-06-25'
WHERE NOT EXISTS (SELECT 1 FROM fee_structures WHERE id = 'fee-structure-2');

INSERT INTO fee_payments (id, student_id, fee_structure_id, paid_amount, paid_date, status, receipt_no, payment_mode, remarks)
SELECT 'fee-payment-1', 'stu-1', 'fee-structure-1', 12000, DATE '2026-06-18', 'PAID', 'RCPT-001', 'CASH', 'Paid in full'
WHERE NOT EXISTS (SELECT 1 FROM fee_payments WHERE id = 'fee-payment-1');

INSERT INTO fee_payments (id, student_id, fee_structure_id, paid_amount, paid_date, status, receipt_no, payment_mode, remarks)
SELECT 'fee-payment-2', 'stu-2', 'fee-structure-1', 6000, DATE '2026-06-19', 'PARTIALLY_PAID', 'RCPT-002', 'UPI', 'Balance pending'
WHERE NOT EXISTS (SELECT 1 FROM fee_payments WHERE id = 'fee-payment-2');

INSERT INTO fee_payments (id, student_id, fee_structure_id, paid_amount, paid_date, status, receipt_no, payment_mode, remarks)
SELECT 'fee-payment-3', 'stu-3', 'fee-structure-1', 0, NULL, 'PENDING', NULL, NULL, 'Not paid yet'
WHERE NOT EXISTS (SELECT 1 FROM fee_payments WHERE id = 'fee-payment-3');
