import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {Plus} from "lucide-react";

import Dashboards from "../components/Dashboards.jsx";
import ExpenseList from "../components/lists/ExpenseList.jsx";
import Modals from "../components/Modals.jsx";
import AddExpenseForm from "../components/forms/AddExpenseForm.jsx";
import AxiosConfig from "../util/AxiosConfig.jsx";
import {API_ENDPOINTS} from "../util/API_ENDPOINTS.js";

const Expense = () => {
    const [expenseData, setExpenseData] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [expenseToDelete, setExpenseToDelete] = useState(null);
    const [expenseToUpdate, setExpenseToUpdate] = useState(null);

    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);

    useEffect(() => {
        document.title = "Expense - Money Manager";
        window.scrollTo({top: 0, behavior: "smooth"});
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        setLoading(true);
        try {
            const [expenseRes, categoryRes] = await Promise.all([
                AxiosConfig.get(API_ENDPOINTS.GET_ALL_EXPENSES),
                AxiosConfig.get(API_ENDPOINTS.GET_CATEGORY_BY_TYPE("EXPENSE"))
            ]);
            setExpenseData(expenseRes.data?.data || expenseRes.data || []);
            setCategories(categoryRes.data?.data || categoryRes.data || []);
        } catch (error) {
            toast.error("Failed to load expense data");
        } finally {
            setLoading(false);
        }
    };

    const fetchExpenseDetails = async () => {
        try {
            const {data} = await AxiosConfig.get(API_ENDPOINTS.GET_ALL_EXPENSES);
            setExpenseData(data?.data || data || []);
        } catch (error) {
            toast.error("Failed to refresh expenses");
        }
    };

    const handleAddSubmit = async (formData) => {
        setIsSubmitting(true);
        try {
            await AxiosConfig.post(API_ENDPOINTS.ADD_EXPENSE, formData);
            toast.success("Expense added successfully!");
            setIsAddModalOpen(false);
            fetchExpenseDetails();
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to add expense");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleUpdateExecution = async (formData) => {
        if (!expenseToUpdate?.id) return;
        setIsSubmitting(true);
        try {
            await AxiosConfig.put(API_ENDPOINTS.UPDATE_EXPENSE_BY_ID(expenseToUpdate.id), formData);
            toast.success("Expense updated!");
            setIsUpdateModalOpen(false);
            setExpenseToUpdate(null);
            fetchExpenseDetails();
        } catch (error) {
            toast.error("Update failed");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDeleteExecution = async () => {
        if (!expenseToDelete?.id) return;
        try {
            await AxiosConfig.delete(API_ENDPOINTS.DELETE_EXPENSE_BY_ID(expenseToDelete.id));
            toast.success("Expense deleted");
            setIsDeleteModalOpen(false);
            setExpenseToDelete(null);
            fetchExpenseDetails();
        } catch (error) {
            toast.error("Delete failed");
        }
    };

    return (
        <Dashboards activeMenu="Expense">
            <div className="my-5 mx-auto px-4">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-semibold text-gray-800">All Expenses</h2>
                    <button
                        className="flex items-center gap-2 px-5 py-2.5 bg-rose-600 text-white font-bold rounded-xl shadow-lg hover:bg-rose-700 active:scale-95 transition-all"
                        onClick={() => setIsAddModalOpen(true)}
                    >
                        <Plus size={18} strokeWidth={3}/>
                        <span>Add Expense</span>
                    </button>
                </div>

                <ExpenseList
                    transactions={expenseData}
                    categories={categories}
                    onDelete={(exp) => { setExpenseToDelete(exp); setIsDeleteModalOpen(true); }}
                    onUpdate={(exp) => { setExpenseToUpdate(exp); setIsUpdateModalOpen(true); }}
                />

                <Modals isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Add Expense">
                    <AddExpenseForm onAction={handleAddSubmit} categories={categories} isSubmitting={isSubmitting}/>
                </Modals>

                <Modals isOpen={isUpdateModalOpen} onClose={() => { setIsUpdateModalOpen(false); setExpenseToUpdate(null); }} title="Update Expense">
                    <AddExpenseForm initialData={expenseToUpdate} onAction={handleUpdateExecution} categories={categories} isUpdate={true} isSubmitting={isSubmitting}/>
                </Modals>

                <Modals isOpen={isDeleteModalOpen} onClose={() => setIsDeleteModalOpen(false)} title="Confirm Deletion">
                    <div className="p-4 text-center">
                        <p className="text-gray-600 mb-6">Delete expense record for <b>{expenseToDelete?.name}</b>?</p>
                        <div className="flex gap-3">
                            <button onClick={() => setIsDeleteModalOpen(false)} className="flex-1 py-3 bg-gray-100 rounded-xl font-semibold text-gray-600">Cancel</button>
                            <button onClick={handleDeleteExecution} className="flex-1 py-3 bg-red-600 text-white rounded-xl font-bold">Delete</button>
                        </div>
                    </div>
                </Modals>
            </div>
        </Dashboards>
    );
};

export default Expense;