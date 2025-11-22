CREATE DATABASE acare_clinic
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE acare_clinic;

-- USERS
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(150) NOT NULL,
    email       VARCHAR(150) UNIQUE,
    phone       VARCHAR(20)  UNIQUE,
    password_hash VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'PATIENT'
        CHECK (role IN ('ADMIN','DOCTOR','STAFF','PATIENT')),
    gender VARCHAR(10) DEFAULT 'OTHER'
        CHECK (gender IN ('MALE','FEMALE','OTHER')),
    birth_date  DATE,
    address     VARCHAR(255),
    id_number   VARCHAR(30) UNIQUE,      -- CCCD
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ROOMS
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(120) NOT NULL,
    room_type VARCHAR(80)  NOT NULL,
    location  VARCHAR(120) NOT NULL,
    UNIQUE KEY uq_room (name, location)
) ENGINE=InnoDB;

-- SERVICES
CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(150) NOT NULL UNIQUE,
    price  DECIMAL(12,2) NOT NULL CHECK (price >= 0),
    description  TEXT,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    duration_min INT NOT NULL DEFAULT 30 COMMENT 'Thời lượng trung bình (phút)',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- APPOINTMENTS
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id  BIGINT,
    room_id    BIGINT,
    start_time DATETIME NOT NULL,
    status VARCHAR(12) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','DONE')),
    note       VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appt_patient
        FOREIGN KEY (patient_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_appt_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT fk_appt_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- Tránh trùng lịch
CREATE UNIQUE INDEX uq_appt_doctor_time ON appointments(doctor_id, start_time);
CREATE UNIQUE INDEX uq_appt_room_time   ON appointments(room_id,  start_time);

-- ACTIVITY_LOG
CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type    VARCHAR(255),
    message VARCHAR(500),
    time    DATETIME
) ENGINE=InnoDB;

-- 1. USERS (50 người)
-- ADMIN (3 người)
INSERT INTO users (full_name, email, phone, password_hash, role, gender, birth_date, address, id_number) VALUES
('Nguyễn Văn Quản', 'nguyen.quan@acareclinic.vn', '0900000001', '$2a$10$hash1', 'ADMIN', 'MALE', '1975-03-15', '123 Hoàng Quốc Việt, Cầu Giấy, Hà Nội', '001075031501'),
('Trần Thị Hương', 'tran.huong@acareclinic.vn', '0900000002', '$2a$10$hash2', 'ADMIN', 'FEMALE', '1978-07-20', '45 Láng Hạ, Đống Đa, Hà Nội', '001078072002'),
('Lê Minh Tuấn', 'le.tuan@acareclinic.vn', '0900000003', '$2a$10$hash3', 'ADMIN', 'MALE', '1980-11-10', '67 Trần Duy Hưng, Cầu Giấy, Hà Nội', '001080111003');

