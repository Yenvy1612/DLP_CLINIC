export async function searchServices(searchParams = {}) {
    try {
        const params = new URLSearchParams();
        
        if (searchParams.name) params.append('name', searchParams.name);
        if (searchParams.minPrice) params.append('minPrice', searchParams.minPrice);
        if (searchParams.maxPrice) params.append('maxPrice', searchParams.maxPrice);
        if (searchParams.active !== undefined && searchParams.active !== '') {
            params.append('active', searchParams.active);
        }

        const url = `http://localhost:8080/api/services/search${params.toString() ? '?' + params.toString() : ''}`;
        
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
