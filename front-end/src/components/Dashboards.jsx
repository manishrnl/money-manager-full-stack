import Menubar from "./Menubar.jsx";
import Sidebar from "./Sidebar.jsx";
import {useContext} from "react";
import {AppContext} from "../context/AppContext.jsx";

const Dashboards = ({children, activeMenu}) => {
    const {user} = useContext(AppContext);

    return (
        /* 1. Force the wrapper to be exactly the height of the screen and hide global scroll */
        <div className="h-screen flex flex-col overflow-hidden bg-gray-50">

            {/* Menubar stays at the top */}
            <Menubar activeMenu={activeMenu}/>

            <div className="flex flex-1 overflow-hidden">
                {user && (
                    /* 2. Sidebar stays fixed on the left */
                    <div className="hidden lg:block h-full shrink-0">
                        <Sidebar activeMenu={activeMenu}/>
                    </div>
                )}

                {/* 3. The Content Area: This is the ONLY part that scrolls */}
                <main className="flex-1 overflow-y-auto p-5 custom-scrollbar">
                    <div className="max-w-[1600px] mx-auto">
                        {children}

                        {/* 4. Added a "Spacer" at the bottom for better UX */}
                        <div className="h-20"/>
                    </div>
                </main>
            </div>
        </div>
    );
};

export default Dashboards;