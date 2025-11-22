export async function getServiceById(id) {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/services/${id}`, {
            method: "GET",
        });
        if (!getResponse.ok) throw new Error("Can't load data");
        const data = await getResponse.json()
        return data;
    }
    catch (error) {
        console.error("Failed to fetch API", error.message);
    }
}