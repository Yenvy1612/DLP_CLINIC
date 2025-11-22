export async function searchRooms(searchParams = {}) {
    const params = new URLSearchParams();
    
    if (searchParams.floor) {
        params.append('floor', searchParams.floor);
    }

    const url = `http://localhost:8080/api/rooms/search${params.toString() ? `?${params.toString()}` : ''}`;
    
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error('Failed to search rooms');
    }
    return response.json();
}
