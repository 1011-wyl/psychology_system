-- Execute once after reviewing the duplicate checks below.
-- Back up the database before applying DDL in production.

-- These queries must return no rows before adding the unique constraints.
SELECT username, COUNT(*) AS duplicate_count
FROM sys_user
GROUP BY username
HAVING COUNT(*) > 1;

SELECT username, COUNT(*) AS duplicate_count
FROM sys_counselor
GROUP BY username
HAVING COUNT(*) > 1;

SELECT counselor_id, config_date, time_slot, COUNT(*) AS duplicate_count
FROM appointment_time_config
GROUP BY counselor_id, config_date, time_slot
HAVING COUNT(*) > 1;

ALTER TABLE sys_user
    ADD CONSTRAINT uk_sys_user_username UNIQUE (username);

ALTER TABLE sys_counselor
    ADD CONSTRAINT uk_sys_counselor_username UNIQUE (username);

ALTER TABLE appointment_time_config
    ADD CONSTRAINT uk_time_config_slot UNIQUE (counselor_id, config_date, time_slot);

CREATE INDEX idx_appointment_user_created
    ON appointment_record (user_id, create_time);

CREATE INDEX idx_appointment_counselor_date
    ON appointment_record (counselor_id, appointment_date, appointment_time);

CREATE INDEX idx_evaluate_report_user_created
    ON evaluate_report (user_id, create_time);
