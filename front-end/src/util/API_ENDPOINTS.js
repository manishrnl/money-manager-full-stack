export const BASE_URL = "https://money-manager-backend-2w6b.onrender.com/api/v1";
export const CLOUDINARY_CLOUD_NAME = "dwnftgpia";
export const API_ENDPOINTS = {

    ACTIVATE_ACCOUNTS: (activationToken) => `/profile/activate?activationToken=${activationToken}`,
    UPLOAD_IMAGE: `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
    LOGIN: "/profile/login",
    REFRESH_TOKEN: (refreshToken) => `/profile/refresh/${refreshToken}`,
    REGISTER: "/profile/register",
    GET_USER_PROFILE: "/profile",

    // Categories
    GET_ALL_CATEGORY: "/category",
    SAVE_CATEGORY: "/category",
    UPDATE_CATEGORY: (categoryId) => `/category/${categoryId}`,
    GET_CATEGORY_BY_TYPE: (type) => `/category/${type}`,
    DELETE_CATEGORY: (categoryId) => `/category/${categoryId}`,

    // Incomes
    GET_ALL_INCOMES: "/incomes",
    ADD_INCOME: "/incomes",
    UPDATE_INCOME_BY_ID: (incomeId) => `/incomes/${incomeId}`,
    DELETE_INCOME_BY_ID: (incomeId) => `/incomes/${incomeId}`,

    // Expenses
    GET_ALL_EXPENSES: "/expenses",
    ADD_EXPENSE: "/expenses",
    UPDATE_EXPENSE_BY_ID: (expenseId) => `/expenses/${expenseId}`,
    DELETE_EXPENSE_BY_ID: (expensesId) => `/expenses/${expensesId}`,

    // Filters (New)
    FILTER_TRANSACTIONS: "/filters",
    SEND_FILTERED_EMAIL: (email) => `/notification/filterData/${email}`,

    // Notifications & Reports
    SEND_INCOME_REPORT: (email) => `/notification/incomes/${email}`,
    SEND_EXPENSES_REPORT: (email) => `/notification/expenses/${email}`,

    // Get Dashboard Data
    GET_DASHBOARD_DATA: `/dashboard`


}