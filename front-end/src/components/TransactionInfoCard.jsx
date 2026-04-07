import {Layers2, Pencil, Trash2, TrendingUp, TrendingDown} from "lucide-react";
import {addThousandsSeparator} from "../util/ThousandsSeparator.js";

const TransactionInfoCard = ({icon, title, date, amount, category, type = "income", onDelete, onUpdate}) => {
    const isExpense = type === "expense";

    const renderIcon = () => {
        const isLink = typeof icon === 'string' && (icon.startsWith('http') || icon.startsWith('/'));
        if (isLink) {
            return <img src={icon} alt={title} className="w-8 h-8 object-contain" onError={(e) => { e.target.src = "https://cdn-icons-png.flaticon.com/512/2454/2454282.png" }} />;
        }
        if (icon && icon.length > 0) return <span className="text-2xl leading-none">{icon}</span>;
        return <Layers2 size={20} className="text-slate-400"/>;
    };

    return (
        <div className="group flex items-center gap-4 p-3 bg-white rounded-2xl hover:bg-gray-50 transition-all border border-transparent hover:border-gray-100 shadow-sm">
            <div className="w-12 h-12 shrink-0 flex items-center justify-center bg-white border border-gray-100 rounded-xl group-hover:scale-105 transition-transform">
                {renderIcon()}
            </div>

            <div className="flex-1 flex items-center justify-between min-w-0">
                <div className="truncate pr-2">
                    <p className="text-sm text-slate-900 font-bold truncate">{title}</p>
                    <p className="text-[11px] text-slate-400 font-medium uppercase">{category} • {date}</p>
                </div>

                <div className="flex items-center gap-2">
                    <div className="flex items-center opacity-0 group-hover:opacity-100 transition-opacity">
                        <button onClick={(e) => { e.stopPropagation(); onUpdate(); }} className="p-2 text-blue-500 hover:bg-blue-50 rounded-lg"><Pencil size={16}/></button>
                        <button onClick={(e) => { e.stopPropagation(); onDelete(); }} className="p-2 text-rose-500 hover:bg-rose-50 rounded-lg"><Trash2 size={16}/></button>
                    </div>

                    <div className={`flex items-center px-3 py-1.5 rounded-xl border ${isExpense ? 'bg-rose-50 text-rose-700 border-rose-100' : 'bg-emerald-50 text-emerald-700 border-emerald-100'}`}>
                        <h6 className="flex items-center gap-1.5 text-xs font-black">
                            <span>{isExpense ? "-" : "+"} ₹{addThousandsSeparator(amount)}</span>
                            {isExpense ? <TrendingDown size={14}/> : <TrendingUp size={14}/>}
                        </h6>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TransactionInfoCard;