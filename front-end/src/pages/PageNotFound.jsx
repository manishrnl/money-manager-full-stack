import { Link } from "react-router-dom";
import { Home, AlertCircle } from "lucide-react";

const PageNotFound = () => {
    return (
        <div className="min-h-screen w-full flex items-center justify-center bg-slate-50 px-6">
            <div className="max-w-md w-full text-center">
                {/* Visual Icon */}
                <div className="flex justify-center mb-6">
                    <div className="p-5 bg-red-50 rounded-full">
                        <AlertCircle className="w-16 h-16 text-red-500" />
                    </div>
                </div>

                {/* Text Content */}
                <h1 className="text-9xl font-black text-slate-200">404</h1>
                <h2 className="text-3xl font-bold text-slate-900 mt-[-40px]">Lost in Space?</h2>
                <p className="text-slate-600 mt-4 mb-8">
                    The page you're looking for doesn't exist or has been moved to a different vault.
                </p>

                {/* Action Button */}
                <Link
                    to="/dashboard"
                    className="inline-flex items-center gap-2 bg-violet-950 text-white px-8 py-3 rounded-xl font-semibold shadow-lg shadow-violet-200 hover:bg-violet-800 transition-all active:scale-95"
                >
                    <Home size={18} />
                    Return to Dashboard
                </Link>

                <div className="mt-10">
                    <p className="text-sm text-slate-400">
                        Need help? <Link to="/login" className="text-violet-600 hover:underline">Sign in again</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default PageNotFound;