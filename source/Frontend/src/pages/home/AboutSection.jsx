import { motion } from "framer-motion";
import doctorImg1 from "../../assets/images/doctor/doctor.png";

const fadeLeft = {
    hidden: { opacity: 0, x: -60 },
    show: { opacity: 1, x: 0, transition: { duration: 0.8, ease: "easeOut" } }
};
const fadeRight = {
    hidden: { opacity: 0, x: 60 },
    show: { opacity: 1, x: 0, transition: { duration: 0.8, ease: "easeOut" } }
};
const fadeUp = (d = 0) => ({
    hidden: { opacity: 0, y: 24 },
    show: { opacity: 1, y: 0, transition: { duration: 0.7, delay: d, ease: "easeOut" } }
});

function AboutSection() {
    
    return (
        <section className="px-6 md:px-12 lg:px-20 py-16 bg-[#F6FCFF] rounded-t-[30%]">
            <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-12 items-center bg-white shadow-xl shadow-slate-200 p-10 rounded-3xl">
                {/* LEFT */}
                <motion.div
                    className="relative flex justify-center overflow-visible"
                    initial="hidden"
                    whileInView="show"
                    viewport={{ once: true, amount: 0.3 }}
                    variants={fadeLeft}
                >
                    {/* Nền oval */}
                    <motion.div
                        className="absolute inset-0 -z-10 flex items-center justify-center"
                        variants={fadeUp(0.1)}
                    >
                        <div className="w-[420px] h-[520px] bg-sky-50 rounded-[240px]" />
                    </motion.div>

                    {/* Chấm bi bên phải */}
                    <motion.div
                        className="hidden md:block absolute right-0 top-1/2 -translate-y-1/2 -z-10"
                        variants={fadeUp(0.2)}
                    >
                        <div className="grid grid-cols-6 gap-3 opacity-40">
                            {Array.from({ length: 36 }).map((_, i) => (
                                <span key={i} className="w-2 h-2 rounded-full bg-sky-200 inline-block" />
                            ))}
                        </div>
                    </motion.div>

                    {/* Ảnh tròn (tràn ra ngoài nhẹ) */}
                    <motion.img
                        src={doctorImg1}
                        alt="Bác sĩ A*Care Clinic"
                        className="w-[420px] h-[420px] bg-gray-100 rounded-full object-cover shadow-2xl border-8 border-white relative right-[-48px] top-[24px]"
                        variants={fadeLeft}
                        transition={{ duration: 0.9 }}
                        whileHover={{ scale: 1.03 }}
                    />

                    {/* Huy hiệu “Years of Experiences” */}
                    <motion.div
                        className="absolute -bottom-6 left-8 rotate-[-8deg] z-20"
                        variants={fadeUp(0.3)}
                    >
                        <div className="bg- text-white px-6 py-4 rounded-2xl shadow-2xl border-4 border-white">
                            <div className="flex items-center gap-3">
                                <svg width="28" height="28" viewBox="0 0 24 24" className="fill-white">
                                    <path d="M12 2c3.9 0 7 2.7 7 6.5 0 1.7-.6 3.4-1.6 4.7-.6.8-1.4 1.1-2.1.8-.6-.2-1.1-.9-1.3-1.6-.3-1-.5-1.5-1.2-1.5s-.9.5-1.2 1.5c-.2.7-.7 1.4-1.3 1.6-.8.3-1.6 0-2.1-.8C5.6 11.9 5 10.2 5 8.5 5 4.7 8.1 2 12 2z" />
                                </svg>
                                <div>
                                    <p className="text-base leading-tight font-semibold">25 Years Of</p>
                                    <p className="text-lg leading-tight font-bold">Experiences</p>
                                </div>
                            </div>
                        </div>
                    </motion.div>
                </motion.div>

                {/* RIGHT */}
                <motion.div
                    initial="hidden"
                    whileInView="show"
                    viewport={{ once: true, amount: 0.2 }}
                    variants={fadeRight}
                >
                    <motion.p className="text-sky-600 font-semibold uppercase tracking-wide mb-2" variants={fadeUp(0.0)}>
                        Về A<sup>*</sup>Care Clinic
                    </motion.p>

                    <motion.h2
                        className="text-3xl md:text-4xl font-bold text-[#0b2b6b] leading-tight mb-4"
                        variants={fadeUp(0.1)}
                    >
                        Tìm một phòng khám đa khoa <br /> đáng tin cậy cho bạn?
                    </motion.h2>

                    <motion.p className="text-slate-600 mb-6" variants={fadeUp(0.2)}>
                        A<sup>*</sup>Care Clinic mang đến dịch vụ khám ngoại trú đa khoa toàn diện: nội, ngoại, nhi,
                        tai mũi họng, da liễu, sản phụ khoa… với đội ngũ bác sĩ giàu kinh nghiệm, quy trình
                        chuẩn quốc tế và công nghệ chẩn đoán hiện đại, tập trung vào sự an tâm và hài lòng của bệnh nhân.
                    </motion.p>

                    <motion.div className="flex gap-4 items-stretch mb-6" variants={fadeUp(0.3)}>
                        <div className="flex-1 border-l-3 rounded-lg border-sky-500 pl-4">
                            <h3 className="text-xl font-semibold text-[#0b2b6b]">
                                Lựa chọn đúng đắn cho dịch vụ y tế chất lượng
                            </h3>
                            <ul className="mt-3 space-y-2 text-slate-700">
                                <li className="flex items-start gap-2">
                                    <span className="mt-1 w-5 h-5 rounded-full bg-sky-100 flex items-center justify-center">
                                        <span className="w-2 h-2 rounded-full bg-sky-600"></span>
                                    </span>
                                    Giải pháp chăm sóc theo dõi liên tục & cá nhân hoá
                                </li>
                                <li className="flex items-start gap-2">
                                    <span className="mt-1 w-5 h-5 rounded-full bg-sky-100 flex items-center justify-center">
                                        <span className="w-2 h-2 rounded-full bg-sky-600"></span>
                                    </span>
                                    Danh mục chuyên khoa toàn diện, quy trình nhanh gọn
                                </li>
                                <li className="flex items-start gap-2">
                                    <span className="mt-1 w-5 h-5 rounded-full bg-sky-100 flex items-center justify-center">
                                        <span className="w-2 h-2 rounded-full bg-sky-600"></span>
                                    </span>
                                    Chẩn đoán hình ảnh & xét nghiệm chi tiết – tin cậy
                                </li>
                            </ul>
                        </div>
                    </motion.div>

                    <motion.p className="text-slate-600 mb-6" variants={fadeUp(0.35)}>
                        Chúng tôi cam kết đồng hành dài lâu cùng sức khỏe của bạn và gia đình — nhanh chóng,
                        chính xác và tận tâm.
                    </motion.p>
                </motion.div>
            </div>
        </section>
    );
}

export default AboutSection;
