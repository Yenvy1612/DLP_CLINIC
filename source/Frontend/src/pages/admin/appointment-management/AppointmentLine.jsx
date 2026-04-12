import { useEffect, useState } from "react";
import { serviceService, userService } from "../../../api";

function AppointmentLine({ a, idx }) {

    const [doctor, setDoctor] = useState({});
    const [patient, setPatient] = useState({});
    const [service, setService] = useState({});

    const convertTimeFormat = (a) => {
        var res = "";
        var x;
        for (x in a) {
            if (a[x] != "T" && a[x] != '-') res += a[x];
            else if (a[x] == "-") res += "/";
            else res += "\n";
        }
        return res;
    }

    useEffect(() => {
        let mounted = true;

        const fetchRelated = async () => {
            try {
                const serviceId = Number.parseInt(a.serviceId ?? a.note, 10);
                const [doctorData, patientData, serviceData] = await Promise.all([
                    userService.getById(a.doctorId),
                    userService.getById(a.patientId),
                    Number.isNaN(serviceId) ? Promise.resolve(null) : serviceService.getById(serviceId),
                ]);

                if (!mounted) return;
                setDoctor(doctorData || {});
                setPatient(patientData || {});
                setService(serviceData || {});
            } catch (error) {
                console.log(error.message);
            }
        };

        fetchRelated();

        return () => {
            mounted = false;
        };
    }, [a.doctorId, a.patientId, a.serviceId, a.note]);
    return (
        <>
            <tr
                key={a.id}
                className={`hover:bg-sky-50 transition ${idx % 2 === 0 ? "bg-white" : "bg-slate-50/60"
                    }`}
            >
                <td className="px-6 py-3 text-slate-800">{idx + 1}</td>
                <td className="px-6 py-3 text-slate-700">{doctor.fullName}</td>
                <td className="px-6 py-3 text-left text-slate-700">{patient.fullName}</td>
                <td className="px-6 py-3 text-left text-slate-700">{service.name}</td>
                <td className="px-6 py-3 text-left text-slate-700">{convertTimeFormat(a.startTime)}</td>
                <td className="px-6 py-3 text-left text-slate-700">{convertTimeFormat(a.createdAt)}</td>
                <td className={`px-6 py-3 text-center ${a.status == "DONE" ? "text-green-500 bg-green-100" : a.status == "PENDING" ? "text-yellow-500 bg-yellow-100" : "text-red-500 bg-red-100"}`}>{a.status}</td>
            </tr>
        </>
    )
}

export default AppointmentLine;