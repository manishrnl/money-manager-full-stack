import axios from "axios";
import { API_ENDPOINTS, BASE_URL } from "./API_ENDPOINTS.js";

export const AxiosConfig = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
        Accept: "application/json"
    }
});

// Added refresh endpoint to exclusions so we don't send expired tokens TO the refresh call
const excludeEndPoints = [
    "/profile/login",
    "/profile/register",
    "/status",
    "/health",
    "/profile/activate",
    "/profile/refresh"
];

// Request Interceptor
AxiosConfig.interceptors.request.use(
    (config) => {
        const shouldSkipToken = excludeEndPoints.some((endPoint) =>
            config.url?.includes(endPoint)
        );

        if (!shouldSkipToken) {
            const accessToken = localStorage.getItem("accessToken");
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor
AxiosConfig.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // 1. Check for BOTH 401 and 403 based on your backend behavior
        const isUnauthorized = error.response?.status === 401 || error.response?.status === 403;

        if (isUnauthorized && !originalRequest._retry) {
            originalRequest._retry = true;
            const refreshToken = localStorage.getItem("refreshToken");

            if (refreshToken) {
                try {
                    // 2. Corrected the call logic. Use a clean axios instance.
                    // Assuming your backend uses POST for refresh based on previous logic
                    const response = await axios.post(`${BASE_URL}${API_ENDPOINTS.REFRESH_TOKEN(refreshToken)}`);

                    const responseData = response.data.data || response.data;
                    const { accessToken, refreshToken: newRefreshToken } = responseData;

                    // 3. Update Storage
                    localStorage.setItem("accessToken", accessToken);
                    if (newRefreshToken) {
                        localStorage.setItem("refreshToken", newRefreshToken);
                    }

                    // 4. Retry original request
                    originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    return AxiosConfig(originalRequest);
                } catch (refreshError) {
                    console.error("Silent refresh failed:", refreshError);
                    localStorage.clear();
                    window.location.href = "/login";
                    return Promise.reject(refreshError);
                }
            } else {
                localStorage.clear();
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
);

export default AxiosConfig;