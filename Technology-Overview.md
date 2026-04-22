# Tổng quan công nghệ và kỹ nghệ nổi bật (A-Care)

## 1. Ngăn xếp chính

- Frontend: React 19 + Vite 7 + React Router + Tailwind CSS 4 + Axios.
- Backend: Java 21 + Spring Boot 3.5 + Spring Web + Spring Data JPA + Spring Security.
- Database: MySQL trên Aiven, migration bằng Flyway.

## 2. Kỹ thuật nổi bật

- Phân quyền theo role (PATIENT, DOCTOR, ADMIN) từ frontend route guard đến backend authorization.
- Xác thực JWT theo mô hình stateless, dùng HttpOnly cookie cho access/refresh token.
- Kiến trúc backend Controller - Service - Repository, tách DTO rõ ràng.
- Lịch khám có kiểm tra xung đột theo bác sĩ và bệnh nhân (không đặt trùng giờ).
- Thống kê và biểu đồ sử dụng Recharts (dashboard admin/doctor).

## 3. Deploy (nhấn mạnh cái này)

- Backend deploy lên Render bằng Docker (multi-stage build Maven + JRE), phù hợp free tier.
- Frontend deploy lên Vercel (SPA rewrite về index.html), phù hợp free tier.
- Database dùng Aiven MySQL, phù hợp free tier mức đủ dùng.
- Tổng thể hệ thống đang theo hướng tối ưu chi phí: mọi thành phần chạy trên gói free ở mức vận hành được.

## 4. Thư viện và công cụ bổ trợ

- UI/UX: Framer Motion, Anime.js, React Icons.
- Integration: EmailJS.
- Vận hành: Dockerfile cho backend, vercel.json cho frontend routing.
