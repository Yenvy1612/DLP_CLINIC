
import { FiArrowRight, FiHeart, FiActivity, FiCpu } from "react-icons/fi";
import { MdOutlineMonitorHeart, MdOutlineLocalPharmacy } from "react-icons/md";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

const clinicServices = [
    {
        icon: <MdOutlineMonitorHeart className="w-7 h-7" />,
        title: "Khám ngoại trú tổng quát",
        desc: "Tư vấn, sàng lọc toàn diện, xây dựng hồ sơ sức khỏe cá nhân.",
    },
    {
        icon: <MdOutlineMonitorHeart className="w-7 h-7" />,
        title: "Khám chuyên khoa sâu",
        desc: "Tim mạch, hô hấp, tiêu hóa, cơ xương khớp, nội tiết, thần kinh.",
    },
    {
        icon: <MdOutlineMonitorHeart className="w-7 h-7" />,
        title: "Chẩn đoán hình ảnh số",
        desc: "Siêu âm, X-quang số hóa, CT/MRI liên kết, lưu trữ PACS.",
    },
    {
        icon: <FiCpu className="w-7 h-7" />,
        title: "Ứng dụng AI hỗ trợ chẩn đoán",
        desc: "Gợi ý bất thường, đối chiếu guideline, giảm sai sót lâm sàng.",
    },
    {
        icon: <MdOutlineLocalPharmacy className="w-7 h-7" />,
        title: "Quản lý thuốc & điều trị",
        desc: "E-prescription, nhắc liều, tương tác thuốc, đồng bộ nhà thuốc.",
    },
    {
        icon: <FiHeart className="w-7 h-7" />,
        title: "Theo dõi & tái khám thông minh",
        desc: "Đặt lịch online, nhắc hẹn, theo dõi chỉ số sức khỏe từ xa.",
    },
];

function ClinicServicesSection() {
    const navigate = useNavigate();

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1,
                delayChildren: 0.2
            }
        }
    };

    const itemVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { duration: 0.5 }
        }
    };

    const cardVariants = {
        hidden: { opacity: 0, scale: 0.9, y: 20 },
        visible: {
            opacity: 1,
            scale: 1,
            y: 0,
            transition: { duration: 0.4 }
        }
    };

    return (
        <section className="w-full bg-white py-16">
            <div className="max-w-6xl mx-auto flex flex-col xl:flex-row gap-10 xl:gap-14 items-stretch">
                {/* Left content */}
                <motion.div 
                    className="flex-1"
                    initial="hidden"
                    whileInView="visible"
                    viewport={{ once: true, amount: 0.3 }}
                    variants={containerVariants}
                >
                    <motion.p 
                        className="uppercase tracking-[0.25em] text-sky-500 font-semibold text-xs mb-3"
                        variants={itemVariants}
                    >
                        providing outpatient clinic services
                    </motion.p>
                    <motion.h2 
                        className="text-4xl xl:text-5xl font-bold text-[#00278D] leading-tight mb-4"
                        variants={itemVariants}
                    >
                        Chăm Sóc Ngoại Trú Hiện Đại
                        <br />
                        Cá Nhân Hóa Cho Sức Khỏe Toàn Diện
                    </motion.h2>
                    <motion.p 
                        className="text-slate-500 max-w-xl mb-6"
                        variants={itemVariants}
                    >
                        Hệ thống phòng khám đa khoa ngoại trú ứng dụng công nghệ số và AI:
                        đặt lịch trực tuyến, hồ sơ sức khỏe điện tử, chẩn đoán hỗ trợ thông minh
                        và theo dõi dài hạn cho từng bệnh nhân.
                    </motion.p>

                    {/* Rating + button */}
                    <motion.div 
                        className="flex items-center justify-between gap-4 mb-8"
                        variants={itemVariants}
                    >
                        <div className="flex items-center gap-3">
                            <div className="flex -space-x-2">
                                <div className="w-8 h-8 rounded-full bg-sky-200 border-2 border-white" />
                                <div className="w-8 h-8 rounded-full bg-sky-300 border-2 border-white" />
                                <div className="w-8 h-8 rounded-full bg-sky-400 border-2 border-white" />
                            </div>
                            <div>
                                <div className="flex items-center gap-1 text-amber-400 text-sm">
                                    {"★ ★ ★ ★ ★"}
                                </div>
                                <p className="text-xs text-slate-500">
                                    4.9/5 từ hơn 3.200 lượt đánh giá
                                </p>
                            </div>
                        </div>

                        <motion.button 
                            onClick={() => navigate("/services")} 
                            className="cursor-pointer hidden sm:inline-flex items-center gap-2 px-6 py-3 rounded-full bg-sky-500 text-white text-sm font-semibold shadow-md hover:bg-sky-600 transition"
                        >
                            Xem tất cả dịch vụ
                            <FiArrowRight className="w-4 h-4" />
                        </motion.button>
                    </motion.div>

                    {/* Services grid */}
                    <motion.div 
                        className="grid md:grid-cols-2 gap-4"
                        variants={containerVariants}
                    >
                        {clinicServices.map((s, i) => (
                            <motion.div
                                key={i}
                                className="group bg-sky-100 transition-all rounded-3xl px-5 py-5 flex flex-col gap-2 shadow-lg border border-sky-100"
                                variants={cardVariants}
                            >
                                <div className="w-12 h-12 rounded-2xl bg-sky-500 flex items-center justify-center text-white transition">
                                    {s.icon}
                                </div>
                                <h3 className="font-semibold text-[#00278D] text-base">
                                    {s.title}
                                </h3>
                                <p className="text-xs text-slate-500 leading-relaxed">
                                    {s.desc}
                                </p>
                            </motion.div>
                        ))}
                    </motion.div>

                    {/* Faded OUR SERVICES text */}
                    <motion.div 
                        className="mt-8 text-[40px] md:text-[52px] font-extrabold tracking-[0.15em] text-sky-100 select-none"
                        initial={{ opacity: 0, x: -20 }}
                        whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }}
                        transition={{ duration: 0.8, delay: 0.5 }}
                    >
                        OUR SERVICES
                    </motion.div>
                </motion.div>

            </div>
        </section>
    );
}

export default ClinicServicesSection;