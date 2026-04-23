# API Reference

Danh sách dưới đây chỉ gồm các API mà frontend của web đang gọi thực tế.

## Auth
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`
- `GET /api/auth/me`
- `PUT /api/auth/me`
- `GET /api/auth/me/doctor-profile`
- `PUT /api/auth/me/doctor-profile`
- `PUT /api/auth/change-password`
- `POST /api/auth/logout`

## Users
- `GET /api/users`
- `GET /api/users/search`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `GET /api/users/doctor`
- `GET /api/users/{id}/doctor-profile`
- `PUT /api/users/{id}/doctor-profile`

## Services
- `GET /api/services`
- `GET /api/services/search`
- `GET /api/services/{id}`
- `POST /api/services`
- `PUT /api/services/{id}`
- `DELETE /api/services/{id}`

## Specialty
- `GET /api/specialties`

## Appointments
- `POST /api/appointments`
- `POST /api/appointments/book`
- `GET /api/appointments`
- `GET /api/appointments/filter`
- `GET /api/appointments/{id}`
- `PUT /api/appointments/{id}`
- `DELETE /api/appointments/{id}`
- `PATCH /api/appointments/{id}/status`
- `PATCH /api/appointments/done/{id}`
- `PATCH /api/appointments/cancel/{id}`
- `GET /api/appointments/today/pending`
- `GET /api/appointments/month/done`
- `GET /api/appointments/month/done/{doctorId}`
- `GET /api/appointments/availability`
- `GET /api/appointments/doctor-availability`
- `GET /api/appointments/doctors-by-service`
- `GET /api/appointments/pending/patient/{patientId}`
- `GET /api/appointments/not-pending/patient/{patientId}`
- `GET /api/appointments/history/patient/{patientId}`
- `GET /api/appointments/pending/doctor/{doctorId}`
- `GET /api/appointments/doctor/{doctorId}`
- `GET /api/doctor/statistics/dashboard`
- `GET /api/doctor/statistics/patients/{patientId}/appointments`

## Activities
- `GET /api/activities/recent`
- `GET /api/activities/recent/admin`
- `GET /api/activities/recent/user/{userId}`
- `GET /api/activities/recent/user/{userId}/count`
- `DELETE /api/activities/recent/admin/{notificationId}`
- `DELETE /api/activities/recent/user/{userId}/{notificationId}`

## Admin Dashboard
- `GET /api/admin/dashboard/summary`
