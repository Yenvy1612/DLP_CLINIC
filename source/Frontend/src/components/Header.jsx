import { FiShoppingCart } from "react-icons/fi";
import { BsFillCartCheckFill } from "react-icons/bs";
import { TbPhone } from "react-icons/tb";
import { RiMenuFill } from "react-icons/ri";
import { headerListByRole } from "../data/headerList";
import { useSidebarContext } from "../contexts/SideBarContext";
import logo from "../assets/images/logo/logo_png.png";
import { NavLink, useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { appointmentService, authService } from "../api";
import useAuthSnapshot from "../hooks/useAuthSnapshot";
import { clearCurrentUser, getRoleLabel, getRoleProfilePath } from "../utils/authUtils";

function Header() {
    const { toggleSidebar } = useSidebarContext();
    const { role: currentRole, userId, isLoggedIn } = useAuthSnapshot();

    const navigate = useNavigate();
    const role = currentRole || "GUEST";
    const headerList = headerListByRole[role] || headerListByRole.GUEST;
    const [isFill, setIsFill] = useState(false);
    const [showHamburger, setShowHamburger] = useState(false);
    const containerRef = useRef(null);
    const logoRef = useRef(null);
    const navRef = useRef(null);
    const rightRef = useRef(null);

    useEffect(() => {
        if (role !== "PATIENT" || !userId) {
            setIsFill(false);
            return;
        }

        const getPendingAppointments = async () => {
            try {
                const pendingAppointments = await appointmentService.pendingByPatientId(userId);
                setIsFill(pendingAppointments.length >= 1);
            }
            catch (err) {
                setIsFill(false);
            }
        }
        getPendingAppointments();
    }, [role, userId]);

    useEffect(() => {
        const updateLayout = () => {
            if (typeof window === "undefined") return;

            if (window.innerWidth < 768) {
                setShowHamburger(true);
                return;
            }

            const container = containerRef.current;
            const logoNode = logoRef.current;
            const navNode = navRef.current;
            const rightNode = rightRef.current;

            if (!container || !logoNode || !navNode || !rightNode) {
                setShowHamburger(false);
                return;
            }

            const availableWidth = container.clientWidth;
            const requiredWidth = logoNode.scrollWidth + navNode.scrollWidth + rightNode.scrollWidth + 64;
            setShowHamburger(requiredWidth > availableWidth);
        };

        updateLayout();
        window.addEventListener("resize", updateLayout);
        return () => window.removeEventListener("resize", updateLayout);
    }, [isLoggedIn, role, isFill, headerList.length]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch {
            // Continue local cleanup even if API logout fails.
        } finally {
            clearCurrentUser();
            window.location.replace("/");
        }
    };

    const roleProfilePath = getRoleProfilePath(role);


    return (
        <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/95 backdrop-blur">
            <div ref={containerRef} className="mx-auto flex h-20 max-w-7xl items-center justify-between px-4 md:px-8">
            <img ref={logoRef} src={logo} alt="logo" className="logo h-28 w-44 bg-cover cursor-pointer object-contain" onClick={() => navigate("/")} />
            <div ref={navRef} className={`header-menu ${showHamburger ? 'hidden' : 'hidden md:flex'} flex-wrap gap-x-8 text-[var(--brand-navy)]`}>
                {headerList.map((item) => (
                    <div key={item.id} className="header-items relative group">
                        <NavLink to={item.to} className={({ isActive }) => `${isActive ? 'text-[var(--brand-600)]' : ''} font-semibold flex items-center gap-1 hover:text-[var(--brand-600)] transition-colors`}>
                            {item.name}
                            {item.hasArrow && (
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
                                    className="w-3.5 h-3.5 mt-[1px]">
                                    <path d="M6 9l6 6 6-6" />
                                </svg>
                            )}
                        </NavLink>

                        {/* dropdown cấp 1 */}
                        {item.children && (
                            <div
                                className="
                                    invisible opacity-0 translate-y-2 scale-95
                                    group-hover:visible group-hover:opacity-100 group-hover:translate-y-0 group-hover:scale-100
                                    absolute left-1/2 -translate-x-1/2 mt-2 min-w-[220px]
                                    rounded-xl bg-white shadow-xl transition-all duration-200 ease-out z-40
 
                                "
                                role="menu"
                            >
                                <div className="absolute -top-2 left-0 right-0 h-2" />
                                <div className="absolute -top-2 left-1/2 -translate-x-1/2 w-3 h-3 rotate-45 bg-white border-l border-t border-slate-200" />
                                <ul className="py-2">
                                    {item.children.map((c) => (
                                        <li key={c.id} className="relative group/item">
                                            <NavLink
                                                to={c.to}
                                                    className={({ isActive }) => `${isActive ? 'text-[var(--brand-600)]' : ''} font-medium flex items-center justify-between gap-3 px-4 py-2.5 text-[15px] text-[var(--brand-navy)] transition-colors hover:text-[var(--brand-600)] hover:bg-slate-100`}
                                                role="menuitem"
                                            >
                                                {c.label}
                                                {c.children && (
                                                    <svg viewBox="0 0 24 24" className="w-3.5 h-3.5 text-[#00278D] pointer-events-none"
                                                        fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M9 6l6 6-6 6" />
                                                    </svg>
                                                )}
                                            </NavLink>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </div>
                ))}
            </div>
            <div ref={rightRef} className="other-header-items flex items-center gap-x-4 text-[var(--brand-600)]">
                {role == "PATIENT" ? (<div className="hidden cursor-pointer text-xl md:block"><NavLink to={"/patient/cart"}>{!isFill ? <FiShoppingCart /> : <BsFillCartCheckFill />}</NavLink></div>) : ""}
                <div className="phone gap-x-2 hidden md:flex">
                    <div className="text-xl w-11 h-11 flex justify-center items-center rounded-full p-3 border border-slate-200 bg-slate-100 text-[var(--brand-600)]">
                        <TbPhone />
                    </div>
                    <div className="flex flex-col pr-2">
                        <p className="text-xs uppercase tracking-[0.14em] text-slate-500">Emergency Line</p>
                        <a href="tel:0379330721" className="text-lg font-bold text-[var(--brand-navy)] cursor-pointer">+84-379-330-721</a>
                    </div>
                </div>
                <div className="hidden items-center gap-2 md:flex">
                    {isLoggedIn ? (
                        <>
                            <button
                                onClick={() => navigate(roleProfilePath)}
                                className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-semibold text-[var(--brand-navy)] hover:border-[var(--brand-500)] hover:text-[var(--brand-600)]"
                            >
                                {getRoleLabel(role)}
                            </button>
                            <button
                                onClick={handleLogout}
                                className="rounded-lg bg-[var(--brand-600)] px-3 py-1.5 text-sm font-semibold text-white hover:bg-[var(--brand-700)]"
                            >
                                Đăng xuất
                            </button>
                        </>
                    ) : (
                        <button
                            onClick={() => navigate("/login")}
                            className="rounded-lg bg-[var(--brand-600)] px-3 py-1.5 text-sm font-semibold text-white hover:bg-[var(--brand-700)]"
                        >
                            Đăng nhập
                        </button>
                    )}
                </div>

                <button onClick={toggleSidebar} className={`${showHamburger ? 'inline-flex' : 'hidden'} text-2xl border border-gray-300 rounded-sm p-2 cursor-pointer hover:border-[var(--brand-500)] transition duration-500 ease-in-out`}>
                    <RiMenuFill />
                </button>
            </div>
            </div>
        </header>
    );
}

export default Header;
