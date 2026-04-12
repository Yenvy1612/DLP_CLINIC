import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUserRole } from "../../utils/authUtils";
import { appointmentService, serviceService, userService } from "../../api";
import ActionModal from "../../components/ActionModal";

function AppointmentCard({ appointment }) {
    const { id, patientId, doctorId, serviceId: appointmentServiceId, startTime, status, note, createdAt } = appointment;
    const [doctorInfo, setDoctorInfo] = useState({});
    const [patientInfo, setPatientInfo] = useState({});
    const [serviceInfo, setServiceInfo] = useState({});
    const [currentStatus, setCurrentStatus] = useState(status);
    const [isActing, setIsActing] = useState(false);
    const [confirmModal, setConfirmModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        confirmText: "Đồng ý",
        action: null,
    });
    const [noticeModal, setNoticeModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
    });

    const serviceId = Number.parseInt(appointmentServiceId ?? note, 10);
    const role = getUserRole();
    const navigate = useNavigate();

    const convertTimeFormat = (value) => String(value || "").replace("T", "\n");
    const displayStartTime = convertTimeFormat(startTime);
    const displayCreatedAt = convertTimeFormat(createdAt);

    useEffect(() => {
        setCurrentStatus(status);
    }, [status]);

    const statusColors = {
        PENDING: "bg-sky-200 text-sky-700 border-sky-300",
        DONE: "bg-green-100 text-green-700 border-green-300",
        CANCELLED: "bg-red-100 text-red-700 border-red-300"
    };

    useEffect(() => {
        let mounted = true;

        const fetchRelated = async () => {
            try {
                const requests = [
                    role === "DOCTOR" ? userService.getById(patientId) : userService.getById(doctorId),
                    Number.isNaN(serviceId) ? Promise.resolve(null) : serviceService.getById(serviceId),
                ];

                const [personData, serviceData] = await Promise.all(requests);
                if (!mounted) return;

                if (role === "DOCTOR") {
                    setPatientInfo(personData || {});
                } else {
                    setDoctorInfo(personData || {});
                }
                setServiceInfo(serviceData || {});
            } catch (error) {
                console.error(error.message);
            }
        };

        if (doctorId || patientId) {
            fetchRelated();
        }

        return () => {
            mounted = false;
        };
    }, [doctorId, patientId, role, serviceId]);

    const openConfirmModal = ({ title, message, confirmText, action }) => {
        setConfirmModal({
            isOpen: true,
            title,
            message,
            confirmText,
            action,
        });
    };

    const closeConfirmModal = () => {
        if (isActing) return;
        setConfirmModal({
            isOpen: false,
            title: "",
            message: "",
            confirmText: "Đồng ý",
            action: null,
        });
    };

    const handleConfirmAction = async () => {
        if (!confirmModal.action) return;

        setIsActing(true);
        try {
            await confirmModal.action();
            closeConfirmModal();
        }
        catch (error) {
            setNoticeModal({
                isOpen: true,
                title: "Thao tác thất bại",
                message: error?.message || "Không thể thực hiện thao tác. Vui lòng thử lại.",
                tone: "warning",
            });
        }
        finally {
            setIsActing(false);
        }
    };

    const requestMarkCancelled = () => {
        openConfirmModal({
            title: "Xác nhận hủy cuộc hẹn",
            message: "Bạn có chắc chắn muốn hủy cuộc hẹn này?",
            confirmText: "Hủy cuộc hẹn",
            action: async () => {
                await appointmentService.markCancelled(id);
                setCurrentStatus("CANCELLED");
            },
        });
    };

    const resolvedStatusColor = statusColors[currentStatus] || "bg-slate-100 text-slate-700 border-slate-300";
    const statusLabel = currentStatus === "DONE" ? "Đã khám" : "Chưa khám";

    return (
        <>
            <div className="bg-white h-[40vh] rounded-xl p-5 shadow-xl hover:shadow-md transition duration-200 flex flex-col justify-between">
                <h3 className="text-lg font-semibold text-[#00278D] mb-3">
                    Cuộc hẹn #{id}
                </h3>

                <div className="grid grid-cols-3 gap-x-4 gap-y-2 text-gray-700 flex-grow">
                    <div>
                        <p className="font-medium text-gray-500">{role === "DOCTOR" ? "Bệnh nhân:" : "Bác sĩ:"}</p>{" "}
                        <p className="font-semibold">{role === "DOCTOR" ? patientInfo.fullName : doctorInfo.fullName} </p>
                    </div>
                    <div>
                        <p className="font-medium text-gray-500">Dịch vụ:</p>{" "}
                        <p className="font-semibold">{serviceInfo.name}</p>
                    </div>
                    <div>
                        <p className="font-medium text-gray-500">Thời gian:</p>{" "}
                        <p className="font-semibold">{displayStartTime}</p>
                    </div>
                    {role === "PATIENT" ?
                        (<>
                            <div className="col-span-2">
                                <p className="font-medium text-gray-500">Ghi chú: {serviceInfo.name + "\n"}</p>
                                <p className="font-medium text-gray-500">Đơn giá: {serviceInfo.price + " vnđ"}</p>
                            </div>
                            <div className="text-sm text-gray-400">
                                <p className="font-medium text-gray-400">Tạo lúc:</p> {displayCreatedAt}
                            </div>
                        </>) : ""}
                </div>

                <div className="mt-4 flex justify-between">
                    <span
                        className={`px-3 py-2 rounded-md text-sm font-medium border ${resolvedStatusColor} w-2/3 text-center`}
                    >
                        {statusLabel}
                    </span>

                    {role === "PATIENT" ? (
                        <button
                            onClick={() => appointment && navigate(`/patient/edit-appointment/${appointment.id}`)}
                            disabled={currentStatus === "DONE"}
                            className="cursor-pointer bg-sky-500 hover:bg-sky-600 text-white w-[120px] rounded text-sm disabled:opacity-60"
                        >
                            Sửa lịch
                        </button>
                    ) : ""}

                    {role === "DOCTOR" && currentStatus === "PENDING" ? (
                        <button
                            onClick={requestMarkCancelled}
                            className="cursor-pointer hover:bg-red-800 delete-appointment bg-red-700 text-white w-[100px] rounded text-sm"
                        >
                            Hủy
                        </button>
                    ) : ""}
                </div>

                {role === "PATIENT" ? null : ""}
            </div>

            <ActionModal
                isOpen={confirmModal.isOpen}
                title={confirmModal.title}
                message={confirmModal.message}
                tone="warning"
                confirmText={confirmModal.confirmText}
                cancelText="Hủy"
                showCancel
                loading={isActing}
                onConfirm={handleConfirmAction}
                onClose={closeConfirmModal}
                closeOnBackdrop={!isActing}
            />

            <ActionModal
                isOpen={noticeModal.isOpen}
                title={noticeModal.title}
                message={noticeModal.message}
                tone={noticeModal.tone}
                confirmText="Đã hiểu"
                onConfirm={() => setNoticeModal((prev) => ({ ...prev, isOpen: false }))}
                onClose={() => setNoticeModal((prev) => ({ ...prev, isOpen: false }))}
            />
        </>
    );
}

export default AppointmentCard;