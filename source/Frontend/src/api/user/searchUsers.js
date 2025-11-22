export async function searchUsers(searchParams = {}) {
    try {
        const params = new URLSearchParams();
        
        if (searchParams.fullName) params.append('fullName', searchParams.fullName);
        if (searchParams.role) params.append('role', searchParams.role);
        if (searchParams.email) params.append('email', searchParams.email);

        const url = `http://localhost:8080/api/users/search${params.toString() ? '?' + params.toString() : ''}`;
        
        const getResponse = await fetch(url, {
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
