import { httpDelete, httpGet } from "./http";

export const activityService = {
    async getRecent() {
        return httpGet("/activities/recent");
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
