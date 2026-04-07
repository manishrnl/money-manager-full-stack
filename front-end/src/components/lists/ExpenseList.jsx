import {Download, Layers2, LoaderPinwheel, Mail} from "lucide-react";
import TransactionInfoCard from "../TransactionInfoCard.jsx";
import moment from "moment";
import UseUser from "../../hooks/UseUser.jsx";
import {useState} from "react";
import toast from "react-hot-toast";
import * as XLSX from "xlsx";
import AxiosConfig from "../../util/AxiosConfig.jsx";
import {API_ENDPOINTS} from "../../util/API_ENDPOINTS.js";

const ExpenseList = ({transactions, categories, onDelete, onUpdate}) => {
    const {user} = UseUser();
    const [loadingEmail, setLoadingEmail] = useState(false);
    const [loadingExports, setLoadingExports] = useState(false);

    const getCategoryName = (expense) => {
        if (expense.categoryName) return expense.categoryName;
        const found = categories?.find(cat => String(cat.id) === String(expense.categoryId));
        return found ? found.name : "Uncategorized";
    };

    const handleExportToExcel = () => {
        if (!transactions?.length) return toast.error("No data to export");
        setLoadingExports(true);
        try {
            const excelData = transactions.map((item, i) => ({
                "Sl. No": i + 1,
                "Expense Source": item.name,
                "Amount": item.amount,
                "Category": getCategoryName(item),
                "Date": moment(item.date).format("YYYY-MM-DD"),
            }));
            const ws = XLSX.utils.json_to_sheet(excelData);
            const wb = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, "Expenses");
            XLSX.writeFile(wb, `${user.fullName}_Expense_Report_${moment().format("DD_MM_YY")}.xlsx`);
            toast.success("Excel downloaded!");
        } finally {
            setLoadingExports(false);
        }
    };

    const handleSendEmail = async () => {
        if (!user?.email) return toast.error("User email not found");
        if (!transactions?.length) return toast.error("No data to send");
        setLoadingEmail(true);
        try {
            await AxiosConfig.post(API_ENDPOINTS.SEND_EXPENSES_REPORT(user.email), transactions);
            toast.success(`Report sent to ${user.email}`);
        } catch (error) {
            toast.error("Failed to send email report");
        } finally {
            setLoadingEmail(false);
        }
    };

    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <div className="flex items-center justify-between border-b pb-4 mb-6">
                <h5 className="text-xl font-bold text-gray-800">Expense History</h5>
                <div className="flex gap-3">
                    <button onClick={handleSendEmail} disabled={loadingEmail} className="flex items-center gap-2 px-4 py-2 bg-rose-50 text-rose-700 text-xs font-bold rounded-xl hover:bg-rose-100">
                        {loadingEmail ? <LoaderPinwheel className="animate-spin" size={16}/> : <Mail size={16}/>}
                        <span>Email Report</span>
                    </button>
                    <button onClick={handleExportToExcel} disabled={loadingExports} className="flex items-center gap-2 px-4 py-2 bg-emerald-50 text-emerald-700 text-xs font-bold rounded-xl hover:bg-emerald-100">
                        {loadingExports ? <LoaderPinwheel className="animate-spin" size={16}/> : <Download size={16}/>}
                        <span>Excel</span>
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                {transactions.length > 0 ? (
                    transactions.map((expense) => (
                        <TransactionInfoCard
                            key={expense.id}
                            title={expense.name}
                            icon={expense?.icon}
                            date={moment(expense.date).format("Do MMM YYYY")}
                            amount={expense.amount}
                            category={getCategoryName(expense)}
                            type="expense"
                            onDelete={() => onDelete(expense)}
                            onUpdate={() => onUpdate(expense)}
                        />
                    ))
                ) : (
                    <div className="col-span-full py-16 text-center border-2 border-dashed border-gray-100 rounded-3xl">
                        <Layers2 className="mx-auto text-gray-300 mb-3" size={48}/>
                        <p className="text-gray-400">No expense records found.</p>
                    </div>
                )}
            </div>
        </div>
    );
};
export default ExpenseList;