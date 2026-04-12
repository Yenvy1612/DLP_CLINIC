import axios from "axios";
import { getCurrentUser } from "../utils/authUtils";

export const apiClient = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json"
    }
});

export const geminiClient = axios.create({
    baseURL: "http://localhost:8005",
    headers: {
        "Content-Type": "application/json"
    }
});

const extractErrorMessage = (error) => {
    const payload = error?.response?.data;

    if (typeof payload === "string" && payload.trim()) {
        return payload;
    }

    if (payload?.message) {
        return payload.message;
    }

    if (payload?.error) {
        return payload.error;
    }

    if (error?.message) {
        return error.message;
    }

    return "Yeu cau that bai";
};

apiClient.interceptors.request.use((config) => {
    const auth = getCurrentUser();
    const token = auth?.accessToken || auth?.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject(new Error(extractErrorMessage(error)))
);