export async function getAnswerFromGemini(prompt) {
    try {
        const URL = `http://localhost:8005/ask`;
        const response = await fetch(URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({"question" : prompt})
        });

        console.log("Response status:", response.status);
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            console.error("API Error Response:", errorData);
            throw new Error(`API error: ${response.status} - ${errorData.error?.message || 'Unknown error'}`);
        }

        const data = await response.json();
        console.log("API Response:", data);
        return data;
    }
    catch (error) {
        console.error(error);
    }
}

