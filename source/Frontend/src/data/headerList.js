// Menu mặc định cho khách
const guestMenu = [
    { id: 1, to: "/", name: "TRANG CHỦ", hasArrow: false },

    {
        id: 2,
        to: "/services",
        name: "DỊCH VỤ",
        hasArrow: false,
    },

    {
        id: 3,
        to: "/about",
        name: "TRANG",
        hasArrow: true,
        children: [
            { id: "p1", label: "GIỚI THIỆU", to: "/about" },
            { id: "p2", label: "HƯỚNG DẪN", to: "/instruction" },
            { id: "p3", label: "HỎI ĐÁP", to: "/faq" },
        ],
    },

    { id: 4, to: "/instruction", name: "HƯỚNG DẪN", hasArrow: false },
    { id: 5, to: "/contact", name: "LIÊN HỆ", hasArrow: false },
];

// Menu cho ADMIN
const adminMenu = [
    { id: 1, to: "/admin/dashboard", name: "BẢNG ĐIỀU KHIỂN", hasArrow: false },
    { id: 2, to: "/admin/users", name: "NGƯỜI DÙNG", hasArrow: false },
    { id: 3, to: "/admin/services", name: "DỊCH VỤ", hasArrow: false },
];

// Menu cho DOCTOR
const doctorMenu = [
    { id: 1, to: "/doctor/schedule", name: "LỊCH KHÁM", hasArrow: false },
    { id: 2, to: "/doctor/reports", name: "THỐNG KÊ", hasArrow: false },
    { id: 3, to: "/instruction", name: "HƯỚNG DẪN", hasArrow: false },
];

// Menu cho PATIENT
const patientMenu = [
    { id: 1, to: "/patient/book", name: "Đặt lịch", hasArrow: false },
    { id: 2, to: "/patient/appointments", name: "Lịch hẹn", hasArrow: false },
    { id: 3, to: "/patient/history", name: "Lịch sử", hasArrow: false },
];

// Export theo vai trò
export const headerListByRole = {
    GUEST: guestMenu,
    ADMIN: adminMenu,
    DOCTOR: doctorMenu,
    PATIENT: patientMenu,
};