-- DOCTOR (15 người - chuyên khoa đa dạng)
INSERT INTO users (full_name, email, phone, password_hash, role, gender, birth_date, address, id_number) VALUES
-- Nội khoa
('Phạm Thu Lan', 'pham.lan@acareclinic.vn', '0901000001', '$2a$10$hash4', 'DOCTOR', 'FEMALE', '1982-05-12', '234 Nguyễn Trãi, Thanh Xuân, Hà Nội', '001182051204'),
('Hoàng Minh Long', 'hoang.long@acareclinic.vn', '0901000002', '$2a$10$hash5', 'DOCTOR', 'MALE', '1980-04-08', '156 Giải Phóng, Hoàng Mai, Hà Nội', '001180040805'),
('Đỗ Thị Hằng', 'do.hang@acareclinic.vn', '0901000003', '$2a$10$hash6', 'DOCTOR', 'FEMALE', '1985-09-25', '89 Tây Sơn, Đống Đa, Hà Nội', '001185092506'),
-- Nhi khoa
('Vũ Thanh Tùng', 'vu.tung@acareclinic.vn', '0901000004', '$2a$10$hash7', 'DOCTOR', 'MALE', '1983-02-14', '12 Phạm Ngọc Thạch, Đống Đa, Hà Nội', '001183021407'),
('Nguyễn Thị Mai', 'nguyen.mai@acareclinic.vn', '0901000005', '$2a$10$hash8', 'DOCTOR', 'FEMALE', '1986-08-30', '78 Kim Mã, Ba Đình, Hà Nội', '001186083008'),
-- Sản phụ khoa
('Trần Thị Ngọc', 'tran.ngoc@acareclinic.vn', '0901000006', '$2a$10$hash9', 'DOCTOR', 'FEMALE', '1984-06-18', '45 Láng Hạ, Đống Đa, Hà Nội', '001184061809'),
('Lê Thị Hoa', 'le.hoa@acareclinic.vn', '0901000007', '$2a$10$hash10', 'DOCTOR', 'FEMALE', '1987-03-22', '23 Nguyễn Chí Thanh, Đống Đa, Hà Nội', '001187032210'),
-- Tai Mũi Họng
('Phạm Văn Đức', 'pham.duc@acareclinic.vn', '0901000008', '$2a$10$hash11', 'DOCTOR', 'MALE', '1981-12-05', '67 Chùa Bộc, Đống Đa, Hà Nội', '001181120511'),
('Hoàng Thị Linh', 'hoang.linh@acareclinic.vn', '0901000009', '$2a$10$hash12', 'DOCTOR', 'FEMALE', '1988-07-16', '90 Thái Hà, Đống Đa, Hà Nội', '001188071612'),
-- Răng Hàm Mặt
('Nguyễn Văn Khoa', 'nguyen.khoa@acareclinic.vn', '0901000010', '$2a$10$hash13', 'DOCTOR', 'MALE', '1979-10-28', '34 Tôn Đức Thắng, Đống Đa, Hà Nội', '001179102813'),
('Trần Thị Thanh', 'tran.thanh@acareclinic.vn', '0901000011', '$2a$10$hash14', 'DOCTOR', 'FEMALE', '1985-04-11', '56 Phạm Văn Đồng, Bắc Từ Liêm, Hà Nội', '001185041114'),
-- Chẩn đoán hình ảnh
('Lê Minh Hải', 'le.hai@acareclinic.vn', '0901000012', '$2a$10$hash15', 'DOCTOR', 'MALE', '1982-01-19', '123 Trường Chinh, Thanh Xuân, Hà Nội', '001182011915'),
('Vũ Thị Lan', 'vu.lan@acareclinic.vn', '0901000013', '$2a$10$hash16', 'DOCTOR', 'FEMALE', '1986-11-07', '45 Lê Duẩn, Hoàn Kiếm, Hà Nội', '001186110716'),
-- Xét nghiệm
('Đỗ Văn Nam', 'do.nam@acareclinic.vn', '0901000014', '$2a$10$hash17', 'DOCTOR', 'MALE', '1984-09-03', '78 Nguyễn Lương Bằng, Đống Đa, Hà Nội', '001184090317'),
('Phạm Thị Thảo', 'pham.thao@acareclinic.vn', '0901000015', '$2a$10$hash18', 'DOCTOR', 'FEMALE', '1989-05-26', '12 Cát Linh, Đống Đa, Hà Nội', '001189052618');

-- STAFF (7 người)
INSERT INTO users (full_name, email, phone, password_hash, role, gender, birth_date, address, id_number) VALUES
('Nguyễn Thị Hương', 'nguyen.huong.staff@acareclinic.vn', '0902000001', '$2a$10$hash19', 'STAFF', 'FEMALE', '1992-03-10', '23 Nguyễn Văn Cừ, Long Biên, Hà Nội', '001192031019'),
('Trần Văn Bình', 'tran.binh@acareclinic.vn', '0902000002', '$2a$10$hash20', 'STAFF', 'MALE', '1990-06-15', '45 Minh Khai, Hai Bà Trưng, Hà Nội', '001190061520'),
('Lê Thị Nga', 'le.nga@acareclinic.vn', '0902000003', '$2a$10$hash21', 'STAFF', 'FEMALE', '1993-08-22', '67 Phố Huế, Hai Bà Trưng, Hà Nội', '001193082221'),
('Phạm Văn Tú', 'pham.tu@acareclinic.vn', '0902000004', '$2a$10$hash22', 'STAFF', 'MALE', '1991-12-05', '89 Đê La Thành, Đống Đa, Hà Nội', '001191120522'),
('Hoàng Thị Dung', 'hoang.dung@acareclinic.vn', '0902000005', '$2a$10$hash23', 'STAFF', 'FEMALE', '1994-04-18', '12 Xã Đàn, Đống Đa, Hà Nội', '001194041823'),
('Vũ Văn Cường', 'vu.cuong@acareclinic.vn', '0902000006', '$2a$10$hash24', 'STAFF', 'MALE', '1988-07-30', '34 Khâm Thiên, Đống Đa, Hà Nội', '001188073024'),
('Đỗ Thị Liên', 'do.lien@acareclinic.vn', '0902000007', '$2a$10$hash25', 'STAFF', 'FEMALE', '1995-11-12', '56 Ô Chợ Dừa, Đống Đa, Hà Nội', '001195111225');

