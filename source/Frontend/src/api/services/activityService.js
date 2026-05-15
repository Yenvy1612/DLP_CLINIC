import { httpDelete, httpGet } from "./http";

export const activityService = {
    async getRecent() {
        return httpGet("/activities/recent");
    },

    async getRecentAdmin() {
        return httpGet("/activities/recent/admin");
    },

    async deleteRecent(notificationId) {
        return httpDelete(`/activities/recent/admin/${notificationId}`);
    },

    async deleteAdmin(notificationId) {
        return httpDelete(`/activities/recent/admin/${notificationId}`);
    },

    async getRecentByUser(userId) {
        return httpGet(`/activities/recent/user/${userId}`);
    },

    async getCountByUser(userId) {
        return httpGet(`/activities/recent/user/${userId}/count`);
    },

    async deleteByUser(userId, notificationId) {
        return httpDelete(`/activities/recent/user/${userId}/${notificationId}`);
    },
};
