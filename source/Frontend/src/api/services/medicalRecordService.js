import { httpGet, httpPost, httpPut } from "./http";

export const medicalRecordService = {
    async getByPatientId(patientId) {
        return httpGet(`/medical-records/patient/${patientId}`);
    },
    async getByDoctorId(doctorId) {
        return httpGet(`/medical-records/doctor/${doctorId}`);
    },
    async getByAppointmentId(appointmentId) {
        return httpGet(`/medical-records/appointment/${appointmentId}`);
    },
    async getById(id) {
        return httpGet(`/medical-records/${id}`);
    },
    async create(record) {
        return httpPost("/medical-records", record);
    },
    async update(id, record) {
        return httpPut(`/medical-records/${id}`, record);
    },
};
