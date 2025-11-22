export async function getAppointmentsByDoctorId(id) {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/appointments/doctor/${id}`, {
            method: "GET",
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const appointments = await getResponse.json();
        return appointments;
    }
    catch (error) {
        console.log(error.message);
    }
}