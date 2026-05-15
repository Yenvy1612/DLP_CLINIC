import { useEffect, useMemo, useRef, useState } from "react";
import { motion } from "framer-motion";
import { MdPerson2 } from "react-icons/md";
import { GrFormSchedule } from "react-icons/gr";
import { FaChartSimple, FaTableCells } from "react-icons/fa6";
import {
    ResponsiveContainer,
    LineChart,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    Line,
} from "recharts";

import { appointmentService } from "../../api";
import { APPOINTMENT_CHANGED_EVENT } from "../../api/services/appointmentService";
import CustomDropdown from "../../components/CustomDropdown";

const PERIOD_DAY = "DAY";
const PERIOD_MONTH = "MONTH";
const PERIOD_QUARTER = "QUARTER";
const HISTORY_PAGE_SIZE = 5;
const TABLE_PAGE_SIZE = 10;

const toInputDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
};

const formatDateLabel = (rawDate) => {
    if (!rawDate) {
        return "-";
    }

    const [year, month, day] = String(rawDate).split("-");
    if (!year || !month || !day) {
        return rawDate;
    }

    return `${day}/${month}/${year}`;
};

const formatDateTimeLabel = (rawDateTime) => {
    if (!rawDateTime) {
        return "-";
    }

    const parsed = new Date(rawDateTime);
    if (Number.isNaN(parsed.getTime())) {
        return rawDateTime;
    }

    return parsed.toLocaleString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

function Statistic() {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;
    const currentQuarter = Math.floor((currentMonth - 1) / 3) + 1;

    const [periodType, setPeriodType] = useState(PERIOD_MONTH);
    const [dayPreset, setDayPreset] = useState(toInputDate(now));
    const [monthPreset, setMonthPreset] = useState(String(currentMonth));
    const [quarterPreset, setQuarterPreset] = useState(String(currentQuarter));
    const [yearPreset, setYearPreset] = useState(String(currentYear));

    const [viewMode, setViewMode] = useState("chart");
    const [tablePage, setTablePage] = useState(1);
    const [searchInput, setSearchInput] = useState("");
    const [searchKeyword, setSearchKeyword] = useState("");

    const [dashboard, setDashboard] = useState(null);
    const [loadingDashboard, setLoadingDashboard] = useState(true);
    const [refreshingDashboard, setRefreshingDashboard] = useState(false);
    const [dashboardError, setDashboardError] = useState("");
    const [dashboardRefreshKey, setDashboardRefreshKey] = useState(0);

    const [selectedPatient, setSelectedPatient] = useState(null);
    const [historyPage, setHistoryPage] = useState(0);
    const [historyData, setHistoryData] = useState(null);
    const [loadingHistory, setLoadingHistory] = useState(false);
    const [historyError, setHistoryError] = useState("");

    const dashboardRequestRef = useRef(0);

    const yearOptions = useMemo(() => {
        return [
            { value: String(currentYear - 2), label: String(currentYear - 2) },
            { value: String(currentYear - 1), label: String(currentYear - 1) },
            { value: String(currentYear), label: String(currentYear) },
            { value: String(currentYear + 1), label: String(currentYear + 1) },
        ];
    }, [currentYear]);

    const monthOptions = useMemo(
        () => Array.from({ length: 12 }, (_, index) => {
            const value = String(index + 1);
            return { value, label: `Thang ${value}` };
        }),
        []
    );

    const quarterOptions = useMemo(
        () => [1, 2, 3, 4].map((quarter) => ({ value: String(quarter), label: `Quy ${quarter}` })),
        []
    );

    const periodParams = useMemo(() => {
        if (periodType === PERIOD_DAY) {
            return {
                periodType,
                date: dayPreset,
            };
        }

        if (periodType === PERIOD_QUARTER) {
            return {
                periodType,
                quarter: Number(quarterPreset),
                year: Number(yearPreset),
            };
        }

        return {
            periodType: PERIOD_MONTH,
            month: Number(monthPreset),
            year: Number(yearPreset),
        };
    }, [dayPreset, monthPreset, periodType, quarterPreset, yearPreset]);

    const dashboardParams = useMemo(() => {
        const keyword = searchKeyword.trim();
        if (!keyword) {
            return periodParams;
        }
        return {
            ...periodParams,
            keyword,
        };
    }, [periodParams, searchKeyword]);

    useEffect(() => {
        const debounceTimer = setTimeout(() => {
            setSearchKeyword(searchInput.trim());
            setHistoryPage(0);
        }, 350);

        return () => clearTimeout(debounceTimer);
    }, [searchInput]);

    useEffect(() => {
        const handleAppointmentChanged = () => {
            setDashboardRefreshKey((prev) => prev + 1);
        };

        window.addEventListener(APPOINTMENT_CHANGED_EVENT, handleAppointmentChanged);
        return () => window.removeEventListener(APPOINTMENT_CHANGED_EVENT, handleAppointmentChanged);
    }, []);

    useEffect(() => {
        let isMounted = true;
        const requestId = ++dashboardRequestRef.current;

        const loadDashboard = async () => {
            const hasData = !!dashboard;
            if (hasData) {
                setRefreshingDashboard(true);
            } else {
                setLoadingDashboard(true);
            }
            setDashboardError("");

            try {
                const response = await appointmentService.getDoctorDashboard(dashboardParams);
                if (!isMounted || requestId !== dashboardRequestRef.current) {
                    return;
                }
                setDashboard(response);
            } catch (error) {
                if (!isMounted || requestId !== dashboardRequestRef.current) {
                    return;
                }
                if (!hasData) {
                    setDashboard(null);
                }
                setDashboardError(error.message || "Khong the tai thong ke bac si");
            } finally {
                if (!isMounted || requestId !== dashboardRequestRef.current) {
                    return;
                }
                setLoadingDashboard(false);
                setRefreshingDashboard(false);
            }
        };

        loadDashboard();
        return () => {
            isMounted = false;
        };
    }, [dashboardParams, dashboardRefreshKey]);

    useEffect(() => {
        if (!selectedPatient?.patientId) {
            return;
        }

        const rows = Array.isArray(dashboard?.patientRows) ? dashboard.patientRows : [];
        const stillExists = rows.some((row) => row.patientId === selectedPatient.patientId);
        if (!stillExists) {
            setSelectedPatient(null);
            setHistoryPage(0);
            setHistoryData(null);
            setHistoryError("");
        }
    }, [dashboard, selectedPatient]);

    useEffect(() => {
        if (!selectedPatient?.patientId) {
            setHistoryData(null);
            setHistoryError("");
            return;
        }

        const loadHistory = async () => {
            setLoadingHistory(true);
            setHistoryError("");

            try {
                const response = await appointmentService.getDoctorPatientAppointments(
                    selectedPatient.patientId,
                    {
                        ...periodParams,
                        page: historyPage,
                        size: HISTORY_PAGE_SIZE,
                    }
                );
                setHistoryData(response);
            } catch (error) {
                setHistoryData(null);
                setHistoryError(error.message || "Khong the tai lich su benh nhan");
            } finally {
                setLoadingHistory(false);
            }
        };

        loadHistory();
    }, [historyPage, periodParams, selectedPatient, dashboardRefreshKey]);

    const dailyVisits = useMemo(() => {
        return Array.isArray(dashboard?.dailyVisits) ? dashboard.dailyVisits : [];
    }, [dashboard]);

    const chartData = useMemo(
        () => dailyVisits.map((row) => ({
            date: row.date,
            dateLabel: formatDateLabel(row.date),
            doneAppointments: Number(row.doneAppointments) || 0,
        })),
        [dailyVisits]
    );

    const patientRows = useMemo(() => {
        return Array.isArray(dashboard?.patientRows) ? dashboard.patientRows : [];
    }, [dashboard]);

    const totalTablePages = useMemo(() => {
        const pages = Math.ceil(chartData.length / TABLE_PAGE_SIZE);
        return pages > 0 ? pages : 1;
    }, [chartData]);

    const paginatedChartRows = useMemo(() => {
        const start = (tablePage - 1) * TABLE_PAGE_SIZE;
        return chartData.slice(start, start + TABLE_PAGE_SIZE);
    }, [chartData, tablePage]);

    const totalPages = historyData?.totalPages || 0;
    const currentPage = (historyData?.page || 0) + 1;

    useEffect(() => {
        setTablePage(1);
    }, [periodType, dayPreset, monthPreset, quarterPreset, yearPreset, dashboard?.fromDate, dashboard?.toDate]);

    const periodLabel = useMemo(() => {
        const fromDate = formatDateLabel(dashboard?.fromDate);
        const toDate = formatDateLabel(dashboard?.toDate);
        if (fromDate === "-" || toDate === "-") {
            return "-";
        }
        return `${fromDate} - ${toDate}`;
    }, [dashboard]);

    const CustomTooltip = ({ active, payload, label }) => {
        if (!active || !payload || !payload.length) {
            return null;
        }

        return (
            <div className="rounded-xl border border-slate-200 bg-white px-4 py-3 shadow-xl">
                <p className="text-sm font-semibold text-slate-800">{label}</p>
                <p className="mt-1 text-xs text-sky-600">
                    Luot kham: <span className="font-semibold">{payload[0]?.value || 0}</span>
                </p>
            </div>
        );
    };

    return (
        <section className="min-h-screen bg-[var(--surface)] px-6 py-10 text-slate-800">
            <div className="mx-auto max-w-7xl space-y-6">
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2 }}
                    className="rounded-3xl border border-slate-100 bg-white px-6 py-5 shadow-xl"
                >
                    <h1 className="text-3xl font-bold text-[#00278D]">Thong ke bac si</h1>
                    <p className="mt-2 text-slate-600">
                        Toan bo so lieu duoc tong hop tu backend theo bo loc ngay, thang, quy.
                    </p>
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 8 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.05 }}
                    className="rounded-3xl border border-slate-100 bg-white p-6 shadow-xl"
                >
                    <div className="grid grid-cols-1 gap-6">
                        <div className="space-y-3">
                            <p className="text-sm font-semibold text-[#00278D]">Loai thong ke</p>
                            <div className="flex flex-wrap gap-4 text-sm">
                                <label className="inline-flex items-center gap-2">
                                    <input
                                        type="radio"
                                        name="period-mode"
                                        checked={periodType === PERIOD_DAY}
                                        onChange={() => {
                                            setPeriodType(PERIOD_DAY);
                                            setHistoryPage(0);
                                        }}
                                    />
                                    Theo ngay
                                </label>
                                <label className="inline-flex items-center gap-2">
                                    <input
                                        type="radio"
                                        name="period-mode"
                                        checked={periodType === PERIOD_MONTH}
                                        onChange={() => {
                                            setPeriodType(PERIOD_MONTH);
                                            setHistoryPage(0);
                                        }}
                                    />
                                    Theo thang
                                </label>
                                <label className="inline-flex items-center gap-2">
                                    <input
                                        type="radio"
                                        name="period-mode"
                                        checked={periodType === PERIOD_QUARTER}
                                        onChange={() => {
                                            setPeriodType(PERIOD_QUARTER);
                                            setHistoryPage(0);
                                        }}
                                    />
                                    Theo quy
                                </label>
                            </div>

                            {periodType === PERIOD_DAY ? (
                                <div className="grid grid-cols-1 gap-3 sm:max-w-xs">
                                    <label className="flex flex-col gap-1 text-sm text-slate-600">
                                        Chon ngay
                                        <input
                                            type="date"
                                            value={dayPreset}
                                            onChange={(event) => {
                                                setDayPreset(event.target.value);
                                                setHistoryPage(0);
                                            }}
                                            className="rounded-lg border border-slate-300 px-3 py-2"
                                        />
                                    </label>
                                </div>
                            ) : periodType === PERIOD_MONTH ? (
                                <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:max-w-md">
                                    <CustomDropdown
                                        value={monthPreset}
                                        onValueChange={(value) => {
                                            setMonthPreset(value);
                                            setHistoryPage(0);
                                        }}
                                        options={monthOptions}
                                        buttonClassName="py-2.5 text-sm"
                                    />
                                    <CustomDropdown
                                        value={yearPreset}
                                        onValueChange={(value) => {
                                            setYearPreset(value);
                                            setHistoryPage(0);
                                        }}
                                        options={yearOptions}
                                        buttonClassName="py-2.5 text-sm"
                                    />
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:max-w-md">
                                    <CustomDropdown
                                        value={quarterPreset}
                                        onValueChange={(value) => {
                                            setQuarterPreset(value);
                                            setHistoryPage(0);
                                        }}
                                        options={quarterOptions}
                                        buttonClassName="py-2.5 text-sm"
                                    />
                                    <CustomDropdown
                                        value={yearPreset}
                                        onValueChange={(value) => {
                                            setYearPreset(value);
                                            setHistoryPage(0);
                                        }}
                                        options={yearOptions}
                                        buttonClassName="py-2.5 text-sm"
                                    />
                                </div>
                            )}
                        </div>
                    </div>
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.1 }}
                    className="grid grid-cols-1 gap-6 md:grid-cols-2"
                >
                    <Card
                        title="Benh nhan duy nhat"
                        value={loadingDashboard ? "..." : (dashboard?.uniquePatientCount ?? 0).toLocaleString("vi-VN")}
                        icon={<MdPerson2 />}
                    />
                    <Card
                        title="Lich hen da kham"
                        value={loadingDashboard ? "..." : (dashboard?.doneAppointmentCount ?? 0).toLocaleString("vi-VN")}
                        icon={<GrFormSchedule />}
                    />
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 12 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.15 }}
                    className="overflow-hidden rounded-3xl border border-slate-100 bg-white shadow-xl"
                >
                    <div className="flex flex-col gap-3 border-b border-slate-100 px-6 py-4 md:flex-row md:items-center md:justify-between">
                        <div>
                            <h2 className="text-lg font-semibold text-[#00278D]">Luu luong kham theo ngay</h2>
                            <p className="mt-1 text-sm text-slate-500">Pham vi: {periodLabel}</p>
                        </div>
                        <div className="flex gap-2">
                            <button
                                type="button"
                                onClick={() => setViewMode("chart")}
                                className={`inline-flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium ${
                                    viewMode === "chart"
                                        ? "bg-[#00278D] text-white"
                                        : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                                }`}
                            >
                                <FaChartSimple /> Bieu do duong
                            </button>
                            <button
                                type="button"
                                onClick={() => setViewMode("table")}
                                className={`inline-flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium ${
                                    viewMode === "table"
                                        ? "bg-[#00278D] text-white"
                                        : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                                }`}
                            >
                                <FaTableCells /> Bang du lieu
                            </button>
                        </div>
                    </div>

                    {loadingDashboard ? (
                        <div className="px-6 py-10 text-center text-slate-500">Dang tai thong ke...</div>
                    ) : dashboardError ? (
                        <div className="px-6 py-10 text-center text-rose-600">{dashboardError}</div>
                    ) : viewMode === "chart" ? (
                        <div className="px-4 py-6">
                            <ResponsiveContainer width="100%" height={380}>
                                <LineChart data={chartData} margin={{ top: 5, right: 20, left: 5, bottom: 10 }}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                    <XAxis dataKey="dateLabel" tick={{ fontSize: 11 }} />
                                    <YAxis
                                        allowDecimals={false}
                                        tick={{ fontSize: 11 }}
                                        label={{ value: "Luot kham", angle: -90, position: "insideLeft" }}
                                    />
                                    <Tooltip content={<CustomTooltip />} />
                                    <Line
                                        type="monotone"
                                        dataKey="doneAppointments"
                                        stroke="#0ea5e9"
                                        strokeWidth={2}
                                        dot={{ r: 2 }}
                                        activeDot={{ r: 4 }}
                                        name="Luot kham"
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    ) : (
                        <div>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm">
                                    <thead className="bg-sky-50 text-[#00278D]">
                                        <tr>
                                            <th className="px-4 py-3 text-left font-semibold">Ngay</th>
                                            <th className="px-4 py-3 text-right font-semibold">Lich da kham</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {paginatedChartRows.map((row, index) => (
                                            <tr key={row.date || index} className={index % 2 === 0 ? "bg-white" : "bg-slate-50"}>
                                                <td className="px-4 py-3 text-slate-700">{row.dateLabel}</td>
                                                <td className="px-4 py-3 text-right font-semibold text-slate-800">
                                                    {row.doneAppointments.toLocaleString("vi-VN")}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>

                            <div className="flex items-center justify-between border-t border-slate-100 px-4 py-3">
                                <button
                                    type="button"
                                    onClick={() => setTablePage((prev) => Math.max(1, prev - 1))}
                                    disabled={tablePage <= 1}
                                    className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50"
                                >
                                    Trang truoc
                                </button>
                                <span className="text-sm text-slate-600">
                                    Trang {tablePage}/{totalTablePages} ({TABLE_PAGE_SIZE} ngay/trang)
                                </span>
                                <button
                                    type="button"
                                    onClick={() => setTablePage((prev) => Math.min(totalTablePages, prev + 1))}
                                    disabled={tablePage >= totalTablePages}
                                    className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50"
                                >
                                    Trang sau
                                </button>
                            </div>
                        </div>
                    )}
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 14 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.2 }}
                    className="overflow-hidden rounded-3xl border border-slate-100 bg-white shadow-xl"
                >
                    <div className="border-b border-slate-100 px-6 py-4">
                        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                            <div>
                                <h2 className="text-lg font-semibold text-[#00278D]">Danh sach benh nhan da kham</h2>
                                <p className="mt-1 text-sm text-slate-500">Nhan vao mot dong de xem lich su chi tiet cua benh nhan.</p>
                            </div>
                            <div className="w-full md:w-80">
                                <input
                                    type="text"
                                    value={searchInput}
                                    onChange={(event) => setSearchInput(event.target.value)}
                                    placeholder="Tim benh nhan theo ten"
                                    className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
                                />
                                <p className="mt-1 text-xs text-slate-500">
                                    {refreshingDashboard ? "Dang cap nhat danh sach..." : "Loc theo ten benh nhan."}
                                </p>
                            </div>
                        </div>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead className="bg-sky-50 text-[#00278D]">
                                <tr>
                                    <th className="px-4 py-3 text-left font-semibold">STT</th>
                                    <th className="px-4 py-3 text-left font-semibold">Benh nhan</th>
                                    <th className="px-4 py-3 text-right font-semibold">Lich da kham</th>
                                    <th className="px-4 py-3 text-left font-semibold">Dich vu lien quan</th>
                                </tr>
                            </thead>
                            <tbody>
                                {patientRows.length === 0 ? (
                                    <tr>
                                        <td colSpan={4} className="px-4 py-6 text-center text-slate-500">
                                            Khong co benh nhan trong pham vi da chon.
                                        </td>
                                    </tr>
                                ) : (
                                    patientRows.map((row, index) => {
                                        const isSelected = selectedPatient?.patientId === row.patientId;
                                        return (
                                            <tr
                                                key={row.patientId}
                                                onClick={() => {
                                                    setSelectedPatient(row);
                                                    setHistoryPage(0);
                                                }}
                                                className={`cursor-pointer border-b transition hover:bg-sky-50 ${
                                                    isSelected ? "bg-sky-50" : "bg-white"
                                                }`}
                                            >
                                                <td className="px-4 py-3">{index + 1}</td>
                                                <td className="px-4 py-3 font-medium text-slate-800">{row.patientName}</td>
                                                <td className="px-4 py-3 text-right font-semibold text-slate-700">
                                                    {Number(row.doneAppointments || 0).toLocaleString("vi-VN")}
                                                </td>
                                                <td className="px-4 py-3 text-slate-600">
                                                    {Array.isArray(row.services) && row.services.length > 0
                                                        ? row.services.join(", ")
                                                        : "-"}
                                                </td>
                                            </tr>
                                        );
                                    })
                                )}
                            </tbody>
                        </table>
                    </div>
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 16 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: 0.25 }}
                    className="overflow-hidden rounded-3xl border border-slate-100 bg-white shadow-xl"
                >
                    <div className="border-b border-slate-100 px-6 py-4">
                        <h2 className="text-lg font-semibold text-[#00278D]">Lich su hen kham theo benh nhan</h2>
                        <p className="mt-1 text-sm text-slate-500">
                            {selectedPatient?.patientName
                                ? `Dang hien thi: ${selectedPatient.patientName}`
                                : "Chon benh nhan o bang phia tren de xem chi tiet."}
                        </p>
                    </div>

                    {!selectedPatient ? (
                        <div className="px-6 py-8 text-center text-slate-500">Chua chon benh nhan.</div>
                    ) : loadingHistory ? (
                        <div className="px-6 py-8 text-center text-slate-500">Dang tai lich su...</div>
                    ) : historyError ? (
                        <div className="px-6 py-8 text-center text-rose-600">{historyError}</div>
                    ) : (
                        <>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm">
                                    <thead className="bg-sky-50 text-[#00278D]">
                                        <tr>
                                            <th className="px-4 py-3 text-left font-semibold">Ma lich</th>
                                            <th className="px-4 py-3 text-left font-semibold">Ngay gio</th>
                                            <th className="px-4 py-3 text-left font-semibold">Dich vu</th>
                                            <th className="px-4 py-3 text-left font-semibold">Trang thai</th>
                                            <th className="px-4 py-3 text-left font-semibold">Ly do</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {Array.isArray(historyData?.items) && historyData.items.length > 0 ? (
                                            historyData.items.map((item) => (
                                                <tr key={item.appointmentId} className="border-b bg-white">
                                                    <td className="px-4 py-3 font-medium text-slate-800">{item.appointmentCode || "-"}</td>
                                                    <td className="px-4 py-3 text-slate-700">{formatDateTimeLabel(item.startTime)}</td>
                                                    <td className="px-4 py-3 text-slate-700">{item.serviceName || "-"}</td>
                                                    <td className="px-4 py-3 text-slate-700">{item.status || "-"}</td>
                                                    <td className="px-4 py-3 text-slate-600">{item.reason || "-"}</td>
                                                </tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan={5} className="px-4 py-6 text-center text-slate-500">
                                                    Khong co lich su trong pham vi loc hien tai.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>

                            <div className="flex items-center justify-between border-t border-slate-100 px-4 py-3">
                                <button
                                    type="button"
                                    onClick={() => setHistoryPage((prev) => Math.max(0, prev - 1))}
                                    disabled={historyPage <= 0}
                                    className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50"
                                >
                                    Trang truoc
                                </button>
                                <span className="text-sm text-slate-600">
                                    Trang {totalPages === 0 ? 0 : currentPage}/{totalPages} ({HISTORY_PAGE_SIZE} dong/trang)
                                </span>
                                <button
                                    type="button"
                                    onClick={() => setHistoryPage((prev) => prev + 1)}
                                    disabled={historyPage + 1 >= totalPages}
                                    className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50"
                                >
                                    Trang sau
                                </button>
                            </div>
                        </>
                    )}
                </motion.div>
            </div>
        </section>
    );
}

function Card({ title, value, icon }) {
    return (
        <div className="flex flex-col items-start rounded-2xl border border-slate-100 bg-white p-6 shadow-2xl transition hover:shadow-md">
            <div className="mb-3 text-4xl text-sky-500">{icon}</div>
            <h3 className="text-sm uppercase text-slate-500">{title}</h3>
            <p className="text-2xl font-bold text-[#00278D]">{value}</p>
        </div>
    );
}

export default Statistic;
