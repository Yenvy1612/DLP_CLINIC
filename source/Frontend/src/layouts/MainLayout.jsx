import Footer from "../components/Footer";
import Header from "../components/Header";
import Sidebar from "../components/SideBar";
import SidebarProvider from "../contexts/SideBarContext";
import { Outlet } from "react-router-dom";

function MainLayout() {
  return (
    <SidebarProvider>
      <div className="main-layout flex min-h-screen flex-col bg-white">
        <Header />
        <Sidebar />
        <main className="flex-1">
          <Outlet/>
        </main>
        <Footer />
      </div>
    </SidebarProvider>
  );
}

export default MainLayout;