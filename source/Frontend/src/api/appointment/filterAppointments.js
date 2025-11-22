export async function filterAppointments(filters = {}) {
    try {
        const params = new URLSearchParams();
        
        if (filters.doctorName) params.append('doctorName', filters.doctorName);
        if (filters.patientName) params.append('patientName', filters.patientName);
        if (filters.appointmentDate) params.append('appointmentDate', filters.appointmentDate);
        if (filters.status) params.append('status', filters.status);
        if (filters.roomName) params.append('roomName', filters.roomName);

        const url = `http://localhost:8080/api/appointments/filter${params.toString() ? '?' + params.toString() : ''}`;
        
        const getResponse = await fetch(url, {
            method: "GET"
        });
        
        if (!getResponse.ok) throw new Error("Can't fetch API");
        const data = await getResponse.json();
        return data;
    }
    catch (error) {
        console.log(error.message);
        throw error;
    }
}
