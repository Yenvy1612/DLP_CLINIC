export async function getRooms() {
    try {
        const getResponse = await fetch("http://localhost:8080/api/rooms", {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    } 
    catch (error) {
        console.error(error.message);
    }
}