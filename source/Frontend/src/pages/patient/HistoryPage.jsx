import { useEffect, useRef, useState } from "react";
import { getUserId } from "../../utils/authUtils";
import AppointmentCard from "./AppointmentCard";
import { appointmentService } from "../../api";
import { animatePageEnter } from "../../utils/animeAnimations";

function HistoryPage() {
    const id = getUserId();
    const pageRef = useRef(null);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    useEffect(() => {
        if (!id) {
            setLoading(false);
            return;
        }

        const getDoneAppointments = async () => {
            try {
                const DoneAppointments = await appointmentService.notPendingByPatientId(id);
                setData(DoneAppointments);
            }
            catch (err) {
                console.log(err.message);
            }
            finally {
                setLoading(false);
            }
        };
        getDoneAppointments();

    }, [id]);

    if (loading) return <p className="text-center text-gray-500 py-10">Đang tải...</p>;

    return (
        <div ref={pageRef} className="bg-[var(--surface)] min-h-[40vh]">
            <div className="max-w-7xl mx-auto py-8 px-4">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between mb-8">
                    <div className="bg-white w-fit h-fit p-3 rounded-2xl shadow-lg">
                        <h1 className="text-4xl font-bold text-[#00278D]">Lịch sử dịch vụ</h1>
                    </div>
                </div>
                {data.length > 0 ? (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {data.map((a, i) => (
                            <div key={a.id} className="will-change-transform transform-gpu">
                                <div className="transition-shadow duration-200 hover:shadow-xl rounded-xl">
                                    <AppointmentCard appointment={a} />
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="text-xl text-[#00278D]">
                        {"Không có cuộc hẹn nào hoàn thành."}
                    </p>
                )}
            </div>
        </div>
    );
}

export default HistoryPage;
