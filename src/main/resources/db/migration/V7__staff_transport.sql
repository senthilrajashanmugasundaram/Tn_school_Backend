INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'driver-user-1', 'Mr. Ramesh', '9000000003', 'ramesh@tnschool.local', '{noop}Driver@123', 'TEACHER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'driver-user-1');

INSERT INTO users (id, name, phone, email, password_hash, role, active)
SELECT 'office-user-1', 'Mrs. Lakshmi', '9000000004', 'lakshmi@tnschool.local', '{noop}Office@123', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 'office-user-1');

CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id),
    staff_type VARCHAR(20) NOT NULL,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    designation VARCHAR(100),
    department VARCHAR(100),
    join_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transport_vans (
    id VARCHAR(64) PRIMARY KEY,
    vehicle_no VARCHAR(50) NOT NULL UNIQUE,
    model VARCHAR(100),
    capacity INTEGER NOT NULL,
    route_name VARCHAR(100) NOT NULL,
    route_details VARCHAR(255),
    driver_staff_id VARCHAR(64) REFERENCES staff(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO staff (id, user_id, staff_type, employee_id, designation, department, join_date)
SELECT 'staff-1', 'teacher-1', 'TEACHER', 'EMP001', 'Maths Teacher', 'Academics', DATE '2024-06-01'
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE id = 'staff-1');

INSERT INTO staff (id, user_id, staff_type, employee_id, designation, department, join_date)
SELECT 'staff-2', 'driver-user-1', 'DRIVER', 'DRV001', 'School Driver', 'Transport', DATE '2023-01-10'
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE id = 'staff-2');

INSERT INTO staff (id, user_id, staff_type, employee_id, designation, department, join_date)
SELECT 'staff-3', 'office-user-1', 'OFFICE', 'OFF001', 'Office Assistant', 'Administration', DATE '2022-07-15'
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE id = 'staff-3');

INSERT INTO transport_vans (id, vehicle_no, model, capacity, route_name, route_details, driver_staff_id, active)
SELECT 'van-1', 'TN01AB1234', 'Ashok Leyland', 35, 'North Route', 'Anna Nagar, Mogappair, Ayanavaram', 'staff-2', TRUE
WHERE NOT EXISTS (SELECT 1 FROM transport_vans WHERE id = 'van-1');
