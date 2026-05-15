import { createContext, useContext, useState } from "react";

const SidebarContext = createContext();

export const useSidebarContext = () => useContext(SidebarContext);

export default function SidebarProvider({ children }) {
  const [isOpen, setIsOpen] = useState(false);

  const toggleSidebar = () => setIsOpen(prev => !prev);

  const value = { isOpen, toggleSidebar };

  return (
    <SidebarContext.Provider value={value}>
      {children}
    </SidebarContext.Provider>
  );
}
