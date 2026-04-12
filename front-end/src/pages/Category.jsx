import Dashboards from "../components/Dashboards.jsx";
import useUser from "../hooks/UseUser.jsx";
import { Plus, Trash2 } from "lucide-react";
import CategoryList from "../components/lists/CategoryList.jsx";
import { useEffect, useState } from "react";
import axiosConfig from "../util/AxiosConfig.jsx";
import { API_ENDPOINTS } from "../util/API_ENDPOINTS.js";
import toast from "react-hot-toast";
import Modals from "../components/Modals.jsx";
import AddCategoryForm from "../components/forms/AddCategoryForm.jsx";
import PremiumLoader from "../hooks/PremiumLoader.jsx";

const Category = () => {
    useUser();

    const [loading, setLoading] = useState(false);
    const [categoryData, setCategoryData] = useState([]);

    // These states will be useful for your modals later
    const [openAddCategoryModal, setOpenAddCategoryModal] = useState(false);
    const [openEditCategoryModal, setOpenEditCategoryModal] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState(null);

    const [openDeleteModal, setOpenDeleteModal] = useState(false);
    const [categoryToDelete, setCategoryToDelete] = useState(null);

    const fetchCategoryDetails = async () => {
        setLoading(true);
        try {
            const response = await axiosConfig.get(
                API_ENDPOINTS.GET_ALL_CATEGORY,
            );
            console.log("Full Axios Object:", response);
            console.log("Just the JSON body:", response.data);
            // Your JSON structure has the list inside response.data.data
            if (response.data && response.data.data) {
                console.log("Categories fetched:", response.data.data);
                setCategoryData(response.data.data);
            }
        } catch (error) {
            console.error("Error encountered: ", error);
            toast.error(
                error.response?.data?.message || "Failed to fetch categories",
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        document.title = "Category - Money Manager";
        window.scrollTo({ top: 0, behavior: "smooth" });
        fetchCategoryDetails();
    }, []); // Add empty dependency array to prevent infinite loop

    const handleEditCategory = (categoryToEdit) => {
        setSelectedCategory(categoryToEdit); // Populates data to be edited  into the Editor
        setOpenEditCategoryModal(true); //     Display modals
    };
    const handleUpdateCategory = async (updatedCategory) => {
        const { id, name, icon, type } = updatedCategory;

        console.log("Updating category", updatedCategory);
        if (!name) {
            toast.error("Category name is required");
            return;
        }
        if (!id) {
            toast.error("Category id is missing .");
            return;
        }

        try {
            await axiosConfig.put(API_ENDPOINTS.UPDATE_CATEGORY(id), {
                name,
                icon,
                type,
            });
            setOpenEditCategoryModal(false);
            setSelectedCategory(null);
            toast.success("Category Updated Successfully");
            fetchCategoryDetails();
        } catch (error) {
            toast.error(
                error.response?.data?.message || "Something went wrong",
            );
        }
    };
    const handleConfirmDelete = (category) => {
        setCategoryToDelete(category);
        setOpenDeleteModal(true);
    };
    const handleDeleteCategory = async () => {
        if (!categoryToDelete) return;

        setLoading(true);
        try {
            await axiosConfig.delete(
                API_ENDPOINTS.DELETE_CATEGORY(categoryToDelete.id),
            );
            toast.success("Category deleted successfully");
            setOpenDeleteModal(false);
            setCategoryToDelete(null);
            fetchCategoryDetails(); // Refresh list
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to delete");
        } finally {
            setLoading(false);
        }
    };

    const handleAddCategory = async (category) => {
        const { name, type, icon } = category;

        if (!name.trim()) {
            toast.error("Category Name is Required");
            return;
        }
        //  check if category already exists skips db call then
        const isDuplicate = categoryData.some((category) => {
            return (
                category.name.toLowerCase() === name.trim().toLowerCase() &&
                category.type === type
            );
        });
        if (isDuplicate) {
            toast.error(
                `Category Name ${category.name} and Type ${category.type} Already exists . Can't add Duplicate`,
            );
            return;
        }
        try {
            const response = await axiosConfig.post(
                API_ENDPOINTS.SAVE_CATEGORY,
                {
                    name,
                    type,
                    icon,
                },
            );

            // Check for BOTH 200 and 201 to be safe
            if (response.status === 201 || response.status === 200) {
                toast.success("Category Added Successfully");
                setOpenAddCategoryModal(false);

                // Refresh the list so the new category appears immediately
                fetchCategoryDetails();
            }
        } catch (error) {
            console.error(
                "Error Adding Category:",
                error.response?.data || error.message,
            );

            // Specific error handling for Auth vs Validation
            if (
                error.response?.status === 401 ||
                error.response?.status === 403
            ) {
                toast.error("Session expired. Please login again.");
            } else {
                toast.error(
                    error.response?.data?.message || "Failed to add Category",
                );
            }
        }
    };

    if (loading) {
        return <PremiumLoader isDone={loading} />;
    }

    return (
        <Dashboards activeMenu="Category">
            <div className="my-5 mx-auto">
                <div className="flex justify-between items-center mb-5">
                    <h2 className="text-2xl font-semibold">All Categories</h2>
                    <button
                        onClick={() => setOpenAddCategoryModal(true)}
                        className="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white text-sm font-bold rounded-xl shadow-md hover:bg-emerald-700 transition-transform active:scale-95"
                    >
                        <Plus size={15} />
                        Add Category
                    </button>
                </div>

                {/* Pass the fetched data and loading state to your List component */}
                <CategoryList
                    categories={categoryData}
                    isLoading={loading}
                    onEditCategory={handleEditCategory}
                    onDeleteCategory={handleConfirmDelete}
                />

                {/* Adding category Modals */}
                <Modals
                    isOpen={openAddCategoryModal}
                    onClose={() => setOpenAddCategoryModal(false)}
                    title="Add Category"
                >
                    <AddCategoryForm onAddCategory={handleAddCategory} />
                </Modals>

                <Modals
                    isOpen={openEditCategoryModal}
                    onClose={() => {
                        setOpenEditCategoryModal(false);
                        setSelectedCategory(null);
                    }}
                    title="Update Category"
                >
                    {/* Updating  category Modals */}
                    <AddCategoryForm
                        onAddCategory={handleUpdateCategory}
                        isEditing={true}
                        initialCategoryData={selectedCategory}
                    />
                </Modals>
                {/* Delete Confirmation Modal */}
                <Modals
                    isOpen={openDeleteModal}
                    onClose={() => setOpenDeleteModal(false)}
                    title="Delete Category"
                >
                    <div className="p-6 text-center">
                        <div className="w-16 h-16 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Trash2 size={32} />
                        </div>
                        <h3 className="text-lg font-bold text-gray-900 mb-2">
                            Are you sure?
                        </h3>
                        <p className="text-gray-500 mb-8">
                            Do you really want to delete{" "}
                            <b>{categoryToDelete?.name}</b>? This action cannot
                            be undone.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setOpenDeleteModal(false)}
                                className="flex-1 px-4 py-3 bg-gray-100 text-gray-700 font-semibold rounded-xl hover:bg-gray-200 transition"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDeleteCategory}
                                disabled={loading}
                                className="flex-1 px-4 py-3 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700 shadow-lg shadow-red-200 transition disabled:bg-red-300"
                            >
                                {loading ? "Deleting..." : "Yes, Delete"}
                            </button>
                        </div>
                    </div>
                </Modals>
            </div>
        </Dashboards>
    );
};

export default Category;
