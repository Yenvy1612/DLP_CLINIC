import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearCurrentUser, getCurrentUser, getRoleHomePath, getUserRole, resolveUserRole, setCurrentUser } from "../utils/authUtils";
import { authService, specialtyService } from "../api";
import CustomDropdown from "../components/CustomDropdown";
import ActionModal from "../components/ActionModal";
import { animatePageEnter } from "../utils/animeAnimations";

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

const WEEKDAY_OPTIONS = [
    { value: "MONDAY", label: "T2" },
    { value: "TUESDAY", label: "T3" },
    { value: "WEDNESDAY", label: "T4" },
    { value: "THURSDAY", label: "T5" },
    { value: "FRIDAY", label: "T6" },
];

const splitWorkingDays = (value) => {
    const allowed = new Set(["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]);
    if (!value) return Array.from(allowed);
    const normalized = String(value)
        .split(",")
        .map((day) => day.trim())
        .filter((day) => allowed.has(day));
    return normalized.length ? normalized : Array.from(allowed);
};

const resolveSpecialtyId = (doctorProfile, specialtyList) => {
    const directId =
        doctorProfile?.specialtyId
        ?? doctorProfile?.specialityId
        ?? doctorProfile?.specialty_id
        ?? doctorProfile?.speciality_id;

    if (directId !== null && directId !== undefined && String(directId).trim() !== "") {
        return String(directId);
    }

    const specialtyName = String(doctorProfile?.specialty || "").trim().toLowerCase();
    if (!specialtyName || !Array.isArray(specialtyList)) {
        return "";
    }

    const matched = specialtyList.find((item) => String(item?.name || "").trim().toLowerCase() === specialtyName);
    return matched?.id ? String(matched.id) : "";
};

function EditProfile() {
    const navigate = useNavigate();
    const role = getUserRole();
    const pageRef = useRef(null);

    const [form, setForm] = useState({
        fullName: "",
        phone: "",
        gender: "OTHER",
        birthDate: "",
        address: "",
        idNumber: "",
        email: "",
        clinicLocation: "",
        specialtyId: "",
        workingDays: ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
        shiftStart: "08:00",
        shiftEnd: "17:00",
        yearsExperience: 0,
    });
    const [specialties, setSpecialties] = useState([]);

    const [passwordForm, setPasswordForm] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    });

    const [loading, setLoading] = useState(true);
    const [savingProfile, setSavingProfile] = useState(false);
    const [changingPassword, setChangingPassword] = useState(false);
    const [error, setError] = useState("");
    const [passwordError, setPasswordError] = useState("");

    const [confirmProfileOpen, setConfirmProfileOpen] = useState(false);
    const [pendingProfileUpdate, setPendingProfileUpdate] = useState(null);

    const [confirmPasswordOpen, setConfirmPasswordOpen] = useState(false);
    const [pendingPasswordChange, setPendingPasswordChange] = useState(null);
    const [resultModal, setResultModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
        nextAction: "none",
    });
    const [isDoctor, setIsDoctor] = useState(false);
    const [doctorSpecialtyLabel, setDoctorSpecialtyLabel] = useState("");

    const specialtyOptions = useMemo(() => {
        const base = specialties.map((item) => ({
            value: String(item.id),
            label: `${item.name} (${item.code})`,
        }));

        if (isDoctor && form.specialtyId && !base.some((opt) => opt.value === String(form.specialtyId))) {
            base.unshift({
                value: String(form.specialtyId),
                label: doctorSpecialtyLabel || `Chuyên môn hiện tại (ID: ${form.specialtyId})`,
            });
        }

        return base;
    }, [doctorSpecialtyLabel, form.specialtyId, isDoctor, specialties]);

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    useEffect(() => {
        (async () => {
            try {
                const data = await authService.me();

                setForm({
                    fullName: data?.fullName || "",
                    phone: data?.phone || "",
                    gender: data?.gender || "",
                    address: data?.address || "",
                    birthDate: dmyToYmd(data?.birthDate) || "",
                    idNumber: data?.idNumber || "",
                    email: data?.email || "",
                    clinicLocation: "",
                    specialtyId: "",
                    workingDays: ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
                    shiftStart: "08:00",
                    shiftEnd: "17:00",
                    yearsExperience: 0,
                });

                const doctorRole = data?.role === "DOCTOR";
                setIsDoctor(doctorRole);

                if (doctorRole) {
                    const specialtyData = await specialtyService.getAll();
                    const activeSpecialties = Array.isArray(specialtyData) ? specialtyData.filter((item) => item?.active !== false) : [];
                    setSpecialties(activeSpecialties);

                    const doctorProfile = await authService.getMyDoctorProfile();
                    setForm((prev) => ({
                        ...prev,
                        clinicLocation: doctorProfile?.clinicLocation || "",
                        specialtyId: resolveSpecialtyId(doctorProfile, activeSpecialties),
                        workingDays: splitWorkingDays(doctorProfile?.workingDays),
                        shiftStart: String(doctorProfile?.shiftStart || "08:00").slice(0, 5),
                        shiftEnd: String(doctorProfile?.shiftEnd || "17:00").slice(0, 5),
                        yearsExperience: Number(doctorProfile?.yearsExperience) || 0,
                    }));
                    setDoctorSpecialtyLabel(doctorProfile?.specialty || "");
                }
            }
            catch (e) {
                setError(e?.message || "Không tải được dữ liệu");
            }
            finally {
                setLoading(false);
            }
        })();
    }, []);

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

    const handleWorkingDaysChange = (event) => {
        const day = event.target.value;
        setForm((prev) => {
            const current = [...prev.workingDays];
            if (event.target.checked && !current.includes(day)) {
                current.push(day);
            }
            if (!event.target.checked) {
                return { ...prev, workingDays: current.filter((value) => value !== day) };
            }
            return { ...prev, workingDays: current };
        });
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
            email: form.email.trim()
        };

        if (isDoctor) {
            if (!form.specialtyId) {
                setError("Bác sĩ phải có chuyên môn.");
                return;
            }
            if (!form.clinicLocation.trim()) {
                setError("Bác sĩ phải có phòng làm việc.");
                return;
            }
            if (!form.workingDays.length) {
                setError("Bác sĩ phải có ít nhất một ngày làm việc.");
                return;
            }

            if (Number(form.yearsExperience) < 0) {
                setError("Số năm kinh nghiệm không hợp lệ.");
                return;
            }
        }

        setPendingProfileUpdate(data);
        setConfirmProfileOpen(true);
    };

    const handleConfirmProfileUpdate = async () => {
        if (!pendingProfileUpdate) return;

        setSavingProfile(true);
        setError("");
        try {
            const updatedProfile = await authService.updateMe(pendingProfileUpdate);
            const current = getCurrentUser() || {};

            if (isDoctor) {
                await authService.updateMyDoctorProfile({
                    specialtyId: Number(form.specialtyId),
                    yearsExperience: Number(form.yearsExperience || 0),
                    clinicLocation: form.clinicLocation.trim(),
                    workingDays: form.workingDays.join(","),
                    shiftStart: form.shiftStart,
                    shiftEnd: form.shiftEnd,
                });
            }

            setCurrentUser({
                ...current,
                ...updatedProfile,
                originalRole: resolveUserRole(updatedProfile) || resolveUserRole(current),
            });

            setConfirmProfileOpen(false);
            setResultModal({
                isOpen: true,
                title: "Lưu thành công",
                message: "Thông tin hồ sơ đã được cập nhật.",
                tone: "success",
                nextAction: "reload-profile",
            });
        }
        catch (e) {
            setError(e?.message || "Cập nhật thất bại");
            setConfirmProfileOpen(false);
        }
        finally {
            setSavingProfile(false);
            setPendingProfileUpdate(null);
        }
    };

    const closeProfileConfirmModal = () => {
        if (savingProfile) return;
        setConfirmProfileOpen(false);
        setPendingProfileUpdate(null);
    };

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

        if (nextAction === "reload-profile") {
            window.location.reload();
            return;
        }

        if (nextAction === "logout") {
            clearCurrentUser();
            window.location.replace("/login");
        }
    };

    if (loading) return <div className="p-4 text-center">Đang tải...</div>;
    return (
        <div ref={pageRef} className="bg-slate-100">
            <section className="bg-[url(../../assets/images/booking/booking-bg.png)] bg-cover min-h-screen p-10">
                <div className="w-[min(960px,92vw)] mx-auto p-6 rounded-4xl bg-white shadow-2xl">
                    <h1 className="w-full p-3 rounded-xl text-3xl font-semibold mb-3 text-[#00278D]">
                        Chỉnh sửa hồ sơ
                    </h1>

                    {error && (
                        <div className="mb-3 text-slate-700">
                            {error}
                        </div>
                    )}

                    <form onSubmit={onSubmit} className="space-y-3 md:grid md:grid-cols-2 md:gap-5">
                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Họ và tên</label>
                            <input
                                name="fullName"
                                value={form.fullName}
                                onChange={onChange}
                                required
                                className="w-full text-slate-800 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Số điện thoại</label>
                            <input
                                name="phone"
                                value={form.phone}
                                onChange={onChange}
                                pattern="\+?\d{9,15}"
                                placeholder="VD: 0901234567"
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Email</label>
                            <input
                                name="email"
                                value={form.email}
                                onChange={onChange}
                                placeholder="VD: hung.clinic@ptit.edu.vn"
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Giới tính</label>
                            <CustomDropdown
                                name="gender"
                                value={form.gender}
                                onChange={onChange}
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
                                type="date"
                                name="birthDate"
                                value={form.birthDate || ""}
                                onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Địa chỉ</label>
                            <input
                                name="address"
                                value={form.address}
                                onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">CMND/CCCD</label>
                            <input
                                name="idNumber"
                                value={form.idNumber}
                                onChange={onChange}
                                className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        {isDoctor && (
                            <div className="col-span-2 grid grid-cols-1 md:grid-cols-2 gap-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
                                <div className="md:col-span-2">
                                    <h3 className="text-lg font-semibold text-[#00278D]">Thông tin làm việc của bác sĩ</h3>
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-slate-800 text-sm mb-1">Chuyên môn</label>
                                    <CustomDropdown
                                        name="specialtyId"
                                        value={form.specialtyId}
                                        onChange={onChange}
                                        options={specialtyOptions}
                                        placeholder="-- Chọn chuyên môn --"
                                    />
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-slate-800 text-sm mb-1">Phòng làm việc</label>
                                    <input
                                        name="clinicLocation"
                                        value={form.clinicLocation}
                                        onChange={onChange}
                                        placeholder="Ví dụ: Tòa A - Phòng 301"
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Số năm kinh nghiệm</label>
                                    <input
                                        type="number"
                                        min="0"
                                        name="yearsExperience"
                                        value={form.yearsExperience}
                                        onChange={onChange}
                                        placeholder="Ví dụ: 5"
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-slate-800 text-sm mb-1">Ngày làm việc trong tuần</label>
                                    <div className="flex flex-wrap gap-3">
                                        {WEEKDAY_OPTIONS.map((day) => (
                                            <label key={day.value} className="flex items-center gap-2 text-slate-700 text-sm">
                                                <input
                                                    type="checkbox"
                                                    value={day.value}
                                                    checked={form.workingDays.includes(day.value)}
                                                    onChange={handleWorkingDaysChange}
                                                    className="rounded text-[#00278D] focus:ring-[#00278D]"
                                                />
                                                <span>{day.label}</span>
                                            </label>
                                        ))}
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Giờ bắt đầu</label>
                                    <input
                                        type="time"
                                        name="shiftStart"
                                        value={form.shiftStart}
                                        onChange={onChange}
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>

                                <div>
                                    <label className="block text-slate-800 text-sm mb-1">Giờ kết thúc</label>
                                    <input
                                        type="time"
                                        name="shiftEnd"
                                        value={form.shiftEnd}
                                        onChange={onChange}
                                        className="w-full border text-slate-800 border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                                    />
                                </div>
                            </div>
                        )}

                        <div className="pt-2 flex gap-2 col-span-2">
                            <button
                                type="submit"
                                disabled={savingProfile}
                                className="px-4 py-2 rounded-xl bg-[#00278D] hover:bg-[#001f5f] text-white disabled:opacity-60 transition duration-400 cursor-pointer"
                            >
                                {savingProfile ? "Đang lưu..." : "Lưu thay đổi"}
                            </button>

                            <button
                                type="button"
                                onClick={() => navigate(getRoleHomePath(role))}
                                className="px-4 py-2 cursor-pointer rounded-xl bg-slate-700 text-white hover:bg-slate-800 transition duration-400"
                            >
                                Huỷ
                            </button>
                        </div>
                    </form>

                    <div className="mt-8 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                        <h2 className="text-xl font-semibold text-[#00278D]">Đổi mật khẩu</h2>
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
                </div>
            </section>

            <ActionModal
                isOpen={confirmProfileOpen}
                title="Xác nhận lưu thay đổi"
                message="Thông tin hồ sơ sẽ được cập nhật ngay sau khi xác nhận."
                tone="warning"
                confirmText="Lưu thay đổi"
                cancelText="Hủy"
                showCancel
                loading={savingProfile}
                onConfirm={handleConfirmProfileUpdate}
                onClose={closeProfileConfirmModal}
                closeOnBackdrop={!savingProfile}
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

export default EditProfile;