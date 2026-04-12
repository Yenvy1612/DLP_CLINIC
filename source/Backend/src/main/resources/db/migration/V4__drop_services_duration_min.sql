-- V4: Remove service duration column, booking now uses fixed 30-minute slots.

ALTER TABLE services
    DROP CHECK ck_services_duration;

ALTER TABLE services
    DROP COLUMN duration_min;
