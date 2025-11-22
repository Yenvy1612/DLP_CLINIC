import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { getUserRole } from "../../utils/authUtils";
import team from "../../assets/images/doctor/teams.jpg";

export default function HeroSection() {

    const navigate = useNavigate();
    const role = getUserRole() || "GUEST";
    return (
        <section className="flex flex-col md:flex-row items-center justify-between px-10 md:px-20 py-16 bg-sky-500 overflow-hidden">
            {/* Bên trái: nội dung */}
            <motion.div
                className="flex-1 text-left space-y-6"
                initial={{ opacity: 0, x: -80 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 1, ease: "easeOut" }}
            >
                <p className="text-white font-semibold uppercase tracking-wide">
                    A*Care Clinic – Chăm sóc sức khỏe toàn diện cho bạn
                </p>

                <motion.h1
                    className="text-5xl md:text-6xl font-bold text-white leading-tight"
                    initial={{ opacity: 0, y: 30 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.3, duration: 0.8 }}
                >
                    Khám chữa bệnh <br />
                    <span className="text-cyan-100">Chuyên nghiệp & Tận tâm</span>
                </motion.h1>

                <motion.div
                    className="flex flex-wrap items-center gap-6 mt-8"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.6, duration: 0.8 }}
                >
                    <div className="flex items-center space-x-3">
                        <div className="p-3 bg-sky-100 rounded-full shadow-md">
                            <img
                                src="https://cdn-icons-png.flaticon.com/512/2966/2966488.png"
                                alt="Khám tổng quát"
                                className="w-6 h-6"
                            />
                        </div>
                        <p className="text-white font-semibold">Khám tổng quát & chuyên khoa</p>
                    </div>

                    <div className="border-l border-white/30 h-8"></div>

                    <div className="flex items-center space-x-3">
                        <div className="p-3 bg-white/90 rounded-full shadow-md">
                            <img
                                src="https://cdn-icons-png.flaticon.com/512/2966/2966430.png"
                                alt="Xét nghiệm & chẩn đoán"
                                className="w-6 h-6"
                            />
                        </div>
                        <p className="text-white font-semibold">Xét nghiệm – Chẩn đoán hình ảnh</p>
                    </div>

                    <div className="border-l border-white/30 h-8"></div>

                    <div className="flex items-center space-x-3">
                        <div className="p-3 bg-white/90 rounded-full shadow-md">
                            <img
                                src="https://cdn-icons-png.flaticon.com/512/2966/2966471.png"
                                alt="Dịch vụ cấp cứu"
                                className="w-6 h-6"
                            />
                        </div>
                        <p className="text-white font-semibold">Dịch vụ cấp cứu & theo dõi sức khỏe</p>
                    </div>
                </motion.div>

                <motion.p
                    className="text-white/90 max-w-md leading-relaxed"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 1, duration: 1 }}
                >
                    <span className="font-semibold text-white">A*Care Clinic</span> – phòng khám đa khoa
                    ứng dụng công nghệ hiện đại, đội ngũ bác sĩ giàu kinh nghiệm,
                    mang đến giải pháp chăm sóc sức khỏe **toàn diện, nhanh chóng và hiệu quả**
                    cho mọi lứa tuổi và nhu cầu.
                </motion.p>

                {role == "PATIENT" || role == "GUEST" ? <motion.div
                    className="mt-8"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 1.2, duration: 0.6 }}
                >
                    <a
                        href="/patient/book"
                        className="inline-block px-6 py-3 bg-white text-[#00278D] font-semibold rounded-lg shadow-md hover:bg-slate-200 hover:shadow-lg transition-all duration-300"
                    >
                        Đăng ký lịch khám ngay
                    </a>
                </motion.div> : ""}
            </motion.div>

            {/* Bên phải: hình ảnh */}
            <motion.div
                className="flex-1 mt-10 md:mt-0 md:ml-10"
                initial={{ opacity: 0, x: 100 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 1, ease: "easeOut" }}
            >
                <img
                    src={team}
                    alt="Đội ngũ bác sĩ A*Care Clinic"
                    className="rounded-lg border border-white border-5 rounded-br-[20%] shadow-2xl w-full object-cover"
                />
            </motion.div>
        </section>
    );
}
