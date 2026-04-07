import {API_ENDPOINTS} from "./API_ENDPOINTS.js";

const CLOUDINARY_UPLOAD_PRESET = "money-manager";
const uploadProfileImage = async (image) => {
    const formData = new FormData();
    formData.append("file", image);
    formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);
    try {
        const response = await fetch(API_ENDPOINTS.UPLOAD_IMAGE, {
            method: "POST",
            body: formData

        })


        const data = await response.json();

        // Check if data exists and contains the secure_url (Cloudinary) or url (ImgBB)
        if (data && data.secure_url) {
            return data.secure_url;
        } else if (data && data.url) {
            return data.url;
        }

        throw new Error("Upload failed: No URL returned from server");
    } catch (error) {
        console.error("Error uploading file:", error);
        return null; // Return null so the Signup logic can handle it
    }
};

export default uploadProfileImage;
