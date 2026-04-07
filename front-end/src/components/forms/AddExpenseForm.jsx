import {useEffect, useState} from "react";
import EmojiPickerPopup from "../EmojiPickerPopup.jsx";
import Input from "../Input.jsx";
import {LoaderCircle} from "lucide-react";
import toast from "react-hot-toast";

const AddExpenseForm = ({ onAction, categories, initialData, isUpdate = false, isSubmitting = false }) => {
    const [expense, setExpense] = useState({
        name: initialData?.name || "",
        amount: initialData?.amount || "",
        date: initialData?.date ? new Date(initialData.date).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
        icon: initialData?.icon || "💸",
        categoryId: initialData?.categoryId || ""
    });

    useEffect(() => {
        if (categories?.length > 0 && !expense.categoryId) {
            setExpense(prev => ({...prev, categoryId: categories[0].id}));
        }
    }, [categories]);

    const handleSubmit = async () => {
        if (!expense.name || !expense.amount) return toast.error("Please fill required fields");
        if (!expense.categoryId) return toast.error("Please select a category");
        await onAction({ ...expense, amount: Number(expense.amount), categoryId: Number(expense.categoryId) });
    };

    const renderIconPreview = () => {
        const isLink = typeof expense.icon === 'string' && (expense.icon.startsWith('http') || expense.icon.startsWith('/'));
        return isLink ? <img src={expense.icon} alt="icon" className="w-10 h-10 object-contain mx-auto" /> : <span className="text-3xl">{expense.icon}</span>;
    };

    return (
        <div className="space-y-5 p-2">
            <div className="flex justify-center mb-4">
                <EmojiPickerPopup icon={renderIconPreview()} onSelect={(i) => setExpense({...expense, icon: i})}/>
            </div>
            <Input label="Expense Source" value={expense.name} onChange={(e) => setExpense({...expense, name: e.target.value})} placeholder="e.g. Grocery"/>
            <div className="grid grid-cols-2 gap-4">
                <Input label="Amount" type="number" value={expense.amount} onChange={(e) => setExpense({...expense, amount: e.target.value})}/>
                <Input label="Date" type="date" value={expense.date} onChange={(e) => setExpense({...expense, date: e.target.value})}/>
            </div>
            <Input label="Category" isSelect={true} value={expense.categoryId} onChange={(e) => setExpense({...expense, categoryId: e.target.value})} options={categories?.map(c => ({value: c.id, label: c.name})) || []}/>
            <button onClick={handleSubmit} disabled={isSubmitting} className="w-full mt-4 py-4 bg-rose-600 text-white rounded-2xl font-bold shadow-lg hover:bg-rose-700 disabled:bg-rose-300 transition-all">
                {isSubmitting && <LoaderCircle className="animate-spin inline mr-2"/>}
                {isUpdate ? "Save Changes" : "Add Expense"}
            </button>
        </div>
    );
};

export default AddExpenseForm;