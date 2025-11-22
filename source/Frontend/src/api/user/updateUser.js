export async function updateUser(id, updated) {
    try {
        const putResponse = await fetch(`http://localhost:8080/api/users/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(updated)
        });
        if (!putResponse.ok) throw new Error("Failed to put");
        const updatedUser = await putResponse.json();
        return updateUser;
    }
    catch (err) {
        console.error(err.message);
    }
}
