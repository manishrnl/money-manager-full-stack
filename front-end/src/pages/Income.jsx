import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {Plus} from "lucide-react";

import Dashboards from "../components/Dashboards.jsx";
import IncomeList from "../components/lists/IncomeList.jsx";
import Modals from "../components/Modals.jsx";
import AddIncomeForm from "../components/forms/AddIncomeForm.jsx";
import AxiosConfig from "../util/AxiosConfig.jsx";
import {API_ENDPOINTS} from "../util/API_ENDPOINTS.js";

const Income = () => {
    const [incomeData, setIncomeData] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Selection States
    const [incomeToDelete, setIncomeToDelete] = useState(null);
    const [incomeToUpdate, setIncomeToUpdate] = useState(null);

    // Modal Visibility States
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);

    useEffect(() => {
        document.title = "Income - Money Manager";
        window.scrollTo({top: 0, behavior: "smooth"});
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        setLoading(true);
        await Promise.all([fetchIncomeDetails(), fetchIncomeCategories()]);
        setLoading(false);
    };

    const fetchIncomeDetails = async () => {
        try {
            const {data} = await AxiosConfig.get(API_ENDPOINTS.GET_ALL_INCOMES);
            setIncomeData(data?.data || data || []);
        } catch (error) {
            toast.error("Failed to load incomes");
        }
    };

    const fetchIncomeCategories = async () => {
        try {
            const {data} = await AxiosConfig.get(API_ENDPOINTS.GET_CATEGORY_BY_TYPE("INCOME"));
            setCategories(data?.data || data || []);
        } catch (error) {
            console.error("Category Fetch Error:", error);
        }
    };

    // --- Action Handlers ---
    const handleAddSubmit = async (formData) => {
        setIsSubmitting(true);
        try {
            await AxiosConfig.post(API_ENDPOINTS.ADD_INCOME, {
                ...formData,
                amount: Number(formData.amount),
            });
            toast.success("Income added successfully!");
            setIsAddModalOpen(false);
            fetchIncomeDetails();
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to add income");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleUpdateExecution = async (formData) => {
        if (!incomeToUpdate?.id) return;
        setIsSubmitting(true);
        console.log("income id to be updated is : ", incomeToUpdate.id)
        try {
            console.log("Updating Income ID:", incomeToUpdate.id);
            console.log("Form Data Payload:", formData);

            await AxiosConfig.put(API_ENDPOINTS.UPDATE_INCOME_BY_ID(incomeToUpdate.id), {
                ...formData,
                amount: Number(formData.amount)
            });

            toast.success("Income updated!");
            setIsUpdateModalOpen(false);
            setIncomeToUpdate(null);
            fetchIncomeDetails();
        } catch (error) {
            console.error("Update error details:", error);
            toast.error("Update failed");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDeleteExecution = async () => {
        if (!incomeToDelete?.id) return;
        setLoading(true);
        try {
            await AxiosConfig.delete(API_ENDPOINTS.DELETE_INCOME_BY_ID(incomeToDelete.id));
            toast.success("Income deleted");
            setIsDeleteModalOpen(false);
            setIncomeToDelete(null);
            fetchIncomeDetails();
        } catch (error) {
            toast.error("Delete failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dashboards activeMenu="Income">
            <div className="my-5 mx-auto px-4">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-semibold text-gray-800">All Incomes</h2>
                    <button
                        className="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white font-bold rounded-xl shadow-lg hover:bg-emerald-700 active:scale-95 transition-all"
                        onClick={() => setIsAddModalOpen(true)}
                    >
                        <Plus size={18} strokeWidth={3}/>
                        <span>Add Income</span>
                    </button>
                </div>

                <IncomeList
                    transactions={incomeData}
                    onDelete={(income) => {
                        setIncomeToDelete(income);
                        setIsDeleteModalOpen(true);
                    }}
                    onUpdate={(income) => {
                        setIncomeToUpdate(income);
                        setIsUpdateModalOpen(true);
                    }}
                />

                {/* Add Modal */}
                <Modals isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)}
                        title="Add Income">
                    <AddIncomeForm onAction={handleAddSubmit} categories={categories}
                                   isSubmitting={isSubmitting}/>
                </Modals>

                {/* Update Modal */}
                <Modals isOpen={isUpdateModalOpen} onClose={() => {
                    setIsUpdateModalOpen(false);
                    setIncomeToUpdate(null);
                }} title="Update Income">
                    <AddIncomeForm
                        initialData={incomeToUpdate}
                        onAction={handleUpdateExecution} // Prop name matches the fixed AddIncomeForm
                        categories={categories}
                        isUpdate={true}
                        isSubmitting={isSubmitting}
                    />
                </Modals>

                {/* Delete Modal */}
                <Modals isOpen={isDeleteModalOpen} onClose={() => setIsDeleteModalOpen(false)}
                        title="Confirm Deletion">
                    <div className="p-4 text-center">
                        <p className="text-gray-600 mb-6">Delete income record
                            for <b>{incomeToDelete?.name}</b>?</p>
                        <div className="flex gap-3">
                            <button onClick={() => setIsDeleteModalOpen(false)}
                                    className="flex-1 py-3 bg-gray-100 rounded-xl font-semibold text-gray-600">Cancel
                            </button>
                            <button onClick={handleDeleteExecution}
                                    className="flex-1 py-3 bg-red-600 text-white rounded-xl font-bold hover:bg-red-700">Delete
                            </button>
                        </div>
                    </div>
                </Modals>
            </div>
        </Dashboards>
    );
};

export default Income;
