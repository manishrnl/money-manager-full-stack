import toast from "react-hot-toast";
import { AlertCircle, CheckCircle2, Info, XCircle } from "lucide-react";

/**
 * @param {string} message - The text to display
 * @param {'success' | 'error' | 'info' | 'warning'} type - The theme of the toast
 */
export const ShowPremiumToast = (message, type = "info") => {
    // 1. Define theme configurations
    const themes = {
        success: {
            icon: <CheckCircle2 className="w-5 h-5 text-emerald-500" />,
            title: "Success",
            bgColor: "bg-emerald-50",
            borderColor: "border-emerald-100",
            titleColor: "text-emerald-800",
            toastType: "success"
        },
        error: {
            icon: <XCircle className="w-5 h-5 text-red-500" />,
            title: "System Error",
            bgColor: "bg-red-50",
            borderColor: "border-red-100",
            titleColor: "text-red-800",
            toastType: "error"
        },
        warning: {
            icon: <AlertCircle className="w-5 h-5 text-amber-500" />,
            title: "Action Required",
            bgColor: "bg-amber-50",
            borderColor: "border-amber-100",
            titleColor: "text-amber-800",
            toastType: "error" // react-hot-toast uses 'error' styling for warnings usually
        },
        info: {
            icon: <Info className="w-5 h-5 text-blue-500" />,
            title: "Notice",
            bgColor: "bg-blue-50",
            borderColor: "border-blue-100",
            titleColor: "text-blue-800",
            toastType: "blank"
        }
    };

    const theme = themes[type] || themes.info;

    // 2. Trigger the toast
    return toast[theme.toastType](
        (t) => (
            <div className="flex flex-col gap-1 min-w-[250px]">
                <div className="flex items-center gap-2">
                    {theme.icon}
                    <span className={`font-bold text-sm uppercase tracking-wide ${theme.titleColor}`}>
                        {theme.title}
                    </span>
                </div>
                <p className="text-sm text-slate-600 ml-7 leading-relaxed">
                    {message}
                </p>
            </div>
        ),
        {
            duration: 4000,
            position: 'top-right',
            style: {
                borderRadius: '16px',
                background: '#ffffff',
                border: '1px solid #e2e8f0',
                boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
                padding: '12px 16px',
            },
        }
    );
};

export default ShowPremiumToast;