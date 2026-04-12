import { httpGet } from "./http";

export const activityService = {
    async getRecent() {
        return httpGet("/activities/recent");
    },
};
