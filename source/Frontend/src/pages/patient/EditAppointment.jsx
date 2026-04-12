import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FiCalendar, FiUser } from "react-icons/fi";
import { getUserId } from "../../utils/authUtils";
import { appointmentService, serviceService, userService } from "../../api";
import CustomDropdown from "../../components/CustomDropdown";
import ActionModal from "../../components/ActionModal";
import { animatePageEnter } from "../../utils/animeAnimations";

const getNextDays = () => {
    let dates = [];
    let cur = new Date();
    for (let i = 0; i < 7; i++) {
        const label = i === 0 ? "Hôm nay" : i === 1 ? "Ngày mai" : `Thứ ${cur.getDay() === 0 ? "CN" : cur.getDay() + 1}`;
        const dd = String(cur.getDate()).padStart(2, "0");
        const mm = String(cur.getMonth() + 1).padStart(2, "0");
        const value = cur.toISOString().split("T")[0];
        dates.push({ label: `${label} - ${dd}/${mm}`, value });
        cur.setDate(cur.getDate() + 1);
    }
    return dates;
};

const parseDateAndTime = (startTime) => {
    if (!startTime) return { date: "", time: "" };
    const raw = String(startTime);
    const [date, timePart] = raw.split("T");
    const time = String(timePart || "").slice(0, 5);
    return { date, time };
};

const toNumberOrNaN = (value) => {
    const parsed = Number(value);
    return Number.isNaN(parsed) ? Number.NaN : parsed;
};

const resolveInitialServiceId = (appointmentData) => {
    const directId = toNumberOrNaN(appointmentData?.serviceId);
    if (!Number.isNaN(directId)) return directId;

    const serviceObjId = toNumberOrNaN(appointmentData?.service?.id);
    if (!Number.isNaN(serviceObjId)) return serviceObjId;

    const legacyNoteId = toNumberOrNaN(appointmentData?.note);
    if (!Number.isNaN(legacyNoteId)) return legacyNoteId;

    return Number.NaN;
};

const formatDateOptionLabel = (value) => {
    const date = new Date(`${value}T00:00:00`);
    if (Number.isNaN(date.getTime())) return value;
    const dd = String(date.getDate()).padStart(2, "0");
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    return `${dd}/${mm}`;
};

