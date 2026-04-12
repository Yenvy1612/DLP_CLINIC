import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FiCalendar, FiClock, FiCreditCard, FiMapPin, FiPhone, FiUser } from "react-icons/fi";
import { appointmentService } from "../../api";
import ActionModal from "../../components/ActionModal";

const STORAGE_KEY = "pendingBookingPayment";

const formatCurrency = (amount) => {
    return `${Number(amount || 0).toLocaleString("vi-VN")} VND`;
};

const formatDate = (isoDate) => {
    if (!isoDate || typeof isoDate !== "string") return "--/--/----";
    const [year, month, day] = isoDate.split("-");
    return `${day}/${month}/${year}`;
};

export default function Payment() {
    const navigate = useNavigate();
    const location = useLocation();
    const bookingDraftFromState = location.state?.bookingDraft;

    const [draft, setDraft] = useState(() => {
        if (bookingDraftFromState) {
            sessionStorage.setItem(STORAGE_KEY, JSON.stringify(bookingDraftFromState));
            return bookingDraftFromState;
        }

        const cached = sessionStorage.getItem(STORAGE_KEY);
        if (!cached) return null;

        try {
            return JSON.parse(cached);
        } catch {
            sessionStorage.removeItem(STORAGE_KEY);
            return null;
        }
    });

    const [paying, setPaying] = useState(false);
    const [openCancelModal, setOpenCancelModal] = useState(false);
    const [resultModal, setResultModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
    });
    const [paymentSucceeded, setPaymentSucceeded] = useState(false);

    useEffect(() => {
        if (!bookingDraftFromState) return;
        setDraft(bookingDraftFromState);
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(bookingDraftFromState));
    }, [bookingDraftFromState]);

    useEffect(() => {
        if (draft) return;
        navigate("/patient/book", {
            replace: true,
            state: {
                bookingResult: {
                    type: "info",
                    message: "Không tìm thấy hóa đơn cần thanh toán. Vui lòng đặt lịch lại.",
                },
            },
        });
    }, [draft, navigate]);

    const summary = useMemo(() => draft?.summary || {}, [draft]);

    const handleConfirmPayment = async () => {
        if (!draft?.payload) return;

        setPaying(true);
        try {
            const response = await appointmentService.bookForPatient(draft.payload);
            const appointmentData = response?.data || response;

            sessionStorage.removeItem(STORAGE_KEY);
            setPaymentSucceeded(true);
            setResultModal({
                isOpen: true,
                tone: "success",
                title: "Thanh toán thành công",
                message: `Mã lịch hẹn: ${appointmentData?.appointmentCode || "Mới"}\nLịch hẹn đã được đưa vào hàng chờ khám.`,
            });
        } catch (error) {
            setPaymentSucceeded(false);
            setResultModal({
                isOpen: true,
                tone: "warning",
                title: "Thanh toán thất bại",
                message: error?.message || "Không thể hoàn tất thanh toán. Vui lòng thử lại.",
            });
        } finally {
            setPaying(false);
        }
    };

    const handleCancelPayment = () => {
        sessionStorage.removeItem(STORAGE_KEY);
        setOpenCancelModal(false);
        navigate("/patient/book", {
            replace: true,
            state: {
                bookingResult: {
                    type: "info",
                    message: "Bạn đã hủy thanh toán. Lịch hẹn chưa được lưu.",
                },
            },
        });
    };

    const handleCloseResultModal = () => {
        setResultModal((prev) => ({ ...prev, isOpen: false }));

        if (!paymentSucceeded) return;

        navigate("/patient/history", {
            replace: true,
            state: {
                bookingResult: {
                    type: "success",
                    message: "Lịch hẹn đã được lưu và đang ở hàng chờ khám.",
                },
            },
        });
    };

    if (!draft) return null;

    return (
        <div className="min-h-screen bg-slate-100 py-10 px-4">
            <div className="max-w-3xl mx-auto space-y-6">
                <section className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
                    <h1 className="text-2xl md:text-3xl font-extrabold text-[#00278D]">Thanh toán đặt khám</h1>
                    <p className="text-slate-600 mt-2">Vui lòng kiểm tra thông tin hóa đơn trước khi xác nhận thanh toán.</p>
                    <div className="h-1 w-28 bg-[#00278D] rounded-full mt-4"></div>
                </section>

                <section className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
                    <h2 className="text-lg font-bold text-slate-900 mb-4">Chi tiết hóa đơn</h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiUser /> Bệnh nhân</p>
                            <p className="font-semibold text-slate-800">{summary.patientName || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiPhone /> Số điện thoại bệnh nhân</p>
                            <p className="font-semibold text-slate-800">{summary.patientPhone || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiUser /> Bác sĩ</p>
                            <p className="font-semibold text-slate-800">{summary.doctorName || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiPhone /> Số điện thoại bác sĩ</p>
                            <p className="font-semibold text-slate-800">{summary.doctorPhone || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiCreditCard /> Dịch vụ</p>
                            <p className="font-semibold text-slate-800">{summary.serviceName || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiCalendar /> Ngày khám</p>
                            <p className="font-semibold text-slate-800">{formatDate(summary.date)}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiClock /> Giờ khám</p>
                            <p className="font-semibold text-slate-800">{summary.time || "--:--"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4 md:col-span-2">
                            <p className="text-slate-500 mb-1 flex items-center gap-2"><FiMapPin /> Địa chỉ phòng khám</p>
                            <p className="font-semibold text-slate-800">{summary.clinicLocation || "Đang cập nhật"}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-white p-4 md:col-span-2">
                            <p className="text-slate-500 mb-1">Lý do khám</p>
                            <p className="font-medium text-slate-700">{summary.reason?.trim() || "Không có ghi chú"}</p>
                        </div>
                    </div>

                    <div className="mt-5 border-t border-slate-200 pt-4 flex items-center justify-between">
                        <span className="text-slate-600 font-medium">Tổng thanh toán</span>
                        <span className="text-2xl font-extrabold text-[#001f5f]">{formatCurrency(summary.price)}</span>
                    </div>

                    <div className="mt-6 flex flex-col sm:flex-row gap-3 justify-end">
                        <button
                            type="button"
                            onClick={() => setOpenCancelModal(true)}
                            disabled={paying}
                            className="px-5 py-2.5 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-100 transition disabled:opacity-60 cursor-pointer"
                        >
                            Không thanh toán
                        </button>

                        <button
                            type="button"
                            onClick={handleConfirmPayment}
                            disabled={paying}
                            className="px-5 py-2.5 rounded-lg bg-[#00278D] hover:bg-[#001f5f] text-white transition disabled:opacity-60 cursor-pointer"
                        >
                            {paying ? "Đang xử lý thanh toán..." : "Đồng ý thanh toán"}
                        </button>
                    </div>
                </section>
            </div>

            <ActionModal
                isOpen={openCancelModal}
                title="Xác nhận hủy thanh toán"
                message="Nếu hủy lúc này, lịch hẹn sẽ không được lưu vào hệ thống."
                tone="warning"
                confirmText="Hủy thanh toán"
                cancelText="Quay lại"
                showCancel
                onConfirm={handleCancelPayment}
                onClose={() => setOpenCancelModal(false)}
            />

            <ActionModal
                isOpen={resultModal.isOpen}
                title={resultModal.title}
                message={resultModal.message}
                tone={resultModal.tone}
                confirmText={paymentSucceeded ? "Xem lịch khám" : "Đã hiểu"}
                onConfirm={handleCloseResultModal}
                onClose={handleCloseResultModal}
            />
        </div>
    );
}