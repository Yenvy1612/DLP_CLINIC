import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import { FiUsers, FiActivity, FiCalendar, FiBarChart2, FiArrowUpRight } from "react-icons/fi";
import { adminDashboardService } from "../../../api";

const EMPTY_LABEL = "Chưa có dữ liệu";

function Dashboard() {

    const [summary, setSummary] = useState({
        userCount: 0,
        topDoctorName: EMPTY_LABEL,
        topDoctorDoneCount: 0,
        topServiceName: EMPTY_LABEL,
        topServiceDoneCount: 0,
        monthRevenue: 0,
        month: "--",
        year: "--",
    });

    useEffect(() => {
        const getDashboardSummary = async () => {
            try {
                const data = await adminDashboardService.getSummary();
                setSummary({
                    userCount: Number(data?.userCount) || 0,
                    topDoctorName: data?.topDoctorName || EMPTY_LABEL,
                    topDoctorDoneCount: Number(data?.topDoctorDoneCount) || 0,
                    topServiceName: data?.topServiceName || EMPTY_LABEL,
                    topServiceDoneCount: Number(data?.topServiceDoneCount) || 0,
                    monthRevenue: Number(data?.monthRevenue) || 0,
                    month: data?.month || "--",
                    year: data?.year || "--",
                });
            }
            catch (error) {
                console.log(error.message);
            }
        }

        getDashboardSummary();
    }, []);

    const stats = [
        { id: 1, icon: <FiUsers />, label: "Người dùng", value: summary.userCount, to: "/admin/users" },
        {
            id: 2,
            icon: <FiActivity />,
            label: `Bác sĩ được yêu thích nhất tháng ${summary.month}/${summary.year}`,
            value: summary.topDoctorName,
            meta: summary.topDoctorDoneCount > 0 ? `${summary.topDoctorDoneCount} lịch đã khám` : "",
            to: "/admin/users",
            valueClassName: "text-xl md:text-2xl",
        },
        {
            id: 3,
            icon: <FiCalendar />,
            label: `Dịch vụ dùng nhiều nhất tháng ${summary.month}/${summary.year}`,
            value: summary.topServiceName,
            meta: summary.topServiceDoneCount > 0 ? `${summary.topServiceDoneCount} lượt sử dụng` : "",
            to: "/admin/services",
            valueClassName: "text-xl md:text-2xl",
        },
        {
            id: 4,
            icon: <FiBarChart2 />,
            label: `Doanh thu tháng ${summary.month}/${summary.year}`,
            value: summary.monthRevenue.toLocaleString("vi-VN") + " ₫",
            to: "/admin/statistics"
        },
    ];

    return (
        <div className="min-h-screen bg-[var(--surface)] text-slate-800">
            {/* HEADER */}
            <div className="mx-auto max-w-7xl px-4 pt-6 sm:px-6 sm:pt-8">
                <h1 className="inline-flex items-center rounded-2xl border border-slate-200 bg-white px-4 py-3 text-2xl font-bold text-[#00278D] shadow-[0_10px_26px_rgba(15,23,42,0.08)] sm:px-5 sm:text-3xl">
                    Xin chào! Quản trị viên A<sup className="text-yellow-500">*</sup><span className="text-sky-500">Care</span>
                </h1>
            </div>

            {/* MAIN */}
            <main className="mx-auto max-w-7xl space-y-6 px-4 py-6 sm:space-y-8 sm:px-6 sm:py-8">
                {/* STATS */}
                <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
                    {stats.map((s) => (
                        <NavLink key={s.id} to={s.to} className="block h-full">
                            <div className="h-full min-h-[190px] cursor-pointer rounded-3xl border border-slate-200/90 bg-white p-5 shadow-[0_12px_30px_rgba(15,23,42,0.08)] transition-shadow duration-200 hover:shadow-[0_20px_38px_rgba(15,23,42,0.14)] sm:min-h-[220px] sm:p-6">
                                <div className="flex h-full flex-col">
                                    <div className="h-12 w-12 flex items-center justify-center rounded-2xl bg-[var(--brand-600)] text-xl text-white shadow-md">
                                        {s.icon}
                                    </div>
                                    <div className="mt-5 flex-1">
                                        <h3 className="min-h-[42px] text-sm font-semibold leading-5 text-slate-500">
                                            {s.label}
                                        </h3>
                                        <p className={`${s.valueClassName || "text-3xl"} mt-2 break-words font-extrabold leading-tight tracking-tight text-[#00278D]`}>
                                            {s.value}
                                        </p>
                                        {s.meta ? (
                                            <p className="mt-2 text-xs text-slate-500">{s.meta}</p>
                                        ) : null}
                                    </div>
                                </div>
                            </div>
                        </NavLink>
                    ))}
                </div>

                {/* CONTENT */}
                <section className="rounded-3xl border border-slate-200/80 bg-white/95 p-5 shadow-[0_14px_34px_rgba(15,23,42,0.10)] sm:p-8">
                    <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
                        <div>
                            <h2 className="text-2xl font-bold text-[#00278D]">Trang tổng quan đã được tối giản</h2>
                            <p className="text-slate-600 mt-2 max-w-2xl">
                                Biểu đồ và log đã được tách khỏi dashboard để giảm nhiễu khi theo dõi nhanh.
                                Bạn có thể xem thống kê chi tiết theo tháng, theo quý, theo dịch vụ và theo bác sĩ ở trang thống kê admin.
                            </p>
                        </div>
                        <NavLink
                            to="/admin/statistics"
                            className="inline-flex w-full items-center justify-center gap-2 rounded-xl border border-[#00278D]/20 bg-[var(--brand-600)] px-5 py-3 text-sm font-semibold text-white shadow-lg shadow-[#00278D]/25 transition-colors duration-200 hover:bg-[var(--brand-700)] sm:w-fit"
                        >
                            Mở trang thống kê admin
                            <FiArrowUpRight className="text-base" />
                        </NavLink>
                    </div>
                </section>
            </main>
        </div>
    );
}

export default Dashboard;

