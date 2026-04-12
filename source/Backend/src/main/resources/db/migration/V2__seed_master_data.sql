-- V2: Seed core master data.

INSERT INTO specialties (code, name, active) VALUES
    ('GENERAL', 'General', TRUE),
    ('CARDIOLOGY', 'Cardiology', TRUE),
    ('LABORATORY', 'Laboratory', TRUE),
    ('DIAGNOSTIC_IMAGING', 'Diagnostic Imaging', TRUE),
    ('INTERNAL_MEDICINE', 'Internal Medicine', TRUE),
    ('PEDIATRICS', 'Pediatrics', TRUE),
    ('DERMATOLOGY', 'Dermatology', TRUE),
    ('OPHTHALMOLOGY', 'Ophthalmology', TRUE);

INSERT INTO services (name, price, description, specialty_id, active, duration_min) VALUES
    ('Khám Cơ Xương Khớp', 200000, 'Khám và tư vấn chuyên sâu về cơ xương khớp', (SELECT id FROM specialties WHERE code = 'GENERAL'), TRUE, 30),
    ('General Consultation', 150000, 'General checkup and doctor consultation', (SELECT id FROM specialties WHERE code = 'GENERAL'), TRUE, 30),
    ('Blood Test', 250000, 'Standard blood test package', (SELECT id FROM specialties WHERE code = 'LABORATORY'), TRUE, 20),
    ('Ultrasound', 350000, 'Diagnostic ultrasound service', (SELECT id FROM specialties WHERE code = 'DIAGNOSTIC_IMAGING'), TRUE, 35),
    ('Acne Consultation', 100000, 'Basic acne consultation', (SELECT id FROM specialties WHERE code = 'DERMATOLOGY'), TRUE, 30),
    ('Eye Exam', 150000, 'Routine eye examination', (SELECT id FROM specialties WHERE code = 'OPHTHALMOLOGY'), TRUE, 30);
