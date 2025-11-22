export async function getNotPendingAppointmentByPatientId(id) {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/appointments/not-pending/patient/${id}`, {
            method: "GET",
            headers: {
                "Content-Type" : "application/json"
            }
        });
        if (!getResponse.ok) throw new Error("Failed to fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.error(error.message);
    }
}
