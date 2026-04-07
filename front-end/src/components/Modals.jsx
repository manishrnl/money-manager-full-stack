import {X} from "lucide-react";

const Modals = ({isOpen, onClose, title, children}) => {


    if (!isOpen) return null;
    return (
        <div
            className="fixed inset-0 z-50 flex justify-center items-center w-full h-full overflow-hidden bg-black/40 backdrop-blur-sm ">
            <div className="relative p-4 w-full max-w-2xl max-h-[90vh]">
                {/*Modal Header*/}
                <div
                    className="relative bg-white rounded-xl shadow-2xl border border-gray-100">
                    {/*Modal Content*/}
                    <div
                        className="flex items-center justify-between p-5 md:p-6 border-b border-gray-100 rounded-t-xl">
                        <h3 className="text-xl font-semibold text-gray-800">
                            {title}
                        </h3>

                        <button
                            type="button"
                            onClick={onClose} // Assuming this is your close button
                            className="group flex items-center justify-center w-9 h-9 text-gray-400 bg-white border border-gray-100 rounded-xl shadow-sm hover:bg-gray-50 hover:text-rose-500 hover:border-rose-100 transition-all duration-300 ease-in-out focus:outline-none focus:ring-2 focus:ring-rose-500/20 active:scale-90"
                        >
                            <X
                                size={18}
                                className="transition-transform duration-300 group-hover:rotate-90"
                            />
                        </button>
                    </div>


                    {/*    Modal Body*/}
                    <div className="p-5 md:p-6 text-gray-600">
                        {children}
                    </div>
                </div>
            </div>
        </div>
    )
}
export default Modals;