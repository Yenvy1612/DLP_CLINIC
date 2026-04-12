import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getUserId } from "../../utils/authUtils";
import { appointmentService, serviceService, userService } from "../../api";
import CustomDropdown from "../../components/CustomDropdown";
import ActionModal from "../../components/ActionModal";
import { animatePageEnter } from "../../utils/animeAnimations";


function EditAppointment() {
    const navigate = useNavigate();
    const pageRef = useRef(null);

    const [form, setForm] = useState({
        patientId: getUserId(),
        doctorId: "",
        startTime: "",
        serviceId: "",
    });

    const [doctors, setDoctors] = useState([]);
    const [services, setServices] = useState([]);
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [saving, setSaving] = useState(false);
    const [pendingPayload, setPendingPayload] = useState(null);
    const [resultModal, setResultModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
        nextAction: "none",
    });

    /* lấy tham số dộng trên url */
    const { id } = useParams();

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    useEffect(() => {
        if (!id) return;

        const fetchInitialData = async () => {
            try {
                const [appointmentData, doctorData, serviceData] = await Promise.all([
                    appointmentService.getById(id),
                    userService.getDoctors(),
                    serviceService.getAll(),
                ]);

                setForm({
                    patientId: appointmentData.patientId,
                    doctorId: appointmentData.doctorId,
                    startTime: appointmentData.startTime,
                    serviceId: appointmentData.serviceId ?? appointmentData.note ?? "",
                });

                setDoctors(doctorData || []);
                setServices(serviceData || []);
            }
            catch (error) {
                console.error(error.message);
            }
        };

        fetchInitialData();
    }, [id]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const payload = {
            ...form,
            patientId: Number(form.patientId),
            doctorId: Number(form.doctorId),
            serviceId: Number(form.serviceId),
        };

        setPendingPayload(payload);
        setConfirmOpen(true);
    };

    const handleConfirmUpdate = async () => {
        if (!pendingPayload) return;

        setSaving(true);
        try {
            const data = await appointmentService.update(id, pendingPayload);
            if (data.status == 404) throw new Error(data.message);
            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Cập nhật thành công",
                message: `Lịch hẹn #${data.id} đã được cập nhật.`,
                tone: "success",
                nextAction: "back-history",
            });
        }
        catch (err) {
            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Cập nhật thất bại",
                message: err?.message || "Không thể cập nhật lịch hẹn. Vui lòng thử lại.",
                tone: "warning",
                nextAction: "none",
            });
        }
        finally {
            setSaving(false);
            setPendingPayload(null);
        }
    };

    const closeConfirmModal = () => {
        if (saving) return;
        setConfirmOpen(false);
        setPendingPayload(null);
    };

    const closeResultModal = () => {
        const { nextAction } = resultModal;
        setResultModal((prev) => ({ ...prev, isOpen: false, nextAction: "none" }));

        if (nextAction === "back-history") {
            navigate("/patient/history");
        }
    };

    

    return (
        <div ref={pageRef} className="bg-slate-100">
            <section className="relative flex justify-center items-center min-h-screen bg-[var(--surface)]">
                <div className="absolute inset-0 bg-white/50 backdrop-blur-[2px]" />

                <form
                    onSubmit={handleSubmit}
                    className="relative bg-white/95 shadow-2xl p-8 rounded-2xl w-full max-w-lg ring-1 ring-slate-200"
                >
                    <h2 className="text-2xl font-bold mb-6 text-[#00278D]">Chỉnh sửa lịch hẹn</h2>

                    <div className="space-y-4 text-slate-600">
                        <div>
                            <label className="block mb-1">Mã bệnh nhân</label>
                            <input
                                type="text"
                                name="patientId"
                                value={form.patientId}
                                onChange={handleChange}
                                className="w-full border border-gray-300 bg-gray-200 p-2 rounded-md text-slate-600 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition"
                                disabled
                            />
                        </div>

                        <div>
                            <label className="block mb-1">Mã bác sĩ</label>
                            <CustomDropdown
                                name="doctorId"
                                value={form.doctorId}
                                onChange={handleChange}
                                options={doctors.map((doctor) => ({
                                    value: String(doctor.id),
                                    label: doctor.fullName,
                                }))}
                                placeholder="-- Chọn bác sĩ --"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1">Thời gian bắt đầu</label>
                            <input
                                type="datetime-local"
                                name="startTime"
                                value={form.startTime}
                                onChange={handleChange}
                                min={new Date().toISOString().slice(0, 16)}
                                className="w-full border border-gray-300 p-2 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1">Dịch vụ</label>
                            <CustomDropdown
                                name="serviceId"
                                value={form.serviceId}
                                onChange={handleChange}
                                options={services.map((service) => ({
                                    value: String(service.id),
                                    label: service.name,
                                }))}
                                placeholder="-- Chọn dịch vụ --"
                                required
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={saving}
                        className="mt-6 w-full bg-[#00278D] hover:bg-[#001f5f] text-white py-2 rounded-lg font-semibold shadow focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#00278D] disabled:opacity-60"
                    >
                        {saving ? "Đang xử lý..." : "Cập nhật lịch"}
                    </button>
                </form>

                <ActionModal
                    isOpen={confirmOpen}
                    title="Xác nhận cập nhật lịch hẹn"
                    message="Thông tin lịch hẹn sẽ được cập nhật ngay sau khi xác nhận."
                    tone="warning"
                    confirmText="Cập nhật"
                    cancelText="Hủy"
                    showCancel
                    loading={saving}
                    onConfirm={handleConfirmUpdate}
                    onClose={closeConfirmModal}
                    closeOnBackdrop={!saving}
                />

                <ActionModal
                    isOpen={resultModal.isOpen}
                    title={resultModal.title}
                    message={resultModal.message}
                    tone={resultModal.tone}
                    confirmText="Đã hiểu"
                    onConfirm={closeResultModal}
                    onClose={closeResultModal}
                />
            </section>
        </div>
    );
}

export default EditAppointment;