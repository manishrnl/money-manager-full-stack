import { createContext, useState } from "react";

export const AppContext = createContext();

export const AppContextProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem("accessToken");
        const savedData = localStorage.getItem("user_data");

        // If we have a token AND saved data, use the saved data to prevent a DB call
        if (token && savedData) {
            try {
                return JSON.parse(savedData);
            } catch (e) {
                console.error("Error parsing user_data from localStorage", e);
                return { loggedIn: true }; // Fallback
            }
        }

        // If we have a token but NO data, we return a flag so UseUser knows to fetch
        return token ? { loggedIn: true } : null;
    });

    const clearUser = () => {
        setUser(null);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user_data"); // Added: Clear data on logout
    };

    const handleSetUser = (data) => {
        if (data) {
            localStorage.setItem("user_data", JSON.stringify(data));
        }
        setUser(data);
    };

    const contextValue = { user, setUser: handleSetUser, clearUser };

    return (
        <AppContext.Provider value={contextValue}>
            {children}
        </AppContext.Provider>
    );
};