import {
    Layers2,
    Pencil,
    Trash2,
    TrendingUp,
    TrendingDown,
} from "lucide-react";
import { addThousandsSeparator } from "../util/ThousandsSeparator.js";

const TransactionInfoCard = ({
    icon,
    title,
    date,
    amount,
    category,
    type = "income",
    onDelete,
    onUpdate,
}) => {
    const isExpense = type === "expense";

    const renderIcon = () => {
        const isLink =
            typeof icon === "string" &&
            (icon.startsWith("http") || icon.startsWith("/"));
        if (isLink) {
            return (
                <img
                    src={icon}
                    alt={title}
                    className="w-8 h-8 object-contain"
                    onError={(e) => {
                        e.target.src =
                            "https://cdn-icons-png.flaticon.com/512/2454/2454282.png";
                    }}
                />
            );
        }
        if (icon && icon.length > 0)
            return <span className="text-2xl leading-none">{icon}</span>;
        return <Layers2 size={20} className="text-slate-400" />;
    };

    return (
        <div className="group flex items-center gap-4 p-3 bg-white rounded-2xl hover:bg-gray-50 transition-all border border-transparent hover:border-gray-100 shadow-sm">
            {/* Left: Icon Container */}
            <div className="w-12 h-12 shrink-0 flex items-center justify-center bg-white border border-gray-100 rounded-xl group-hover:scale-105 transition-transform">
                {renderIcon()}
            </div>

            {/* Middle: Content Section - Use flex-1 and min-w-0 to allow shrinking/wrapping */}
            <div className="flex-1 min-w-0 py-1">
                <span className="text-[12px] font-bold text-blue-500 break-words leading-tight mb-1.5 bg-blue-100 rounded-full px-2 py-1 uppercase tracking-widest">
                    Source
                </span>
                <span className="mx-2 text-[14px]">{title}</span>

                <div className="flex flex-wrap items-center gap-2 mt-2">
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-blue-100 text-blue-600 whitespace-nowrap tracking-widest">
                        category - {category}
                    </span>
                    <p className="text-[13px] text-gray-800   whitespace-nowrap">
                        {date}
                    </p>
                </div>
            </div>

            {/* Right: Actions & Amount */}
            <div className="flex items-center gap-3 shrink-0">
                {/* Quick Actions (Desktop Hover) */}
                <div className="hidden sm:flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all transform translate-x-2 group-hover:translate-x-0">
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            onUpdate();
                        }}
                        className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                    >
                        <Pencil size={15} />
                    </button>
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            onDelete();
                        }}
                        className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-full transition-colors"
                    >
                        <Trash2 size={15} />
                    </button>
                </div>

                {/* Amount Badge */}
                <div
                    className={`flex items-center min-w-[90px] justify-end px-3 py-1.5 rounded-xl border-2 ${
                        isExpense
                            ? "bg-white text-rose-600 border-rose-50"
                            : "bg-white text-emerald-600 border-emerald-50"
                    }`}
                >
                    <div className="flex flex-col items-end">
                        <span className="text-[9px] uppercase font-black opacity-50 leading-none mb-1">
                            {isExpense ? "Out" : "In"}
                        </span>
                        <h6 className="flex items-center gap-1 text-sm font-bold tracking-tight">
                            <span className="whitespace-nowrap">
                                {isExpense ? "-" : "+"} ₹
                                {addThousandsSeparator(amount)}
                            </span>
                            {isExpense ? (
                                <TrendingDown size={14} strokeWidth={3} />
                            ) : (
                                <TrendingUp size={14} strokeWidth={3} />
                            )}
                        </h6>
                    </div>
                </div>
            </div>
        </div>
    );
};
export default TransactionInfoCard;
