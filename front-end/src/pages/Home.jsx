import React, { useEffect, useState } from "react";
import Dashboards from "../components/Dashboards.jsx";
import UseUser from "../hooks/UseUser.jsx";
import AxiosConfig from "../util/AxiosConfig.jsx";
import { API_ENDPOINTS } from "../util/API_ENDPOINTS.js";
import { useNavigate } from "react-router-dom";
import {
    Area,
    AreaChart,
    CartesianGrid,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";
import {
    ArrowDownLeft,
    ArrowUpRight,
    Clock,
    Loader2,
    Plus,
    TrendingDown,
    TrendingUp,
    Wallet,
    Zap,
} from "lucide-react";
import TransactionOverlay from "../components/TransactionOverlay.jsx";
import { addThousandsSeparator } from "../util/ThousandsSeparator.js";
import PremiumLoader from "../hooks/PremiumLoader.jsx";
import Footer from "../components/Footer.jsx";

const Home = () => {
    // Authenticate user
    UseUser();
    const navigate = useNavigate();

    // --- State Management ---
    const [showInsights, setShowInsights] = useState(false);
    const [isHistoryOpen, setIsHistoryOpen] = useState(false);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState({
        totalBalance: 0,
        totalIncome: 0,
        totalExpenses: 0,
        recentTransactions: [],
    });

    useEffect(() => {
        let isMounted = true;
        document.title = "Overview - Money Manager";

        const fetchDashboard = async () => {
            try {
                const res = await AxiosConfig.get(
                    API_ENDPOINTS.GET_DASHBOARD_DATA,
                );
                if (isMounted && res.data?.data) {
                    setData(res.data.data);
                }
            } catch (err) {
                console.error("Dashboard fetch error", err);
            } finally {
                if (isMounted) setLoading(false);
            }
        };

        fetchDashboard();
        return () => {
            isMounted = false;
        };
    }, []);

    if (loading) {
        return <PremiumLoader isDone={loading} />;
    }

    return (
        <Dashboards activeMenu="Dashboards">
            {/* 1. BLURRED HISTORY OVERLAY */}
            <TransactionOverlay
                isOpen={isHistoryOpen}
                onClose={() => setIsHistoryOpen(false)}
                transactions={data.recentTransactions}
            />

            <div className="p-4 md:p-8 bg-slate-50 min-h-screen space-y-8">
                {/* 2. INSIGHTS PANEL (Conditional) */}
                {showInsights && (
                    <div className="bg-emerald-900 text-emerald-50 p-6 rounded-[2rem] shadow-xl animate-in fade-in slide-in-from-top-4 duration-500">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="font-black flex items-center gap-2 underline decoration-emerald-400">
                                <Zap size={20} className="text-emerald-400" />{" "}
                                AI Financial Analysis
                            </h3>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm italic">
                            <div className="bg-emerald-800/50 p-4 rounded-xl border border-emerald-700">
                                "Your spending on <strong>Expenses</strong> is{" "}
                                {data.totalIncome > 0
                                    ? (
                                          (data.totalExpenses /
                                              data.totalIncome) *
                                          100
                                      ).toFixed(1)
                                    : 0}
                                % of your income."
                            </div>
                            <div className="bg-emerald-800/50 p-4 rounded-xl border border-emerald-700">
                                "You have{" "}
                                <strong>
                                    {data.recentTransactions.length}
                                </strong>{" "}
                                transactions. Largest: ₹{" "}
                                {Math.max(
                                    ...data.recentTransactions.map(
                                        (t) => t.amount,
                                    ),
                                    0,
                                ).toLocaleString()}
                                ."
                            </div>
                        </div>
                    </div>
                )}

                {/* 3. HEADER & QUICK ACTIONS */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div>
                        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
                            Financial Overview
                        </h1>
                        <p className="text-slate-500 font-medium italic">
                            Welcome back! Here's what's happening with your
                            money.
                        </p>
                    </div>
                    <div className="flex gap-3">
                        <button
                            onClick={() => setShowInsights(!showInsights)}
                            className={`${showInsights ? "bg-emerald-600" : "bg-emerald-500"} text-white px-6 py-3 rounded-2xl font-bold flex items-center gap-2 hover:bg-emerald-600 transition-all shadow-lg shadow-emerald-100`}
                        >
                            <Zap size={18} />{" "}
                            {showInsights ? "Close Insights" : "Insights"}
                        </button>
                    </div>
                </div>

                {/* 4. STAT CARDS */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 relative overflow-hidden group">
                        <div className="absolute -right-4 -top-4 bg-emerald-500/5 w-32 h-32 rounded-full group-hover:scale-150 transition-transform duration-700" />
                        <div className="relative z-10">
                            <div className="bg-slate-900 w-12 h-12 rounded-2xl flex items-center justify-center text-white mb-6">
                                <Wallet size={24} />
                            </div>
                            <p className="text-slate-400 text-xs font-bold uppercase tracking-[0.2em] mb-1">
                                Available Balance
                            </p>
                            <h2 className="text-4xl font-black text-slate-900">
                                ₹ {addThousandsSeparator(data.totalBalance)}
                            </h2>
                        </div>
                    </div>

                    <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
                        <div className="bg-emerald-50 w-12 h-12 rounded-2xl flex items-center justify-center text-emerald-500 mb-6">
                            <TrendingUp size={24} />
                        </div>
                        <p className="text-slate-400 text-xs font-bold uppercase tracking-[0.2em] mb-1">
                            Total Income
                        </p>
                        <h2 className="text-3xl font-black text-emerald-600">
                            ₹ {addThousandsSeparator(data.totalIncome)}
                        </h2>
                    </div>

                    <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
                        <div className="bg-rose-50 w-12 h-12 rounded-2xl flex items-center justify-center text-rose-500 mb-6">
                            <TrendingDown size={24} />
                        </div>
                        <p className="text-slate-400 text-xs font-bold uppercase tracking-[0.2em] mb-1">
                            Total Expenses
                        </p>
                        <h2 className="text-3xl font-black text-rose-600">
                            ₹ {addThousandsSeparator(data.totalExpenses)}
                        </h2>
                    </div>
                </div>

                {/* 5. CHART & ACTIVITY */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <div className="lg:col-span-2 bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
                        <div className="flex justify-between items-center mb-10">
                            <h3 className="text-sm font-black text-slate-800 uppercase tracking-widest flex items-center gap-2">
                                <Clock size={16} className="text-emerald-500" />{" "}
                                Cash Flow Trend
                            </h3>
                        </div>
                        <div style={{ width: "100%", height: 350 }}>
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart
                                    data={[...data.recentTransactions]
                                        .slice(0, 10)
                                        .reverse()}
                                >
                                    <defs>
                                        <linearGradient
                                            id="colorValue"
                                            x1="0"
                                            y1="0"
                                            x2="0"
                                            y2="1"
                                        >
                                            <stop
                                                offset="5%"
                                                stopColor="#10b981"
                                                stopOpacity={0.3}
                                            />
                                            <stop
                                                offset="95%"
                                                stopColor="#10b981"
                                                stopOpacity={0}
                                            />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid
                                        strokeDasharray="3 3"
                                        vertical={false}
                                        stroke="#f1f5f9"
                                    />
                                    <XAxis
                                        dataKey="date"
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fill: "#94a3b8", fontSize: 10 }}
                                    />
                                    <YAxis
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fill: "#94a3b8", fontSize: 10 }}
                                    />
                                    <Tooltip
                                        contentStyle={{
                                            borderRadius: "20px",
                                            border: "none",
                                            boxShadow:
                                                "0 20px 25px -5px rgba(0,0,0,0.1)",
                                        }}
                                    />
                                    <Area
                                        type="monotone"
                                        dataKey="amount"
                                        stroke="#10b981"
                                        strokeWidth={4}
                                        fillOpacity={1}
                                        fill="url(#colorValue)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Recent Transactions List */}
                    <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 flex flex-col">
                        <div className="flex justify-between items-center mb-8">
                            <h3 className="text-sm font-black text-slate-800 uppercase tracking-widest">
                                Recent Activity
                            </h3>
                        </div>

                        <div className="space-y-6 overflow-y-auto max-h-[400px] pr-2 custom-scrollbar">
                            {data.recentTransactions.length > 0 ? (
                                data.recentTransactions
                                    .slice(0, 10)
                                    .map((t, idx) => (
                                        <div
                                            key={idx}
                                            className="flex items-center justify-between group p-2 rounded-2xl transition-all"
                                        >
                                            <div className="flex items-center gap-4">
                                                <div
                                                    className={`w-12 h-12 rounded-2xl flex items-center justify-center text-lg ${t.type === "income" ? "bg-emerald-50 text-emerald-600" : "bg-rose-50 text-rose-600"}`}
                                                >
                                                    {t.type === "income" ? (
                                                        <ArrowUpRight
                                                            size={20}
                                                        />
                                                    ) : (
                                                        <ArrowDownLeft
                                                            size={20}
                                                        />
                                                    )}
                                                </div>
                                                <div>
                                                    <p className="font-black text-slate-800 text-sm">
                                                        {t.name}
                                                    </p>
                                                    <p className="text-[10px] text-slate-400 font-bold uppercase">
                                                        {t.date}
                                                    </p>
                                                </div>
                                            </div>
                                            <p
                                                className={`font-black text-sm ${t.type === "income" ? "text-emerald-500" : "text-slate-900"}`}
                                            >
                                                {t.type === "income"
                                                    ? "+"
                                                    : "-"}{" "}
                                                ₹{" "}
                                                {addThousandsSeparator(
                                                    t.amount,
                                                )}
                                            </p>
                                        </div>
                                    ))
                            ) : (
                                <div className="text-center py-20 text-slate-400 italic">
                                    No transactions yet
                                </div>
                            )}
                        </div>

                        {/* Fixed "View All History" Button */}
                        <button
                            onClick={() => setIsHistoryOpen(true)}
                            className="mt-8 w-full py-4 bg-slate-50 text-slate-600 rounded-2xl font-bold text-xs uppercase tracking-widest hover:bg-slate-100 transition-all flex items-center justify-center gap-2"
                        >
                            View All History <ArrowUpRight size={14} />
                        </button>
                    </div>
                </div>
            </div>
        </Dashboards>
    );
};

export default Home;
