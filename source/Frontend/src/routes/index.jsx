import { createBrowserRouter, Navigate, Outlet } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import Home from "../pages/home/Home";
import NotFound from "../pages/NotFound";
import Login from "../pages/auth/Login";
import Services from "../pages/Services";
import Booking from "../pages/patient/Booking";
import Profile from "../pages/Profile";
import EditProfile from "../pages/EditProfile";
import Cart from "../pages/patient/Cart";
import { getUserRole } from "../utils/authUtils";
import HistoryPage from "../pages/patient/HistoryPage";
import EditAppointment from "../pages/patient/EditAppointment";
import Register from "../pages/auth/Register";
import Schedule from "../pages/doctor/Schedule";
import Statistic from "../pages/doctor/Statistic";
import Patients from "../pages/doctor/Patients";
import Dashboard from "../pages/admin/dashboard/Dashboard";
import AdminUserManagement from "../pages/admin/user-management/Users";
import AdminEditProfile from "../pages/admin/user-management/UpdateUser";
import AdminShowProfile from "../pages/admin/user-management/ShowUser";
import AdminServiceManagement from "../pages/admin/service-management/Services";
import AddUser from "../pages/admin/user-management/AddUser";
import AdminShowService from "../pages/admin/service-management/showService";
import AdminEditService from "../pages/admin/service-management/UpdateService";
import AddService from "../pages/admin/service-management/AddService";
import AdminRoomManagement from "../pages/admin/room-management/Rooms";
import AddRoom from "../pages/admin/room-management/AddRoom";
import EditRoom from "../pages/admin/room-management/UpdateRoom";
import AdminAppointmentManagement from "../pages/admin/appointment-management/Appointments";
import About from "../pages/About";
import Instruction from "../pages/Instruction";
import FAQ from "../pages/FAQ";
import Contact from "../pages/Contact";

function RequiredRole({ allowed }) {
    const role = getUserRole();
    if (!role) return <Navigate to="/login" replace />
    if (allowed != role) return <Navigate to="/403" replace />
    return <Outlet />
}

const router = createBrowserRouter([
    {
        element: <MainLayout />,
        children: [
            { path: "/", element: <Home /> },
            { path: "/services", element: <Services /> },
            { path: "/about", element: <About /> },
            { path: "/instruction", element: <Instruction /> },
            { path: "/faq", element: <FAQ /> },
            { path: "/contact", element: <Contact /> },
            //   { path: "/contact", element: <Contact /> },
            {
                element: <RequiredRole allowed={"PATIENT"} />,
                children: [
                    {
                        path: "/patient", element: <Home />,
                        children: [
                            { path: "book", element: <Booking /> },
                            { path: "profile", element: <Profile /> },
                            { path: "edit", element: <EditProfile /> },
                            { path: "cart", element: <Cart />, },
                            { path: "history", element: <HistoryPage /> },
                            { path: "edit-appointment/:id", element: <EditAppointment /> }
                        ],
                    }
                ]
            },
            {
                element: <RequiredRole allowed={"DOCTOR"} />,
                children: [
                    {
                        path: "/doctor", element: <Home />,
                        children: [
                            { path: "profile", element: <Profile /> },
                            { path: "edit", element: <EditProfile /> },
                            { path: "schedule", element: <Schedule /> },
                            { path: "reports", element: <Statistic /> },
                            { path: "patients", element: <Patients /> },
                        ]
                    }
                ]
            },
            {
                element: <RequiredRole allowed={"ADMIN"} />,
                children: [
                    {
                        path: "admin", element: <Home />,
                        children: [
                            { path: "dashboard", element: <Dashboard /> },
                            { path: "users", element: <AdminUserManagement /> },
                            { path: "edit-user/:id", element: <AdminEditProfile /> },
                            { path: "show-user/:id", element: <AdminShowProfile /> },
                            { path: "add-user", element: <AddUser /> },
                            { path: "services", element: <AdminServiceManagement /> },
                            { path: "edit-service/:id", element: <AdminEditService /> },
                            { path: "add-service", element: <AddService /> },
                            { path: "rooms", element: <AdminRoomManagement /> },
                            { path: "add-room", element: <AddRoom /> },
                            { path: "edit-room/:id", element: <EditRoom /> },
                            { path: "appointments", element: <AdminAppointmentManagement /> }
                        ]
                    }
                ]
            },
            { path: "show-service/:id", element: <AdminShowService /> },
        ],
    },
    { path: "/login", element: <Login /> },
    { path: "/register", element: <Register /> },
    { path: "*", element: <NotFound /> },
]);

export default router;
