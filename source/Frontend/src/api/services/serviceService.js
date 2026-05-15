import { httpDelete, httpGet, httpPost, httpPut, toQueryString } from "./http";

export const serviceService = {
    async getAll() {
        return httpGet("/services");
    },
    async getById(id) {
        return httpGet(`/services/${id}`);
    },
    async getByName(name) {
        return httpGet(`/services${toQueryString({ name })}`);
    },
    async search(searchParams = {}) {
        return httpGet(`/services${toQueryString(searchParams)}`);
    },
    async create(service) {
        return httpPost("/services", service);
    },
    async update(id, update) {
        return httpPut(`/services/${id}`, update);
    },
    async remove(id) {
        return httpDelete(`/services/${id}`);
    },
};
