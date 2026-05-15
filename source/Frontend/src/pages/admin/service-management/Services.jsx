import { useEffect, useRef, useState } from "react";
import { FiEdit2, FiTrash2 } from "react-icons/fi";
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

    // Lấy danh sách dịch vụ
    useEffect(() => {
        const fetchServices = async () => {
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
        fetchServices();
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
            setServices((prev) => prev.filter((item) => item.id !== deleteTargetId));
            setNoticeModal({
                isOpen: true,
                title: "Xóa thành công",
                message: "Dịch vụ đã được xóa khỏi hệ thống.",
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
            className="min-h-screen bg-[var(--surface)] px-4 py-6 sm:px-6 sm:py-8"
        >
            <div className="max-w-6xl mx-auto">
                <div className="mb-6 rounded-3xl border border-slate-200/80 bg-white/90 px-6 py-5 shadow-[0_14px_34px_rgba(15,23,42,0.10)]">
                    <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                        <div>
                            <h1 className="text-3xl font-bold text-[#00278D]">Quản lý dịch vụ</h1>
                            <p className="mt-2 text-sm text-slate-600">
                                Theo dõi danh sách dịch vụ, tìm kiếm nhanh và cập nhật thông tin dịch vụ trong hệ thống.
                            </p>
                        </div>
                        <button
                            onClick={() => navigate("/admin/add-service")}
                            className="inline-flex w-fit items-center gap-2 rounded-xl bg-[var(--brand-600)] px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-[#00278D]/20 transition hover:bg-[var(--brand-700)] hover:shadow-xl"
                        >
                            + Thêm dịch vụ
                        </button>
                    </div>
                </div>

                {/* Thanh tìm kiếm */}
                <div className="mb-6 rounded-3xl border border-slate-200/80 bg-white p-4 shadow-[0_12px_30px_rgba(15,23,42,0.08)] sm:p-6">
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
                                className="w-full px-4 py-2.5 border border-slate-300 rounded-xl text-slate-800 focus:ring-2 focus:ring-[#00278D]/20 focus:border-[#00278D]/40"
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
                                className="w-full px-4 py-2.5 border border-slate-300 rounded-xl text-slate-800 focus:ring-2 focus:ring-[#00278D]/20 focus:border-[#00278D]/40"
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
                                className="w-full px-4 py-2.5 border border-slate-300 rounded-xl text-slate-800 focus:ring-2 focus:ring-[#00278D]/20 focus:border-[#00278D]/40"
                            />
                        </div>
                    </div>

                    {/* Buttons */}
                    <div className="flex flex-wrap gap-3">
                        <button
                            onClick={handleSearch}
                            className="flex w-full items-center justify-center gap-2 rounded-xl bg-[#00278D] px-6 py-2.5 font-medium text-white transition-colors hover:bg-[var(--brand-700)] sm:w-auto"
                        >
                            <FiSearch size={16} />
                            Tìm kiếm
                        </button>
                        <button
                            onClick={handleReset}
                            className="w-full rounded-xl bg-slate-600 px-6 py-2.5 font-medium text-white transition-colors hover:bg-slate-700 sm:w-auto"
                        >
                            Đặt lại
                        </button>
                    </div>
                </div>

                <div className="overflow-x-auto rounded-3xl border border-slate-200/80 bg-white shadow-[0_14px_34px_rgba(15,23,42,0.10)]">
                    <table className="min-w-[860px] w-full text-sm">
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
                                        className={`transition hover:bg-slate-50 ${idx % 2 === 0 ? "bg-white" : "bg-slate-50/60"
                                            }`}
                                    >
                                        <td className="px-6 py-3 text-slate-800">{idx + 1}</td>
                                        <td className="px-6 py-3 text-slate-700">{s.name || "—"}</td>
                                        <td className="px-6 py-3 text-left text-slate-700">{s.price.toLocaleString("vi-VN") + " ₫"}</td>
                                        <td className="px-6 py-3 text-left flex justify-center gap-2">
                                            <button
                                                onClick={() => navigate(`/admin/edit-service/${s.id}`)}
                                                className="px-3 py-1.5 rounded-xl border-2 border-slate-400 cursor-pointer text-slate-700 hover:bg-slate-100 flex items-center gap-1 text-xs transition"
                                            >
                                                <FiEdit2 size={14} /> Chi tiết/Sửa
                                            </button>
                                            <button
                                                onClick={() => handleDelete(s.id)}
                                                className="px-3 py-1.5 rounded-xl border border-rose-300 cursor-pointer text-rose-600 hover:bg-rose-50 flex items-center gap-1 text-xs transition"
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