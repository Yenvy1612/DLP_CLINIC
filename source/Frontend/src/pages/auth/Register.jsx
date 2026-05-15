import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { authService } from "../../api";
import CustomDropdown from "../../components/CustomDropdown";

const ymdToDmy = (ymd) => {
    if (!ymd) return "";
    const [y, m, d] = ymd.split("-");
    return `${d}/${m}/${y}`;
};

function Register() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        fullName: "",
        email: "",
        phone: "",
        password: "",
        confirmPassword: "",
        role: "PATIENT",
        gender: "OTHER",
        birthDate: "",
        address: "",
        idNumber: "",
    });

    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);

    const handleChange = (e) => {
        setForm((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (submitting) return;

        if (form.password !== form.confirmPassword) {
            setMessage("PASSWORD MISMATCH");
            return;
        }

        try {
            setSubmitting(true);
            setMessage("");

            const payload = {
                ...form,
                birthDate: ymdToDmy(form.birthDate),
            };

            const data = await authService.register(payload);
            setMessage(data.message || "SIGN UP SUCCESSFULLY");

            if (data.success) {
                navigate("/login", { replace: true });
            }
        } catch (err) {
            setMessage(err.message || "Dang ky that bai");
        } finally {
            setSubmitting(false);
        }
    };

    const getMessageLabel = (value) => {
        if (!value) return null;
        if (value === "SIGN UP SUCCESSFULLY") return "Đăng ký thành công";
        if (value === "EMAIL WAS USED") return "Email đã được sử dụng";
        if (value === "NUMBER WAS USED") return "Số điện thoại đã được sử dụng";
        if (value === "ID NUMBER WAS USED") return "CCCD/CMND đã được sử dụng";
        if (value === "PASSWORD MISMATCH") return "Mật khẩu xác nhận không khớp";
        return value;
    };

    const messageLabel = getMessageLabel(message);
    const isError = message && message !== "SIGN UP SUCCESSFULLY";

    return (
        <section className="mx-auto flex min-h-[calc(100vh-8rem)] w-full max-w-7xl items-center justify-center px-4 py-10">
            <div className="w-full max-w-4xl rounded-3xl border border-slate-200 bg-white p-8 shadow-lg md:p-10">
                <h1 className="mb-2 text-center text-3xl font-bold text-[var(--brand-navy)]">
                    Đăng ký A<sup className="text-[var(--brand-600)]">*</sup>Care
                </h1>
                <p className="mb-8 text-center text-sm text-slate-500">
                    Tạo tài khoản bệnh nhân để đặt lịch khám và theo dõi lịch sử.
                </p>

                <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-x-5">
                    <div>
                        <label className="mb-1 block text-slate-600">Họ và tên</label>
                        <input
                            type="text"
                            name="fullName"
                            value={form.fullName}
                            onChange={handleChange}
                            placeholder="Nhập họ và tên"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                            placeholder="Nhap email"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Số điện thoại</label>
                        <input
                            type="text"
                            name="phone"
                            value={form.phone}
                            onChange={handleChange}
                            placeholder="Nhập số điện thoại"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Mật khẩu</label>
                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            placeholder="Nhập mật khẩu"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Xác nhận mật khẩu</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            value={form.confirmPassword}
                            onChange={handleChange}
                            placeholder="Nhập lại mật khẩu"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Giới tính</label>
                        <CustomDropdown
                            name="gender"
                            value={form.gender}
                            onChange={handleChange}
                            options={[
                                { value: "MALE", label: "Nam" },
                                { value: "FEMALE", label: "Nữ" },
                                { value: "OTHER", label: "Khác" },
                            ]}
                            placeholder="Chọn giới tính"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Ngày sinh</label>
                        <input
                            type="date"
                            name="birthDate"
                            value={form.birthDate}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 text-slate-600 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">Địa chỉ</label>
                        <input
                            type="text"
                            name="address"
                            value={form.address}
                            onChange={handleChange}
                            placeholder="Nhập địa chỉ"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-slate-600">CCCD/CMND</label>
                        <input
                            type="text"
                            name="idNumber"
                            value={form.idNumber}
                            onChange={handleChange}
                            placeholder="Nhập CCCD/CMND"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    <div className="mt-2 md:col-span-2">
                        <button
                            type="submit"
                            disabled={submitting}
                            className="w-full cursor-pointer rounded-lg bg-[var(--brand-600)] py-2.5 font-semibold text-white transition hover:bg-[var(--brand-700)] disabled:cursor-not-allowed disabled:opacity-60"
                        >
                            {submitting ? "Đang xử lý..." : "Đăng ký"}
                        </button>
                    </div>

                    <div className="md:col-span-2 text-center text-sm font-medium text-slate-500">
                        Chỉ có thể đăng ký tài khoản bệnh nhân. Đã có tài khoản?{" "}
                        <NavLink to="/login" className="text-[var(--brand-600)] hover:underline">Đăng nhập</NavLink>
                    </div>

                    {messageLabel ? (
                        <p className={`md:col-span-2 rounded-lg px-3 py-2 text-center text-sm ${isError ? "border border-slate-300 bg-slate-100 text-slate-700" : "border border-[#00278D]/20 bg-[#00278D]/5 text-[#00278D]"}`}>
                            {messageLabel}
                        </p>
                    ) : null}
                </form>
            </div>
        </section>
    );
}

export default Register;
