import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { userService } from "../../api/services/userService";

export default function SpecialistsSection() {
    const [doctors, setDoctors] = useState([]);

    useEffect(() => {
        // Fetch doctors and take the first 5
        userService.getDoctors().then((res) => {
            if (res && res.data) {
                setDoctors(res.data.slice(0, 5));
            }
        }).catch(err => console.error(err));
    }, []);

    if (doctors.length === 0) return null;

    return (
        <section className="bg-white py-16 px-6 md:px-14 xl:px-24">
            <div className="mx-auto max-w-7xl">
                <div className="mb-10 text-center">
                    <h2 className="text-3xl font-bold text-[var(--brand-navy)] md:text-4xl">
                        Đội ngũ chuyên gia
                    </h2>
                    <p className="mx-auto mt-4 max-w-2xl text-slate-600">
                        Các bác sĩ giỏi chuyên môn, giàu kinh nghiệm, tận tâm chăm sóc sức khỏe cho bạn và gia đình.
                    </p>
                </div>

                <div className="flex overflow-x-auto pb-6 gap-6 md:grid md:grid-cols-5 md:overflow-visible">
                    {doctors.map((doctor, idx) => (
                        <motion.div
                            key={doctor.id}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: idx * 0.1, duration: 0.5 }}
                            className="group flex flex-col items-center min-w-[200px]"
                        >
                            <div className="relative mb-4 h-32 w-32 overflow-hidden rounded-full border-4 border-slate-100 shadow-md">
                                <img
                                    src={`https://api.dicebear.com/7.x/notionists/svg?seed=${doctor.fullName}`}
                                    alt={doctor.fullName}
                                    className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105 bg-slate-50"
                                />
                            </div>
                            <h3 className="text-center text-[15px] font-bold text-slate-800 line-clamp-1">
                                {doctor.fullName}
                            </h3>
                            <p className="mt-1 text-center text-[13px] font-medium text-slate-500 uppercase tracking-wide">
                                Chuyên gia y tế
                            </p>
                        </motion.div>
                    ))}
                </div>
            </div>
        </section>
    );
}
