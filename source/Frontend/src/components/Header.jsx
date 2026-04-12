import { useEffect, useRef, useState } from "react";
import { FiBell, FiTrash2 } from "react-icons/fi";
import { RiMenuFill } from "react-icons/ri";
import { TbPhone } from "react-icons/tb";
import { NavLink, useNavigate } from "react-router-dom";
import { activityService, appointmentService, authService } from "../api";
import logo from "../assets/images/logo/logo_png.png";
import { useSidebarContext } from "../contexts/SideBarContext";
import { headerListByRole } from "../data/headerList";
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
    const [notifications, setNotifications] = useState([]);
    const [notificationCount, setNotificationCount] = useState(0);
    const [isBellOpen, setIsBellOpen] = useState(false);
    const [selectedNotification, setSelectedNotification] = useState(null);
    const [deletingNotificationId, setDeletingNotificationId] = useState(null);
    const containerRef = useRef(null);
    const logoRef = useRef(null);
    const navRef = useRef(null);
    const rightRef = useRef(null);
    const bellPanelRef = useRef(null);

    const isNotificationEnabledRole = isLoggedIn && (role === "DOCTOR" || role === "PATIENT") && !!userId;
    const notificationTitle = role === "DOCTOR" ? "Thông báo bác sĩ" : "Thông báo bệnh nhân";

    const normalizeNoticeMessage = (raw) => String(raw || "").replace(/^\[USER:\d+\]\s*/, "");

    const formatNoticeTime = (time) => {
        if (!time) return "";
        const value = new Date(time);
        if (Number.isNaN(value.getTime())) return "";
        const dd = String(value.getDate()).padStart(2, "0");
        const mm = String(value.getMonth() + 1).padStart(2, "0");
        const yyyy = value.getFullYear();
        const hh = String(value.getHours()).padStart(2, "0");
        const min = String(value.getMinutes()).padStart(2, "0");
        return `${dd}/${mm}/${yyyy} ${hh}:${min}`;
    };

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

    useEffect(() => {
        if (!isNotificationEnabledRole) {
            setNotifications([]);
            setNotificationCount(0);
            setSelectedNotification(null);
            setIsBellOpen(false);
            return;
        }

        let isMounted = true;

        const loadNotifications = async () => {
            try {
                const [list, count] = await Promise.all([
                    activityService.getRecentByUser(userId),
                    activityService.getCountByUser(userId),
                ]);
                if (!isMounted) return;

                const normalizedList = Array.isArray(list) ? list : [];
                setNotifications(normalizedList);
                setNotificationCount(Number(count) || normalizedList.length);

                if (normalizedList.length === 0) {
                    setSelectedNotification(null);
                    return;
                }

                setSelectedNotification((prev) => {
                    if (!prev) return normalizedList[0];
                    return normalizedList.find((item) => item.id === prev.id) || normalizedList[0];
                });
            } catch {
                if (!isMounted) return;
                setNotifications([]);
                setNotificationCount(0);
                setSelectedNotification(null);
            }
        };

        loadNotifications();
        const timer = setInterval(loadNotifications, 15000);
        return () => {
            isMounted = false;
            clearInterval(timer);
        };
    }, [isNotificationEnabledRole, userId]);

    useEffect(() => {
        if (!isBellOpen) return undefined;

        const handleClickOutside = (event) => {
            if (!bellPanelRef.current?.contains(event.target)) {
                setIsBellOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [isBellOpen]);

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

                            {isNotificationEnabledRole ? (
                                <div ref={bellPanelRef} className="relative">
                                    <button
                                        type="button"
                                        onClick={() => setIsBellOpen((prev) => !prev)}
                                        className="relative rounded-lg border border-slate-300 p-2 text-[var(--brand-navy)] hover:border-[var(--brand-500)] hover:text-[var(--brand-600)]"
                                        aria-label="Thông báo"
                                    >
                                        <FiBell size={18} />
                                        {notificationCount > 0 ? (
                                            <span className="absolute -top-2 -right-2 min-w-[20px] h-5 px-1 rounded-full bg-rose-600 text-white text-[11px] leading-5 font-bold text-center">
                                                {notificationCount > 5 ? "5+" : notificationCount}
                                            </span>
                                        ) : null}
                                    </button>

                                    {isBellOpen ? (
                                        <div className="absolute right-0 mt-2 w-[380px] max-w-[90vw] rounded-xl border border-slate-200 bg-white shadow-2xl z-50 overflow-hidden">
                                            <div className="px-4 py-3 border-b border-slate-200 flex items-center justify-between">
                                                <p className="text-sm font-bold text-slate-800">{notificationTitle}</p>
                                                <span className="text-xs text-slate-500">{notificationCount > 5 ? "5+" : notificationCount}</span>
                                            </div>

                                            {notifications.length === 0 ? (
                                                <p className="px-4 py-6 text-sm text-slate-500">Chưa có thông báo nào.</p>
                                            ) : (
                                                <div className="grid grid-cols-1 md:grid-cols-2">
                                                    <div className="max-h-72 overflow-auto border-r border-slate-200">
                                                        {notifications.map((notice) => (
                                                            <button
                                                                key={notice.id}
                                                                type="button"
                                                                onClick={() => setSelectedNotification(notice)}
                                                                className={`w-full text-left px-3 py-2.5 border-b border-slate-100 hover:bg-slate-50 ${selectedNotification?.id === notice.id ? "bg-slate-100" : "bg-white"}`}
                                                            >
                                                                <p className="text-xs font-semibold text-slate-700 truncate">
                                                                    {normalizeNoticeMessage(notice.message)}
                                                                </p>
                                                                <p className="text-[11px] text-slate-500 mt-1">{formatNoticeTime(notice.time)}</p>
                                                            </button>
                                                        ))}
                                                    </div>

                                                    <div className="p-3 min-h-[160px] flex flex-col justify-between">
                                                        <div>
                                                            <p className="text-xs uppercase tracking-wide text-slate-500">Nội dung</p>
                                                            <p className="text-sm text-slate-800 mt-2 leading-relaxed">
                                                                {selectedNotification
                                                                    ? normalizeNoticeMessage(selectedNotification.message)
                                                                    : "Chọn một thông báo để xem chi tiết"}
                                                            </p>
                                                            {selectedNotification?.time ? (
                                                                <p className="text-xs text-slate-500 mt-2">{formatNoticeTime(selectedNotification.time)}</p>
                                                            ) : null}
                                                        </div>

                                                        <div className="pt-3 mt-3 border-t border-slate-200 flex justify-end">
                                                            <button
                                                                type="button"
                                                                disabled={!selectedNotification || deletingNotificationId === selectedNotification?.id}
                                                                onClick={async () => {
                                                                    if (!selectedNotification) return;
                                                                    setDeletingNotificationId(selectedNotification.id);
                                                                    try {
                                                                        await activityService.deleteByUser(userId, selectedNotification.id);
                                                                        setNotifications((prev) => {
                                                                            const next = prev.filter((item) => item.id !== selectedNotification.id);
                                                                            setSelectedNotification(next[0] || null);
                                                                            return next;
                                                                        });
                                                                        setNotificationCount((prev) => Math.max(0, Number(prev || 0) - 1));
                                                                    } finally {
                                                                        setDeletingNotificationId(null);
                                                                    }
                                                                }}
                                                                className="inline-flex items-center gap-1 rounded-lg border border-rose-300 px-3 py-1.5 text-sm font-semibold text-rose-700 hover:bg-rose-50 disabled:opacity-60"
                                                            >
                                                                <FiTrash2 size={14} />
                                                                Xóa
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    ) : null}
                                </div>
                            ) : null}

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
