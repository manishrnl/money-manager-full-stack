import {useState} from "react";
import {Image, X} from "lucide-react";
import EmojiPicker from "emoji-picker-react";

const EmojiPickerPopup = ({icon, onSelect}) => {


    const [isOpen, setIsOpen] = useState(false);
    const handleEmojiClick = (emoji) => {

        onSelect(emoji?.imageUrl || "");
        setIsOpen(false);

    }


    return (
        <div className="flex flex-col gap-4 mb-6">
            <div
                onClick={() => setIsOpen(true)}
                className="flex items-center gap-4 cursor-pointer group"
            >
                <div
                    className="w-12 h-12 flex items-center justify-center bg-purple-50 rounded-xl border border-purple-100 overflow-hidden shrink-0">
                    {icon ? (
                        <img src={icon} alt="Icon"
                             className="w-full h-full object-contain p-2"/>
                    ) : (
                        <Image className="text-purple-400" size={24}/>
                    )}
                </div>
                <p className="text-sm font-medium text-gray-600 group-hover:text-purple-600 transition-colors">
                    {icon ? "Change icon" : "Pick Icon"}
                </p>
            </div>

            {isOpen && (
                <div className="relative inline-block">
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            setIsOpen(false);
                        }}
                        className="w-8 h-8 flex items-center justify-center bg-white border border-gray-200 rounded-full absolute -top-3 -right-3 shadow-md hover:bg-gray-50 z-20 transition-transform active:scale-90"
                    >
                        <X size={14} className="text-gray-500"/>
                    </button>
                    <div className="z-10 relative">
                        <EmojiPicker
                            onEmojiClick={handleEmojiClick}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

export default EmojiPickerPopup;