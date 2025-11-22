export async function getUserById(id) {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/users/${id}`, {
            method: "GET", 
            headers: {
                "Content-Type" : "application/json"
            }
        });
        if (!getResponse.ok) throw new Error("Failed to fetch user by id");
        const userData = await getResponse.json();
        return userData;
    }
    catch (error) {
        console.error(error.message);
    }
}