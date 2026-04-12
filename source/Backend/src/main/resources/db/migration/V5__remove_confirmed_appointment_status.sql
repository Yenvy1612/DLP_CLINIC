UPDATE appointments
SET status = 'PENDING'
WHERE status = 'CONFIRMED';

ALTER TABLE appointments
DROP CHECK ck_appointments_status;

ALTER TABLE appointments
ADD CONSTRAINT ck_appointments_status CHECK (status IN ('PENDING', 'DONE', 'CANCELLED', 'NO_SHOW'));
