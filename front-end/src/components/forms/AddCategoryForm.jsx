import { useEffect, useState } from "react";
import Input from "../Input.jsx";
import EmojiPickerPopup from "../EmojiPickerPopup.jsx";
import { LoaderPinwheel, Trash2 } from "lucide-react"; // Added Trash2 icon

const AddCategoryForm = ({ onAddCategory, initialCategoryData, isEditing, onDeleteCategory }) => {
    const [loading, setLoading] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false); // New state for delete loading
    const [category, setCategory] = useState({
        name: "",
        type: "income",
        icon: ""
    });

    useEffect(() => {
        if (isEditing && initialCategoryData) {
            setCategory(initialCategoryData);
        } else {
            setCategory({ name: "", type: "income", icon: "" });
        }
    }, [isEditing, initialCategoryData]);

    const handleChange = (key, value) => {
        setCategory({ ...category, [key]: value });
    };

    const handleSubmit = async () => {
        setLoading(true);
        try {
            await onAddCategory(category);
        } finally {
            setLoading(false);
        }
    };

    // New Delete Handler
    const handleDelete = async () => {
        onDeleteCategory(category);
    };

    const categoryTypeOptions = [
        { value: "income", label: "Income" },
        { value: "expense", label: "Expense" },
    ];

    return (
        <div className="p-4">
            <EmojiPickerPopup
                onSelect={(selectedIcon) => handleChange("icon", selectedIcon)}
                icon={category.icon}
            />

            <Input
                value={category.name}
                onChange={({ target }) => handleChange("name", target.value)}
                label="Category Name"
                placeholder="e.g., Freelance, Salary, Groceries"
                type="text"
            />

            <Input
                label="Category Type"
                value={category.type}
                onChange={({ target }) => handleChange("type", target.value)}
                isSelect={true}
                options={categoryTypeOptions}
            />

            <div className={`flex mt-8 border-t border-gray-100 pt-6 ${isEditing ? 'justify-between' : 'justify-end'}`}>

                {/* Delete Button - Only shows when editing */}
                {isEditing && (
                    <button
                        onClick={handleDelete}
                        disabled={isDeleting || loading}
                        type="button"
                        className="flex items-center gap-2 px-4 py-2 text-red-600 font-medium rounded-xl hover:bg-red-50 active:scale-95 transition-all disabled:opacity-50"
                    >
                        {isDeleting ? (
                            <div className="w-5 h-5 border-2 border-red-200 border-t-red-600 rounded-full animate-spin" />
                        ) : (
                            <>
                                <Trash2 size={18} />
                                Delete
                            </>
                        )}
                    </button>
                )}

                {/* Submit/Update Button */}
                <button
                    onClick={handleSubmit}
                    disabled={loading || isDeleting}
                    type="button"
                    className="relative flex items-center justify-center px-8 py-3 bg-indigo-600 text-white font-bold rounded-xl shadow-lg shadow-indigo-200 hover:bg-indigo-700 hover:-translate-y-0.5 active:scale-95 transition-all duration-200 disabled:bg-gray-400 disabled:shadow-none disabled:cursor-not-allowed"
                >
                    {loading ? (
                        <div className="flex items-center gap-2">
                            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                            <span>{isEditing ? "Updating..." : "Adding..."}</span>
                        </div>
                    ) : (
                        <>{isEditing ? "Update Category" : "Add Category"}</>
                    )}
                </button>
            </div>
        </div>
    );
};

export default AddCategoryForm;