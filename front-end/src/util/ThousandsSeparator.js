export const addThousandsSeparator = (num) => {
    if (num === null || num === undefined) return "0";

    return new Intl.NumberFormat('en-IN', {
        maximumFractionDigits: 2,
        minimumFractionDigits: 0
    }).format(num);
};