import { httpGet, httpPost, httpPut } from "./http";

export const authService = {
    async login({ email, password }) {
        return httpPost("/auth/login", {
            username: email,
            password,
        });
    },
    async register(payload) {
        return httpPost("/auth/register", payload);
    },
    async me() {
        return httpGet("/auth/me");
    },
    async updateMe(payload) {
        return httpPut("/auth/me", payload);
    },
    async getMyDoctorProfile() {
        return httpGet("/auth/me/doctor-profile");
    },
    async updateMyDoctorProfile(payload) {
        return httpPut("/auth/me/doctor-profile", payload);
    },
    async changePassword(payload) {
        return httpPut("/auth/change-password", payload);
    },
    async logout() {
        return httpPost("/auth/logout", {});
    },
};
