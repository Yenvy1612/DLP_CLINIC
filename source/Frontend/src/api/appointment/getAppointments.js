export async function getAppointments() {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/appointments`, {
            method: "GET",
        });
        if (!getResponse.ok) throw new Error("ERROR");
        return await getResponse.json();
    }
    catch (error) {
        console.error(error.message);
    }
}