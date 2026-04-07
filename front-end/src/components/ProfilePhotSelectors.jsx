import {useRef, useState} from "react";
import {Trash, Upload, User} from "lucide-react"; // Added User import

// Destructure props correctly inside { }
const ProfilePhotoSelector = ({image, setImage}) => {
    const inputRef = useRef(null);
    const [previewUrl, setPreviewUrl] = useState(null);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImage(file);
            // Create a preview URL for the selected file
            const preview = URL.createObjectURL(file);
            setPreviewUrl(preview);
        }
    };

    const handleRemoveImage = (e) => {
        e.preventDefault();
        setImage(null);
        setPreviewUrl(null);
        // Reset the input value so the same file can be selected again
        if (inputRef.current) inputRef.current.value = "";
    };

    const onChooseFile = (e) => {
        e.preventDefault();
        inputRef.current?.click();
    };

    return (
        <div className="flex justify-center mb-6">
            <input
                type="file"
                accept="image/*"
                ref={inputRef}
                onChange={handleImageChange}
                className="hidden"
            />

            {!image ? (
                /* Empty State: Shows Purple Circle with User Icon */
                <div
                    className="relative w-24 h-24 flex items-center justify-center bg-purple-100 rounded-full border-2 border-dashed border-purple-300">
                    <User className="text-purple-500" size={40}/>

                    <button
                        type="button"
                        onClick={onChooseFile}
                        className="absolute bottom-0 right-0 w-8 h-8 flex items-center justify-center bg-violet-600 text-white rounded-full hover:bg-violet-700 transition-colors shadow-md"
                    >
                        <Upload size={16}/>
                    </button>
                </div>
            ) : (
                /* Preview State: Shows Selected Image */
                <div className="relative">
                    <img
                        src={previewUrl}
                        alt="Profile Preview"
                        className="w-24 h-24 rounded-full object-cover border-2 border-violet-500 shadow-lg"
                    />
                    <button
                        type="button"
                        onClick={handleRemoveImage}
                        className="absolute bottom-0 right-0 w-8 h-8 flex items-center justify-center bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors shadow-md"
                    >
                        <Trash size={16}/>
                    </button>
                </div>
            )}
        </div>
    );
};

export default ProfilePhotoSelector;