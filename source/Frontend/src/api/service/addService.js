export async function addService(service) {
    try {
        const postResponse = await fetch("http://localhost:8080/api/services", {
            method: "POST",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(service)
        });
        if (!postResponse.ok) throw new Error("Can't fetch API");
        const data = await postResponse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
    }
}