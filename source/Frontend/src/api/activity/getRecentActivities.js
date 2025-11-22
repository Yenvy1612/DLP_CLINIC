export async function getRecentActivities() {
    try {
        const getResponse = await fetch('http://localhost:8080/api/activities/recent', {
            method: "GET"
        });
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.log(error);
    }
}