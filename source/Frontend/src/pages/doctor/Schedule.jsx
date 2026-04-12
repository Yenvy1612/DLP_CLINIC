import { useEffect, useState } from "react";
import AppointmentCard from "../patient/AppointmentCard";
import { getUserId } from "../../utils/authUtils";
import { motion } from "framer-motion";
import { appointmentService } from "../../api";

const container = {
    hidden: { opacity: 0, y: 20 },
    show: {
        opacity: 1, y: 0,
        transition: { duration: 0.2, ease: "easeOut", when: "beforeChildren", staggerChildren: 0.06 }
    }
};
const item = {
    hidden: { opacity: 0, y: 10 },
    show: { opacity: 1, y: 0, transition: { duration: 0.2, ease: "easeOut" } }
};

function Schedule() {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const id = getUserId();

    useEffect(() => {
        if (!id) {
            setLoading(false);
            return;
        }

        const fetchData = async () => {
            try {
                const response = await appointmentService.pendingByDoctorId(id);
                setData(response);
            }
            catch (error) {
                console.error(error.message);
            }
            finally {
                setLoading(false);
            }
        };
        fetchData();

    }, [id]);

    if (loading) {
        return <p className="py-10 text-center text-slate-500">Dang tai lich hen...</p>;
    }

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.4, ease: "easeOut" }}
            className="bg-[var(--surface)] min-h-[40vh]"
        >
            <div className="c py-8 px-4 max-w-7xl mx-auto">
                <motion.div
                    initial={{ y: -8, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    transition={{ type: "spring", stiffness: 180, damping: 22 }}
                    className="flex items-center mb-8"
                >
                    <h1 className="page-title-chip text-3xl font-bold">
                        Lịch hẹn sắp tới
                    </h1>
                </motion.div>

                {data.length > 0 ? (
                    <motion.div
                        variants={container}
                        initial="hidden"
                        animate="show"
                        className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 "
                    >
                        {data.map((a) => (
                            <motion.div
                                key={a.id}
                                variants={item}
                                whileHover={{ translateY: -4 }}
                                transition={{ type: "spring", damping: 24 }}
                                className="will-change-transform transform-gpu"
                            >
                                <div className="transition-shadow duration-200 hover:shadow-xl rounded-xl">
                                    <AppointmentCard appointment={a} />
                                </div>
                            </motion.div>
                        ))}
                    </motion.div>
                ) : (
                    <motion.p
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        transition={{ duration: 0.4 }}
                        className="text-xl text-[var(--brand-navy)]"
                    >
                        {"Không có cuộc hẹn nào đang chờ."}
                    </motion.p>
                )}
            </div>
        </motion.div>
    );
}

export default Schedule;
