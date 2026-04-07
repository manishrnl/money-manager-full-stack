import { Navigate, useLocation } from "react-router-dom";

const ProtectedRoute = ({ children }) => {

    const location = useLocation();
    const token = localStorage.getItem("accessToken");

    if (!token) {
        // We pass 'unauthorized: true' in the state so the Login page knows why they are there
        return <Navigate
            to="/login"
            state={{ from: location.pathname, unauthorized: true }}
            replace
        />;
    }

    return children;
};

export default ProtectedRoute;