import { useEffect, useRef, useState } from "react";
import { appointmentService } from "../../../api";
import CustomDropdown from "../../../components/CustomDropdown";
import AppointmentLine from "./AppointmentLine";
import { animatePageEnter } from "../../../utils/animeAnimations";

function AdminAppointmentManagement() {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const pageRef = useRef(null);

    // State cho filter
    const [filters, setFilters] = useState({
        doctorName: '',
        patientName: '',
        appointmentDate: '',
        status: ''
    });

    useEffect(() => {
        const animation = animatePageEnter(pageRef.current);
        return () => {
            animation?.pause?.();
        };
    }, []);

    // Lấy danh sách appointments
    useEffect(() => {
        const fetchAppointments = async () => {
            try {
                const response = await appointmentService.getAll();
                setAppointments(response);
            }
            catch (err) {
                console.error("Error fetching users:", err);
            }
            finally {
                setLoading(false);
            }
        };
        fetchAppointments();

    }, []);

    // Xử lý thay đổi filter
    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Xử lý tìm kiếm
    const handleSearch = async () => {
        setLoading(true);
        try {
            const filteredData = await appointmentService.filter(filters);
            setAppointments(filteredData);
        } 
        catch (err) {
            console.error("Error filtering appointments:", err);
        } 
        finally {
            setLoading(false);
        }
    };

    // Reset filter
    const handleReset = async () => {
        setFilters({
            doctorName: '',
            patientName: '',
            appointmentDate: '',
            status: ''
        });
        setLoading(true);
        try {
            const response = await appointmentService.getAll();
            setAppointments(response);
        } 
        catch (err) {
            console.error("Error fetching appointments:", err);
        } 
        finally {
            setLoading(false);
        }
    };
    return (
        <div
            ref={pageRef}
            className="min-h-screen bg-[var(--surface)] px-6 py-8"
        >
            <div className="max-w-9xl mx-auto">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold text-[#00278D] mb-6 p-2 bg-white rounded-xl shadow-xl">Danh sách lịch hẹn</h1>
                </div>

                {/* Thanh tìm kiếm / Filter */}
                <div className="bg-white rounded-2xl shadow-xl p-6 mb-6">
                    <h2 className="text-lg font-semibold text-[#00278D] mb-4">Tìm kiếm lịch hẹn</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {/* Tên bác sĩ */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Tên bác sĩ
                            </label>
                            <input
                                type="text"
                                name="doctorName"
                                value={filters.doctorName}
                                onChange={handleFilterChange}
                                placeholder="Nhập tên bác sĩ..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#00278D] focus:border-transparent"
                            />
                        </div>

                        {/* Tên bệnh nhân */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Tên bệnh nhân
                            </label>
                            <input
                                type="text"
                                name="patientName"
                                value={filters.patientName}
                                onChange={handleFilterChange}
                                placeholder="Nhập tên bệnh nhân..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#00278D] focus:border-transparent"
                            />
                        </div>

                        {/* Ngày khám */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Ngày khám
                            </label>
                            <input
                                type="date"
                                name="appointmentDate"
                                value={filters.appointmentDate}
                                onChange={handleFilterChange}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#00278D] focus:border-transparent"
                            />
                        </div>

                        {/* Trạng thái */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Trạng thái
                            </label>
                            <CustomDropdown
                                name="status"
                                value={filters.status}
                                onChange={handleFilterChange}
                                options={[
                                    { value: "", label: "Tất cả trạng thái" },
                                    { value: "PENDING", label: "Chờ xác nhận" },
                                    { value: "DONE", label: "Hoàn thành" },
                                    { value: "CANCELLED", label: "Đã hủy" },
                                ]}
                                placeholder="Tất cả trạng thái"
                            />
                        </div>

                        {/* Buttons */}
                        <div className="flex items-end gap-2">
                            <button
                                onClick={handleSearch}
                                className="flex-1 bg-[#00278D] text-white px-6 py-2 rounded-lg hover:bg-[#001f5f] transition-colors font-medium"
                            >
                                Tìm kiếm
                            </button>
                            <button
                                onClick={handleReset}
                                className="flex-1 bg-slate-700 text-white px-6 py-2 rounded-lg hover:bg-slate-800 transition-colors font-medium"
                            >
                                Đặt lại
                            </button>
                        </div>
                    </div>
                </div>

                <div className="bg-white border border-slate-200 rounded-2xl shadow-xl overflow-hidden">
                    <table className="min-w-full text-sm">
                        <thead className="bg-slate-100 text-[#00278D]">
                            <tr>
                                <th className="px-6 py-3 text-left font-semibold">STT</th>
                                <th className="px-6 py-3 text-left font-semibold">Tên bác sĩ</th>
                                <th className="px-6 py-3 text-left font-semibold">Tên bệnh nhân</th>
                                <th className="px-6 py-3 text-center font-semibold">Dịch vụ</th>
                                <th className="px-6 py-3 text-center font-semibold">Thời gian</th>
                                <th className="px-6 py-3 text-center font-semibold">Ngày tạo</th>
                                <th className="px-6 py-3 text-center font-semibold">Trạng thái</th>
                            </tr>
                        </thead>

                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="7" className="px-6 py-6 text-center text-slate-500">
                                        Đang tải danh sách lịch hẹn...
                                    </td>
                                </tr>
                            ) : appointments.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="px-6 py-6 text-center text-slate-500">
                                        Không có lịch hẹn nào.
                                    </td>
                                </tr>
                            ) : (
                                appointments.map((a, idx) => (
                                    <AppointmentLine a={a} key={idx} idx={idx} />
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default AdminAppointmentManagement;