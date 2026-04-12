import "bootstrap-icons/font/bootstrap-icons.css";

import { motion } from "framer-motion";
import {
    BiCurrentLocation,
    BiLogoFacebook,
    BiLogoGithub,
    BiLogoGmail,
    BiLogoInstagram,
    BiLogoLinkedin,
    BiLogoTwitter,
    BiLogoWhatsapp,
} from "react-icons/bi";
import React from "react";

const Footer = () => {
    return (
        <footer className="bg-gray-300 text-gray-900 mt-8">
            <div className="max-w-[85%] mx-auto py-8">
                <div className="row text-center text-md-start">
                    {/* Column 1 — About / Message */}
                    <div className="col-md-4 mb-6 mb-md-0">
                        <h2 className="text-sm font-bold mb-3 tracking-widest text-muted uppercase">
                            Let's Connect 🌐
                        </h2>
                        <p className="lh-base text-sm font-medium">
                            I'm always open to collaborating on exciting
                            projects or discussing new ideas.
                        </p>
                    </div>

                    {/* Column 2 — Contact Info */}
                    <div className="col-md-4 mb-6 mb-md-0">
                        <h3 className="text-sm font-bold mb-3 tracking-widest text-muted uppercase">
                            Contact Me
                        </h3>

                        <div className="d-flex flex-column gap-2">
                            <a
                                href="https://www.mappls.com/ctd6rr"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="group text-gray-700 text-sm text-decoration-none gap-x-2 d-inline-flex align-items-center opacity-75 hover-opacity-100 transition-all hover:translate-x-1 hover:-translate-y-1"
                            >
                                <BiCurrentLocation
                                    size={22}
                                    className="transition-transform duration-1000 group-hover:rotate-[360deg]"
                                ></BiCurrentLocation>
                                <span className="text-gray-900">
                                    Madhubani, Bihar
                                </span>
                            </a>

                            <a
                                href="mailto:manishrajrnl@zohomail.in"
                                className="group text-gray-700 text-sm text-decoration-none gap-x-2 d-inline-flex align-items-center opacity-75 hover-opacity-100 transition-all hover:translate-x-1 hover:-translate-y-1"
                                target="_blank"
                                rel="noopener noreferrer"
                            >
                                <BiLogoGmail
                                    className="transition-transform duration-700 group-hover:rotate-[360deg] text-red-400"
                                    size={22}
                                />
                                <span className="text-gray-900">
                                    manishrajrnl@zohomail.in
                                </span>
                            </a>

                            <a
                                href="tel:+919501421887"
                                className="group text-gray-700 text-sm text-decoration-none d-inline-flex align-items-center opacity-75 hover-opacity-100 transition-all hover:translate-x-1 hover:-translate-y-1"
                            >
                                <i className="bi bi-telephone-fill me-2 text-shadow-green-600 transition-transform duration-700 group-hover:rotate-[360deg]"></i>
                                <span className="text-gray-900">
                                    +91 9501421887
                                </span>
                            </a>
                        </div>
                    </div>

                    {/* Column 3 — Social Links */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.2 }}
                        className="col-md-4"
                    >
                        <h3 className="text-sm font-bold text-uppercase tracking-widest text-muted">
                            Reach Me
                        </h3>
                        <div className="d-flex flex-wrap justify-content-center justify-content-md-start gap-3 pt-4">
                            {/* Social Badges - Reduced padding and text */}
                            {[
                                {
                                    href: "https://www.linkedin.com/in/manishrnl",
                                    icon: BiLogoLinkedin,
                                    color: "#0077b5",
                                    label: "LinkedIn",
                                    border: "border-gray-300",
                                    bg: "bg-white",
                                    text: "text-dark",
                                },
                                {
                                    href: "https://github.com/manishrnl",
                                    icon: BiLogoGithub,
                                    color: "white",
                                    label: "Github",
                                    border: "border-gray-900",
                                    bg: "bg-dark",
                                    text: "text-white",
                                },
                                {
                                    href: "https://wa.me/919501421887",
                                    icon: BiLogoWhatsapp,
                                    color: "#25D366",
                                    label: "WhatsApp",
                                    border: "border-success",
                                    bg: "bg-light",
                                    hover: "hover:bg-green-50",
                                },
                                {
                                    href: "https://www.instagram.com/manish.rnl/",
                                    icon: BiLogoInstagram,
                                    color: "#e4405f",
                                    label: "Instagram",
                                    border: "border-danger",
                                    bg: "bg-light",
                                    hover: "hover:bg-red-50",
                                },
                                {
                                    href: "https://www.facebook.com/profile.php?id=100011121437261",
                                    icon: BiLogoFacebook,
                                    color: "#1877F2",
                                    label: "Facebook",
                                    border: "border-primary",
                                    bg: "bg-light",
                                    hover: "hover:bg-blue-50",
                                },
                                {
                                    href: "https://x.com/manishrnl",
                                    icon: BiLogoTwitter,
                                    color: "#000000",
                                    label: "Twitter",
                                    border: "border-secondary",
                                    bg: "bg-light",
                                    hover: "hover:bg-gray-100",
                                },
                            ].map((social, i) => (
                                <a
                                    key={i}
                                    href={social.href}
                                    target="_blank"
                                    rel="noreferrer"
                                    className={`group d-flex align-items-center gap-2 px-3 py-1.5 rounded-full border ${social.border} text-decoration-none ${social.text || "text-dark"} ${social.bg} ${social.hover || ""} shadow-sm hover:shadow-md hover:-translate-y-1 hover:translate-x-1 transition-all`}
                                >
                                    <social.icon
                                        className="transition-transform duration-700 group-hover:rotate-[360deg]"
                                        size={16}
                                        color={social.color}
                                    />
                                    <span
                                        className="fw-bold"
                                        style={{ fontSize: "11px" }}
                                    >
                                        {social.label}
                                    </span>
                                </a>
                            ))}
                        </div>
                    </motion.div>
                </div>

                <hr className="border-secondary opacity-30 my-6" />

                <div className="text-center text-gray-950">
                    <p className="mb-1" style={{ fontSize: "12px" }}>
                        © 2026 My Portfolio. All rights reserved.
                    </p>
                    <p
                        className="mb-0 font-medium"
                        style={{ fontSize: "12px" }}
                    >
                        Designed with ❤️ by{" "}
                        <strong className="text-yellow-700">Manish</strong>
                    </p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
