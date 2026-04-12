import { httpDelete, httpGet, httpPatch, httpPost, httpPut, toQueryString } from "./http";

export const appointmentService = {
    async getAll() {
        return httpGet("/appointments");
    },
    async getById(id) {
        return httpGet(`/appointments/${id}`);
    },
    async getByDoctorId(id) {
        return httpGet(`/appointments${toQueryString({ doctorId: id })}`);
    },
    async getToday() {
        return httpGet(`/appointments${toQueryString({ today: true })}`);
    },
    async filter(filters = {}) {
        return httpGet(`/appointments${toQueryString(filters)}`);
    },
    async getAvailability(serviceId, date) {
        return httpGet(`/appointments/availability${toQueryString({ serviceId, date })}`);
    },
    async getDoctorAvailability(doctorId, serviceId, date, appointmentId) {
        return httpGet(`/appointments/doctor-availability${toQueryString({ doctorId, serviceId, date, appointmentId })}`);
    },
    async getDoctorsByService(serviceId) {
        return httpGet(`/appointments/doctors-by-service${toQueryString({ serviceId })}`);
    },
    async create(appointment) {
        return httpPost("/appointments", appointment);
    },
    async bookForPatient(appointment) {
        return httpPost("/appointments", appointment);
    },
    async update(id, updated) {
        return httpPut(`/appointments/${id}`, updated);
    },
    async remove(id, cancelledBy = "PATIENT", cancelReason = "") {
        return httpDelete(`/appointments/${id}${toQueryString({ cancelledBy, cancelReason })}`);
    },
    async markDone(id) {
        return httpPatch(`/appointments/${id}/status${toQueryString({ status: "DONE" })}`);
    },
    async markCancelled(id) {
        return httpPatch(`/appointments/${id}/status${toQueryString({ status: "CANCELLED" })}`);
    },
    async pendingToday() {
        return httpGet(`/appointments${toQueryString({ today: true, pending: true })}`);
    },
    async pendingByDoctorId(id) {
        return httpGet(`/appointments${toQueryString({ doctorId: id, pending: true })}`);
    },
    async pendingByPatientId(id) {
        return httpGet(`/appointments${toQueryString({ patientId: id, pending: true })}`);
    },
    async doneThisMonth() {
        return httpGet(`/appointments${toQueryString({ doneThisMonth: true })}`);
    },
    async doneThisMonthByDoctorId(id) {
        return httpGet(`/appointments${toQueryString({ doneThisMonth: true, doctorId: id })}`);
    },
    async notPendingByPatientId(id) {
        return httpGet(`/appointments${toQueryString({ patientId: id, pending: false })}`);
    },
};