-- PATIENT (25 người)
INSERT INTO users (full_name, email, phone, password_hash, role, gender, birth_date, address, id_number) VALUES
('Lê Văn Hùng', 'le.hung@gmail.com', '0903000001', '$2a$10$hash26', 'PATIENT', 'MALE', '1995-06-12', '123 Lê Duẩn, Đống Đa, Hà Nội', '001195061226'),
('Nguyễn Thị Mai', 'nguyen.mai.patient@gmail.com', '0903000002', '$2a$10$hash27', 'PATIENT', 'FEMALE', '1998-09-09', '45 Kim Mã, Ba Đình, Hà Nội', '001198090927'),
('Trần Văn An', 'tran.an@gmail.com', '0903000003', '$2a$10$hash28', 'PATIENT', 'MALE', '2000-03-15', '67 Giảng Võ, Ba Đình, Hà Nội', '002000031528'),
('Phạm Thị Lan', 'pham.lan.patient@gmail.com', '0903000004', '$2a$10$hash29', 'PATIENT', 'FEMALE', '1992-11-20', '89 Ngọc Khánh, Ba Đình, Hà Nội', '001192112029'),
('Hoàng Văn Bình', 'hoang.binh@gmail.com', '0903000005', '$2a$10$hash30', 'PATIENT', 'MALE', '1988-07-08', '12 Láng Hạ, Đống Đa, Hà Nội', '001188070830'),
('Vũ Thị Hà', 'vu.ha@gmail.com', '0903000006', '$2a$10$hash31', 'PATIENT', 'FEMALE', '1990-02-25', '34 Thái Hà, Đống Đa, Hà Nội', '001190022531'),
('Đỗ Văn Tùng', 'do.tung@gmail.com', '0903000007', '$2a$10$hash32', 'PATIENT', 'MALE', '1985-05-14', '56 Chùa Bộc, Đống Đa, Hà Nội', '001185051432'),
('Lý Thị Ngọc', 'ly.ngoc@gmail.com', '0903000008', '$2a$10$hash33', 'PATIENT', 'FEMALE', '1993-08-30', '78 Tôn Đức Thắng, Đống Đa, Hà Nội', '001193083033'),
('Bùi Văn Đức', 'bui.duc@gmail.com', '0903000009', '$2a$10$hash34', 'PATIENT', 'MALE', '1997-12-05', '90 Trường Chinh, Thanh Xuân, Hà Nội', '001197120534'),
('Ngô Thị Thảo', 'ngo.thao@gmail.com', '0903000010', '$2a$10$hash35', 'PATIENT', 'FEMALE', '2002-04-18', '123 Nguyễn Trãi, Thanh Xuân, Hà Nội', '002002041835'),
('Đinh Văn Long', 'dinh.long@gmail.com', '0903000011', '$2a$10$hash36', 'PATIENT', 'MALE', '1991-09-22', '45 Khương Đình, Thanh Xuân, Hà Nội', '001191092236'),
('Trương Thị Yến', 'truong.yen@gmail.com', '0903000012', '$2a$10$hash37', 'PATIENT', 'FEMALE', '1994-06-11', '67 Giải Phóng, Hoàng Mai, Hà Nội', '001194061137'),
('Phan Văn Hải', 'phan.hai@gmail.com', '0903000013', '$2a$10$hash38', 'PATIENT', 'MALE', '1989-03-07', '89 Minh Khai, Hai Bà Trưng, Hà Nội', '001189030738'),
('Mai Thị Hương', 'mai.huong@gmail.com', '0903000014', '$2a$10$hash39', 'PATIENT', 'FEMALE', '1996-10-28', '12 Bạch Mai, Hai Bà Trưng, Hà Nội', '001196102839'),
('Cao Văn Minh', 'cao.minh@gmail.com', '0903000015', '$2a$10$hash40', 'PATIENT', 'MALE', '2001-01-19', '34 Phố Huế, Hai Bà Trưng, Hà Nội', '002001011940'),
('Dương Thị Linh', 'duong.linh@gmail.com', '0903000016', '$2a$10$hash41', 'PATIENT', 'FEMALE', '1987-11-07', '56 Thanh Nhàn, Hai Bà Trưng, Hà Nội', '001187110741'),
('Lương Văn Tuấn', 'luong.tuan@gmail.com', '0903000017', '$2a$10$hash42', 'PATIENT', 'MALE', '1999-05-26', '78 Đê La Thành, Đống Đa, Hà Nội', '001199052642'),
('Tạ Thị Phương', 'ta.phuong@gmail.com', '0903000018', '$2a$10$hash43', 'PATIENT', 'FEMALE', '1992-08-15', '90 Xã Đàn, Đống Đa, Hà Nội', '001192081543'),
('Hồ Văn Quân', 'ho.quan@gmail.com', '0903000019', '$2a$10$hash44', 'PATIENT', 'MALE', '1986-02-09', '123 Ô Chợ Dừa, Đống Đa, Hà Nội', '001186020944'),
('Đặng Thị Thu', 'dang.thu@gmail.com', '0903000020', '$2a$10$hash45', 'PATIENT', 'FEMALE', '1995-12-30', '45 Khâm Thiên, Đống Đa, Hà Nội', '001195123045'),
('Chu Văn Phúc', 'chu.phuc@gmail.com', '0903000021', '$2a$10$hash46', 'PATIENT', 'MALE', '2003-07-04', '67 Nguyễn Lương Bằng, Đống Đa, Hà Nội', '002003070446'),
('Võ Thị Nhung', 'vo.nhung@gmail.com', '0903000022', '$2a$10$hash47', 'PATIENT', 'FEMALE', '1991-04-22', '89 Cát Linh, Đống Đa, Hà Nội', '001191042247'),
('Hà Văn Sơn', 'ha.son@gmail.com', '0903000023', '$2a$10$hash48', 'PATIENT', 'MALE', '1998-09-16', '12 Nguyễn Chí Thanh, Đống Đa, Hà Nội', '001198091648'),
('Lại Thị Trang', 'lai.trang@gmail.com', '0903000024', '$2a$10$hash49', 'PATIENT', 'FEMALE', '2000-06-08', '34 Phạm Ngọc Thạch, Đống Đa, Hà Nội', '002000060849'),
('Kiều Văn Thắng', 'kieu.thang@gmail.com', '0903000025', '$2a$10$hash50', 'PATIENT', 'MALE', '1984-10-13', '56 Tây Sơn, Đống Đa, Hà Nội', '001184101350');

