import Menubar from "./Menubar.jsx";
import Sidebar from "./Sidebar.jsx";
import { useContext } from "react";
import { AppContext } from "../context/AppContext.jsx";
// Remove Section import if you aren't using the icon
import Footer from "../components/Footer.jsx";

const Dashboards = ({ children, activeMenu }) => {
    const { user } = useContext(AppContext);

    return (
        /* Use a standard div or React Fragment instead of the Section icon */
        <div className="h-screen flex flex-col overflow-hidden bg-gray-50">
            
            {/* 1. Top Navbar */}
            <Menubar activeMenu={activeMenu} />

            <div className="flex flex-1 overflow-hidden">
                {/* 2. Sidebar */}
                {user && (
                    <div className="hidden lg:block h-full shrink-0 border-r border-gray-200">
                        <Sidebar activeMenu={activeMenu} />
                    </div>
                )}

                {/* 3. Scrollable Content Area */}
                <main className="flex-1 overflow-y-auto custom-scrollbar flex flex-col">
                    {/* Inner wrapper for padding and max-width */}
                    <div className="flex-1 p-5">
                        <div className="max-w-[1600px] mx-auto min-h-screen">
                            {children}
                        </div>
                    </div>

                    {/* 4. Footer stays at the bottom of the scrollable area */}
                   
                </main>
            </div>
        </div>
    );
};

export default Dashboards;