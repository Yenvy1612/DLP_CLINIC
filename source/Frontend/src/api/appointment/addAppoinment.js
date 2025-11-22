export async function addAppointment(appointment) {
    try {
        const postResponse = await fetch("http://localhost:8080/api/appointments/book", {
            method: "POST",
            headers: {
                "Content-type" : "application/json"
            },
            body: JSON.stringify(appointment)
        });
        if (!postResponse.ok) throw new Error("Failed to POST");
        return postResponse.json();
    }
    catch (err) {
        console.error(err.message);
    }
}