import { useEffect, useMemo, useRef, useState } from "react";
import { FiChevronDown } from "react-icons/fi";

const toText = (value) => (value === null || value === undefined ? "" : String(value));

const cx = (...classes) => classes.filter(Boolean).join(" ");

export default function CustomDropdown({
    name,
    value,
    options = [],
    placeholder = "Chọn một giá trị",
    onChange,
    onValueChange,
    disabled = false,
    required = false,
    className = "",
    buttonClassName = "",
    menuClassName = "",
}) {
    const [open, setOpen] = useState(false);
    const rootRef = useRef(null);

    const selectedOption = useMemo(() => {
        return options.find((option) => toText(option?.value) === toText(value));
    }, [options, value]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (!rootRef.current?.contains(event.target)) {
                setOpen(false);
            }
        };

        const handleEscape = (event) => {
            if (event.key === "Escape") {
                setOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        document.addEventListener("keydown", handleEscape);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
            document.removeEventListener("keydown", handleEscape);
        };
    }, []);

    const handleSelect = (nextValue) => {
        if (disabled) return;

        const eventLike = { target: { name, value: nextValue } };
        onChange?.(eventLike);
        onValueChange?.(nextValue);
        setOpen(false);
    };

    return (
        <div ref={rootRef} className={cx("relative", className)}>
            {name ? <input type="hidden" name={name} value={toText(value)} required={required} /> : null}

            <button
                type="button"
                disabled={disabled}
                onClick={() => setOpen((prev) => !prev)}
                className={cx(
                    "w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-left text-slate-800 shadow-sm",
                    "transition hover:border-[#001f5f] focus:outline-none focus:ring-2 focus:ring-[#001f5f]/30",
                    disabled && "cursor-not-allowed bg-slate-100 text-slate-400 hover:border-slate-300",
                    buttonClassName,
                )}
                aria-haspopup="listbox"
                aria-expanded={open}
            >
                <span className="block truncate pr-8 text-sm font-medium">
                    {selectedOption?.label || placeholder}
                </span>
                <FiChevronDown
                    className={cx(
                        "pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-lg text-slate-500 transition-transform",
                        open && "rotate-180",
                    )}
                    aria-hidden="true"
                />
            </button>

            {open ? (
                <ul
                    role="listbox"
                    className={cx(
                        "absolute z-[70] mt-2 max-h-64 w-full overflow-auto rounded-xl border border-slate-200 bg-white p-1.5 shadow-lg",
                        menuClassName,
                    )}
                >
                    {options.map((option) => {
                        const optionValue = option?.value;
                        const selected = toText(optionValue) === toText(value);
                        const optionDisabled = !!option?.disabled;

                        return (
                            <li key={toText(optionValue)} role="option" aria-selected={selected}>
                                <button
                                    type="button"
                                    disabled={optionDisabled}
                                    onClick={() => handleSelect(optionValue)}
                                    className={cx(
                                        "w-full rounded-lg px-3 py-2 text-left text-sm transition",
                                        selected
                                            ? "bg-[#001f5f] text-white"
                                            : "text-slate-700 hover:bg-slate-100",
                                        optionDisabled && "cursor-not-allowed opacity-50",
                                    )}
                                >
                                    {option?.label}
                                </button>
                            </li>
                        );
                    })}
                </ul>
            ) : null}
        </div>
    );
}
