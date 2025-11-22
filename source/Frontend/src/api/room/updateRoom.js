export async function updateRoom(id, update) {
    try {
        const putResponse = await fetch(`http://localhost:8080/api/rooms/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(update)
        });
        if (!putResponse.ok) throw new Error("Can't fetch API");
        const data = await putResponse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
    }
}