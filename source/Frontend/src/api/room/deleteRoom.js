export async function deleteRoom(id) {
    try {
        const deleteResonse = await fetch(`http://localhost:8080/api/rooms/${id}`, {
            method: "DELETE"
        });
        if (!deleteResonse.ok) throw new Error("Can't fetch API");
        const data = await deleteResonse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
    }
}