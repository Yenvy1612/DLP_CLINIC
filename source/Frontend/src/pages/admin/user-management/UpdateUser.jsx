import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { authService, userService } from "../../../api";
import CustomDropdown from "../../../components/CustomDropdown";
import ActionModal from "../../../components/ActionModal";
import { clearCurrentUser, getCurrentUser, getUserId, resolveUserRole, setCurrentUser } from "../../../utils/authUtils";
import { animatePageEnter } from "../../../utils/animeAnimations";

// Normalize date formats between API and <input type="date">.
const ymdToDmy = (value) => {
    const raw = String(value || "").trim();
    if (!raw) return "";

    const dmyMatch = raw.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
    if (dmyMatch) {
        const [, d, m, y] = dmyMatch;
        return `${String(d).padStart(2, "0")}/${String(m).padStart(2, "0")}/${y}`;
    }

    const ymdMatch = raw.match(/^(\d{4})-(\d{1,2})-(\d{1,2})/);
    if (!ymdMatch) return "";

    const [, y, m, d] = ymdMatch;
    return `${String(d).padStart(2, "0")}/${String(m).padStart(2, "0")}/${y}`;
};

const dmyToYmd = (value) => {
    const raw = String(value || "").trim();
    if (!raw) return "";

    const ymdMatch = raw.match(/^(\d{4})-(\d{1,2})-(\d{1,2})/);
    if (ymdMatch) {
        const [, y, m, d] = ymdMatch;
        return `${y}-${String(m).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
    }

    const dmyMatch = raw.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
    if (!dmyMatch) return "";

    const [, d, m, y] = dmyMatch;
    return `${y}-${String(m).padStart(2, "0")}-${String(d).padStart(2, "0")}`;
};

function AdminEditProfile() {
    const navigate = useNavigate();
    const { id: routeId } = useParams();
    const pageRef = useRef(null);
    const currentUserId = getUserId();
    const targetUserId = routeId || currentUserId;
    const isSelfEditing = !routeId;

    const [form, setForm] = useState({
        fullName: "",
        phone: "",
        gender: "OTHER",
        birthDate: "",
        address: "",
        idNumber: "",
        email: "",
        role: "",
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [pendingUpdate, setPendingUpdate] = useState(null);
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    });
    const [passwordError, setPasswordError] = useState("");
    const [changingPassword, setChangingPassword] = useState(false);
    const [confirmPasswordOpen, setConfirmPasswordOpen] = useState(false);
    const [pendingPasswordChange, setPendingPasswordChange] = useState(null);
    const [resultModal, setResultModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
        nextAction: "none",
    });

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    useEffect(() => {
        if (!targetUserId) {
            setError("Không xác định được người dùng cần chỉnh sửa");
            setLoading(false);
            return;
        }

        (async () => {
            try {
                const data = await userService.getById(targetUserId);

                setForm({
                    fullName: data?.fullName || "",
                    phone: data?.phone || "",
                    gender: data?.gender || "",
                    address: data?.address || "",
                    birthDate: dmyToYmd(data?.birthDate) || "",
                    idNumber: data?.idNumber || "",
                    email: data?.email || "",
                    role: data?.role || ""
                })
            }
            catch (e) {
                setError(e?.message || "Không tải được dữ liệu");
            }
            finally {
                setLoading(false);
            }
        })();
    }, [targetUserId]);

    const onChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const onPasswordChange = (e) => {
        setPasswordForm((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        setError("");

        const data = {
            fullName: form.fullName.trim(),
            phone: form.phone.trim(),
            gender: form.gender,
            birthDate: ymdToDmy(form.birthDate),
            address: form.address.trim(),
            idNumber: form.idNumber.trim(),
            email: form.email.trim(),
            role: form.role.trim()
        };

        setPendingUpdate(data);
        setConfirmOpen(true);
    };

    const handleConfirmUpdate = async () => {
        if (!pendingUpdate) return;

        setSaving(true);
        setError("");
        try {
            const updatedUser = await userService.update(targetUserId, pendingUpdate);

            if (isSelfEditing) {
                const current = getCurrentUser() || {};
                setCurrentUser({
                    ...current,
                    ...updatedUser,
                    originalRole: resolveUserRole(updatedUser) || resolveUserRole(current),
                });
            }

            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Lưu thành công",
                message: "Thông tin đã được cập nhật.",
                tone: "success",
                nextAction: isSelfEditing ? "none" : "back-users",
            });
        }
        catch (e) {
            setError(e?.message || "Cập nhật thất bại");
            setConfirmOpen(false);
        }
        finally {
            setSaving(false);
            setPendingUpdate(null);
        }
    };

    const closeConfirmModal = () => {
        if (saving) return;
        setConfirmOpen(false);
        setPendingUpdate(null);
    };

    const canChangePassword = isSelfEditing && form.role === "ADMIN";

    const onSubmitPassword = (e) => {
        e.preventDefault();
        setPasswordError("");

        const payload = {
            currentPassword: passwordForm.currentPassword,
            newPassword: passwordForm.newPassword,
            confirmPassword: passwordForm.confirmPassword,
        };

        if (!payload.currentPassword || !payload.newPassword || !payload.confirmPassword) {
            setPasswordError("Vui lòng nhập đầy đủ thông tin đổi mật khẩu.");
            return;
        }

        if (payload.newPassword.length < 8) {
            setPasswordError("Mật khẩu mới phải có ít nhất 8 ký tự.");
            return;
        }

        if (payload.newPassword !== payload.confirmPassword) {
            setPasswordError("Mật khẩu xác nhận không khớp.");
            return;
        }

        setPendingPasswordChange(payload);
        setConfirmPasswordOpen(true);
    };

    const handleConfirmPasswordChange = async () => {
        if (!pendingPasswordChange) return;

        setChangingPassword(true);
        setPasswordError("");
        try {
            await authService.changePassword(pendingPasswordChange);
            setConfirmPasswordOpen(false);
            setResultModal({
                isOpen: true,
                title: "Đổi mật khẩu thành công",
                message: "Bạn cần đăng nhập lại để tiếp tục sử dụng hệ thống.",
                tone: "success",
                nextAction: "logout",
            });
        }
        catch (e) {
            setPasswordError(e?.message || "Đổi mật khẩu thất bại");
            setConfirmPasswordOpen(false);
        }
        finally {
            setChangingPassword(false);
            setPendingPasswordChange(null);
        }
    };

    const closePasswordConfirmModal = () => {
        if (changingPassword) return;
        setConfirmPasswordOpen(false);
        setPendingPasswordChange(null);
    };

    const closeResultModal = () => {
        const { nextAction } = resultModal;
        setResultModal((prev) => ({ ...prev, isOpen: false, nextAction: "none" }));

        if (nextAction === "back-users") {
            navigate("/admin/users");
        }

        if (nextAction === "logout") {
            clearCurrentUser();
            window.location.replace("/login");
        }
    };

    if (loading) return <div className="p-4 text-center">Đang tải...</div>;
    return (
        <div ref={pageRef} className="bg-slate-100">
            <section className="bg-[var(--surface)] min-h-screen p-10">
                <div className="w-[50vw] mx-auto p-6 rounded-4xl bg-white shadow-2xl">
                    <h1 className="w-full p-3 rounded-xl text-3xl font-semibold mb-3 text-[#00278D]">
                        {isSelfEditing ? "Chỉnh sửa hồ sơ cá nhân" : "Chỉnh sửa hồ sơ"}
                    </h1>

                    {error && (
                        <div className="mb-3 text-slate-700">
                            {error}
                        </div>
                    )}

                    <form
                        onSubmit={onSubmit}
                        className="space-y-3 md:grid md:grid-cols-2 md:gap-5"
                    >
                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Họ và tên</label>
                            <input
                                name="fullName" value={form.fullName} onChange={onChange} required
                                className="w-full text-slate-800 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Số điện thoại</label>
                            <input
                                name="phone" value={form.phone} onChange={onChange}
                                pattern="\+?\d{9,15}" placeholder="VD: 0901234567"
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Email</label>
                            <input
                                name="email" value={form.email} onChange={onChange}
                                placeholder="VD: hung.clinic@ptit.edu.vn"
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Giới tính</label>
                            <CustomDropdown
                                name="gender" value={form.gender} onChange={onChange}
                                options={[
                                    { value: "MALE", label: "Nam" },
                                    { value: "FEMALE", label: "Nữ" },
                                    { value: "OTHER", label: "Khác" },
                                ]}
                                placeholder="-- Chọn giới tính --"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Ngày sinh</label>
                            <input
                                type="date" name="birthDate" value={form.birthDate || ""} onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        {!isSelfEditing && (
                            <div>
                                <label className="block text-slate-800 text-sm mb-1">Vai trò</label>
                                <CustomDropdown
                                    name="role" value={form.role} onChange={onChange}
                                    options={[
                                        { value: "PATIENT", label: "Bệnh nhân" },
                                        { value: "DOCTOR", label: "Bác sĩ" },
                                        { value: "ADMIN", label: "Admin" },
                                    ]}
                                    placeholder="-- Sửa vai trò --"
                                />
                            </div>
                        )}

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Địa chỉ</label>
                            <input
                                name="address" value={form.address} onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">CMND/CCCD</label>
                            <input
                                name="idNumber" value={form.idNumber} onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div className="pt-2 flex gap-2 col-span-2">
                            <button
                                type="submit"
                                disabled={saving}
                                className="px-4 py-2 rounded-xl bg-[#00278D] hover:bg-[#001f5f] text-white disabled:opacity-60 transition duration-400 cursor-pointer"
                            >
                                {saving ? "Đang lưu..." : "Lưu thay đổi"}
                            </button>

                            <button
                                type="button"
                                onClick={() => navigate(isSelfEditing ? "/admin/profile" : "/admin/users")}
                                className="px-4 py-2 cursor-pointer rounded-xl bg-slate-700 text-white hover:bg-slate-800 transition duration-400"
                            >
                                Huỷ
                            </button>
                        </div>
                    </form>

                    {canChangePassword ? (
                        <div className="mt-8 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                            <h2 className="text-xl font-semibold text-[#00278D]">Đổi mật khẩu admin</h2>
                            <p className="mt-1 text-sm text-slate-600">Sau khi đổi mật khẩu thành công, hệ thống sẽ đăng xuất để bảo mật tài khoản.</p>

                            {passwordError ? (
                                <p className="mt-3 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{passwordError}</p>
                            ) : null}

                            <form onSubmit={onSubmitPassword} className="mt-4 space-y-3 md:grid md:grid-cols-3 md:gap-4">
                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Mật khẩu hiện tại</label>
                                    <input
                                        type="password"
                                        name="currentPassword"
                                        value={passwordForm.currentPassword}
                                        onChange={onPasswordChange}
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Mật khẩu mới</label>
                                    <input
                                        type="password"
                                        name="newPassword"
                                        value={passwordForm.newPassword}
                                        onChange={onPasswordChange}
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Xác nhận mật khẩu mới</label>
                                    <input
                                        type="password"
                                        name="confirmPassword"
                                        value={passwordForm.confirmPassword}
                                        onChange={onPasswordChange}
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div className="md:col-span-3 pt-1">
                                    <button
                                        type="submit"
                                        disabled={changingPassword}
                                        className="px-4 py-2 rounded-xl bg-slate-800 text-white hover:bg-slate-900 disabled:opacity-60"
                                    >
                                        {changingPassword ? "Đang đổi mật khẩu..." : "Cập nhật mật khẩu"}
                                    </button>
                                </div>
                            </form>
                        </div>
                    ) : null}
                </div>
            </section>

            <ActionModal
                isOpen={confirmOpen}
                title="Xác nhận lưu thay đổi"
                message="Thông tin người dùng sẽ được cập nhật ngay sau khi xác nhận."
                tone="warning"
                confirmText="Lưu thay đổi"
                cancelText="Hủy"
                showCancel
                loading={saving}
                onConfirm={handleConfirmUpdate}
                onClose={closeConfirmModal}
                closeOnBackdrop={!saving}
            />

            <ActionModal
                isOpen={confirmPasswordOpen}
                title="Xác nhận đổi mật khẩu"
                message="Bạn sẽ phải đăng nhập lại sau khi đổi mật khẩu. Tiếp tục?"
                tone="warning"
                confirmText="Đổi mật khẩu"
                cancelText="Hủy"
                showCancel
                loading={changingPassword}
                onConfirm={handleConfirmPasswordChange}
                onClose={closePasswordConfirmModal}
                closeOnBackdrop={!changingPassword}
            />

            <ActionModal
                isOpen={resultModal.isOpen}
                title={resultModal.title}
                message={resultModal.message}
                tone={resultModal.tone}
                confirmText="Đã hiểu"
                onConfirm={closeResultModal}
                onClose={closeResultModal}
            />
        </div>
    );
}

export default AdminEditProfile;