import { baseData } from '../data/baseContextData';
import { getAnswerFromGemini } from './getAnswerFromGemini';
import { formatLearningDataForContext } from './learningData';

const GEMINI_API_KEY = import.meta.env.VITE_GEMINI_API_KEY;

const getClinicContext = () => {
    const baseContext = baseData;

    // Thêm kiến thức được dạy từ admin 
    const learningContext = formatLearningDataForContext();
    const fullContext = baseContext + learningContext;
    return fullContext;
};

// Gọi Gemini API
export async function sendMessageToGemini(userMessage) {
    if (!GEMINI_API_KEY) {
        throw new Error("API key chưa được cấu hình. Vui lòng thêm VITE_GEMINI_API_KEY vào file .env");
    }

    const contextData = getClinicContext();
    console.log('Context includes learning data:', contextData.includes('Kiến thức đã học'));

    const prompt = `${contextData}
                    === NHIỆM VỤ ===
                    Bạn là trợ lý ảo của A*Care Clinic. Tra lời câu hỏi sau bằng tiếng Việt, văn phong thân thiện, không dùng markdown (**, *, __, _).
                    Câu hỏi: ${userMessage}
                    Hãy trả lời ngắn gọn, rõ ràng dựa trên thông tin ở trên.`;

    try {
        console.log("Sending request to Gemini API...");
        const data = await getAnswerFromGemini(GEMINI_API_KEY, prompt);

        if (data.candidates && data.candidates.length > 0 && data.candidates[0].content?.parts?.length > 0) {
            const rawText = data.candidates[0].content.parts[0].text;
            const cleanedText = rawText
                .replace(/\*\*/g, '')
                .replace(/\*/g, '')
                .replace(/__/g, '')
                .replace(/_/g, '');
            return cleanedText;
        } 
        else {
            console.error("Invalid response structure:", data);
            if (data.promptFeedback?.blockReason) {
                throw new Error(`Request bị chặn: ${data.promptFeedback.blockReason}`);
            }
            throw new Error("API không trả về kết quả hợp lệ. Vui lòng thử lại.");
        }
    } 
    catch (error) {
        console.error("Gemini API Error:", error);
        throw error;
    }
}
