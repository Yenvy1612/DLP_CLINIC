import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import { serviceService } from "../../../api";
import ActionModal from "../../../components/ActionModal";
import { getUserRole, isLoggedIn } from "../../../utils/authUtils";

const container = {
    hidden: { opacity: 0, y: 20 },
    show: {
        opacity: 1,
        y: 0,
        transition: {
            duration: 0.2,
            ease: "easeOut",
            when: "beforeChildren",
            staggerChildren: 0.05,
        },
    },
};

const item = {
    hidden: { opacity: 0, y: 14 },
    show: {
        opacity: 1,
        y: 0,
        transition: { duration: 0.45, ease: "easeOut" },
    },
};

function AdminShowService() {
    const navigate = useNavigate();
    const { id } = useParams();

    const role = getUserRole();

    const [service, setService] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [actionModal, setActionModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
        confirmText: "Đóng",
        cancelText: "Hủy",
        showCancel: false,
        onConfirm: null,
    });

    useEffect(() => {
        const fetchService = async () => {
            try {
                const data = await serviceService.getById(id);
                setService(data);
            } catch (err) {
                setError(err.message || "Không thể tải thông tin dịch vụ");
            } finally {
                setLoading(false);
            }
        };
        fetchService();
    }, [id]);

    const closeActionModal = () => {
        setActionModal((prev) => ({ ...prev, isOpen: false, onConfirm: null }));
    };

    const handleBookService = () => {
        if (!service?.id) return;

        const currentRole = getUserRole();

        if (!isLoggedIn()) {
            setActionModal({
                isOpen: true,
                title: "Cần đăng nhập",
                message: "Bạn cần đăng nhập tài khoản bệnh nhân trước khi đặt lịch hẹn.",
                tone: "warning",
                confirmText: "Đăng nhập",
                cancelText: "Để sau",
                showCancel: true,
                onConfirm: () => navigate("/login"),
            });
            return;
        }

        if (currentRole !== "PATIENT") {
            setActionModal({
                isOpen: true,
                title: "Không thể đặt lịch",
                message: "Chỉ tài khoản bệnh nhân mới có thể đặt lịch hẹn từ trang dịch vụ.",
                tone: "warning",
                confirmText: "Đã hiểu",
                cancelText: "Hủy",
                showCancel: false,
                onConfirm: null,
            });
            return;
        }

        navigate("/patient/book", {
            state: { preselectedServiceId: String(service.id) },
        });
    };

    if (loading)
        return (
            <div className="h-screen flex items-center justify-center text-slate-600">
                Đang tải thông tin dịch vụ...
            </div>
        );

    if (error)
        return (
            <div className="h-screen flex items-center justify-center text-red-500">
                {error}
            </div>
        );

    if (!service)
        return (
            <div className="h-screen flex items-center justify-center text-slate-500">
                Không tìm thấy dịch vụ.
            </div>
        );

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.4, ease: "easeOut" }}
            className="min-h-screen bg-[var(--surface)] px-6 py-10"
        >
            <motion.div
                variants={container}
                initial="hidden"
                animate="show"
                className="max-w-5xl mx-auto"
            >
                <motion.h1 className="text-[#00278D] text-3xl font-bold mb-10 p-2 bg-white rounded-xl w-1/3 text-center shadow-xl" variants={item}>Chi tiết dịch vụ</motion.h1>
                <motion.div
                    variants={item}
                    className="rounded-3xl bg-white border border-slate-100 shadow-xl p-8"
                >
                    <motion.h1
                        variants={item}
                        className="text-3xl font-bold text-[#00278D] mb-2"
                    >
                        {service.name}
                    </motion.h1>

                    <motion.div
                        variants={item}
                        className="inline-flex items-center gap-2 px-4 py-2 bg-sky-50 rounded-2xl border border-sky-100"
                    >
                        <span className="text-sm text-slate-500">Giá dịch vụ</span>
                        <span className="text-2xl font-semibold text-sky-600">
                            {service.price?.toLocaleString("vi-VN")} ₫
                        </span>
                    </motion.div>

                    <motion.div
                        variants={item}
                        className="pt-4 border-t border-slate-100 space-y-3 text-[15px] leading-relaxed text-slate-800"
                    >
                        <h2 className="text-xl font-semibold text-[#00278D]">
                            Thông tin dịch vụ
                        </h2>
                        <p>{service.description || "Dịch vụ chưa có mô tả chi tiết."}</p>
                    </motion.div>

                    <motion.div
                        variants={item}
                        className="flex justify-end gap-2 pt-4 border-t border-slate-100"
                    >
                        <button
                            onClick={handleBookService}
                            className="px-5 py-2.5 rounded-xl bg-[#001f5f] text-white text-sm font-medium hover:bg-[#001647] transition"
                        >
                            Đặt lịch hẹn
                        </button>
                        <button
                            onClick={() => navigate(role == "ADMIN" ? "/admin/services" : "/services")}
                            className="px-5 py-2.5 rounded-xl bg-[#00278D] text-white text-sm font-medium hover:bg-sky-500 transition"
                        >
                            Quay lại
                        </button>
                    </motion.div>
                </motion.div>
            </motion.div>

            <ActionModal
                isOpen={actionModal.isOpen}
                title={actionModal.title}
                message={actionModal.message}
                tone={actionModal.tone}
                confirmText={actionModal.confirmText}
                cancelText={actionModal.cancelText}
                showCancel={actionModal.showCancel}
                onClose={closeActionModal}
                onConfirm={actionModal.onConfirm || closeActionModal}
            />
        </motion.div>
    );
}

export default AdminShowService;
