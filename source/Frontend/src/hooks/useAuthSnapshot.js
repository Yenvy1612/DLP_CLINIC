import { useEffect, useMemo, useState } from "react";
import { AUTH_CHANGED_EVENT, getCurrentUser } from "../utils/authUtils";

const resolveRole = (user) => {
    const role = user?.originalRole || user?.roles?.[0] || user?.role;
    return role ? String(role).toUpperCase() : null;
};

export default function useAuthSnapshot() {
    const [user, setUser] = useState(getCurrentUser());

    useEffect(() => {
        const sync = () => setUser(getCurrentUser());

        window.addEventListener(AUTH_CHANGED_EVENT, sync);
        window.addEventListener("storage", sync);

        return () => {
            window.removeEventListener(AUTH_CHANGED_EVENT, sync);
            window.removeEventListener("storage", sync);
        };
    }, []);

    return useMemo(() => ({
        user,
        userId: user?.id || null,
        role: resolveRole(user),
        isLoggedIn: !!user,
    }), [user]);
}
