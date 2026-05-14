/**
 * DLP Dashboard — Trang quản trị DLP cho Admin.
 *
 * Hiển thị:
 * 1. Thống kê cards (tổng vi phạm, bị chặn, theo mức rủi ro)
 * 2. Biểu đồ phân bổ vi phạm theo loại (Recharts BarChart)
 * 3. Bảng danh sách violations mới nhất (phân trang)
 * 4. Nút Block/Unblock user
 *
 * Route: /admin/dlp (chỉ ADMIN mới truy cập được)
 */

import { useEffect, useState, useCallback } from "react";
import { motion } from "framer-motion";
import {
    FiShield, FiAlertTriangle, FiSlash, FiActivity,
    FiLock, FiUnlock, FiRefreshCw, FiChevronLeft, FiChevronRight
} from "react-icons/fi";
import {
    ResponsiveContainer, BarChart, CartesianGrid,
    XAxis, YAxis, Tooltip, Bar, Cell,
    PieChart, Pie, Legend
} from "recharts";
import * as dlpService from "../../../api/services/dlpService";

// ==================== CONSTANTS ====================

/** Màu sắc cho từng mức rủi ro */
const RISK_COLORS = {
    LOW: "#22c55e",       // Xanh lá — ít nguy hiểm
    MEDIUM: "#f59e0b",    // Vàng — cần chú ý
    HIGH: "#ef4444",      // Đỏ — nghiêm trọng
    CRITICAL: "#7c3aed",  // Tím — rất nghiêm trọng
};

/** Màu cho biểu đồ tròn (violation type) */
const TYPE_COLORS = ["#0ea5e9", "#ef4444", "#f59e0b", "#22c55e", "#8b5cf6", "#ec4899"];

/** Label tiếng Việt cho violation type */
const TYPE_LABELS = {
    CCCD_DETECTED: "Số CCCD",
    PHONE_DETECTED: "Số điện thoại",
    EMAIL_DETECTED: "Email",
    SENSITIVE_WORD: "Từ khóa nhạy cảm",
    RATE_LIMIT: "Vượt giới hạn request",
    OFF_HOURS: "Ngoài giờ làm việc",
    VOLUME_EXCEEDED: "Vượt giới hạn download",
};

const PAGE_SIZE = 10;

