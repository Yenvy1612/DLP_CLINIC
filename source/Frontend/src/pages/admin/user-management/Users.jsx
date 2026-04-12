import { useEffect, useRef, useState } from "react";
import { FiEdit2, FiTrash2, FiSearch, FiUserPlus } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { userService } from "../../../api";
import CustomDropdown from "../../../components/CustomDropdown";
import ActionModal from "../../../components/ActionModal";
import { animatePageEnter } from "../../../utils/animeAnimations";

function AdminUserManagement() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const pageRef = useRef(null);
    
    // State cho search
    const [searchParams, setSearchParams] = useState({
        fullName: '',
        role: '',
        email: ''
    });
    const [deleteTargetId, setDeleteTargetId] = useState(null);
    const [deleting, setDeleting] = useState(false);
    const [noticeModal, setNoticeModal] = useState({
        isOpen: false,
        title: "",
        message: "",
        tone: "info",
    });

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    // Lấy danh sách user
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await userService.getAll();
                setUsers(response);
            }
            catch (err) {
                console.error("Error fetching users:", err);
            }
            finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    // Xử lý thay đổi search input
    const handleSearchChange = (e) => {
        const { name, value } = e.target;
        setSearchParams(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Xử lý tìm kiếm
    const handleSearch = async () => {
        setLoading(true);
        try {
            const searchResults = await userService.search(searchParams);
            setUsers(searchResults);
        } 
        catch (err) {
            console.error("Error searching users:", err);
        } 
        finally {
            setLoading(false);
        }
    };

    // Reset search
    const handleReset = async () => {
        setSearchParams({
            fullName: '',
            role: '',
            email: ''
        });
        setLoading(true);
        try {
            const response = await userService.getAll();
            setUsers(response);
        } catch (err) {
            console.error("Error fetching users:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = (id) => {
        setDeleteTargetId(id);
    };

    const handleConfirmDelete = async () => {
        if (!deleteTargetId) return;

        setDeleting(true);
        try {
            await userService.remove(deleteTargetId);
            setUsers((prev) => prev.filter((user) => user.id !== deleteTargetId));
            setNoticeModal({
                isOpen: true,
                title: "Đã xóa người dùng",
                message: "Người dùng đã được xóa thành công.",
                tone: "success",
            });
        }
        catch (error) {
            setNoticeModal({
                isOpen: true,
                title: "Xóa thất bại",
                message: error?.message || "Không thể xóa người dùng. Vui lòng thử lại.",
                tone: "warning",
            });
        }
        finally {
            setDeleting(false);
            setDeleteTargetId(null);
        }
    };

    return (
        <div
            ref={pageRef}
            className="min-h-screen bg-[var(--surface)] px-6 py-8"
        >
            <div className="max-w-6xl mx-auto">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold text-[#00278D] mb-6 p-2 bg-white rounded-xl shadow-xl">Danh sách người dùng</h1>
                    <button onClick={() => navigate("/admin/add-user")} className="flex items-center gap-2 text-sm mb-6 bg-[#00278D] text-white p-2 rounded-xl hover:shadow-xl hover:bg-[#001f5f] transition duration-300 cursor-pointer">
                        <FiUserPlus />
                        Thêm người dùng
                    </button>
                </div>

                {/* Thanh tìm kiếm */}
                <div className="bg-white rounded-2xl shadow-xl p-6 mb-6">
                    <div className="flex items-center gap-2 mb-4">
                        <FiSearch className="text-[#00278D]" size={20} />
                        <h2 className="text-lg font-semibold text-[#00278D]">Tìm kiếm người dùng</h2>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {/* Tên người dùng */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Tên người dùng
                            </label>
                            <input
                                type="text"
                                name="fullName"
                                value={searchParams.fullName}
                                onChange={handleSearchChange}
                                placeholder="Nhập tên người dùng..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#00278D] focus:border-transparent"
                            />
                        </div>

                        {/* Email */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Email
                            </label>
                            <input
                                type="text"
                                name="email"
                                value={searchParams.email}
                                onChange={handleSearchChange}
                                placeholder="Nhập email..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#00278D] focus:border-transparent"
                            />
                        </div>

                        {/* Vai trò */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Vai trò
                            </label>
                            <CustomDropdown
                                name="role"
                                value={searchParams.role}
                                onChange={handleSearchChange}
                                options={[
                                    { value: "", label: "Tất cả vai trò" },
                                    { value: "ADMIN", label: "Admin" },
                                    { value: "DOCTOR", label: "Bác sĩ" },
                                    { value: "PATIENT", label: "Bệnh nhân" },
                                    { value: "STAFF", label: "Nhân viên" },
                                ]}
                                placeholder="Tất cả vai trò"
                            />
                        </div>
                    </div>

                    {/* Buttons */}
                    <div className="flex gap-3 mt-4">
                        <button
                            onClick={handleSearch}
                            className="bg-[#00278D] text-white px-6 py-2 rounded-lg hover:bg-[#001f5f] transition-colors font-medium flex items-center gap-2"
                        >
                            <FiSearch size={16} />
                            Tìm kiếm
                        </button>
                        <button
                            onClick={handleReset}
                            className="bg-slate-700 text-white px-6 py-2 rounded-lg hover:bg-slate-800 transition-colors font-medium"
                        >
                            Đặt lại
                        </button>
                    </div>
                </div>

                <div className="bg-white border border-slate-200 rounded-3xl shadow-xl overflow-hidden">
                    <table className="min-w-full text-sm">
                        <thead className="bg-slate-100 text-[#00278D]">
                            <tr>
                                <th className="px-6 py-3 text-left font-semibold">STT</th>
                                <th className="px-6 py-3 text-left font-semibold">Email</th>
                                <th className="px-6 py-3 text-left font-semibold">Họ tên</th>
                                <th className="px-6 py-3 text-left font-semibold">Vai trò</th>
                                <th className="px-6 py-3 text-center font-semibold">Thao tác</th>
                            </tr>
                        </thead>

                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="5" className="px-6 py-6 text-center text-slate-500">
                                        Đang tải danh sách người dùng...
                                    </td>
                                </tr>
                            ) : users.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="px-6 py-6 text-center text-slate-500">
                                        Không có người dùng nào.
                                    </td>
                                </tr>
                            ) : (
                                users.map((u, idx) => (
                                    <tr
                                        key={u.id ?? idx}
                                        className={`hover:bg-slate-100 transition ${idx % 2 === 0 ? "bg-white" : "bg-slate-50/60"
                                            }`}
                                    >
                                        <td className="px-6 py-3 text-slate-800">{idx + 1}</td>
                                        <td className="px-6 py-3 text-slate-700">{u.email}</td>
                                        <td className="px-6 py-3 text-slate-700">{u.fullName || "—"}</td>
                                        <td className="px-6 py-3">
                                            <span
                                                className={`px-2 py-1 rounded-full text-xs font-medium ${u.role === "ADMIN"
                                                    ? "bg-slate-200 text-slate-800"
                                                    : u.role === "DOCTOR" || u.role === "STAFF"
                                                        ? "bg-[#001f5f]/10 text-[#001f5f]"
                                                        : "bg-slate-100 text-slate-700"
                                                    }`}
                                            >
                                                {u.role || "USER"}
                                            </span>
                                        </td>
                                        <td className="px-6 py-3 text-right flex justify-end gap-2">
                                            <button
                                                onClick={() => navigate(`/admin/edit-user/${u.id}`)}
                                                className="px-3 py-1.5 rounded-xl border border-slate-400 border-2 cursor-pointer text-slate-700 hover:bg-slate-100 flex items-center gap-1 text-xs transition"
                                            >
                                                <FiEdit2 size={14} /> Chi tiết/Sửa
                                            </button>
                                            <button
                                                onClick={() => handleDelete(u.id)}
                                                className="px-3 py-1.5 rounded-xl border border-slate-500 border-2 cursor-pointer text-slate-800 hover:bg-slate-200 flex items-center gap-1 text-xs transition"
                                            >
                                                <FiTrash2 size={14} /> Xóa
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            <ActionModal
                isOpen={deleteTargetId !== null}
                title="Xác nhận xóa người dùng"
                message="Thao tác này không thể hoàn tác. Bạn có chắc chắn muốn xóa người dùng này?"
                tone="warning"
                confirmText="Xóa"
                cancelText="Hủy"
                showCancel
                loading={deleting}
                onConfirm={handleConfirmDelete}
                onClose={() => {
                    if (deleting) return;
                    setDeleteTargetId(null);
                }}
                closeOnBackdrop={!deleting}
            />

            <ActionModal
                isOpen={noticeModal.isOpen}
                title={noticeModal.title}
                message={noticeModal.message}
                tone={noticeModal.tone}
                confirmText="Đã hiểu"
                onConfirm={() => setNoticeModal((prev) => ({ ...prev, isOpen: false }))}
                onClose={() => setNoticeModal((prev) => ({ ...prev, isOpen: false }))}
            />
        </div>
    );
}

export default AdminUserManagement;