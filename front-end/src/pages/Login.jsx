import { useContext, useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Input from "../components/Input.jsx";
import { AxiosConfig } from "../util/AxiosConfig.jsx";
import { API_ENDPOINTS } from "../util/API_ENDPOINTS.js";
import toast from "react-hot-toast";
import { LoaderPinwheel, ShieldAlert, AlertCircle } from "lucide-react";
import { AppContext } from "../context/AppContext.jsx";
import ShowPremiumToast from "../util/ShowPremiumToast.jsx";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { setUser } = useContext(AppContext);

    const location = useLocation();
    const hasToasted = useRef(false);

    useEffect(() => {
        document.title = "Login - Money Manager";
        window.scrollTo({ top: 0, behavior: "smooth" });

        const state = location.state;
        if (state?.unauthorized && !hasToasted.current) {
            const pageName = state.from
                ? state.from.replace("/", "").toUpperCase()
                : "THAT PAGE";

            // Using Premium Toast for the route guard message
            ShowPremiumToast(`Access Denied. Please login to view ${pageName}`, "warning");
            hasToasted.current = true;
        }
    }, [location]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        localStorage.clear();
        setIsLoading(true);
        setError("");

        if (!email || !password) {
            setIsLoading(false);
            ShowPremiumToast("Please fill in all fields", "warning");
            return;
        }

        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LOGIN, { email, password });

            if (response.data?.error) {
                ShowPremiumToast(response.data.error.message, "error");
                setIsLoading(false);
                return;
            }

            if (response.status === 200) {
                const userData = response.data.data;

                if (userData && userData.accessToken) {
                    localStorage.setItem("accessToken", userData.accessToken);
                    localStorage.setItem("refreshToken", userData.refreshToken);

                    setUser({
                        id: userData.id,
                        fullName: userData.fullName,
                        email: email,
                        profileImageUrl: userData.profileImageUrl
                    });

                    ShowPremiumToast(`Welcome back, ${userData.fullName}!`, "success");
                    setTimeout(() => navigate("/dashboard"), 500);
                } else {
                    ShowPremiumToast("Login failed: Session token missing.", "error");
                }
            }
        } catch (error) {
            const responseData = error.response?.data;
            const status = error.response?.status;

            const serverMessage = responseData?.error?.message
                || responseData?.message
                || "An unexpected error occurred";

            if (!error.response) {
                ShowPremiumToast("Server unreachable. Check your internet.", "error");
            } else if (status === 404) {
                handleAccountNotFoundCountdown();
            } else {
                ShowPremiumToast(serverMessage, "error");
            }
            console.error("Login Error Details:", responseData);
        } finally {
            setIsLoading(false);
        }
    };

    const handleAccountNotFoundCountdown = () => {
        let secondsLeft = 7;

        // Custom JSX for the countdown to match the Premium style
        const countdownContent = (s) => (
            <div className="flex flex-col gap-1 min-w-[250px]">
                <div className="flex items-center gap-2">
                    <ShieldAlert className="w-5 h-5 text-amber-500" />
                    <span className="font-bold text-sm uppercase tracking-wide text-amber-800">Account Not Found</span>
                </div>
                <p className="text-sm text-slate-600 ml-7">
                    No account with this email. Redirecting to Signup in {s}s...
                </p>
            </div>
        );

        const toastId = toast.error(countdownContent(secondsLeft), {
            duration: 6000,
            style: { background: '#fffbeb', border: '1px solid #fde68a' },
            icon: null
        });

        const timer = setInterval(() => {
            secondsLeft -= 1;
            if (secondsLeft <= 0) {
                clearInterval(timer);
                toast.dismiss(toastId);
                navigate("/signup");
            } else {
                toast.error(countdownContent(secondsLeft), { id: toastId });
            }
        }, 1000);
    };

    return (
        <div className="h-screen w-full relative flex items-center justify-center overflow-hidden bg-slate-50">
            <div className="relative z-10 bg-white/90 backdrop-blur-md rounded-2xl shadow-2xl w-[90%] max-w-md p-10 border border-white/20">
                <h3 className="text-3xl font-bold text-slate-900 text-center mb-2">Welcome Back</h3>
                <p className="text-sm text-slate-600 text-center mb-8">Please enter your details to Login</p>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <Input
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        label="Email Address"
                        placeholder="sample@zohomail.in"
                        type="email"
                    />

                    <Input
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        label="Password"
                        placeholder="*************"
                        type="password"
                    />

                    <button
                        disabled={isLoading}
                        type="submit"
                        className={`w-full font-semibold py-3 rounded-lg transition-all mt-4 shadow-lg flex items-center justify-center gap-2 
                        ${isLoading ? 'bg-violet-800 cursor-not-allowed' : 'bg-violet-950 hover:bg-violet-500 shadow-blue-200 text-white'}`}>
                        {isLoading ? (
                            <><LoaderPinwheel className="animate-spin w-5 h-5 text-white" /><span>Logging In...</span></>
                        ) : ("Log In")}
                    </button>

                    <p className="text-center text-sm text-slate-600 mt-4">
                        Don't have an account? <span
                        className="text-blue-600 font-medium cursor-pointer hover:underline"
                        onClick={() => navigate('/signup')}>Signup</span>
                    </p>
                </form>
            </div>
        </div>
    );
};

export default Login;