const AUTH_KEY = "auth";
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

export function getCurrentUser() {
    const auth = localStorage.getItem(AUTH_KEY);
    if (!auth) return null;

    try {
        return JSON.parse(auth);
    } catch {
        return null;
    }
}

export function resolveUserRole(user) {
    const resolvedRole = user?.originalRole || user?.roles?.[0] || user?.role;
    return resolvedRole ? String(resolvedRole).toUpperCase() : null;
}

export function setCurrentUser(authPayload) {
    if (!authPayload) {
        return;
    }
    localStorage.setItem(AUTH_KEY, JSON.stringify(authPayload));
    dispatchAuthChanged();
}

export function clearCurrentUser() {
    localStorage.removeItem(AUTH_KEY);
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