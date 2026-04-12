import React, {useCallback, useEffect, useState} from "react";
import Dashboards from "../components/Dashboards.jsx";
import PremiumLoader from "../hooks/PremiumLoader.jsx";
import {
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from 'recharts';
import AxiosConfig from "../util/AxiosConfig.jsx";
import {API_ENDPOINTS} from "../util/API_ENDPOINTS.js";
import {
    ArrowDownCircle,
    ArrowUpCircle,
    ChevronRight,
    FileSpreadsheet,
    Filter as FilterIcon,
    Hash,
    Loader2,
    Mail,
    Search
} from "lucide-react";
import * as XLSX from 'xlsx';
import UseUser from "../hooks/UseUser.jsx";
import ShowPremiumToast from "../util/ShowPremiumToast.jsx";
import {addThousandsSeparator} from "../util/ThousandsSeparator.js"; // npm install xlsx

const Filter = () => {
    const COLORS = {income: "#10b981", expense: "#f43f5e", bg: "#f8fafc"};

    // --- State ---
    const {user} = UseUser();
    const [filterType, setFilterType] = useState("range");
    const [dates, setDates] = useState({
        start: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
        end: new Date().toISOString().split('T')[0]
    });
    const [keyword, setKeyword] = useState("");
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [emailLoading, setEmailLoading] = useState(false);

    // --- Actions ---
    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const payload = {
                ...dates,
                startDate: dates.start,
                endDate: dates.end,
                keyword,
                sortField: "date",
                sortOrder: "DESC"
            };
            const [incRes, expRes] = await Promise.all([
                AxiosConfig.post(API_ENDPOINTS.FILTER_TRANSACTIONS, {
                    ...payload,
                    type: "income"
                }),
                AxiosConfig.post(API_ENDPOINTS.FILTER_TRANSACTIONS, {
                    ...payload,
                    type: "expense"
                })
            ]);

            const combined = [
                ...(incRes.data?.data || []).map(i => ({
                    ...i,
                    type: 'INCOME',
                    amount: parseFloat(i.amount)
                })),
                ...(expRes.data?.data || []).map(e => ({
                    ...e,
                    type: 'EXPENSE',
                    amount: parseFloat(e.amount)
                }))
            ].sort((a, b) => new Date(b.date) - new Date(a.date));

            setTransactions(combined);
        } catch (error) {
            console.error("Fetch failed", error);
        } finally {
            setLoading(false);
        }
    }, [dates, keyword]);

    useEffect(() => {
        document.title="Filters - Money Manager";
        fetchData();
    }, [fetchData]);

    // 1. Export to Excel (Client Side)
    const exportToExcel = () => {
        // 1. Prepare clean data for Excel (Mapping to specific columns)
        const excelData = transactions.map((t, index) => ({
            "Sl. No": index + 1,
            "Id":t.id,
            "Date": t.date,
            "Description": t.displayName || t.name || t.source,
            "Type": t.transactionType || t.type,
            "Amount (₹)": t.amount,
        }));

        // 2. Create the worksheet from the clean data
        const worksheet = XLSX.utils.json_to_sheet(excelData);

        // 3. Optional: Set column widths for better readability
        const wscols = [
            { wch: 10 }, // Sl. No
            { wch: 15 }, // Date
            { wch: 30 }, // Description
            { wch: 15 }, // Type
            { wch: 15 }, // Amount
        ];
        worksheet['!cols'] = wscols;

        // 4. Standard XLSX process
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Financial_Report");

        // Generate file name based on filter dates
        const fileName = `MoneyManager_Report_${dates.start}_to_${dates.end}.xlsx`;
        XLSX.writeFile(workbook, fileName);
    };

    // 2. Send Filtered Data to Backend for Email
    const sendEmailReport = async () => {
        if (transactions.length === 0) return alert("No data to send!");
        setEmailLoading(true);
        try {
            // Sending the EXACT filtered data from state to backend
            const response = await AxiosConfig.post(API_ENDPOINTS.SEND_FILTERED_EMAIL(user.email), transactions);
            console.log("Sent to email : ", user.email);
            if (response.status === 201 || response.status === 200)

               ShowPremiumToast("Report sent to your registered email successfully!","success");
        } catch (error) {
            alert("Failed to send email. Check backend logs.");
        } finally {
            setEmailLoading(false);
        }
    };

    const totalIncome = transactions.filter(t => t.type === 'INCOME').reduce((s, i) => s + i.amount, 0);
    const totalExpense = transactions.filter(t => t.type === 'EXPENSE').reduce((s, i) => s + i.amount, 0);


    if (loading) {
        return <PremiumLoader isDone={loading} />;
    }
    return (
        <Dashboards activeMenu="Filters">
            <div className="flex flex-col lg:flex-row min-h-screen bg-[#f9fafb]">

                {/* LEFT SIDE: Floating Filter Panel (Premium Glass) */}
                <div
                    className="lg:w-80 p-6 lg:sticky lg:top-0 lg:h-screen border-r border-slate-200 bg-white">
                    <div className="mb-8">
                        <h2 className="text-xl font-black text-slate-800 tracking-tight flex items-center gap-2">
                            <FilterIcon size={20} className="text-emerald-500"/> Apply Filters
                        </h2>
                    </div>

                    <div className="space-y-6">
                        <div>
                            <label
                                className="text-[10px] font-black text-slate-400 uppercase mb-2 block">Keyword
                                Search</label>
                            <div className="relative">
                                <Search className="absolute left-3 top-3 text-slate-400"
                                        size={16}/>
                                <input type="text" placeholder="Search..."
                                       className="w-full pl-10 pr-4 py-3 bg-slate-50 rounded-2xl border-none text-sm focus:ring-2 ring-emerald-500 transition-all"
                                       onChange={e => setKeyword(e.target.value)}/>
                            </div>
                        </div>

                        <div>
                            <label
                                className="text-[10px] font-black text-slate-400 uppercase mb-2 block">Timeframe</label>
                            <select
                                className="w-full p-3 bg-slate-50 rounded-2xl border-none text-sm font-bold mb-3"
                                onChange={e => setFilterType(e.target.value)}>
                                <option value="range">Date Range</option>
                                <option value="month">Specific Month</option>
                            </select>
                            {filterType === "range" ? (
                                <div className="space-y-2">
                                    <input type="date" value={dates.start}
                                           className="w-full p-3 bg-slate-50 rounded-xl border-none text-xs"
                                           onChange={e => setDates({
                                               ...dates,
                                               start: e.target.value
                                           })}/>
                                    <input type="date" value={dates.end}
                                           className="w-full p-3 bg-slate-50 rounded-xl border-none text-xs"
                                           onChange={e => setDates({
                                               ...dates,
                                               end: e.target.value
                                           })}/>
                                </div>
                            ) : (
                                <input type="month"
                                       className="w-full p-3 bg-slate-50 rounded-xl border-none text-xs"
                                       onChange={e => {
                                           const [y, m] = e.target.value.split('-');
                                           setDates({
                                               start: `${y}-${m}-01`,
                                               end: new Date(y, m, 0).toISOString().split('T')[0]
                                           });
                                       }}/>
                            )}
                        </div>

                        <button onClick={fetchData}
                                className="w-full py-4 bg-slate-900 text-white rounded-2xl font-bold shadow-lg hover:bg-black transition-all flex justify-center items-center gap-2">
                            {loading ? <Loader2 className="animate-spin"
                                                size={18}/> : "Update Results"}
                        </button>
                    </div>

                    {/* Actions Panel */}
                    <div className="mt-10 pt-10 border-t border-slate-100 space-y-3">
                        <button onClick={sendEmailReport} disabled={emailLoading}
                                className="w-full py-3 px-4 bg-emerald-50 text-emerald-700 rounded-xl text-xs font-black flex items-center justify-between hover:bg-emerald-100 transition-all">
                            <span
                                className="flex items-center gap-2 uppercase tracking-widest"><Mail
                                size={16}/> Email Data</span>
                            {emailLoading ? <Loader2 className="animate-spin" size={14}/> :
                                <ChevronRight size={14}/>}
                        </button>
                        <button onClick={exportToExcel}
                                className="w-full py-3 px-4 bg-blue-50 text-blue-700 rounded-xl text-xs font-black flex items-center justify-between hover:bg-blue-100 transition-all">
                            <span
                                className="flex items-center gap-2 uppercase tracking-widest"><FileSpreadsheet
                                size={16}/> Export To Excel</span>
                            <ChevronRight size={14}/>
                        </button>
                    </div>
                </div>

                {/* RIGHT SIDE: Visuals & Data */}
                <div className="flex-1 p-6 lg:p-10 space-y-8">
                    {/* Top Stats */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div
                            className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100">
                            <p className="text-slate-400 text-[10px] font-black uppercase tracking-widest mb-1">Income</p>
                            <h3 className="text-3xl font-black text-emerald-500">₹ {addThousandsSeparator(totalIncome)}</h3>
                        </div>
                        <div
                            className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100">
                            <p className="text-slate-400 text-[10px] font-black uppercase tracking-widest mb-1">Expense</p>
                            <h3 className="text-3xl font-black text-rose-500">₹ {addThousandsSeparator(totalExpense)}</h3>
                        </div>
                        <div
                            className="bg-slate-900 p-6 rounded-3xl shadow-xl shadow-slate-200 text-white">
                            <p className="text-slate-400 text-[10px] font-black uppercase tracking-widest mb-1">Savings</p>
                            <h3 className="text-3xl font-black">₹ {addThousandsSeparator((totalIncome - totalExpense))}</h3>
                        </div>
                    </div>

                    {/* Chart Section */}
                    <div
                        className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
                        <div className="flex justify-between items-center mb-8">
                            <h3 className="text-sm font-black text-slate-800 uppercase tracking-widest flex items-center gap-2">
                                <Hash size={16} className="text-emerald-500"/> Trend Analysis
                            </h3>
                        </div>
                        <div style={{width: '100%', height: 300}}>
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={transactions.slice(0, 10)}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false}
                                                   stroke="#f1f5f9"/>
                                    <XAxis dataKey="date" axisLine={false} tickLine={false}
                                           tick={{fill: '#94a3b8', fontSize: 10}}/>
                                    <YAxis axisLine={false} tickLine={false}
                                           tick={{fill: '#94a3b8', fontSize: 10}}/>
                                    <Tooltip cursor={{fill: '#f8fafc'}} contentStyle={{
                                        borderRadius: '20px',
                                        border: 'none',
                                        boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1)'
                                    }}/>
                                    <Bar dataKey="amount" radius={[10, 10, 0, 0]} barSize={40}>
                                        {transactions.map((entry, index) => (
                                            <Cell key={index}
                                                  fill={entry.type === 'INCOME' ? COLORS.income : COLORS.expense}/>
                                        ))}
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Table Section */}
                    <div
                        className="bg-white rounded-[2.5rem] shadow-sm border border-slate-100 overflow-hidden">
                        <div
                            className="p-8 border-b border-slate-50 flex justify-between items-center">
                            <h3 className="text-sm font-black text-slate-800 uppercase tracking-widest">Transaction
                                Log</h3>
                            <span
                                className="px-4 py-1 bg-slate-100 text-slate-500 text-[10px] font-bold rounded-full uppercase">{transactions.length} items</span>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead
                                    className="bg-slate-50/50 text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">
                                <tr>
                                    <th className="p-6">Date</th>
                                    <th className="p-6">Details</th>
                                    <th className="p-6 text-right">Amount</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                {transactions.map((t, idx) => (
                                    <tr key={idx}
                                        className="hover:bg-slate-50/50 transition-all">
                                        <td className="p-6 text-xs font-bold text-slate-400">{t.date}</td>
                                        <td className="p-6">
                                            <div className="flex items-center gap-3">
                                                <div
                                                    className={`p-2 rounded-xl ${t.type === 'INCOME' ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'}`}>
                                                    {t.type === 'INCOME' ?
                                                        <ArrowUpCircle size={18}/> :
                                                        <ArrowDownCircle size={18}/>}
                                                </div>
                                                <div>
                                                    <p className="font-black text-slate-800 text-sm">{t.name || t.source}</p>
                                                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">{t.categoryName || 'General'}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className={`p-6 text-right font-black text-lg ${t.type === 'INCOME' ? 'text-emerald-500' : 'text-slate-900'}`}>
                                            {t.type === 'INCOME' ? '+' : '-'} ₹ {addThousandsSeparator(t.amount)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </Dashboards>
    );
};

export default Filter;