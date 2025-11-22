import { useEffect, useState } from "react";
import { FiEye, FiEdit2, FiTrash2 } from "react-icons/fi";
import { getUserById } from "../../../api/user/getUser";
import { getServiceById } from "../../../api/service/getServiceById";
import { getRoomsById } from "../../../api/room/getRoomById";

function AppointmentLine({a, idx}) {

    const [doctor, setDoctor] = useState({});
    const [patient, setPatient] = useState({});
    const [service, setService] = useState({});
    const [room, setRoom] = useState({});

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
        const getDoctor = async () => {
            try {
                const data = await getUserById(a.doctorId);
                setDoctor(data);
                console.log(data);
            }
            catch (error) {
                console.log(error.message);
            }
        }
        getDoctor();

        const getPatient = async () => {
            try {
                const data = await getUserById(a.patientId);
                setPatient(data);
            }
            catch (error) {
                console.log(error.message);
            }
        }
        getPatient();

        const getService = async () => {
            try {
                const data = await getServiceById(parseInt(a.note));
                setService(data);
            }
            catch (error) {
                console.log(error.message);
            }
        }
        getService();

        const getRoom = async () => {
            try {
                const data = await getRoomsById(a.roomId);
                setRoom(data);
            }
            catch (error) {
                console.log(error.message);
            }
        }
        getRoom();
    }, []);
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
                <td className="px-6 py-3 text-left text-slate-700">{room.name}</td>
                <td className="px-6 py-3 text-left text-slate-700">{service.name}</td>
                <td className="px-6 py-3 text-left text-slate-700">{convertTimeFormat(a.startTime)}</td>
                <td className="px-6 py-3 text-left text-slate-700">{convertTimeFormat(a.createdAt)}</td>
                <td className={`px-6 py-3 text-center ${a.status == "DONE" ? "text-green-500 bg-green-100" : a.status == "PENDING" ? "text-yellow-500 bg-yellow-100" : "text-red-500 bg-red-100"}`}>{a.status}</td>
            </tr>
        </>
    )
}

export default AppointmentLine;