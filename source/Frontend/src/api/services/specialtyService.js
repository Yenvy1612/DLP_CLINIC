import { httpGet } from "./http";

export const specialtyService = {
    async getAll() {
        return httpGet("/specialties");
    },
};