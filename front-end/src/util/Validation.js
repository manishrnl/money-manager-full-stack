export const validateEmail = (email) => {
    if (email && email.trim()) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }
    return false;
};

export const validatePassword = (password) => {
    if (password && password.trim()) {
        /* ^                : Start of string
           (?=.*[a-z])      : Must contain at least one lowercase letter
           (?=.*[A-Z])      : Must contain at least one uppercase letter
           (?=.*\d)         : Must contain at least one digit
           (?=.*[@$!%*?&#]) : Must contain at least one special character
           [A-Za-z\d@$!%*?&#]{10,} : Allow these characters, minimum 10 total
           $                : End of string
        */
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{10,}$/;
        return regex.test(password);
    }
    return false;
};

export const checkPasswordAndConfirmPassword = (password1, confirmPassword1) => {
    const password = password1?.trim();
    const confirmPassword = confirmPassword1?.trim();

    if (!password) return "Please set Passwords for your account";
    if (!confirmPassword) return "Please retype Confirm password";
    if (password !== confirmPassword) return "Passwords do not match";

    if (!validatePassword(password)) {
        return "Password must be 10+ chars with Upper, Lower, Number, and Special char";
    }

    return ""; // CRITICAL: Return empty string if no error
};

export const checkFullName = (fullName) => {
    const trimmedName = fullName ? fullName.trim() : "";
    if (!trimmedName) return "Full Name is Compulsory to create an account";

    const nameParts = trimmedName.split(" ").filter(part => part.length > 0);
    if (nameParts.length < 2) {
        return "Please enter your full name (First and Last name)";
    }

    return ""; // CRITICAL: Return empty string if no error
};

export const getPasswordStrength = (password = "") => {
    return [
        {label: "10+ Characters", met: password.length >= 10},
        {label: "Uppercase Letter", met: /[A-Z]/.test(password)},
        {label: "Lowercase Letter", met: /[a-z]/.test(password)},
        {label: "Number", met: /\d/.test(password)},
        {label: "Special Character (@$!%*?&#)", met: /[@$!%*?&#]/.test(password)},
    ];
};