-- 2. ROOMS (20 phòng - tên liên quan chức năng)
INSERT INTO rooms (name, room_type, location) VALUES
-- Tầng 1: Tiếp nhận & Xét nghiệm
('Phòng Tiếp nhận 1', 'Reception', 'Tầng 1'),
('Phòng Tiếp nhận 2', 'Reception', 'Tầng 1'),
('Phòng Xét nghiệm Hóa sinh', 'Laboratory', 'Tầng 1'),
('Phòng Xét nghiệm Vi sinh', 'Laboratory', 'Tầng 1'),
('Phòng Lấy mẫu', 'Sampling', 'Tầng 1'),
-- Tầng 2: Khám Nội & Nhi
('Phòng Khám Nội tổng quát 1', 'General', 'Tầng 2'),
('Phòng Khám Nội tổng quát 2', 'General', 'Tầng 2'),
('Phòng Khám Nội tổng quát 3', 'General', 'Tầng 2'),
('Phòng Khám Nhi 1', 'Pediatrics', 'Tầng 2'),
('Phòng Khám Nhi 2', 'Pediatrics', 'Tầng 2'),
('Phòng Tiêm chủng', 'Vaccination', 'Tầng 2'),
-- Tầng 3: Tai Mũi Họng & Răng Hàm Mặt
('Phòng Khám Tai Mũi Họng 1', 'ENT', 'Tầng 3'),
('Phòng Khám Tai Mũi Họng 2', 'ENT', 'Tầng 3'),
('Phòng Khám Răng Hàm Mặt 1', 'Dentistry', 'Tầng 3'),
('Phòng Khám Răng Hàm Mặt 2', 'Dentistry', 'Tầng 3'),
-- Tầng 4: Chẩn đoán hình ảnh
('Phòng Siêu âm 1', 'Ultrasound', 'Tầng 4'),
('Phòng Siêu âm 2', 'Ultrasound', 'Tầng 4'),
('Phòng X-quang', 'Radiology', 'Tầng 4'),
-- Tầng 5: Sản phụ khoa
('Phòng Khám Sản phụ khoa 1', 'Obstetrics', 'Tầng 5'),
('Phòng Khám Sản phụ khoa 2', 'Obstetrics', 'Tầng 5');

