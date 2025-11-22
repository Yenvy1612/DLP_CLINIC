export async function getDoctors() {
    try {
        const getResponse = await fetch("http://localhost:8080/api/users/doctor", {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.error(error);
    }
}