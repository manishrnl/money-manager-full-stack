import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useRef } from "react";
import { API_ENDPOINTS } from "../util/API_ENDPOINTS.js";
import { AppContext } from "../context/AppContext.jsx";
import AxiosConfig from "../util/AxiosConfig.jsx";

export const UseUser = () => {
    const { setUser, clearUser, user } = useContext(AppContext);
    const navigate = useNavigate();
    const isFetching = useRef(false);

    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken");
        const refreshToken = localStorage.getItem("refreshToken");

        // Check if we have ANY way to authenticate
        if (!accessToken && !refreshToken) {
            navigate("/login");
            return;
        }

        // If user is already loaded in context, don't fetch again
        if (user && user.id) return;

        const fetchUserInfo = async () => {
            if (isFetching.current) return;
            isFetching.current = true;

            try {
                const response = await AxiosConfig.get(API_ENDPOINTS.GET_USER_PROFILE);
                const userData = response.data.data || response.data;
                setUser(userData);
            } catch (error) {
                // If it fails here, it means the interceptor also failed to refresh
                clearUser();
                // navigate("/login"); // Interceptor already handles the redirect
            } finally {
                isFetching.current = false;
            }
        };

        fetchUserInfo();
    }, [user, setUser, clearUser, navigate]);

    return { user, setUser, clearUser };
};

export default UseUser;