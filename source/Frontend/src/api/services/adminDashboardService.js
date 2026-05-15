import { httpGet } from "./http";

export const adminDashboardService = {
    async getSummary() {
        return httpGet("/admin/dashboard/summary");
    },
};
