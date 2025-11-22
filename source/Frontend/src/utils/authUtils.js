export function isLoggedIn() {
    return !!localStorage.getItem("auth");
}

export function logout() {
    localStorage.removeItem("auth");
    window.location.href = "/";
}

export function getCurrentUser() {
    const auth = localStorage.getItem("auth");
    if (!auth) return null;
    try {
        return JSON.parse(auth);
    } 
    catch {
        return null;
    }
}

const user = getCurrentUser();

export function getUserRole() {
    return user?.originalRole?.toUpperCase() || null;
}

export function getUserId() {
    return user?.id  || null;
}