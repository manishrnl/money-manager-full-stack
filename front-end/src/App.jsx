import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {Toaster} from "react-hot-toast";
import Home from "./pages/Home.jsx";
import Income from "./pages/Income.jsx";
import Expense from "./pages/Expense.jsx";
import Category from "./pages/Category.jsx";
import Filter from "./pages/Filter.jsx";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx"
import ActivateAccounts from "./components/ActivateAccounts.jsx";
import PageNotFound from "./pages/PageNotFound.jsx";
import TransactionOverlay from "./components/TransactionOverlay.jsx";


function App() {
    const isAuthenticated = !!localStorage.getItem("accessToken");

    return (
        <>
            <BrowserRouter>
                <Routes>
                    {/* 1. Handle the Root Path (/) */}
                    <Route
                        path="/"
                        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"}
                                           replace/>}
                    />

                    {/* 2. Protected Routes */}
                    <Route path="/activate" element={<ActivateAccounts/>}/>
                    <Route path="/dashboard" element={<ProtectedRoute><Home/></ProtectedRoute>}/>
                    <Route path="/income" element={<ProtectedRoute><Income/></ProtectedRoute>}/>
                    <Route path="/expense" element={<ProtectedRoute><Expense/></ProtectedRoute>}/>
                    <Route path="/category" element={<ProtectedRoute><Category/></ProtectedRoute>}/>
                    <Route path="/filter" element={<ProtectedRoute><Filter/></ProtectedRoute>}/>


                    <Route path="/login" element={<Login/>}/>
                    <Route path="/signup" element={<Signup/>}/>
                    <Route path="/page-not-found" element={<PageNotFound/>}/>

                    {/* Optional: Catch-all for 404s */}
                    <Route path="*" element={<Navigate to="/page-not-found" replace/>}/>
                </Routes>
            </BrowserRouter>
            <Toaster position="top-center" reverseOrder={false}/>
        </>
    );
}

export default App;