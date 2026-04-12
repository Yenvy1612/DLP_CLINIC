import { useEffect, useRef, useState } from "react";
import { FiEye, FiEdit2, FiTrash2 } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { FiSearch } from "react-icons/fi";
import { serviceService } from "../../../api";
import ActionModal from "../../../components/ActionModal";
import { animatePageEnter } from "../../../utils/animeAnimations";

function AdminServiceManagement() {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const pageRef = useRef(null);

    // State cho search
    const [searchParams, setSearchParams] = useState({
        name: '',
        minPrice: '',
        maxPrice: ''
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
                const response = await serviceService.getAll();
                setServices(response);
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
            const searchResults = await serviceService.search(searchParams);
            setServices(searchResults);
        } catch (err) {
            console.error("Error searching services:", err);
        } finally {
            setLoading(false);
        }
    };

    // Reset search
    const handleReset = async () => {
        setSearchParams({
            name: '',
            minPrice: '',
            maxPrice: ''
        });
        setLoading(true);
        try {
            const response = await serviceService.getAll();
            setServices(response);
        }
        catch (err) {
            console.error("Error fetching services:", err);
        }
        finally {
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
            await serviceService.remove(deleteTargetId);
            setServices((prev) => prev.filter((service) => service.id !== deleteTargetId));
            setNoticeModal({
                isOpen: true,
                title: "Đã xóa dịch vụ",
                message: "Dịch vụ đã được xóa thành công.",
                tone: "success",
            });
        }
        catch (error) {
            setNoticeModal({
                isOpen: true,
                title: "Xóa thất bại",
                message: error?.message || "Không thể xóa dịch vụ. Vui lòng thử lại.",
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
                    <h1 className="text-3xl font-bold text-[#00278D] mb-6 p-2 bg-white rounded-xl shadow-xl">Danh sách dịch vụ</h1>
                    <button onClick={() => navigate("/admin/add-service")} className="flex items-center text-sm mb-6 bg-sky-500 text-white p-2 rounded-xl hover:shadow-xl hover:bg-sky-700 transition duration-300 cursor-pointer"> + Thêm dịch vụ</button>
                </div>

                {/* Thanh tìm kiếm */}
                <div className="bg-white rounded-2xl shadow-xl p-6 mb-6">
                    <div className="flex items-center gap-2 mb-4">
                        <FiSearch className="text-[#00278D]" size={20} />
                        <h2 className="text-lg font-semibold text-[#00278D]">Tìm kiếm dịch vụ</h2>
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        {/* Tên dịch vụ */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Tên dịch vụ
                            </label>
                            <input
                                type="text"
                                name="name"
                                value={searchParams.name}
                                onChange={handleSearchChange}
                                placeholder="Nhập tên dịch vụ..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-sky-500 focus:border-transparent"
                            />
                        </div>

                        {/* Giá tối thiểu */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Giá tối thiểu (₫)
                            </label>
                            <input
                                type="number"
                                name="minPrice"
                                value={searchParams.minPrice}
                                onChange={handleSearchChange}
                                placeholder="0"
                                min="0"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-sky-500 focus:border-transparent"
                            />
                        </div>

                        {/* Giá tối đa */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Giá tối đa (₫)
                            </label>
                            <input
                                type="number"
                                name="maxPrice"
                                value={searchParams.maxPrice}
                                onChange={handleSearchChange}
                                placeholder="Không giới hạn"
                                min="0"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-sky-500 focus:border-transparent"
                            />
                        </div>
                    </div>

                    {/* Buttons */}
                    <div className="flex gap-3">
                        <button
                            onClick={handleSearch}
                            className="bg-[#00278D] text-white px-6 py-2 rounded-lg hover:bg-sky-700 transition-colors font-medium flex items-center gap-2"
                        >
                            <FiSearch size={16} />
                            Tìm kiếm
                        </button>
                        <button
                            onClick={handleReset}
                            className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition-colors font-medium"
                        >
                            Đặt lại
                        </button>
                    </div>
                </div>

                <div className="bg-white border border-slate-200 rounded-3xl shadow-xl overflow-hidden">
                    <table className="min-w-full text-sm">
                        <thead className="bg-sky-50 text-[#00278D]">
                            <tr>
                                <th className="px-6 py-3 text-left font-semibold">STT</th>
                                <th className="px-6 py-3 text-left font-semibold">Tên dịch vụ</th>
                                <th className="px-6 py-3 text-left font-semibold">Giá dịch vụ</th>
                                <th className="px-6 py-3 text-center font-semibold">Thao tác</th>
                            </tr>
                        </thead>

                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="5" className="px-6 py-6 text-center text-slate-500">
                                        Đang tải danh sách dịch vụ...
                                    </td>
                                </tr>
                            ) : services.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="px-6 py-6 text-center text-slate-500">
                                        Không có dịch vụ nào.
                                    </td>
                                </tr>
                            ) : (
                                services.map((s, idx) => (
                                    <tr
                                        key={s.id}
                                        className={`hover:bg-sky-50 transition ${idx % 2 === 0 ? "bg-white" : "bg-slate-50/60"
                                            }`}
                                    >
                                        <td className="px-6 py-3 text-slate-800">{idx + 1}</td>
                                        <td className="px-6 py-3 text-slate-700">{s.name || "—"}</td>
                                        <td className="px-6 py-3 text-left text-slate-700">{s.price.toLocaleString("vi-VN") + " ₫"}</td>
                                        <td className="px-6 py-3 text-left flex justify-center gap-2">
                                            <button
                                                onClick={() => navigate(`/show-service/${s.id}`)}
                                                className="px-3 py-1.5 rounded-xl border border-sky-300 border-2 cursor-pointer text-[#00278D] hover:bg-sky-50 flex items-center gap-1 text-xs transition"
                                            >
                                                <FiEye size={14} /> Xem
                                            </button>
                                            <button
                                                onClick={() => navigate(`/admin/edit-service/${s.id}`)}
                                                className="px-3 py-1.5 rounded-xl border border-emerald-300 border-2 cursor-pointer text-emerald-600 hover:bg-emerald-50 flex items-center gap-1 text-xs transition"
                                            >
                                                <FiEdit2 size={14} /> Sửa
                                            </button>
                                            <button
                                                onClick={() => handleDelete(s.id)}
                                                className="px-3 py-1.5 rounded-xl border border-red-300 border-2 cursor-pointer text-red-500 hover:bg-red-50 flex items-center gap-1 text-xs transition"
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
                title="Xác nhận xóa dịch vụ"
                message="Thao tác này không thể hoàn tác. Bạn có chắc chắn muốn xóa dịch vụ này?"
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

export default AdminServiceManagement;