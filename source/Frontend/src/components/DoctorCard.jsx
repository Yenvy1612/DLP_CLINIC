export default function DoctorCard({ doctor, className = "" }) {
    const years = Number(doctor?.yearsExperience) || 0;
    const specialty = doctor?.specialty || "Đang cập nhật";
    const experienceLabel = years > 0 ? `${years} năm kinh nghiệm` : "Kinh nghiệm đang cập nhật";

    return (
        <article
            className={`group flex min-w-[220px] flex-col items-center rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition-shadow duration-300 hover:shadow-lg ${className}`}
        >
            <div className="mb-4 h-24 w-24 overflow-hidden rounded-full border-4 border-slate-100 shadow-md">
                <img
                    src={`https://api.dicebear.com/7.x/notionists/svg?seed=${doctor?.fullName || doctor?.id || "doctor"}`}
                    alt={doctor?.fullName || "Bác sĩ"}
                    className="h-full w-full bg-slate-50 object-cover"
                />
            </div>

            <h3 className="line-clamp-1 text-center text-base font-bold text-slate-800">
                {doctor?.fullName || "Bác sĩ"}
            </h3>

            <p className="mt-2 line-clamp-2 min-h-[40px] text-center text-sm font-medium text-[var(--brand-600)]">
                {specialty}
            </p>

            <p className="mt-2 text-center text-xs text-slate-500">
                {experienceLabel}
            </p>
        </article>
    );
}
