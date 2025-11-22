export async function addUser(user) {
    try {
        const postResponse = await fetch("http://localhost:8080/api/users/register", {
            method: "POST",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(user)
        })
        if (!postResponse.ok) throw new Error("Can't add patient");
        return await postResponse.json();
    }
    catch (error) {
        console.error(error.message);
    }
}