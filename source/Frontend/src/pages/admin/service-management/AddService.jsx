import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { serviceService, specialtyService } from "../../../api";
import CustomDropdown from "../../../components/CustomDropdown";
import ActionModal from "../../../components/ActionModal";
import { animatePageEnter } from "../../../utils/animeAnimations";


function AddService() {
    const navigate = useNavigate();
    const pageRef = useRef(null);

    const [form, setForm] = useState({
        name: "",
        price: 0,
        description: "",
        specialtyId: "",
        active: true,
    });

    const [error, setError] = useState("");
    const [specialties, setSpecialties] = useState([]);
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [creating, setCreating] = useState(false);
    const [pendingServiceData, setPendingServiceData] = useState(null);
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
        (async () => {
            try {
                const data = await specialtyService.getAll();
                setSpecialties(Array.isArray(data) ? data : []);
            }
            catch (e) {
                setError(e?.message || "Không tải được danh sách chuyên khoa");
            }
        })();
    }, []);

    const onChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!form.specialtyId) {
            setError("Vui lòng chọn chuyên khoa");
            return;
        }

        const data = {
            name: form.name,
            price: Number(form.price),
            description: form.description,
            specialtyId: Number(form.specialtyId),
            active: form.active,
        };

        setPendingServiceData(data);
        setConfirmOpen(true);
    };

    const handleConfirmCreate = async () => {
        if (!pendingServiceData) return;

        setCreating(true);
        setError("");
        try {
            await serviceService.create(pendingServiceData);
            setConfirmOpen(false);
            setResultModal({
                isOpen: true,
                title: "Thêm thành công",
                message: "Dịch vụ mới đã được tạo.",
                tone: "success",
                nextAction: "back-services",
            });
        } catch (e) {
            setError(e?.message || "Thêm thất bại");
            setConfirmOpen(false);
        } finally {
            setCreating(false);
            setPendingServiceData(null);
        }
    };

    const closeConfirmModal = () => {
        if (creating) return;
        setConfirmOpen(false);
        setPendingServiceData(null);
    };

    const closeResultModal = () => {
        const { nextAction } = resultModal;
        setResultModal((prev) => ({ ...prev, isOpen: false, nextAction: "none" }));

        if (nextAction === "back-services") {
            window.location.assign("/admin/services");
        }
    };

    return (
        <div ref={pageRef} className="bg-slate-100">
            <section className="bg-[var(--surface)] min-h-screen p-10">
                <div className="w-[50vw] mx-auto p-6 rounded-4xl bg-white shadow-2xl">
                    <h1 className="w-full p-3 rounded-xl text-3xl font-semibold mb-3 text-[#00278D]">
                        Thêm dịch vụ
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
                            <label className="block text-slate-800 text-sm mb-1">Tên dịch vụ</label>
                            <input
                                name="name" value={form.name} onChange={onChange} required
                                className="w-full text-slate-800 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Giá dịch vụ</label>
                            <input
                                type="number"
                                min="0"
                                step="1000"
                                name="price" value={form.price} onChange={onChange} required
                                className="w-full text-slate-800 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Chuyên khoa dịch vụ</label>
                            <CustomDropdown
                                name="specialtyId"
                                value={form.specialtyId}
                                onChange={onChange}
                                required
                                options={specialties.map((specialty) => ({
                                    value: String(specialty.id),
                                    label: `${specialty.name} (${specialty.code})`,
                                }))}
                                placeholder="-- Chọn chuyên khoa --"
                            />
                        </div>

                        <div>
                            <label className="block text-slate-800 text-sm mb-1">Thông tin dịch vụ</label>
                            <textarea
                                name="description" value={form.description} onChange={onChange} required
                                className="w-full text-slate-800 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#00278D] focus:border-transparent transition duration-200">
                            </textarea>
                        </div>

                        <div className="pt-2 flex gap-2 col-span-2">
                            <button
                                type="submit"
                                disabled={creating}
                                className="px-4 py-2 rounded-xl bg-[#00278D] hover:bg-[#001f5f] text-white disabled:opacity-60 transition duration-400 cursor-pointer"
                            >
                                {creating ? "Đang xử lý..." : "Thêm"}
                            </button>

                            <button
                                type="button"
                                onClick={() => navigate("/admin/services")}
                                className="px-4 py-2 cursor-pointer rounded-xl bg-slate-700 text-white hover:bg-slate-800 transition duration-400"
                            >
                                Huỷ
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <ActionModal
                isOpen={confirmOpen}
                title="Xác nhận thêm dịch vụ"
                message="Dịch vụ mới sẽ được lưu vào hệ thống ngay sau khi xác nhận."
                tone="warning"
                confirmText="Xác nhận"
                cancelText="Hủy"
                showCancel
                loading={creating}
                onConfirm={handleConfirmCreate}
                onClose={closeConfirmModal}
                closeOnBackdrop={!creating}
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

export default AddService;