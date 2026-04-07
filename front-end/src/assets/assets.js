import logo from "./logo.png";
import money_bag from "./money-bag.png";
import finance from "./finance.png";
import {Coins, FunnelPlus, LayoutDashboard, List, Wallet} from "lucide-react";

export const assets = {
    logo,
    money_bag,
    finance
}
export const SIDE_BAR_DATA = [
    {
        id: "01",
        label: "Dashboards",
        icon: LayoutDashboard,
        path: "/dashboard"
    },
    {
        id: "02",
        label: "Category",
        icon: List,
        path: "/category"
    },
    {
        id: "03",
        label: "Income",
        icon: Wallet,
        path: "/income"
    } ,   {
        id: "04",
        label: "Expense",
        icon: Coins,
        path: "/expense"
    }, {
        id: "05",
        label: "Filters",
        icon: FunnelPlus,
        path: "/filter"
    }
]