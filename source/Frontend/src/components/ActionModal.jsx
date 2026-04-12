import { useEffect, useRef, useState } from "react";
import { FiAlertTriangle, FiCheckCircle, FiInfo } from "react-icons/fi";
import { animateModalIn, animateModalOut } from "../utils/animeAnimations";

const toneMap = {
    info: {
        icon: FiInfo,
        iconClassName: "text-[#00278D]",
        confirmClassName: "bg-[#00278D] hover:bg-[#001f5f] text-white",
    },
    warning: {
        icon: FiAlertTriangle,
        iconClassName: "text-slate-700",
        confirmClassName: "bg-slate-800 hover:bg-slate-900 text-white",
    },
    success: {
        icon: FiCheckCircle,
        iconClassName: "text-[#001f5f]",
        confirmClassName: "bg-[#001f5f] hover:bg-[#001744] text-white",
    },
};

export default function ActionModal({
    isOpen,
    title,
    message,
    tone = "info",
    confirmText = "Đồng ý",
    cancelText = "Hủy",
    showCancel = false,
    loading = false,
    onConfirm,
    onClose,
    closeOnBackdrop = true,
}) {
    const [shouldRender, setShouldRender] = useState(isOpen);
    const backdropRef = useRef(null);
    const panelRef = useRef(null);

    const resolvedTone = toneMap[tone] || toneMap.info;
    const Icon = resolvedTone.icon;

    useEffect(() => {
        if (isOpen) {
            setShouldRender(true);
        }
    }, [isOpen]);

    useEffect(() => {
        if (!shouldRender) return undefined;

        if (isOpen) {
            const animation = animateModalIn({
                backdrop: backdropRef.current,
                panel: panelRef.current,
            });

            return () => {
                animation?.pause?.();
            };
        }

        const outAnimation = animateModalOut({
            backdrop: backdropRef.current,
            panel: panelRef.current,
            onComplete: () => setShouldRender(false),
        });

        return () => {
            outAnimation?.backdropAnimation?.pause?.();
            outAnimation?.panelAnimation?.pause?.();
        };
    }, [isOpen, shouldRender]);

    const handleBackdropClick = () => {
        if (!closeOnBackdrop || loading) return;
        onClose?.();
    };

    if (!shouldRender) return null;

    return (
        <div
            ref={backdropRef}
            style={{ opacity: 0 }}
            className="fixed inset-0 z-[120] bg-slate-950/45 backdrop-blur-[1px] flex items-center justify-center p-4"
            onClick={handleBackdropClick}
        >
            <div
                ref={panelRef}
                style={{ opacity: 0 }}
                className="w-full max-w-md bg-white border border-slate-200 shadow-2xl rounded-2xl p-5"
                onClick={(event) => event.stopPropagation()}
                role="dialog"
                aria-modal="true"
            >
                <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center flex-shrink-0">
                        <Icon className={`text-lg ${resolvedTone.iconClassName}`} />
                    </div>

                    <div className="min-w-0">
                        <h3 className="text-lg font-bold text-slate-900">{title}</h3>
                        <p className="text-sm text-slate-600 mt-1 whitespace-pre-line">{message}</p>
                    </div>
                </div>

                <div className="mt-5 flex justify-end gap-2">
                    {showCancel && (
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                            className="px-4 py-2 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-100 disabled:opacity-60 transition cursor-pointer"
                        >
                            {cancelText}
                        </button>
                    )}
                    <button
                        type="button"
                        onClick={onConfirm}
                        disabled={loading}
                        className={`px-4 py-2 rounded-lg disabled:opacity-60 transition cursor-pointer ${resolvedTone.confirmClassName}`}
                    >
                        {loading ? "Đang xử lý..." : confirmText}
                    </button>
                </div>
            </div>
        </div>
    );
}