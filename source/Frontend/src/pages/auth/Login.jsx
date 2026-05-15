import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { authService } from "../../api";
import { getRoleHomePath, resolveUserRole, setCurrentUser } from "../../utils/authUtils";

export default function Login() {
    const [form, setForm] = useState({ email: "", password: "" });
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (submitting) {
            return;
        }

        try {
            setSubmitting(true);
            setError("");

            const loginResponse = await authService.login({
                email: form.email,
                password: form.password,
            });

            // Login now relies on httpOnly cookies for auth, local snapshot is only for UI role rendering.
            let authPayload = loginResponse;

            if (!authPayload?.id || !resolveUserRole(authPayload)) {
                try {
                    const profile = await authService.me();
                    authPayload = {
                        ...authPayload,
                        ...profile,
                        originalRole: profile?.role || resolveUserRole(authPayload),
                    };
                } catch {
                    // Keep login payload if profile endpoint is temporarily unavailable.
                }
            }

            setCurrentUser(authPayload);

            const role = resolveUserRole(authPayload);
            navigate(getRoleHomePath(role), { replace: true });
        } catch (err) {
            setError(err.message || "Đăng nhập thất bại");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <section className="mx-auto flex min-h-[calc(100vh-8rem)] w-full max-w-7xl items-center justify-center px-4 py-10">
            <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-8 shadow-lg md:p-10">
                <h1 className="mb-2 text-center text-3xl font-bold text-[var(--brand-navy)]">
                    Đăng nhập A<sup className="text-[var(--brand-600)]">*</sup>Care
                </h1>
                <p className="mb-8 text-center text-sm text-slate-500">Truy cập hệ thống theo đúng vai trò tài khoản của bạn.</p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="mb-1 block text-slate-700">Email</label>
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
                        <label className="mb-1 block text-slate-700">Mật khẩu</label>
                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            placeholder="Nhap mat khau"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-[var(--brand-500)] focus:ring-2 focus:ring-[var(--brand-200)]"
                            required
                        />
                    </div>

                    {error ? <p className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p> : null}

                    <button
                        type="submit"
                        disabled={submitting}
                        className="mt-2 w-full cursor-pointer rounded-lg bg-[var(--brand-600)] py-2.5 font-semibold text-white transition hover:bg-[var(--brand-700)] disabled:cursor-not-allowed disabled:opacity-60"
                    >
                        {submitting ? "Đang xử lý..." : "Đăng nhập"}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm font-medium text-slate-500">
                    Bạn chưa có tài khoản? <NavLink to="/register" className="text-[var(--brand-600)] hover:underline">Đăng ký</NavLink>
                </div>
            </div>
        </section>
    );
}
