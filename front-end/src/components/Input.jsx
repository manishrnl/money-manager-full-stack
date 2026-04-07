// Destructure the props inside { }
import {useState} from "react";
import {Eye, EyeOff} from "lucide-react";

const Input = ({label, value, onChange, placeholder, type = "text", isSelect, options}) => {

    const [showPassword, setShowPassword] = useState(false);

    const toggleShowPassword = () => {
        setShowPassword(!showPassword);
    }
    return (
        <div className="mb-4 w-full">
            {/* The actual text label above the input */}
            <label className="text-[13px] text-slate-800 block mb-1 font-medium">
                {label}
            </label>

            <div className="relative">
                {/* Changed from <label> to <input> */}

                {isSelect ? (
                    <select
                        value={value}
                        onChange={onChange}
                        className="w-full bg-transparent outline-none border border-gray-300 rounded-md py-2 px-3 text-gray-700 leading focus:outline-none focus:border-blue-500"
                    >
                        {options.map((option) => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                ) : (
                    <input
                        className="w-full bg-white border border-gray-300 rounded-md py-2 px-3 text-gray-700 leading-tight outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                        type={type === 'password' ? (showPassword ? 'text' : 'password') : type}
                        value={value}
                        onChange={onChange}
                        placeholder={placeholder}
                    />
                )}






                {type === 'password' && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer">

                        {showPassword ? (
                            <Eye
                                className="text-purple-800"
                                size={23}
                                onClick={toggleShowPassword}/>

                        ) : (
                            <EyeOff
                                className="text-slate-400"
                                size={23}
                                onClick={toggleShowPassword}/>

                        )

                        }


                    </span>
                )}
            </div>
        </div>
    );
};

export default Input;