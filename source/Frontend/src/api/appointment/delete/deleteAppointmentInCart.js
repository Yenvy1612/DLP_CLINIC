export async function deleteAppointment(id) {
    try {
        const deleteReponse = await fetch(`http://localhost:8080/api/appointments/${id}`, {
            method: "DELETE",
        });

        if (!deleteReponse.ok) throw new Error("Failed to fetch API");
        
    }
    catch (error) {
        console.error(error.message);
    }
}