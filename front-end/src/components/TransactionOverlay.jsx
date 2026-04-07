import React from 'react';
import { X, ArrowUpRight, ArrowDownLeft, Receipt } from 'lucide-react';
import {addThousandsSeparator} from "../util/ThousandsSeparator.js";

const TransactionOverlay = ({ isOpen, onClose, transactions }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 md:p-6">
            {/* 1. The Blurred Background */}
            <div
                className="absolute inset-0 bg-slate-900/40 backdrop-blur-md transition-opacity"
                onClick={onClose}
            />

            {/* 2. The Modal Content */}
            <div className="relative bg-white w-full max-w-2xl max-h-[80vh] rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col animate-in zoom-in-95 duration-300">

                {/* Header */}
                <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-white sticky top-0">
                    <div>
                        <h2 className="text-xl font-black text-slate-900">Transaction History</h2>
                        <p className="text-xs text-slate-400 font-bold uppercase tracking-widest">Full List</p>
                    </div>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-slate-100 rounded-xl transition-colors text-slate-400 hover:text-slate-900"
                    >
                        <X size={24} />
                    </button>
                </div>

                {/* List Container */}
                <div className="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
                    {transactions.map((t, idx) => (
                        <div key={idx} className="flex items-center justify-between p-4 rounded-2xl border border-slate-50 hover:border-emerald-100 hover:bg-emerald-50/30 transition-all group">
                            <div className="flex items-center gap-4">
                                <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${t.type === 'income' ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-600'}`}>
                                    {t.type === 'income' ? <ArrowUpRight size={20}/> : <ArrowDownLeft size={20}/>}
                                </div>
                                <div>
                                    <p className="font-bold text-slate-800">{t.name}</p>
                                    <p className="text-[10px] text-slate-400 font-bold uppercase">{t.date}</p>
                                </div>
                            </div>
                            <div className="text-right">
                                <p className={`font-black ${t.type === 'income' ? 'text-emerald-600' : 'text-slate-900'}`}>
                                    {t.type === 'income' ? '+' : '-'} ₹ {addThousandsSeparator(t.amount)}
                                </p>
                                <p className="text-[10px] text-slate-300 font-medium">Verified</p>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Footer */}
                <div className="p-6 bg-slate-50 border-t border-slate-100 text-center">
                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">End of records</p>
                </div>
            </div>
        </div>
    );
};

export default TransactionOverlay;