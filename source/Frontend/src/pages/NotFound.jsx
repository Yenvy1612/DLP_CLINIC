function NotFound() {
    return (
        <section className="mx-auto flex min-h-[calc(100vh-8rem)] w-full max-w-7xl items-center justify-center px-4 py-12">
            <div className="w-full max-w-xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-lg">
                <p className="text-sm font-semibold uppercase tracking-[0.16em] text-slate-500">404</p>
                <h1 className="mt-2 text-3xl font-bold text-[var(--brand-navy)]">Trang không tồn tại</h1>
                <p className="mt-3 text-slate-600">Đường dẫn bạn truy cập không hợp lệ hoặc đã được thay đổi.</p>
            </div>
        </section>
    );
}

export default NotFound;