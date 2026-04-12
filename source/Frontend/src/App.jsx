
import { RouterProvider } from "react-router-dom";
import { useEffect } from "react";
import router from "./routes";
import './assets/styles/index.css';
import { authService } from "./api";
import { clearCurrentUser, getCurrentUser, resolveUserRole, setCurrentUser } from "./utils/authUtils";

function AuthSessionSync() {
  useEffect(() => {
    let alive = true;

    const sync = async () => {
      try {
        const profile = await authService.me();
        if (!alive || !profile) return;

        const current = getCurrentUser() || {};
        setCurrentUser({
          ...current,
          ...profile,
          originalRole: resolveUserRole(profile),
        });
      } catch {
        if (!alive) return;
        clearCurrentUser();
      }
    };

    sync();

    return () => {
      alive = false;
    };
  }, []);

  return null;
}

export default function App() {
  return (
    <>
      <AuthSessionSync />
      <RouterProvider router={router} />
    </>
  );
}