function EditAppointment() {
    const navigate = useNavigate();
    const pageRef = useRef(null);
    const { id } = useParams();

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const [patientId, setPatientId] = useState(getUserId());
    const [services, setServices] = useState([]);
    const [serviceInfo, setServiceInfo] = useState(null);
    const [selectedServiceId, setSelectedServiceId] = useState("");
    const [doctors, setDoctors] = useState([]);
    const [doctorSlots, setDoctorSlots] = useState({});
    const [loadingSlotsId, setLoadingSlotsId] = useState(null);

    const [selectedDoctorId, setSelectedDoctorId] = useState("");
    const [selectedDate, setSelectedDate] = useState("");
    const [selectedTime, setSelectedTime] = useState("");
    const [reason, setReason] = useState("");
    const [paymentMethod] = useState("COUNTER");

    const [confirmOpen, setConfirmOpen] = useState(false);
    const [resultModal, setResultModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
        nextAction: "none",
    });

    const availableDates = useMemo(() => getNextDays(), []);
    const dateOptions = useMemo(() => {
        const base = availableDates.map((date) => ({ value: date.value, label: date.label }));
        if (!selectedDate || base.some((option) => option.value === selectedDate)) {
            return base;
        }
        return [
            { value: selectedDate, label: `Ngày đã chọn - ${formatDateOptionLabel(selectedDate)}` },
            ...base,
        ];
    }, [availableDates, selectedDate]);

    const serviceOptions = useMemo(
        () =>
            services.map((service) => ({
                value: String(service.id),
                label: service.name,
            })),
        [services]
    );

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => animation?.pause?.();
    }, []);

    const fetchSlotsForDoctor = async (doctorId, date, serviceId) => {
        if (!doctorId || !date || !serviceId) return;
        setLoadingSlotsId(doctorId);
        try {
            const data = await appointmentService.getDoctorAvailability(doctorId, serviceId, date, Number(id));
            setDoctorSlots((prev) => ({
                ...prev,
                [doctorId]: Array.isArray(data) ? data : [],
            }));
        } catch {
            setDoctorSlots((prev) => ({ ...prev, [doctorId]: [] }));
        } finally {
            setLoadingSlotsId(null);
        }
    };

    const loadDoctorsByService = async (serviceId, preferredDoctorId = "") => {
        if (!serviceId) {
            setDoctors([]);
            return;
        }

        const doctorData = await appointmentService.getDoctorsByService(serviceId);
        let nextDoctors = Array.isArray(doctorData) ? doctorData : [];

        const preferredAsNumber = Number(preferredDoctorId);
        const hasPreferred =
            preferredDoctorId &&
            nextDoctors.some((doctor) => String(doctor?.id) === String(preferredDoctorId));

        if (preferredDoctorId && !hasPreferred && !Number.isNaN(preferredAsNumber)) {
            try {
                const doctorFallback = await userService.getById(preferredAsNumber);
                if (doctorFallback?.id) {
                    nextDoctors = [
                        {
                            id: doctorFallback.id,
                            fullName: doctorFallback.fullName,
                            phone: doctorFallback.phone,
                        },
                        ...nextDoctors,
                    ];
                }
            } catch {
                // Keep existing list if fallback doctor lookup fails.
            }
        }

        setDoctors(nextDoctors);
    };

    useEffect(() => {
        if (!id) return;

        const fetchInitialData = async () => {
            setLoading(true);
            try {
                const [appointmentData, allServices] = await Promise.all([
                    appointmentService.getById(id),
                    serviceService.getAll(),
                ]);

                const normalizedServices = Array.isArray(allServices)
                    ? allServices
                    : Array.isArray(allServices?.content)
                        ? allServices.content
                        : [];

                const initialServiceId = resolveInitialServiceId(appointmentData);
                const { date, time } = parseDateAndTime(appointmentData.startTime);

                const resolvedDate = date || availableDates[0]?.value || "";
                const resolvedDoctorId = String(appointmentData.doctorId || "");

                const selectedService = normalizedServices.find(
                    (service) => String(service.id) === String(initialServiceId)
                ) || null;

                setPatientId(appointmentData.patientId);
                setServices(normalizedServices);
                setSelectedServiceId(Number.isNaN(initialServiceId) ? "" : String(initialServiceId));
                setServiceInfo(selectedService);
                await loadDoctorsByService(initialServiceId, resolvedDoctorId);
                setSelectedDoctorId(resolvedDoctorId);
                setSelectedDate(resolvedDate);
                setSelectedTime(time || "");
                setReason(appointmentData.reason || "");
            } catch (e) {
                setError(e?.message || "Không tải được dữ liệu lịch hẹn");
            } finally {
                setLoading(false);
            }
        };

        fetchInitialData();
    }, [availableDates, id]);

    useEffect(() => {
        if (!selectedServiceId) {
            setServiceInfo(null);
            return;
        }

        const nextService = services.find((item) => String(item.id) === String(selectedServiceId)) || null;
        setServiceInfo(nextService);
    }, [selectedServiceId, services]);

    useEffect(() => {
        if (!selectedDoctorId || !selectedDate || !selectedServiceId) return;
        fetchSlotsForDoctor(Number(selectedDoctorId), selectedDate, Number(selectedServiceId));
    }, [selectedDate, selectedDoctorId, selectedServiceId]);

    const handleServiceChange = async (nextServiceId) => {
        const normalizedServiceId = String(nextServiceId || "");
        setSelectedServiceId(normalizedServiceId);
        setSelectedDoctorId("");
        setSelectedTime("");
        setDoctorSlots({});

        if (!normalizedServiceId) {
            setDoctors([]);
            return;
        }

        try {
            await loadDoctorsByService(Number(normalizedServiceId));
        } catch {
            setDoctors([]);
        }
    };

    const handleDoctorChange = (nextDoctorId) => {
        setSelectedDoctorId(String(nextDoctorId));
        setSelectedTime("");
    };

    const handleDateChange = (nextDate) => {
        setSelectedDate(nextDate);
        setSelectedTime("");
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        setError("");

        if (!selectedServiceId || !selectedDoctorId || !selectedDate || !selectedTime) {
            setError("Vui lòng chọn đủ bác sĩ, ngày và khung giờ.");
            return;
        }

        setConfirmOpen(true);
    };

    const handleConfirmUpdate = async () => {
        if (!selectedServiceId) return;

        setSaving(true);
        try {
            await appointmentService.update(id, {
                patientId: Number(patientId),
                doctorId: Number(selectedDoctorId),
                serviceId: Number(selectedServiceId),
                startTime: `${selectedDate}T${selectedTime}`,
                reason,
                note: reason,
                paymentMethod,
            });

            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Cập nhật thành công",
                message: "Lịch hẹn đã được cập nhật.",
                tone: "success",
                nextAction: "back-appointments",
            });
        } catch (e) {
            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Cập nhật thất bại",
                message: e?.message || "Không thể cập nhật lịch hẹn.",
                tone: "warning",
                nextAction: "none",
            });
        } finally {
            setSaving(false);
        }
    };

    const closeResultModal = () => {
        const { nextAction } = resultModal;
        setResultModal((prev) => ({ ...prev, isOpen: false, nextAction: "none" }));
        if (nextAction === "back-appointments") {
            window.location.assign("/patient/appointments");
        }
    };

    if (loading) {
        return <div className="p-8 text-center text-slate-600">Đang tải lịch hẹn...</div>;
    }

    const doctorOptions = doctors.map((doctor) => ({
        value: String(doctor.id),
        label: doctor.fullName,
    }));

    const currentSlots = doctorSlots[selectedDoctorId] || [];

    return (
        <div ref={pageRef} className="bg-slate-50 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto space-y-6">
                <section className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
                    <h1 className="text-2xl font-extrabold text-[#00278D]">Sửa lịch hẹn</h1>
                    <p className="text-slate-600 mt-2">Bạn có thể đổi bác sĩ và khung giờ khám.</p>
                </section>

                <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-5">
                    {error ? <p className="text-rose-700 text-sm">{error}</p> : null}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-2">Dịch vụ</label>
                            <CustomDropdown
                                value={selectedServiceId}
                                onValueChange={handleServiceChange}
                                options={serviceOptions}
                                placeholder="-- Chọn dịch vụ --"
                            />
                        </div>
                        <div>
                            <p className="text-xs uppercase tracking-wide text-slate-500">Đơn giá</p>
                            <p className="text-base font-semibold text-slate-800">{Number(serviceInfo?.price || 0).toLocaleString("vi-VN")} VND</p>
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-2">Chọn bác sĩ</label>
                        <CustomDropdown
                            value={selectedDoctorId}
                            onValueChange={handleDoctorChange}
                            options={doctorOptions}
                            placeholder="-- Chọn bác sĩ --"
                        />
                    </div>

                    <div className="w-64">
                        <label className="block text-sm font-medium text-slate-700 mb-2">Ngày khám</label>
                        <CustomDropdown
                            value={selectedDate}
                            onValueChange={handleDateChange}
                            options={dateOptions}
                            placeholder="-- Chọn ngày --"
                        />
                    </div>

                    <div>
                        <p className="text-sm font-bold text-slate-700 mb-3 flex items-center gap-2">
                            <FiCalendar /> Chọn khung giờ
                        </p>
                        {loadingSlotsId && selectedDoctorId ? (
                            <p className="text-sm text-slate-500">Đang tải khung giờ...</p>
                        ) : currentSlots.length === 0 ? (
                            <p className="text-sm text-slate-500">Không còn khung giờ trống cho lựa chọn hiện tại.</p>
                        ) : (
                            <div className="flex flex-wrap gap-3">
                                {currentSlots.map((slot) => {
                                    const disabled = !slot.available;
                                    const active = selectedTime === slot.time;
                                    return (
                                        <button
                                            key={slot.time}
                                            type="button"
                                            disabled={disabled}
                                            onClick={() => setSelectedTime(slot.time)}
                                            className={`w-28 py-2 rounded-lg border text-sm font-semibold ${
                                                disabled
                                                    ? "bg-slate-100 text-slate-400 border-slate-200 cursor-not-allowed"
                                                    : active
                                                        ? "bg-[#001f5f] text-white border-[#001f5f]"
                                                        : "bg-white text-slate-800 border-slate-300 hover:border-[#001f5f]"
                                            }`}
                                        >
                                            {String(slot.time).slice(0, 5)}
                                        </button>
                                    );
                                })}
                            </div>
                        )}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-2">Lý do khám (tùy chọn)</label>
                        <textarea
                            value={reason}
                            onChange={(event) => setReason(event.target.value)}
                            rows={3}
                            className="w-full border border-slate-300 p-2 text-sm rounded-md focus:outline-none focus:border-[#001f5f]"
                            placeholder="Mô tả triệu chứng để bác sĩ chuẩn bị tốt hơn..."
                        />
                    </div>

                    <div className="rounded-lg border border-slate-200 bg-slate-50 p-3">
                        <p className="text-slate-700 font-medium mb-2 text-sm">Phương thức thanh toán</p>
                        <label className="flex items-center gap-2 text-sm text-slate-800">
                            <input type="radio" checked readOnly className="h-4 w-4" />
                            Thanh toán tại quầy
                        </label>
                    </div>

                    <div className="flex items-center gap-2">
                        <button
                            type="submit"
                            disabled={saving}
                            className="px-5 py-2.5 rounded-lg bg-[#00278D] text-white hover:bg-[#001f5f] disabled:opacity-60"
                        >
                            {saving ? "Đang xử lý..." : "Lưu thay đổi"}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate("/patient/appointments")}
                            className="px-5 py-2.5 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-100"
                        >
                            Hủy
                        </button>
                    </div>
                </form>
            </div>

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
                onClose={() => !saving && setConfirmOpen(false)}
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
        </div>
    );
}

export default EditAppointment;
