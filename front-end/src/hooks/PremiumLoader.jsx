import React, { useState, useEffect } from "react";
import { ShieldCheck, Zap, Globe, AlertCircle, Terminal } from "lucide-react";

const PremiumLoader = ({ isDone }) => {
    const [progress, setProgress] = useState(0);
    const [logIndex, setLogIndex] = useState(0);

    const technicalLogs = [
        "INITIALIZING NODE INSTANCE...",
        "HANDSHAKING WITH RENDER CLOUD...",
        "WAKING UP IDLE CONTAINERS...",
        "MOUNTING ENCRYPTED VOLUMES...",
        "ESTABLISHING DATABASE HANDSHAKE...",
        "PULLING LATEST BUILD ARTIFACTS...",
        "VERIFYING SSL CERTIFICATES...",
        "BOOTING API GATEWAY...",
        "WARMING UP COLD CACHE...",
        "LOADING ENVIRONMENT VARIABLES...",
        "SYNCHRONIZING CLUSTER NODES...",
        "ATTACHING NETWORK INTERFACE...",
        "RUNNING HEALTH CHECKS...",
        "ALLOCATING VIRTUAL MEMORY...",
        "SETTING UP WEB SOCKETS...",
        "OPTIMIZING ASSET PIPELINES...",
        "FINALIZING LOAD BALANCER...",
        "SYSTEM READY - AWAITING RESPONSE...",
    ];

    useEffect(() => {
        const totalDuration = 180;
        const intervalTime = 1000;
        const increment = 100 / totalDuration;

        // 1. Progress Logic
        const progressTimer = setInterval(() => {
            setProgress((prev) => {
                if (isDone) return 100;
                if (prev >= 98) return 98;
                return prev + increment;
            });
        }, intervalTime);

        const logTimer = setInterval(
            () => {
                setLogIndex((prev) => {
                    // If done and we've reached the last message, stop cycling
                    if (isDone && prev === technicalLogs.length - 1) {
                        clearInterval(logTimer);
                        return prev;
                    }
                    return (prev + 1) % technicalLogs.length;
                });
            },
            isDone ? 500 : 10000,
        );

        // 3. CLEANUP (Crucial: This must always be returned)
        return () => {
            clearInterval(progressTimer);
            clearInterval(logTimer);
        };
    }, [isDone, technicalLogs.length]); // Added length as a safety dependency

    return (
        <div className="h-screen w-full flex flex-col items-center justify-center bg-gray-200 text-slate-900 overflow-hidden font-sans">
            <div className="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_50%_50%,_rgba(16,185,129,0.08),transparent)] pointer-events-none" />

            <div className="relative flex flex-col items-center max-w-xl w-full px-6 text-center">
                {/* Progress Circle */}
                <div className="relative mb-10 flex items-center justify-center">
                    <div className="absolute h-40 w-40 border-[6px] border-white rounded-full shadow-sm" />
                    <svg className="h-40 w-40 rotate-[-90deg]">
                        <circle
                            cx="80"
                            cy="80"
                            r="74"
                            stroke="currentColor"
                            strokeWidth="6"
                            fill="transparent"
                            className="text-emerald-500 transition-all duration-1000 ease-linear"
                            strokeDasharray={465}
                            strokeDashoffset={465 - (465 * progress) / 100}
                            strokeLinecap="round"
                        />
                    </svg>
                    <div className="absolute flex flex-col items-center">
                        <span
                            className={`text-4xl font-black font-mono tracking-tighter transition-colors duration-500 ${
                                progress >= 100
                                    ? "text-emerald-500"
                                    : "text-slate-800"
                            }`}
                        >
                            {Math.floor(progress)}%
                        </span>
                        <div className="flex items-center gap-1">
                            <span className="h-2 w-2 bg-emerald-500 rounded-full animate-pulse" />
                            <span className="text-[10px] font-black text-emerald-700 tracking-[0.2em] uppercase">
                                Active
                            </span>
                        </div>
                    </div>
                </div>

                {/* Real-time Log Console */}
                <div className="w-full bg-slate-900 rounded-xl p-5 mb-8 shadow-2xl border-b-4 border-slate-800">
                    <div className="flex items-center gap-2 mb-3 border-b border-slate-800 pb-2">
                        <Terminal size={16} className="text-emerald-500" />
                        <span
                            className="text-[13px] font-mono text-gray-200 uppercase
                        tracking-widest"
                        >
                            Backend Console Output
                        </span>
                    </div>
                    <div className="h-8 flex items-center">
                        <p className="text-emerald-400 font-mono text-sm md:text-base text-left w-full overflow-hidden whitespace-nowrap">
                            <span className="text-slate-500 mr-2 font-bold">
                                {">"}
                            </span>
                            {technicalLogs[logIndex]}
                            <span className="inline-block w-2 h-4 bg-emerald-500 ml-1 animate-[ping_1s_infinite]" />
                        </p>
                    </div>
                </div>

                {/* Important Visibility Message */}
                <div className="w-full bg-white border-l-8 border-emerald-600 rounded-r-2xl p-6 shadow-lg mb-8 text-left">
                    <div className="flex items-start gap-4">
                        <AlertCircle
                            className="text-emerald-600 mt-1 shrink-0"
                            size={28}
                        />
                        <div>
                            <h3 className="text-xl font-black text-slate-900 mb-1 uppercase tracking-tight">
                                Backend Cold Start
                            </h3>
                            <p className="text-slate-600 text-sm md:text-base leading-relaxed">
                                The server is currently{" "}
                                <span className="font-bold text-slate-900 underline decoration-emerald-500">
                                    sleeping
                                </span>{" "}
                                on Render's Free Tier. Full initialization takes
                                approximately{" "}
                                <span className="font-bold text-emerald-700 italic">
                                    2 minutes
                                </span>
                                . Please do not refresh.
                            </p>
                        </div>
                    </div>
                </div>

                {/* Technical Bottom Bar */}
                <div className="flex justify-between w-full px-2 text-slate-500 font-bold text-[10px] tracking-[0.15em] uppercase border-t border-slate-300 pt-6">
                    <span className="flex items-center gap-1.5">
                        <Globe size={14} /> Edge_Node: IN-01
                    </span>
                    <span className="flex items-center gap-1.5">
                        <Zap size={14} /> Pwr: High_Performance
                    </span>
                    <span className="flex items-center gap-1.5">
                        <ShieldCheck size={14} /> Auth: Encrypted
                    </span>
                </div>
            </div>
        </div>
    );
};

export default PremiumLoader;
