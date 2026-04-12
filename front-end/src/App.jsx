import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios"; // or your AxiosConfig
import { Toaster } from "react-hot-toast";
import Home from "./pages/Home.jsx";
import Income from "./pages/Income.jsx";
import Expense from "./pages/Expense.jsx";
import Category from "./pages/Category.jsx";
import Filter from "./pages/Filter.jsx";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import ActivateAccounts from "./components/ActivateAccounts.jsx";
import PageNotFound from "./pages/PageNotFound.jsx";
import PremiumLoader from "./hooks/PremiumLoader.jsx"; // Import your loader
import { BASE_URL } from "./util/API_ENDPOINTS.js";

function App() {
    const [isServerLoading, setIsServerLoading] = useState(true);
    const [isDone, setIsDone] = useState(false); // New state
    const isAuthenticated = !!localStorage.getItem("accessToken");

    useEffect(() => {
        const wakeUpServer = async () => {
            try {
                await axios.get(`${BASE_URL}/health`); // Hit any endpoint to wake up the server
                setIsDone(true); // Tell loader to hit 100%
                
                // Wait 800ms for the animation to finish
                setTimeout(() => {
                    setIsServerLoading(false);
                }, 800);
            } catch (error) {
                setIsServerLoading(false);
            }
        };
        wakeUpServer();
    }, []);

    if (isServerLoading) {
        return <PremiumLoader isDone={isDone} />;
    }

    // 2. Once server is awake, render the normal application
    return (
        <>
            <BrowserRouter>
                <Routes>
                    {/* Handle the Root Path (/) */}
                    <Route
                        path="/"
                        element={
                            <Navigate
                                to={isAuthenticated ? "/dashboard" : "/login"}
                                replace
                            />
                        }
                    />

                    {/* Protected Routes */}
                    <Route path="/activate" element={<ActivateAccounts />} />
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <Home />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/income"
                        element={
                            <ProtectedRoute>
                                <Income />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/expense"
                        element={
                            <ProtectedRoute>
                                <Expense />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/category"
                        element={
                            <ProtectedRoute>
                                <Category />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/filter"
                        element={
                            <ProtectedRoute>
                                <Filter />
                            </ProtectedRoute>
                        }
                    />

                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/page-not-found" element={<PageNotFound />} />

                    {/* Catch-all for 404s */}
                    <Route
                        path="*"
                        element={<Navigate to="/page-not-found" replace />}
                    />
                </Routes>
            </BrowserRouter>
            <Toaster position="top-center" reverseOrder={false} />
        </>
    );
}

export default App;
