export async function getserviceByName(name) {
    try {
        const getResponse = await fetch(`http://localhost:8080/api/services/search?name=${name}`, {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
    }
}