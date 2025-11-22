export async function updateAppointmentStatusToDone(id) {
    try {
        const patchResponse = await fetch(`http://localhost:8080/api/appointments/done/${id}`, {
            method: "PATCH"
        });

        if (!patchResponse.ok) throw new Error("Can't fetch API");
    }
    catch (error) {
        console.error(error.message);
    }
}