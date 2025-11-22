import { useSidebarContext } from "../contexts/SideBarContext";
import logo from "../assets/images/logo/logo_png.png";
import { NavLink } from "react-router-dom";
import { isLoggedIn, logout } from "../utils/authUtils";
import { useState } from "react";

function Sidebar() {
  const { isOpen, toggleSidebar } = useSidebarContext();
  const loggedIn = isLoggedIn();
  const [pagesOpen, setPagesOpen] = useState(false);
  return (
    <>
      <div
        onClick={toggleSidebar}
        className={`fixed inset-0 bg-black/40 transition-opacity z-40 ${isOpen ? "opacity-100 visible" : "opacity-0 invisible"
          }`}
      />
      <aside
        className={`fixed top-0 left-0 h-full w-90 bg-white shadow-xl z-50 transition-transform duration-300 ${isOpen ? "translate-x-0" : "-translate-x-full"
          }`}
      >
        <div className="p-4 flex justify-between items-center pt-0">
          <img src={logo} alt="logo" className="logo bg-cover w-45 h-30" />
          <button onClick={toggleSidebar} className="flex items-center text-xl cursor-pointer h-12 w-12 bg-sky-500 text-white p-4 rounded-full font-extrabold">✕</button>
        </div>
        <div className="about-us text-[#00278D] p-10 pt-0 pb-5">
          <h2 className="text-2xl font-black">Về chúng tôi</h2>
          <div className="w-15 h-1 bg-sky-500 mt-1"></div>
          <p className="font-regular text-slate-700 pt-5">
            <span className="font-extrabold text-[#00278D]">A<sup className="text-yellow-500">*</sup><span className="text-sky-500">Care</span></span> tự hào là đơn vị chăm sóc sức khỏe
            uy tín, mang đến dịch vụ tận tâm – hiện đại – an toàn cho mọi khách hàng.
          </p>
        </div>
        <nav className="p-10 font-black space-y-2 pt-0 text-[#00278D]">
          <NavLink to="/" className="block p-2 hover:bg-gray-100 rounded">Trang chủ</NavLink>
          <NavLink to="/services" className="block p-2 hover:bg-gray-100 rounded">Dịch vụ</NavLink>

          {/* Menu Trang với submenu */}
          <div>
            <button
              onClick={() => setPagesOpen(!pagesOpen)}
              className="w-full text-left p-2 hover:bg-gray-100 rounded flex items-center justify-between"
            >
              <span>Trang</span>
              <span className={`transition-transform ${pagesOpen ? 'rotate-180' : ''}`}>
                <svg viewBox="0 0 24 24" className="w-3.5 h-3.5 text-[#00278D] pointer-events-none"
                  fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M6 9l6 6 6-6" />
                </svg>
              </span>
            </button>
            {pagesOpen && (
              <div className="ml-4 mt-1 space-y-1">
                <NavLink to="/about" className="block p-2 text-sm hover:bg-gray-100 rounded">
                  Giới thiệu
                </NavLink>
                <NavLink to="/instruction" className="block p-2 text-sm hover:bg-gray-100 rounded">
                  Hướng dẫn
                </NavLink>
                <NavLink to="/faq" className="block p-2 text-sm hover:bg-gray-100 rounded">
                  Hỏi đáp
                </NavLink>
              </div>
            )}
          </div>

          <NavLink to="/contact" className="block p-2 hover:bg-gray-100 rounded">
            Liên hệ
          </NavLink>
          {loggedIn ? (
            <button
              onClick={logout}
              className="login-button bg-slate-600 text-white text-center w-full p-2 rounded-xl hover:bg-slate-500"
            >
              Đăng xuất
            </button>
          ) : (
            <NavLink to="/login">
              <button className="login-button bg-sky-500 text-white text-center w-full p-2 rounded-xl hover:bg-sky-600">
                Đăng nhập
              </button>
            </NavLink>
          )}
        </nav>
      </aside>
    </>
  );
}

export default Sidebar;