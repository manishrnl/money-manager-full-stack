import { Fragment, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { assets } from "../assets/assets.js";
import Input from "../components/Input.jsx";
import { AxiosConfig } from "../util/AxiosConfig.jsx";
import { API_ENDPOINTS } from "../util/API_ENDPOINTS.js";
import ShowPremiumToast from "../util/ShowPremiumToast.jsx"; // Ensure this matches your filename
import toast from "react-hot-toast";
import { CheckCircle2, LoaderPinwheel, ShieldAlert } from "lucide-react";
import ProfilePhotSelectors from "../components/ProfilePhotSelectors.jsx";
import uploadProfileImage from "../util/UploadProfileImage.js";
import PremiumLoader from "../hooks/PremiumLoader.jsx";

import {
    checkFullName,
    checkPasswordAndConfirmPassword,
    getPasswordStrength,
    validateEmail,
} from "../util/Validation.js";

const Signup = () => {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [profilePhoto, setProfilePhoto] = useState("");
    const navigate = useNavigate();
    const strengthCheck = getPasswordStrength(password);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        document.title = "Signup - Money Manager";
        window.scrollTo({ top: 0, behavior: "smooth" });
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        // 1. Validations using the new Utility
        const nameError = checkFullName(fullName);
        if (nameError) {
            setIsLoading(false);
            return ShowPremiumToast(nameError, "warning");
        }

        if (!validateEmail(email)) {
            setIsLoading(false);
            return ShowPremiumToast(
                "Please enter a valid email address",
                "error",
            );
        }

        const passwordError = checkPasswordAndConfirmPassword(
            password,
            confirmPassword,
        );
        if (passwordError) {
            setIsLoading(false);
            return ShowPremiumToast(passwordError, "warning");
        }

        try {
            let profileImageUrl = "";
            if (profilePhoto) {
                profileImageUrl =
                    (await uploadProfileImage(profilePhoto)) || "";
            }

            const response = await AxiosConfig.post(API_ENDPOINTS.REGISTER, {
                fullName,
                email,
                password,
                profileImageUrl,
            });

            // 2. SUCCESS: Custom Countdown Toast (Matches Premium Theme)
            if (response.status === 201) {
                let secondsLeft = 20;

                const successContent = (s) => (
                    <div className="flex flex-col gap-1 min-w-[250px]">
                        <div className="flex items-center gap-2">
                            <CheckCircle2 className="w-5 h-5 text-emerald-500" />
                            <span className="font-bold text-sm uppercase tracking-wide text-emerald-800">
                                Success
                            </span>
                        </div>
                        <div className="ml-7 flex flex-col gap-1">
                            <p className="text-sm text-slate-600">
                                Activation link sent to:
                            </p>
                            <span className="font-mono font-bold text-emerald-700 text-xs break-all bg-emerald-50 p-1 rounded">
                                {email}
                            </span>
                            <p className="text-sm text-slate-600">
                                Please activate your account and then try
                                Logging in ...
                            </p>
                            <span className="text-[13px] text-gray-400 italic">
                                Redirecting to Login page in {s} seconds ...
                            </span>
                        </div>
                    </div>
                );

                const toastId = toast.success(successContent(secondsLeft), {
                    duration: 15000,
                });

                const timer = setInterval(() => {
                    secondsLeft -= 1;
                    if (secondsLeft <= 0) {
                        clearInterval(timer);
                        toast.dismiss(toastId);
                        navigate("/login");
                    } else {
                        toast.success(successContent(secondsLeft), {
                            id: toastId,
                        });
                    }
                }, 1000);
            }
        } catch (error) {
            // 3. ERROR: 409 Conflict (Matches Premium Theme)
            if (error.response?.status === 409) {
                let secondsLeft = 5;

                const conflictContent = (s) => (
                    <div className="flex flex-col gap-1 min-w-[250px]">
                        <div className="flex items-center gap-2">
                            <ShieldAlert className="w-5 h-5 text-amber-500" />
                            <span className="font-bold text-sm uppercase tracking-wide text-amber-800">
                                Account Exists
                            </span>
                        </div>
                        <p className="text-sm text-slate-600 ml-7">
                            This email is already registered. Login redirect in{" "}
                            {s}s...
                        </p>
                    </div>
                );

                const toastId = toast.error(conflictContent(secondsLeft), {
                    duration: 6000,
                    style: { border: "1px solid #fde68a" },
                });

                const timer = setInterval(() => {
                    secondsLeft -= 1;
                    if (secondsLeft <= 0) {
                        clearInterval(timer);
                        toast.dismiss(toastId);
                        navigate("/login");
                    } else {
                        toast.error(conflictContent(secondsLeft), {
                            id: toastId,
                        });
                    }
                }, 1000);
                return;
            }

            // 4. GENERAL ERROR: Server Down / Network Issue
            const errorMessage =
                error.response?.data?.message || "Internal Server Error";
            ShowPremiumToast(errorMessage, "error");
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) {
        return <PremiumLoader isDone={isLoading} />;
    }

    return (
        <div className="min-h-screen w-full relative flex items-center justify-center overflow-y-auto py-10 ">
            <img
                src={assets.finance}
                alt="Background"
                className="absolute inset-0 w-full h-full object-cover blur-xl scale-110 z-0"
            />

            <div className="relative z-10 bg-white/90 backdrop-blur-md rounded-2xl shadow-2xl w-[95%] sm:w-[85%] md:w-[65%] lg:w-[45%] p-10 border border-white/20">
                <h3 className="text-3xl font-bold text-slate-900 text-center mb-2">
                    Create an Account
                </h3>
                <p className="text-sm text-slate-600 text-center mb-8">
                    Start tracking your spending by joining with us
                </p>

                <form onSubmit={handleSubmit} className="space-y-8">
                    <div className="flex justify-center mb-6">
                        <ProfilePhotSelectors
                            image={profilePhoto}
                            setImage={setProfilePhoto}
                        />
                    </div>

                    <Input
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        label="Full Name"
                        placeholder="Manish Kumar"
                        type="text"
                    />
                    <Input
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        label="Email Address"
                        placeholder="sample@zohomail.in"
                        type="email"
                    />

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <Input
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            label="Password"
                            placeholder="*************"
                            type="password"
                        />
                        <Input
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            label="Confirm Password"
                            placeholder="*************"
                            type="password"
                        />
                    </div>

                    {password.length > 0 && (
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-2 mt-2 p-3 bg-slate-50 rounded-lg border border-slate-200">
                            {strengthCheck.map((check, index) => (
                                <div
                                    key={index}
                                    className="flex items-center gap-2 text-[11px]"
                                >
                                    <div
                                        className={`w-2 h-2 rounded-full ${check.met ? "bg-green-500" : "bg-slate-300"}`}
                                    />
                                    <span
                                        className={
                                            check.met
                                                ? "text-green-700 font-medium"
                                                : "text-slate-500"
                                        }
                                    >
                                        {check.label}
                                    </span>
                                </div>
                            ))}
                        </div>
                    )}

                    <button
                        disabled={isLoading}
                        type="submit"
                        className={`w-full font-semibold py-3 rounded-lg transition-all mt-4 shadow-lg flex items-center justify-center gap-2 
                        ${isLoading ? "bg-violet-800 cursor-not-allowed text-white" : "bg-violet-950 hover:bg-violet-500 shadow-blue-200 text-white"}`}
                    >
                        {isLoading ? (
                            <>
                                <LoaderPinwheel className="animate-spin w-5 h-5" />
                                <span>Signing Up...</span>
                            </>
                        ) : (
                            "Sign up"
                        )}
                    </button>

                    <p className="text-center text-sm text-slate-600 mt-4">
                        Already have an account?{" "}
                        <span
                            className="text-blue-600 cursor-pointer hover:underline font-medium"
                            onClick={() => navigate("/login")}
                        >
                            Login
                        </span>
                    </p>
                </form>
            </div>
        </div>
    );
};

export default Signup;
