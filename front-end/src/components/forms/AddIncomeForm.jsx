import {useEffect, useState} from "react";
import EmojiPickerPopup from "../EmojiPickerPopup.jsx";
import Input from "../Input.jsx";
import {LoaderCircle} from "lucide-react";
import toast from "react-hot-toast";

const AddIncomeForm = ({ onAction, categories, initialData, isUpdate = false }) => {
    const [loading, setLoading] = useState(false);
    const [income, setIncome] = useState({
        name: initialData?.name || "",
        amount: initialData?.amount || "",
        date: initialData?.date ? new Date(initialData.date).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
        icon: initialData?.icon || "💰",
        categoryId: initialData?.categoryId || ""
    });

    // FIX: Set default category immediately so you don't get "please select category"
    useEffect(() => {
        if (categories?.length > 0 && !income.categoryId) {
            const salaryCat = categories.find(c => c.name.toLowerCase() === "salary");
            const defaultId = salaryCat ? salaryCat.id : categories[0].id;
            setIncome(prev => ({...prev, categoryId: defaultId}));
        }
    }, [categories, income.categoryId]);

    const handleSubmit = async () => {
        if (!income.name || !income.amount) return toast.error("Please fill required fields");
        if (!income.categoryId) return toast.error("Please select a category");

        setLoading(true);
        await onAction({
            ...income,
            amount: Number(income.amount),
            categoryId: Number(income.categoryId)
        });
        setLoading(false);
    };

    // FIX: Helper to render Image if icon is a URL
    const renderIconPreview = () => {
        const isLink = typeof income.icon === 'string' && (income.icon.startsWith('http') || income.icon.startsWith('/'));
        if (isLink) {
            return <img src={income.icon} alt="icon" className="w-10 h-10 object-contain mx-auto" />;
        }
        return <span className="text-3xl">{income.icon}</span>;
    };

    return (
        <div className="space-y-5 p-2">
            <div className="flex justify-center mb-4">
                {/* We pass the rendered element to the popup's trigger */}
                <EmojiPickerPopup
                    icon={renderIconPreview()}
                    onSelect={(i) => setIncome({...income, icon: i})}
                />
            </div>
            <Input label="Income Source" value={income.name}
                   onChange={(e) => setIncome({...income, name: e.target.value})}
                   placeholder="e.g. Freelance"/>
            <div className="grid grid-cols-2 gap-4">
                <Input label="Amount" type="number" value={income.amount}
                       onChange={(e) => setIncome({...income, amount: e.target.value})}/>
                <Input label="Date" type="date" value={income.date}
                       onChange={(e) => setIncome({...income, date: e.target.value})}/>
            </div>
            <Input
                label="Category"
                isSelect={true}
                value={income.categoryId}
                onChange={(e) => setIncome({...income, categoryId: e.target.value})}
                options={categories?.map(c => ({value: c.id, label: c.name})) || []}
            />
            <button
                onClick={handleSubmit}
                disabled={loading}
                className="w-full mt-4 py-4 bg-indigo-600 text-white rounded-2xl font-bold shadow-lg hover:bg-indigo-700 transition-all disabled:bg-indigo-300"
            >
                {loading ? <LoaderCircle className="animate-spin inline mr-2"/> : null}
                {isUpdate ? "Save Changes" : "Add Income"}
            </button>
        </div>
    );
};

export default AddIncomeForm;