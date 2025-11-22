export async function getTodayPendingAppointments() {
    try {
        const getResponse = await fetch('http://localhost:8080/api/appointments/today/pending', {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
    }
}