export const faqs = [
        {
            category: "Đặt lịch khám",
            color: "bg-[var(--brand-500)]",
            questions: [
                {
                    q: "Làm thế nào để đặt lịch khám?",
                    a: "Đăng nhập vào hệ thống → Vào menu 'ĐẶT LỊCH KHÁM' → Chọn bác sĩ, phòng khám và thời gian phù hợp → Chọn dịch vụ khám → Nhấn 'Đặt lịch ngay'. Hệ thống sẽ hiển thị mã lịch hẹn ngay sau khi đặt thành công."
                },
                {
                    q: "Tôi có thể đặt lịch trước bao lâu?",
                    a: "Bạn có thể đặt lịch từ thời điểm hiện tại trở đi (hệ thống tự động chặn chọn quá khứ). Nên đặt trước ít nhất 1 ngày để đảm bảo bác sĩ và phòng khám còn trống."
                },
                {
                    q: "Có thể sửa hoặc hủy lịch hẹn không?",
                    a: "Có, chỉ áp dụng cho lịch hẹn có trạng thái PENDING. Vào 'LỊCH SỬ KHÁM' → Tìm lịch hẹn cần thay đổi → Nhấn 'Sửa' để cập nhật thông tin hoặc 'Hủy' để hủy lịch. Nên hủy trước ít nhất 2 giờ."
                },
                {
                    q: "Tôi quên mã lịch hẹn, làm sao tìm lại?",
                    a: "Vào 'LỊCH SỬ KHÁM' để xem tất cả lịch hẹn của bạn. Hệ thống hiển thị đầy đủ mã lịch hẹn, bác sĩ, phòng, thời gian và trạng thái (PENDING/DONE/CANCELLED)."
                },
                {
                    q: "Lịch hẹn có thể trùng không?",
                    a: "Không. Hệ thống đảm bảo: (1) Một bác sĩ không thể có 2 lịch hẹn cùng khung giờ. (2) Một phòng khám không thể được đặt 2 lần cùng lúc. Nếu trùng, hệ thống sẽ báo lỗi."
                }
            ]
        },
        {
            category: "Tài khoản",
            color: "bg-[var(--brand-600)]",
            questions: [
                {
                    q: "Làm thế nào để đăng ký tài khoản?",
                    a: "Nhấn 'Đăng nhập' ở góc trên phải → Chọn 'Đăng ký' → Điền đầy đủ: Họ tên, Email, Số điện thoại, Mật khẩu, Giới tính, Ngày sinh, Địa chỉ, CMND/CCCD → Xác nhận để hoàn tất. Sau đó đăng nhập bằng email và mật khẩu vừa tạo."
                },
                {
                    q: "Quên mật khẩu, phải làm sao?",
                    a: "Hiện tại hệ thống chưa có tính năng quên mật khẩu tự động. Vui lòng liên hệ Admin qua email hung.clinic@ptit.edu.vn hoặc trang 'LIÊN HỆ' để được hỗ trợ đặt lại mật khẩu."
                },
                {
                    q: "Có thể sửa thông tin cá nhân không?",
                    a: "Có. Vào 'HỒ SƠ CÁ NHÂN' → Nhấn 'Chỉnh sửa hồ sơ' → Cập nhật thông tin (email, số điện thoại, địa chỉ, CMND/CCCD) → Nhấn 'Lưu thay đổi'. Thông tin sẽ được cập nhật ngay lập tức."
                },
                {
                    q: "Hệ thống có những vai trò nào?",
                    a: "4 vai trò: (1) GUEST - Khách chưa đăng nhập, xem thông tin cơ bản. (2) PATIENT - Bệnh nhân, đặt lịch và quản lý lịch khám. (3) DOCTOR - Bác sĩ, xem lịch khám và thống kê. (4) ADMIN - Quản trị viên, quản lý toàn bộ hệ thống."
                }
            ]
        },
        {
            category: "Dịch vụ & Chi phí",
            color: "bg-[var(--brand-700)]",
            questions: [
                {
                    q: "Phòng khám có những dịch vụ nào?",
                    a: "A*Care Clinic cung cấp: (1) Khám ngoại trú tổng quát - tư vấn sức khỏe toàn diện. (2) Khám chuyên khoa - tim mạch, hô hấp, tiêu hóa, cơ xương khớp, nội tiết, thần kinh. (3) Chẩn đoán hình ảnh - siêu âm, X-quang, CT/MRI. (4) AI hỗ trợ chẩn đoán. (5) Quản lý thuốc và tái khám thông minh."
                },
                {
                    q: "Chi phí khám bệnh như thế nào?",
                    a: "Chi phí phụ thuộc vào dịch vụ và chuyên khoa đã chọn. Xem bảng giá chi tiết tại trang 'DỊCH VỤ' hoặc liên hệ tư vấn. Thanh toán tại quầy sau khi khám, nhận hóa đơn VAT và đơn thuốc."
                },
                {
                    q: "Có nhận bảo hiểm y tế không?",
                    a: "Vui lòng liên hệ trực tiếp qua email hung.clinic@ptit.edu.vn hoặc trang 'LIÊN HỆ' để biết chính sách bảo hiểm y tế hiện tại và các loại bảo hiểm được chấp nhận."
                },
            ]
        },
        {
            category: "Bác sĩ & Phòng khám",
            color: "bg-[var(--brand-800)]",
            questions: [
                {
                    q: "Làm sao để chọn bác sĩ phù hợp?",
                    a: "Khi đặt lịch, hệ thống hiển thị danh sách bác sĩ đang hoạt động. Chọn bác sĩ dựa trên chuyên khoa phù hợp với triệu chứng của bạn. Mỗi bác sĩ đều có chuyên môn rõ ràng trong hồ sơ."
                },
                {
                    q: "Bác sĩ có kinh nghiệm thế nào?",
                    a: "Đội ngũ bác sĩ A*Care Clinic có kinh nghiệm 5-25 năm, được đào tạo chuyên sâu trong và ngoài nước, có chứng chỉ hành nghề hợp lệ. Cam kết chất lượng khám chữa bệnh chuyên nghiệp."
                },
                {
                    q: "Phòng khám được tổ chức như thế nào?",
                    a: "Phòng khám được phân chia theo 5 tầng (Tầng 1-5), mỗi phòng có tên riêng biệt. Admin có thể tìm kiếm và lọc phòng theo tầng để quản lý hiệu quả."
                },
                {
                    q: "Bác sĩ có xem được thống kê không?",
                    a: "Có. Bác sĩ truy cập trang 'THỐNG KÊ' để xem: Tổng số bệnh nhân, Tổng số lượt khám, Doanh thu theo tháng, Dịch vụ được sử dụng nhiều nhất. Giúp bác sĩ nắm rõ hiệu suất làm việc."
                }
            ]
        },
        {
            category: "Tính năng hệ thống",
            color: "bg-[var(--brand-600)]",
            questions: [
                {
                    q: "Admin có quyền gì trong hệ thống?",
                    a: "Admin có toàn quyền: (1) Quản lý người dùng - thêm/sửa/xóa/tìm kiếm theo vai trò. (2) Quản lý dịch vụ - cập nhật giá, mô tả. (3) Quản lý phòng khám theo tầng. (4) Xem tất cả lịch hẹn. (5) Dashboard thống kê tổng quan về lượt khám, doanh thu, hoạt động gần đây."
                },
                {
                    q: "Bác sĩ quản lý lịch khám như thế nào?",
                    a: "Bác sĩ vào trang 'LỊCH KHÁM' để xem tất cả lịch hẹn của mình, có thể lọc theo ngày/tuần/tháng và trạng thái (PENDING/DONE/CANCELLED). Xem danh sách bệnh nhân trong trang 'BỆNH NHÂN'."
                },
                {
                    q: "Có theo dõi hoạt động hệ thống không?",
                    a: "Có. Hệ thống ghi lại Activity Log cho các hoạt động quan trọng: Đặt lịch thành công, Hủy lịch, Thêm/xóa người dùng, Thay đổi cấu hình. Admin xem trong Dashboard để giám sát hoạt động."
                }
            ]
        },
        {
            category: "Khác",
            color: "bg-[var(--brand-500)]",
            questions: [
                {
                    q: "Giờ làm việc của phòng khám?",
                    a: "Thứ Hai - Thứ Sáu: 7:00 - 19:00 | Thứ Bảy: 7:00 - 17:00 | Chủ Nhật: 8:00 - 12:00. Khuyến khích đặt lịch trước để tránh chờ đợi."
                },
                {
                    q: "Có chỗ đậu xe không?",
                    a: "Có. Phòng khám có bãi đậu xe rộng rãi, an toàn, có bảo vệ 24/7 cho cả ô tô và xe máy. Miễn phí đậu xe trong suốt thời gian khám."
                },
                {
                    q: "Làm sao để liên hệ phòng khám?",
                    a: "Email: hung.clinic@ptit.edu.vn | Vào trang 'LIÊN HỆ' để gửi tin nhắn trực tiếp. Đội ngũ hỗ trợ sẽ phản hồi trong vòng 24h."
                }
            ]
        }
    ];