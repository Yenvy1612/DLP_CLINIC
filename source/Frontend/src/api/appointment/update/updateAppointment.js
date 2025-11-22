export async function updateAppointment(id, updated) {
    try {
        const putResponse = await fetch(`http://localhost:8080/api/appointments/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updated)
        });
        if (!putResponse.ok) throw new Error("Can't Fetch API");
        return await putResponse.json();
    }
    catch (error) {
        console.error(error.message);
    }
}