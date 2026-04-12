import { useEffect, useMemo, useRef, useState } from "react";
import { FiArrowRight, FiCalendar, FiMapPin, FiUser } from "react-icons/fi";
import { useLocation, useNavigate } from "react-router-dom";
import { appointmentService, serviceService } from "../../api";
import { getCurrentUser, isLoggedIn } from "../../utils/authUtils";
import CustomDropdown from "../../components/CustomDropdown";
import ActionModal from "../../components/ActionModal";
import { animatePageEnter } from "../../utils/animeAnimations";

export default function Booking() {
    const [services, setServices] = useState([]);
    const [selectedServiceId, setSelectedServiceId] = useState("");
    const pageRef = useRef(null);
    
    const [doctors, setDoctors] = useState([]);
    const [doctorSlots, setDoctorSlots] = useState({}); // { doctorId: slots }
    const [loadingSlotsId, setLoadingSlotsId] = useState(null);
    
    // Lưu trữ ngày được chọn cho TỪNG Bác sĩ
    const [doctorDates, setDoctorDates] = useState({}); // { doctorId: 'YYYY-MM-DD' }
    
    // State cho việc đặt lịch
    const [selectedDoctorId, setSelectedDoctorId] = useState("");
    const [selectedTime, setSelectedTime] = useState("");
    const [reason, setReason] = useState("");
    const [noticeModal, setNoticeModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
    });

    const currentUser = useMemo(() => getCurrentUser(), []);
    const patientId = currentUser?.id || "";
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    const showNoticeModal = (payload) => {
        setNoticeModal({
            isOpen: true,
            title: payload?.title || "Thông báo",
            message: payload?.message || "",
            tone: payload?.tone || "info",
        });
    };

    useEffect(() => {
        const bookingResult = location.state?.bookingResult;
        const preselectedServiceId = location.state?.preselectedServiceId;

        let shouldResetState = false;

        if (bookingResult?.message) {
            const tone = bookingResult.type === "success" ? "success" : bookingResult.type === "warning" ? "warning" : "info";
            showNoticeModal({
                title: bookingResult.type === "success" ? "Thành công" : bookingResult.type === "warning" ? "Lưu ý" : "Thông báo",
                message: bookingResult.message,
                tone,
            });
            shouldResetState = true;
        }

        if (preselectedServiceId) {
            setSelectedServiceId(String(preselectedServiceId));
            shouldResetState = true;
        }

        if (shouldResetState) {
            navigate(location.pathname, { replace: true, state: {} });
        }
    }, [location.pathname, location.state, navigate]);

    // Load active services
    useEffect(() => {
        const fetchServices = async () => {
            const data = await serviceService.getAll();
            const activeOnly = Array.isArray(data) ? data.filter(s => s?.active !== false) : [];
            setServices(activeOnly);
        };
        fetchServices();
    }, []);

    // Load doctors when a service is selected
    useEffect(() => {
        setSelectedDoctorId("");
        setSelectedTime("");
        setDoctorSlots({});
        setDoctorDates({});
        setReason("");
        
        const fetchDoctors = async () => {
            if (!selectedServiceId) {
                setDoctors([]);
                return;
            }
            try {
                const data = await appointmentService.getDoctorsByService(selectedServiceId);
                setDoctors(data || []);
                
                // Set default date for all doctors to today
                const today = new Date().toISOString().split('T')[0];
                const initialDates = {};
                (data || []).forEach(doc => {
                    initialDates[doc.id] = today;
                });
                setDoctorDates(initialDates);
                
            } catch (err) {
                console.error("Error loading doctors", err);
                setDoctors([]);
            }
        };
        fetchDoctors();
    }, [selectedServiceId]);

    // Lấy slots cho 1 bác sĩ nhất định khi đổi ngày
    const fetchSlotsForDoctor = async (doctorId, date) => {
        if (!selectedServiceId || !doctorId || !date) return;
        
        setLoadingSlotsId(doctorId);
        try {
            const data = await appointmentService.getDoctorAvailability(doctorId, selectedServiceId, date);
            setDoctorSlots(prev => ({
                ...prev,
                [doctorId]: Array.isArray(data) ? data : []
            }));
        } catch (err) {
            console.error("Error loading slots for doctor", err);
            setDoctorSlots(prev => ({...prev, [doctorId]: []}));
        } finally {
            setLoadingSlotsId(null);
        }
    };

    // Khi khởi tạo doctors, load slots cho ngày mặc định
    useEffect(() => {
        doctors.forEach(doc => {
            if (doctorDates[doc.id]) {
                fetchSlotsForDoctor(doc.id, doctorDates[doc.id]);
            }
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [doctors]); // Chỉ chạy khi danh sách bác sĩ thay đổi (load lần đầu)

    // Handle date change for a specific doctor
    const handleDateChange = (doctorId, newDate) => {
        setDoctorDates(prev => ({ ...prev, [doctorId]: newDate }));
        // Khi đổi ngày, reset lựa chọn nếu đang chọn bác sĩ này
        if (selectedDoctorId === doctorId) {
            setSelectedTime("");
        }
        fetchSlotsForDoctor(doctorId, newDate);
    };

    // Get available dates (Next 7 days)
    const getNextDays = () => {
        let dates = [];
        let cur = new Date();
        for (let i = 0; i < 7; i++) {
            const label = i === 0 ? "Hôm nay" : i === 1 ? "Ngày mai" : `Thứ ${cur.getDay() === 0 ? "CN" : cur.getDay() + 1}`;
            const dd = String(cur.getDate()).padStart(2, '0');
            const mm = String(cur.getMonth() + 1).padStart(2, '0');
            const value = cur.toISOString().split('T')[0];
            
            dates.push({ label: `${label} - ${dd}/${mm}`, value });
            cur.setDate(cur.getDate() + 1);
        }
        return dates;
    };
    const availableDates = getNextDays();

    const serviceOptions = useMemo(() => {
        return services.map((service) => ({
            value: String(service.id),
            label: `${service.name} - ${service.price?.toLocaleString("vi-VN")} VND`,
        }));
    }, [services]);

    // Xử lý click chọn khung giờ
    const handleSelectSlot = (doctorId, time) => {
        setSelectedDoctorId(doctorId);
        setSelectedTime(time);
        setReason("");
    };

    // Xử lý Đặt lịch
    const handleSubmit = (e) => {
        e.preventDefault();

        if (!isLoggedIn() || !patientId) {
            showNoticeModal({
                title: "Không thể đặt lịch",
                message: "Vui lòng đăng nhập bằng tài khoản bệnh nhân để đặt lịch.",
                tone: "warning",
            });
            return;
        }

        if (!selectedServiceId || !selectedDoctorId || !selectedTime) {
            showNoticeModal({
                title: "Thiếu thông tin",
                message: "Vui lòng chọn đầy đủ thông tin.",
                tone: "warning",
            });
            return;
        }

        const date = doctorDates[selectedDoctorId];

        const selectedDoctor = doctors.find((doc) => String(doc.id) === String(selectedDoctorId));
        const selectedService = services.find((service) => String(service.id) === String(selectedServiceId));

        if (!date || !selectedDoctor || !selectedService) {
            showNoticeModal({
                title: "Thiếu dữ liệu",
                message: "Thiếu dữ liệu hóa đơn, vui lòng chọn lại thông tin đặt khám.",
                tone: "warning",
            });
            return;
        }

        const startTime = `${date}T${selectedTime}`;
        const paymentDraft = {
            payload: {
                patientId,
                doctorId: Number(selectedDoctorId),
                startTime,
                serviceId: Number(selectedServiceId),
                reason,
            },
            summary: {
                patientName: currentUser?.fullName || "Đang cập nhật",
                patientPhone: currentUser?.phone || "Đang cập nhật",
                doctorName: selectedDoctor.fullName,
                doctorPhone: selectedDoctor.phone || "Đang cập nhật",
                clinicLocation: selectedDoctor.clinicLocation || "Đang cập nhật",
                serviceName: selectedService.name,
                price: Number(selectedService.price || 0),
                date,
                time: selectedTime.slice(0, 5),
                reason,
            },
        };

        sessionStorage.setItem("pendingBookingPayment", JSON.stringify(paymentDraft));
        navigate("/patient/payment", { state: { bookingDraft: paymentDraft } });
    };

    const closeNoticeModal = () => {
        setNoticeModal((prev) => ({ ...prev, isOpen: false }));
    };

    return (
        <div ref={pageRef} className="bg-slate-50 min-h-screen pb-16">
            <header className="bg-white shadow py-6 mb-8 text-center">
                <h1 className="text-3xl font-extrabold text-[#00278D]">Đặt lịch khám bệnh</h1>
                <p className="text-slate-500 mt-2 max-w-2xl mx-auto">
                    Chọn dịch vụ, xem hồ sơ bác sĩ và chọn giờ khám phù hợp với bạn.
                </p>
                <div className="h-1 w-32 bg-[#00278D] rounded-full mx-auto mt-4"></div>
            </header>

            <main className="max-w-5xl mx-auto px-4 space-y-6">
                {/* 1. Chọn dịch vụ */}
                <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 sticky top-0 z-10">
                    <h2 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
                        <span className="bg-[#00278D] text-white w-8 h-8 rounded-full flex items-center justify-center font-bold">1</span>
                        Chọn chuyên khoa / Dịch vụ
                    </h2>
                    <CustomDropdown
                        value={selectedServiceId}
                        onValueChange={setSelectedServiceId}
                        options={serviceOptions}
                        placeholder="-- Vui lòng chọn dịch vụ --"
                        buttonClassName="text-base border-2 border-slate-300 py-3"
                    />
                </section>

                {/* 2. Danh sách bác sĩ */}
                {selectedServiceId && (
                    <section className="space-y-6">
                        {doctors.length === 0 ? (
                            <div className="bg-white p-8 rounded-xl shadow-sm text-center text-slate-600 border border-slate-200">
                                <FiUser className="mx-auto mb-4 text-4xl text-slate-500" />
                                <p className="font-medium text-lg">Hiện chưa có bác sĩ nào cho dịch vụ này.</p>
                                <p className="text-sm mt-1">Vui lòng chọn dịch vụ khác hoặc quay lại sau.</p>
                            </div>
                        ) : (
                            doctors.map((doc) => {
                                const doctorSlotsArr = doctorSlots[doc.id] || [];
                                const isLoading = loadingSlotsId === doc.id;
                                const isSelectedDoctor = selectedDoctorId === doc.id;
                                const currentDoctorDate = doctorDates[doc.id] || '';

                                return (
                                    <div key={doc.id} className={`bg-white rounded-xl shadow-md border-2 transition-all overflow-hidden ${isSelectedDoctor && selectedTime ? 'border-[#001f5f] ring-2 ring-[#001f5f]/20' : 'border-slate-200'}`}>
                                        <div className="flex flex-col md:flex-row">
                                            
                                            {/* LEFT: Doctor Info */}
                                            <div className="md:w-5/12 p-6 border-b md:border-b-0 md:border-r border-slate-200 flex sm:flex-row flex-col gap-4 items-center sm:items-start text-center sm:text-left bg-slate-50">
                                                <div className="w-24 h-24 flex-shrink-0 bg-white text-[#00278D] rounded-full shadow-inner flex items-center justify-center border-2 border-slate-200">
                                                    <FiUser className="h-12 w-12" />
                                                </div>
                                                <div>
                                                    <div className="text-[#00278D] font-bold text-sm mb-1 uppercase tracking-wide">Bác sĩ Chuyên khoa</div>
                                                    <h3 className="text-xl font-bold text-slate-800 mb-2">{doc.fullName}</h3>
                                                    <p className="text-sm text-slate-600 line-clamp-3 mb-2">
                                                        {doc.biography || "Bác sĩ giàu kinh nghiệm, tận tâm với nghề."}
                                                    </p>
                                                    <div className="pt-2 border-t border-slate-200">
                                                        <p className="text-xs font-semibold uppercase tracking-wide text-slate-500 flex items-center gap-1">
                                                            <FiMapPin className="text-slate-500" />
                                                            Địa chỉ khám
                                                        </p>
                                                        <p className="text-sm font-semibold text-slate-700">{doc.clinicLocation || "Đang cập nhật"}</p>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* RIGHT: Booking Flow (Date -> Slots) */}
                                            <div className="md:w-7/12 p-6">
                                                
                                                {/* Date Selector */}
                                                <div className="mb-4 w-56">
                                                    <CustomDropdown
                                                        value={currentDoctorDate}
                                                        onValueChange={(value) => handleDateChange(doc.id, value)}
                                                        options={availableDates.map((date) => ({
                                                            value: date.value,
                                                            label: date.label,
                                                        }))}
                                                        buttonClassName="bg-slate-50 border-slate-300 py-2"
                                                    />
                                                </div>

                                                {/* Slots Layout */}
                                                <div className="mt-2">
                                                    <h4 className="text-sm font-bold text-slate-700 mb-3 flex items-center gap-2">
                                                        <FiCalendar className="text-base" /> LỊCH KHÁM
                                                    </h4>
                                                    
                                                    {isLoading ? (
                                                        <div className="animate-pulse flex gap-2">
                                                            <div className="h-10 w-24 bg-slate-200 rounded-md"></div>
                                                            <div className="h-10 w-24 bg-slate-200 rounded-md"></div>
                                                            <div className="h-10 w-24 bg-slate-200 rounded-md"></div>
                                                        </div>
                                                    ) : doctorSlotsArr.length === 0 ? (
                                                        <p className="text-sm text-slate-700 bg-slate-100 rounded-lg p-3 font-medium border border-slate-200 italic">
                                                            Bác sĩ đã kín lịch vào ngày này. Vui lòng chọn ngày khác.
                                                        </p>
                                                    ) : (
                                                        <>
                                                            <div className="flex flex-wrap gap-3">
                                                                {doctorSlotsArr.map((slot) => {
                                                                    const disabled = !slot.available;
                                                                    const isThisSlotSelected = isSelectedDoctor && selectedTime === slot.time;
                                                                    
                                                                    let btnClass = "w-28 py-2 text-sm font-bold rounded-lg border transition-all shadow-sm text-center ";
                                                                    if (disabled) {
                                                                        btnClass += "bg-slate-100 text-slate-400 border-slate-200 cursor-not-allowed opacity-80";
                                                                    } else if (isThisSlotSelected) {
                                                                        btnClass += "bg-[#001f5f] text-white border-[#001f5f] ring-2 ring-[#001f5f]/20 ring-offset-1 transform scale-105 shadow-md";
                                                                    } else {
                                                                        btnClass += "bg-white text-slate-800 border-slate-300 hover:border-[#001f5f] hover:text-[#001f5f] hover:bg-slate-50";
                                                                    }

                                                                    return (
                                                                        <button
                                                                            key={slot.time}
                                                                            type="button"
                                                                            disabled={disabled}
                                                                            onClick={() => handleSelectSlot(doc.id, slot.time)}
                                                                            className={btnClass}
                                                                        >
                                                                            {slot.time.slice(0, 5)}
                                                                        </button>
                                                                    );
                                                                })}
                                                            </div>
                                                            <p className="text-xs text-slate-500 mt-3 font-medium flex items-center gap-1">
                                                                <FiArrowRight /> Chọn và đặt (Giữ chỗ 0đ)
                                                            </p>
                                                        </>
                                                    )}
                                                </div>

                                                {/* Booking Confirmation Form - Chỉ hiện cho Bác sĩ ĐANG CHỌN */}
                                                {isSelectedDoctor && selectedTime ? (
                                                    <div className="overflow-hidden bg-slate-50 p-5 rounded-xl border border-slate-200 mt-6">
                                                        <h5 className="font-bold text-[#001f5f] mb-3 border-b border-slate-200 pb-2">Xác nhận thông tin</h5>
                                                        <div className="mb-4">
                                                            <label className="block text-slate-700 font-medium mb-1 text-sm">Triệu chứng / Lý do khám (Tùy chọn)</label>
                                                            <textarea
                                                                value={reason}
                                                                onChange={(e) => setReason(e.target.value)}
                                                                rows="2"
                                                                placeholder="Mô tả triệu chứng để bác sĩ chuẩn bị tốt hơn..."
                                                                className="w-full border border-slate-300 p-2 text-sm rounded-md focus:outline-none focus:border-[#001f5f] focus:ring-1 focus:ring-[#001f5f] bg-white"
                                                            />
                                                        </div>
                                                        <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
                                                            <div className="text-sm space-y-1">
                                                                Giờ chọn: <strong className="text-lg text-[#001f5f]">{selectedTime.slice(0, 5)}</strong> - {currentDoctorDate.split('-').reverse().join('/')}
                                                                <div>Phòng khám: <strong>{doc.clinicLocation || "Đang cập nhật"}</strong></div>
                                                            </div>
                                                            <button
                                                                onClick={handleSubmit}
                                                                className="w-full sm:w-auto bg-[#00278D] hover:bg-[#001f5f] text-white font-bold py-2.5 px-6 rounded-lg transition-all shadow-md active:transform active:scale-95"
                                                            >
                                                                Xác nhận Đặt khám
                                                            </button>
                                                        </div>
                                                    </div>
                                                ) : null}
                                            </div>
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </section>
                )}
            </main>

            <ActionModal
                isOpen={noticeModal.isOpen}
                title={noticeModal.title}
                message={noticeModal.message}
                tone={noticeModal.tone}
                confirmText="Đã hiểu"
                onConfirm={closeNoticeModal}
                onClose={closeNoticeModal}
            />
        </div>
    );
}