-- 3. SERVICES (25 dịch vụ - tên liên quan phòng)
INSERT INTO services (name, price, description, active, duration_min) VALUES
-- Khám tổng quát
('Khám sức khỏe tổng quát', 200000, 'Khám tổng quát toàn diện, đánh giá tình trạng sức khỏe', TRUE, 30),
('Khám nội khoa chuyên sâu', 250000, 'Khám chuyên khoa tim mạch, hô hấp, tiêu hóa', TRUE, 40),
('Tư vấn sức khỏe cá nhân', 150000, 'Tư vấn chế độ dinh dưỡng, lối sống lành mạnh', TRUE, 20),
-- Nhi khoa
('Khám sức khỏe định kỳ trẻ em', 220000, 'Khám định kỳ, theo dõi phát triển của trẻ', TRUE, 30),
('Khám bệnh nhi khoa', 200000, 'Khám điều trị các bệnh thường gặp ở trẻ em', TRUE, 25),
('Tư vấn dinh dưỡng trẻ em', 180000, 'Tư vấn chế độ ăn uống phù hợp theo lứa tuổi', TRUE, 20),
('Tiêm chủng mở rộng', 150000, 'Tiêm phòng các bệnh theo lịch Bộ Y tế', TRUE, 15),
('Tiêm chủng dịch vụ', 300000, 'Vắc xin dịch vụ (Rotavirus, Viêm gan B, HPV...)', TRUE, 15),
-- Sản phụ khoa
('Khám sản phụ khoa tổng quát', 250000, 'Khám phụ khoa định kỳ, tầm soát ung thư', TRUE, 35),
('Khám thai định kỳ', 280000, 'Theo dõi sức khỏe mẹ và thai nhi', TRUE, 30),
('Tư vấn kế hoạch hóa gia đình', 150000, 'Tư vấn các biện pháp tránh thai an toàn', TRUE, 20),
-- Tai Mũi Họng
('Khám Tai Mũi Họng tổng quát', 180000, 'Khám và điều trị các bệnh về tai, mũi, họng', TRUE, 25),
('Nội soi Tai Mũi Họng', 250000, 'Nội soi chẩn đoán bệnh lý tai mũi họng', TRUE, 20),
('Xông khí dung', 100000, 'Điều trị viêm đường hô hấp, viêm xoang', TRUE, 15),
-- Răng Hàm Mặt
('Khám răng tổng quát', 150000, 'Khám và tư vấn tình trạng răng miệng', TRUE, 20),
('Lấy cao răng', 200000, 'Vệ sinh răng miệng, loại bỏ mảng bám', TRUE, 30),
('Trám răng', 250000, 'Trám răng sâu, phục hồi răng', TRUE, 30),
('Nhổ răng', 200000, 'Nhổ răng sữa, răng vĩnh viễn đơn giản', TRUE, 20),
-- Xét nghiệm
('Xét nghiệm máu tổng quát', 150000, 'Công thức máu, đếm tế bào máu', TRUE, 15),
('Xét nghiệm sinh hóa cơ bản', 200000, 'Glucose, Ure, Creatinine, AST, ALT', TRUE, 15),
('Xét nghiệm nước tiểu', 120000, 'Phân tích nước tiểu cơ bản', TRUE, 15),
('Xét nghiệm chức năng gan', 250000, 'Bộ xét nghiệm đánh giá chức năng gan', TRUE, 15),
('Xét nghiệm chức năng thận', 220000, 'Bộ xét nghiệm đánh giá chức năng thận', TRUE, 15),
-- Chẩn đoán hình ảnh
('Siêu âm bụng tổng quát', 300000, 'Siêu âm gan, mật, tụy, lách, thận', TRUE, 25),
('Siêu âm tim', 350000, 'Đánh giá cấu trúc và chức năng tim', TRUE, 30),
('Chụp X-quang phổi', 180000, 'Chụp X-quang phổi thẳng', TRUE, 10),
('Đo điện tim', 150000, 'Ghi điện tâm đồ, phát hiện rối loạn nhịp tim', TRUE, 15),
('Đo huyết áp 24 giờ', 400000, 'Theo dõi huyết áp liên tục trong 24 giờ', TRUE, 5);

