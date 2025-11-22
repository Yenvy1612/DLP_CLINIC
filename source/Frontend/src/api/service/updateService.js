export async function updateService(id, update) {
    try {
        const putResponse = await fetch(`http://localhost:8080/api/services/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(update)
        });
        if (!putResponse.ok) throw new Error("Can't fetch API");
    }
    catch (error) {
        console.log(error.message);
    }
}