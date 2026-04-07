import React, {useEffect, useState} from 'react';
import {Link, useSearchParams} from 'react-router-dom';
import {API_ENDPOINTS} from "../util/API_ENDPOINTS.js";
import AxiosConfig from "../util/AxiosConfig.jsx";

const ActivateAccount = () => {
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState('loading'); // loading, success, error
    const [message, setMessage] = useState('');

    // Extract the token from the URL: ?activationToken=xxxx
    const token = searchParams.get("activationToken");

    useEffect(() => {
        document.title = "Activate - Money Manager Account";
        window.scrollTo({top: 0, behavior: "smooth"});
        const verifyToken = async () => {
            if (!token) {
                setStatus('error');
                setMessage("Invalid or missing activation token.");
                return;
            }

            try {
                // Calling your Spring Boot Backend
                const response = await AxiosConfig.get(API_ENDPOINTS.ACTIVATE_ACCOUNTS(token));

                // Assuming Spring Boot returns: { "data": "Profile is Activated Successfully", ... }
                setMessage(response.data.data);
                setStatus('success');
            } catch (err) {
                // Handle errors (e.g., token expired, already used, or server down)
                const errorMsg = err.response?.data?.data || "Verification failed. The link may be expired.";
                setMessage(errorMsg);
                setStatus('error');
            }
        };

        verifyToken();
    }, [token]);

    // --- Loading State ---
    if (status === 'loading') {
        return (
            <div
                className="flex flex-col items-center justify-center min-h-screen bg-slate-50">
                <div
                    className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
                <p className="mt-4 text-slate-600 font-medium">Verifying your account...</p>
            </div>
        );
    }

    // --- Success & Error Layout ---
    return (
        <div className="bg-slate-50 flex items-center justify-center min-h-screen p-4">
            <div
                className="max-w-md w-full bg-white rounded-2xl shadow-xl overflow-hidden border border-slate-100">

                {/* Visual Indicator Bar */}
                <div
                    className={`h-2 ${status === 'success' ? 'bg-emerald-500' : 'bg-red-500'}`}></div>

                <div className="p-8 text-center">
                    {/* Icon */}
                    <div className="mb-6 flex justify-center">
                        <div
                            className={`p-4 rounded-full ${status === 'success' ? 'bg-emerald-100' : 'bg-red-100'}`}>
                            {status === 'success' ? (
                                <svg className="w-12 h-12 text-emerald-600" fill="none"
                                     stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round"
                                          strokeWidth="2.5" d="M5 13l4 4L19 7"></path>
                                </svg>
                            ) : (
                                <svg className="w-12 h-12 text-red-600" fill="none"
                                     stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round"
                                          strokeWidth="2.5" d="M6 18L18 6M6 6l12 12"></path>
                                </svg>
                            )}
                        </div>
                    </div>

                    <h1 className="text-2xl font-bold text-slate-800 mb-2">
                        {status === 'success' ? 'Great News!' : 'Oops! Something went wrong'}
                    </h1>

                    <p className="text-slate-500 mb-8 leading-relaxed">
                        {message}
                    </p>

                    {/* Action Button */}
                    <Link
                        to="/login"
                        className="block w-full bg-slate-900 hover:bg-slate-800 text-white font-semibold py-3 rounded-lg transition duration-200 shadow-lg shadow-slate-200"
                    >
                        {status === 'success' ? 'Go to Dashboard' : 'Back to Login'}
                    </Link>
                </div>

                <div
                    className="bg-slate-50 p-4 border-t border-slate-100 text-center text-xs text-slate-400">
                    Secure verification by Money Manager &bull; 2026
                </div>
            </div>
        </div>
    );
};

export default ActivateAccount;