function DlpDashboard() {
    // ==================== STATE ====================
    const [stats, setStats] = useState(null);
    const [logs, setLogs] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState("");
    const [blockingUserId, setBlockingUserId] = useState(null);

    // ==================== DATA LOADING ====================

    /** Load thống kê + danh sách log */
    const loadData = useCallback(async (page = 0, isRefresh = false) => {
        if (isRefresh) setRefreshing(true);
        else setLoading(true);
        setError("");

        try {
            // Gọi song song 2 API: thống kê + danh sách log
            const [statsRes, logsRes] = await Promise.all([
                dlpService.getDlpStats(),
                dlpService.getDlpLogs(page, PAGE_SIZE),
            ]);

            // Stats response: { data: { totalViolations, totalBlocked, byRiskLevel, ... } }
            setStats(statsRes?.data || statsRes);

            // Logs response: { data: { content: [...], totalPages, ... } }
            const logsData = logsRes?.data || logsRes;
            setLogs(logsData?.content || []);
            setTotalPages(logsData?.totalPages || 0);
            setCurrentPage(page);
        } catch (err) {
            console.error("DLP Dashboard load error:", err);
            setError("Không thể tải dữ liệu DLP. Vui lòng thử lại.");
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }, []);

    useEffect(() => { loadData(0); }, [loadData]);

    // ==================== HANDLERS ====================

    /** Block/Unblock user */
    const handleToggleBlock = async (userId, currentEnabled) => {
        setBlockingUserId(userId);
        try {
            if (currentEnabled !== false) {
                await dlpService.blockUser(userId);
            } else {
                await dlpService.unblockUser(userId);
            }
            // Reload data sau khi block/unblock
            await loadData(currentPage, true);
        } catch (err) {
            console.error("Block/Unblock error:", err);
        } finally {
            setBlockingUserId(null);
        }
    };

    /** Chuyển trang */
    const goToPage = (page) => {
        if (page >= 0 && page < totalPages) {
            loadData(page, true);
        }
    };

    // ==================== DERIVED DATA (cho biểu đồ) ====================

    /** Dữ liệu cho biểu đồ cột Risk Level */
    const riskChartData = stats?.byRiskLevel
        ? Object.entries(stats.byRiskLevel).map(([level, count]) => ({
            name: level,
            value: Number(count) || 0,
            color: RISK_COLORS[level] || "#94a3b8",
        }))
        : [];

    /** Dữ liệu cho biểu đồ tròn Violation Type */
    const typeChartData = stats?.byViolationType
        ? Object.entries(stats.byViolationType).map(([type, count]) => ({
            name: TYPE_LABELS[type] || type,
            value: Number(count) || 0,
        }))
        : [];

    // ==================== RENDER ====================

    if (loading) {
        return (
            <div className="min-h-screen bg-[var(--surface)] flex items-center justify-center">
                <div className="text-slate-500 text-lg">Đang tải DLP Dashboard...</div>
            </div>
        );
    }

    return (
        <section className="min-h-screen bg-[var(--surface)] px-4 py-8 text-slate-800 sm:px-6 sm:py-10">
            <div className="mx-auto max-w-7xl space-y-6">

                {/* ==================== HEADER ==================== */}
                <motion.div
                    initial={{ opacity: 0, y: -8 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2 }}
                    className="rounded-3xl border border-slate-100 bg-white px-4 py-4 shadow-xl sm:px-6 sm:py-5"
                >
                    <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                        <div>
                            <h1 className="text-2xl font-bold text-[#00278D] sm:text-3xl flex items-center gap-3">
                                <FiShield className="text-red-500" />
                                DLP Dashboard
                            </h1>
                            <p className="text-slate-600 mt-2">
                                Giám sát vi phạm bảo mật dữ liệu — Data Loss Prevention
                            </p>
                        </div>
                        <button
                            type="button"
                            onClick={() => loadData(currentPage, true)}
                            disabled={refreshing}
                            className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-[var(--brand-600)] px-4 py-2 text-sm font-semibold text-white hover:bg-[var(--brand-700)] disabled:opacity-60 sm:w-fit"
                        >
                            <FiRefreshCw className={refreshing ? "animate-spin" : ""} />
                            {refreshing ? "Đang làm mới..." : "Làm mới dữ liệu"}
                        </button>
                    </div>
                </motion.div>

                {/* ==================== ERROR ALERT ==================== */}
                {error && (
                    <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-red-700 text-sm">
                        {error}
                    </div>
                )}

                {/* ==================== STAT CARDS ==================== */}
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.05 }}
                    className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4"
                >
                    {/* Card: Tổng vi phạm */}
                    <StatCard
                        icon={<FiActivity />}
                        label="Tổng vi phạm (30 ngày)"
                        value={stats?.totalViolations ?? 0}
                        color="bg-sky-500"
                    />
                    {/* Card: Bị chặn */}
                    <StatCard
                        icon={<FiSlash />}
                        label="Request bị chặn"
                        value={stats?.totalBlocked ?? 0}
                        color="bg-red-500"
                    />
                    {/* Card: HIGH + CRITICAL */}
                    <StatCard
                        icon={<FiAlertTriangle />}
                        label="Rủi ro cao (HIGH)"
                        value={(stats?.byRiskLevel?.HIGH ?? 0)}
                        color="bg-orange-500"
                    />
                    <StatCard
                        icon={<FiShield />}
                        label="Rủi ro rất cao (CRITICAL)"
                        value={(stats?.byRiskLevel?.CRITICAL ?? 0)}
                        color="bg-purple-600"
                    />
                </motion.div>

                {/* ==================== CHARTS ==================== */}
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.1 }}
                    className="grid grid-cols-1 gap-6 lg:grid-cols-2"
                >
                    {/* Biểu đồ cột: theo mức rủi ro */}
                    <div className="rounded-3xl border border-slate-100 bg-white p-4 shadow-xl sm:p-6">
                        <h3 className="text-lg font-semibold text-[#00278D] mb-4">Phân bổ theo mức rủi ro</h3>
                        {riskChartData.length > 0 ? (
                            <ResponsiveContainer width="100%" height={280}>
                                <BarChart data={riskChartData}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                                    <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                                    <Tooltip
                                        contentStyle={{ borderRadius: "12px", border: "1px solid #e2e8f0" }}
                                        formatter={(value) => [value, "Số vi phạm"]}
                                    />
                                    <Bar dataKey="value" radius={[8, 8, 0, 0]}>
                                        {riskChartData.map((entry, i) => (
                                            <Cell key={i} fill={entry.color} />
                                        ))}
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        ) : (
                            <p className="text-slate-400 text-center py-10">Chưa có dữ liệu vi phạm</p>
                        )}
                    </div>

                    {/* Biểu đồ tròn: theo loại vi phạm */}
                    <div className="rounded-3xl border border-slate-100 bg-white p-4 shadow-xl sm:p-6">
                        <h3 className="text-lg font-semibold text-[#00278D] mb-4">Phân bổ theo loại vi phạm</h3>
                        {typeChartData.length > 0 ? (
                            <ResponsiveContainer width="100%" height={280}>
                                <PieChart>
                                    <Pie
                                        data={typeChartData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={50}
                                        outerRadius={100}
                                        paddingAngle={3}
                                        dataKey="value"
                                        label={({ name, percent }) =>
                                            `${name} (${(percent * 100).toFixed(0)}%)`
                                        }
                                    >
                                        {typeChartData.map((_, i) => (
                                            <Cell key={i} fill={TYPE_COLORS[i % TYPE_COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip
                                        contentStyle={{ borderRadius: "12px", border: "1px solid #e2e8f0" }}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                        ) : (
                            <p className="text-slate-400 text-center py-10">Chưa có dữ liệu vi phạm</p>
                        )}
                    </div>
                </motion.div>

                {/* ==================== VIOLATIONS TABLE ==================== */}
                <motion.div
                    initial={{ opacity: 0, y: 12 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.15 }}
                    className="rounded-3xl bg-white shadow-xl border border-slate-100 overflow-hidden"
                >
                    <div className="border-b border-slate-100 px-4 py-4 sm:px-6">
                        <h2 className="text-lg font-semibold text-[#00278D]">Danh sách vi phạm gần đây</h2>
                        <p className="text-sm text-slate-500 mt-1">
                            Hiển thị {logs.length} / trang {currentPage + 1} / {totalPages || 1}
                        </p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-sm" id="dlp-violations-table">
                            <thead className="bg-sky-50 text-[#00278D]">
                                <tr>
                                    <th className="px-4 py-3 text-left font-semibold">Thời gian</th>
                                    <th className="px-4 py-3 text-left font-semibold">User</th>
                                    <th className="px-4 py-3 text-left font-semibold">Hành vi</th>
                                    <th className="px-4 py-3 text-left font-semibold">Loại vi phạm</th>
                                    <th className="px-4 py-3 text-center font-semibold">Rủi ro</th>
                                    <th className="px-4 py-3 text-center font-semibold">Chặn?</th>
                                    <th className="px-4 py-3 text-left font-semibold">Endpoint</th>
                                    <th className="px-4 py-3 text-center font-semibold">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                {logs.length === 0 ? (
                                    <tr>
                                        <td colSpan={8} className="px-6 py-10 text-center text-slate-400">
                                            🎉 Không có vi phạm nào! Hệ thống đang an toàn.
                                        </td>
                                    </tr>
                                ) : (
                                    logs.map((log, index) => (
                                        <tr
                                            key={log.id}
                                            className={index % 2 === 0 ? "bg-white" : "bg-slate-50"}
                                        >
                                            <td className="px-4 py-3 text-slate-600 whitespace-nowrap">
                                                {formatDateTime(log.createdAt)}
                                            </td>
                                            <td className="px-4 py-3 text-slate-800 font-medium">
                                                {log.username || `User #${log.userId}` || "N/A"}
                                            </td>
                                            <td className="px-4 py-3 text-slate-600">{log.action}</td>
                                            <td className="px-4 py-3">
                                                <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-700">
                                                    {TYPE_LABELS[log.violationType] || log.violationType}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-center">
                                                <RiskBadge level={log.riskLevel} />
                                            </td>
                                            <td className="px-4 py-3 text-center">
                                                {log.blocked ? (
                                                    <span className="text-red-500 font-semibold">✗ Chặn</span>
                                                ) : (
                                                    <span className="text-green-500">✓ Cho qua</span>
                                                )}
                                            </td>
                                            <td className="px-4 py-3 text-slate-500 text-xs font-mono max-w-[200px] truncate">
                                                {log.httpMethod} {log.endpoint}
                                            </td>
                                            <td className="px-4 py-3 text-center">
                                                {log.userId && (
                                                    <button
                                                        type="button"
                                                        onClick={() => handleToggleBlock(log.userId, true)}
                                                        disabled={blockingUserId === log.userId}
                                                        className="inline-flex items-center gap-1 rounded-lg bg-red-50 border border-red-200 px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-100 disabled:opacity-50"
                                                        title="Khóa tài khoản user này"
                                                    >
                                                        <FiLock className="text-xs" />
                                                        Block
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="flex flex-col items-center justify-between gap-3 border-t border-slate-100 px-4 py-3 sm:flex-row">
                            <button
                                type="button"
                                onClick={() => goToPage(currentPage - 1)}
                                disabled={currentPage === 0}
                                className="w-full inline-flex items-center justify-center gap-1 rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50 sm:w-auto"
                            >
                                <FiChevronLeft /> Trang trước
                            </button>
                            <span className="text-sm text-slate-600">
                                Trang {currentPage + 1} / {totalPages}
                            </span>
                            <button
                                type="button"
                                onClick={() => goToPage(currentPage + 1)}
                                disabled={currentPage >= totalPages - 1}
                                className="w-full inline-flex items-center justify-center gap-1 rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50 sm:w-auto"
                            >
                                Trang sau <FiChevronRight />
                            </button>
                        </div>
                    )}
                </motion.div>
            </div>
        </section>
    );
}

// ==================== SUB-COMPONENTS ====================

/** Card thống kê (dùng ở phần đầu dashboard) */
function StatCard({ icon, label, value, color }) {
    return (
        <div className="rounded-3xl border border-slate-200/90 bg-white p-5 shadow-[0_12px_30px_rgba(15,23,42,0.08)] transition-shadow duration-200 hover:shadow-[0_20px_38px_rgba(15,23,42,0.14)]">
            <div className={`h-12 w-12 flex items-center justify-center rounded-2xl ${color} text-xl text-white shadow-md`}>
                {icon}
            </div>
            <div className="mt-4">
                <h3 className="text-sm font-semibold text-slate-500">{label}</h3>
                <p className="text-3xl mt-1 font-extrabold text-[#00278D]">{Number(value).toLocaleString("vi-VN")}</p>
            </div>
        </div>
    );
}

/** Badge mức rủi ro (hiển thị trong bảng) */
function RiskBadge({ level }) {
    const styles = {
        LOW: "bg-green-100 text-green-700",
        MEDIUM: "bg-yellow-100 text-yellow-700",
        HIGH: "bg-red-100 text-red-700",
        CRITICAL: "bg-purple-100 text-purple-700",
    };
    return (
        <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-bold ${styles[level] || "bg-slate-100 text-slate-600"}`}>
            {level}
        </span>
    );
}

// ==================== HELPERS ====================

/** Format datetime từ backend (ISO string hoặc array) về dạng dd/MM/yyyy HH:mm */
function formatDateTime(dt) {
    if (!dt) return "N/A";
    try {
        // Backend trả về ISO string hoặc mảng [year, month, day, hour, min, sec]
        const date = Array.isArray(dt)
            ? new Date(dt[0], dt[1] - 1, dt[2], dt[3] || 0, dt[4] || 0)
            : new Date(dt);
        if (isNaN(date.getTime())) return String(dt);
        return date.toLocaleString("vi-VN", {
            day: "2-digit", month: "2-digit", year: "numeric",
            hour: "2-digit", minute: "2-digit",
        });
    } catch {
        return String(dt);
    }
}

export default DlpDashboard;
