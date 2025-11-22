export async function deleteUserById(id) {
    try {
        const deleteResponse = await fetch(`http://localhost:8080/api/users/${id}`, {
            method: "DELETE"
        });
        if (!deleteResponse.ok) throw new Error("Can't fetch API");
        return await deleteResponse.json();
    }
    catch (error) {
        console.log(error.message);
    }
}