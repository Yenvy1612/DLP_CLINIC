import { useNavigate } from "react-router-dom";

function Forbidden() {
    const navigate = useNavigate();

    return (
        <section className="mx-auto flex min-h-[calc(100vh-8rem)] w-full max-w-7xl items-center justify-center px-4 py-12">
            <div className="w-full max-w-xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-lg">
                <p className="text-sm font-semibold uppercase tracking-[0.16em] text-slate-500">403</p>
                <h1 className="mt-2 text-3xl font-bold text-[var(--brand-navy)]">Khong du quyen truy cap</h1>
                <p className="mt-3 text-slate-600">Ban khong co quyen vao trang nay. Vui long quay lai khu vuc duoc cap quyen.</p>
                <button
                    onClick={() => navigate("/")}
                    className="mt-6 rounded-xl bg-[var(--brand-600)] px-5 py-2.5 text-white transition hover:bg-[var(--brand-700)]"
                >
                    Về trang chủ
                </button>
            </div>
        </section>
    );
}

export default Forbidden;
