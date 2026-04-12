import { Outlet, useLocation } from "react-router-dom";
import HeroSection from "./HeroSection";
import AboutSection from "./AboutSection";
import ClinicServicesSection from "./ClinicServicesSection";
import SpecialistsSection from "./SpecialistsSection";

function Home() {

    const location = useLocation();
    const isRoot = location.pathname === "/";
    return (
        <div className="bg-[var(--surface)]">
            {isRoot ? <><HeroSection /> <SpecialistsSection /> <AboutSection /> <ClinicServicesSection /></> : ""}

            <div className="">
                <Outlet />
            </div>
        </div>
    );
}

export default Home;