-- 4. APPOINTMENTS (30 lịch hẹn mẫu)
INSERT INTO appointments (patient_id, doctor_id, room_id, start_time, status, note) VALUES
-- Tháng 11/2025 - DONE (note = service_id)
(26, 4, 6, '2025-11-01 08:00:00', 'DONE', '1'),
(27, 5, 7, '2025-11-01 09:00:00', 'DONE', '2'),
(28, 6, 8, '2025-11-01 10:00:00', 'DONE', '1'),
(29, 7, 9, '2025-11-02 08:30:00', 'DONE', '4'),
(30, 8, 10, '2025-11-02 09:30:00', 'DONE', '6'),
(31, 9, 19, '2025-11-03 08:00:00', 'DONE', '10'),
(32, 10, 20, '2025-11-03 10:00:00', 'DONE', '9'),
(33, 11, 12, '2025-11-04 08:00:00', 'DONE', '12'),
(34, 12, 13, '2025-11-04 09:00:00', 'DONE', '13'),
(35, 13, 14, '2025-11-05 08:00:00', 'DONE', '15'),
(36, 14, 15, '2025-11-05 10:00:00', 'DONE', '16'),
(37, 15, 16, '2025-11-06 08:30:00', 'DONE', '24'),
(38, 16, 17, '2025-11-06 09:30:00', 'DONE', '25'),
(39, 17, 3, '2025-11-07 08:00:00', 'DONE', '19'),
(40, 18, 4, '2025-11-07 09:00:00', 'DONE', '22'),

-- Tháng 11/2025 - PENDING
(46, 6, 8, '2025-11-23 08:00:00', 'PENDING', '2'),
(47, 8, 10, '2025-11-23 09:00:00', 'PENDING', '5'),
(48, 10, 20, '2025-11-24 08:00:00', 'PENDING', '9'),
(49, 13, 14, '2025-11-24 09:00:00', 'PENDING', '15'),
(50, 15, 16, '2025-11-25 08:30:00', 'PENDING', '24'),

-- Tháng 11/2025 - CANCELLED
(26, 12, 13, '2025-11-10 08:00:00', 'CANCELLED', '12'),
(28, 16, 17, '2025-11-11 09:00:00', 'CANCELLED', '25'),
(30, 4, 6, '2025-11-12 08:00:00', 'CANCELLED', '1'),
(32, 5, 7, '2025-11-13 09:00:00', 'CANCELLED', '2'),
(34, 7, 9, '2025-11-14 10:00:00', 'CANCELLED', '4');

-- 5. ACTIVITY_LOG (Ghi nhận hoạt động)
INSERT INTO activity_log (type, message, time) VALUES
('SYSTEM', 'Khởi tạo cơ sở dữ liệu A*Care Clinic', '2025-01-01 00:00:00'),
('SYSTEM', 'Nhập dữ liệu mẫu 50 người dùng', '2025-01-01 00:01:00'),
('SYSTEM', 'Nhập dữ liệu 20 phòng khám', '2025-01-01 00:02:00'),
('SYSTEM', 'Nhập dữ liệu 28 dịch vụ y tế', '2025-01-01 00:03:00'),
('USER_LOGIN', 'Admin Nguyễn Văn Quản đăng nhập hệ thống', '2025-11-01 07:30:00'),
('APPOINTMENT_CREATE', 'Tạo lịch hẹn ID 1 - Bệnh nhân Lê Văn Hùng', '2025-11-01 07:45:00'),
('APPOINTMENT_DONE', 'Hoàn thành lịch hẹn ID 1', '2025-11-01 09:00:00'),
('USER_LOGIN', 'Bác sĩ Phạm Thu Lan đăng nhập', '2025-11-01 07:50:00'),
('APPOINTMENT_CREATE', 'Tạo lịch hẹn ID 16 - Confirmed', '2025-11-18 10:00:00'),
('APPOINTMENT_CANCEL', 'Hủy lịch hẹn ID 26', '2025-11-10 07:00:00'),
('SYSTEM', 'Sao lưu dữ liệu định kỳ', '2025-11-15 00:00:00'),
('USER_CREATE', 'Tạo tài khoản mới - Kiều Văn Thắng', '2025-11-16 14:30:00');
