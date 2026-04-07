import { Layers2, Pencil, Trash2 } from "lucide-react";

const CategoryList = ({ categories, onEditCategory, onDeleteCategory }) => {
    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            {/* Header Section */}
            <div className="flex items-center justify-between mb-6">
                <h4 className="text-xl font-bold text-gray-800 tracking-tight">
                    Category Sources
                </h4>
                <span className="px-3 py-1 bg-gray-50 text-gray-500 text-xs font-medium rounded-full border border-gray-100">
                    {categories.length} Total
                </span>
            </div>

            {/* Empty State */}
            {categories.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12 border-2 border-dashed border-gray-100 rounded-2xl">
                    <Layers2 className="text-gray-300 mb-3" size={40} />
                    <p className="text-gray-400 font-medium">
                        No Categories added yet. Please add some to view here.
                    </p>
                </div>
            ) : (
                /* Categories Grid */
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {categories.map((category) => {
                        // Normalize type for color coding
                        const isIncome = category.type?.toLowerCase() === 'income';

                        // Safety check: Is the icon a valid string/URL?
                        const hasIcon = category.icon && category.icon !== "null" && category.icon !== "";

                        return (
                            <div
                                key={category.id}
                                className="group relative flex items-center gap-4 p-4 rounded-xl border border-transparent bg-gray-50/50 hover:bg-white hover:border-gray-200 hover:shadow-md transition-all duration-300"
                            >
                                {/* --- ICON SECTION --- */}
                                <div className="w-12 h-12 shrink-0 flex items-center justify-center rounded-xl bg-white shadow-sm border border-gray-100 overflow-hidden relative">
                                    {hasIcon ? (
                                        <img
                                            src={category.icon}
                                            alt={category.name}
                                            className="w-full h-full object-contain p-2 transition-transform group-hover:scale-110"
                                            // This prevents the "broken image" icon from showing if the URL fails
                                            onError={(e) => {
                                                e.target.style.display = 'none';
                                                e.target.nextSibling.style.display = 'flex';
                                            }}
                                        />
                                    ) : null}

                                    {/* FALLBACK ICON: Shows if URL is missing OR broken */}
                                    <div
                                        className="absolute inset-0 items-center justify-center text-gray-300 bg-white"
                                        style={{ display: hasIcon ? 'none' : 'flex' }}
                                    >
                                        <Layers2 size={20} />
                                    </div>
                                </div>

                                {/* --- CONTENT SECTION --- */}
                                <div className="flex-1 min-w-0">
                                    <p className="text-sm font-bold text-gray-900 truncate">
                                        {category.name}
                                    </p>
                                    <span
                                        className={`text-[10px] uppercase font-black tracking-widest ${
                                            isIncome ? 'text-emerald-500' : 'text-rose-500'
                                        }`}
                                    >
                                        {category.type}
                                    </span>
                                </div>

                                {/* --- ACTION BUTTONS --- */}
                                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                    <button
                                        onClick={() => onEditCategory?.(category)}
                                        className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                                        title="Edit"
                                    >
                                        <Pencil size={15} />
                                    </button>
                                    <button
                                        onClick={() => onDeleteCategory?.(category)}
                                        className="p-2 text-gray-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                                        title="Delete"
                                    >
                                        <Trash2 size={15} />
                                    </button>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default CategoryList;