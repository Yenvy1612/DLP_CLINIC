export async function getPatients() {
    try {
        const getResponse = await fetch("http://localhost:8080/api/users/patient", {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.error(error);
    }
}