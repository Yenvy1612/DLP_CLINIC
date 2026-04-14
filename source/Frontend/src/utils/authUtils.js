const AUTH_KEY = "auth";
const LEGACY_LOGGED_IN_KEY = "isLoggedIn";
export const AUTH_CHANGED_EVENT = "auth-changed";

const ROLE_HOME_MAP = {
    ADMIN: "/admin/dashboard",
    DOCTOR: "/doctor/schedule",
    PATIENT: "/patient/book",
    GUEST: "/",
};

const ROLE_PROFILE_MAP = {
    ADMIN: "/admin/profile",
    DOCTOR: "/doctor/profile",
    PATIENT: "/patient/profile",
    GUEST: "/login",
};

const ROLE_EDIT_PROFILE_MAP = {
    ADMIN: "/admin/edit",
    DOCTOR: "/doctor/edit",
    PATIENT: "/patient/edit",
    GUEST: "/login",
};

const ROLE_LABEL_MAP = {
    ADMIN: "Quản trị viên",
    DOCTOR: "Bác sĩ",
    PATIENT: "Bệnh nhân",
    GUEST: "Khách",
};

function dispatchAuthChanged() {
    if (typeof window !== "undefined") {
        window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
    }
}

function sanitizeAuthPayload(authPayload) {
    if (!authPayload || typeof authPayload !== "object") {
        return null;
    }

    const resolvedRole = resolveUserRole(authPayload);
    const normalizedRoles = Array.isArray(authPayload.roles)
        ? authPayload.roles.map((role) => String(role).toUpperCase()).filter(Boolean)
        : [];

    const roles = normalizedRoles.length > 0
        ? normalizedRoles
        : (resolvedRole ? [String(resolvedRole).toUpperCase()] : []);

    const idValue = authPayload.id;
    const normalizedId = Number.isFinite(Number(idValue)) ? Number(idValue) : null;

    return {
        id: normalizedId,
        role: resolvedRole || null,
        originalRole: resolvedRole || null,
        roles,
        enabled: typeof authPayload.enabled === "boolean" ? authPayload.enabled : true,
    };
}

function removeLegacyLocalAuth() {
    if (typeof window === "undefined") {
        return;
    }

    localStorage.removeItem(AUTH_KEY);
    localStorage.removeItem(LEGACY_LOGGED_IN_KEY);
}

export function getCurrentUser() {
    const sessionAuth = sessionStorage.getItem(AUTH_KEY);
    if (sessionAuth) {
        try {
            const parsed = JSON.parse(sessionAuth);
            const sanitized = sanitizeAuthPayload(parsed);
            if (!sanitized) {
                sessionStorage.removeItem(AUTH_KEY);
                return null;
            }
            return sanitized;
        } catch {
            sessionStorage.removeItem(AUTH_KEY);
            return null;
        }
    }

    const legacyAuth = localStorage.getItem(AUTH_KEY);
    if (!legacyAuth) return null;

    try {
        const parsedLegacy = JSON.parse(legacyAuth);
        const sanitized = sanitizeAuthPayload(parsedLegacy);
        if (!sanitized) {
            removeLegacyLocalAuth();
            return null;
        }

        sessionStorage.setItem(AUTH_KEY, JSON.stringify(sanitized));
        removeLegacyLocalAuth();
        return sanitized;
    } catch {
        removeLegacyLocalAuth();
        return null;
    }
}

export function resolveUserRole(user) {
    const resolvedRole = user?.originalRole || user?.roles?.[0] || user?.role;
    return resolvedRole ? String(resolvedRole).toUpperCase() : null;
}

export function setCurrentUser(authPayload) {
    const sanitized = sanitizeAuthPayload(authPayload);
    if (!sanitized) {
        return;
    }

    sessionStorage.setItem(AUTH_KEY, JSON.stringify(sanitized));
    removeLegacyLocalAuth();
    dispatchAuthChanged();
}

export function clearCurrentUser() {
    sessionStorage.removeItem(AUTH_KEY);
    removeLegacyLocalAuth();
    dispatchAuthChanged();
}

export function isLoggedIn() {
    return !!getCurrentUser();
}

export function getUserRole() {
    return resolveUserRole(getCurrentUser());
}

export function getUserId() {
    const user = getCurrentUser();
    return user?.id || null;
}

export function getRoleHomePath(role) {
    if (!role) {
        return ROLE_HOME_MAP.GUEST;
    }
    return ROLE_HOME_MAP[String(role).toUpperCase()] || ROLE_HOME_MAP.GUEST;
}

export function getRoleProfilePath(role) {
    if (!role) {
        return ROLE_PROFILE_MAP.GUEST;
    }
    return ROLE_PROFILE_MAP[String(role).toUpperCase()] || ROLE_PROFILE_MAP.GUEST;
}

export function getRoleEditPath(role) {
    if (!role) {
        return ROLE_EDIT_PROFILE_MAP.GUEST;
    }
    return ROLE_EDIT_PROFILE_MAP[String(role).toUpperCase()] || ROLE_EDIT_PROFILE_MAP.GUEST;
}

export function getRoleLabel(role) {
    if (!role) {
        return ROLE_LABEL_MAP.GUEST;
    }
    return ROLE_LABEL_MAP[String(role).toUpperCase()] || ROLE_LABEL_MAP.GUEST;
}