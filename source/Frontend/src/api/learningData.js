/* Learning data storage - Dữ liệu chatbot học từ người dùng
Lưu trữ local trong trình duyệt (localStorage)
 */

const STORAGE_KEY = 'acare_chatbot_learning_data';

// Lấy dữ liệu đã học
export const getLearningData = () => {
    try {
        const data = localStorage.getItem(STORAGE_KEY);
        const parsed = data ? JSON.parse(data) : [];
        console.log('Loading learning data:', parsed.length, 'entries');
        return parsed;
    } 
    catch (error) {
        console.error('Error loading learning data:', error);
        return [];
    }
};

// Thêm kiến thức mới
export const addLearningData = (question, answer) => {
    try {
        const data = getLearningData();
        const newEntry = {
            id: Date.now(),
            question: question.trim(),
            answer: answer.trim(),
            createdAt: new Date().toISOString()
        };
        data.push(newEntry);
        localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
        console.log('Saved new learning entry:', newEntry);
        console.log('Total entries now:', data.length);
        return true;
    } 
    catch (error) {
        console.error('Error saving learning data:', error);
        return false;
    }
};

// Xóa một mục kiến thức
export const deleteLearningData = (id) => {
    try {
        const data = getLearningData();
        const filteredData = data.filter(item => item.id !== id);
        localStorage.setItem(STORAGE_KEY, JSON.stringify(filteredData));
        return true;
    } 
    catch (error) {
        console.error('Error deleting learning data:', error);
        return false;
    }
};

// Xóa toàn bộ dữ liệu đã học
export const clearLearningData = () => {
    try {
        localStorage.removeItem(STORAGE_KEY);
        return true;
    } 
    catch (error) {
        console.error('Error clearing learning data:', error);
        return false;
    }
};

// Tìm kiếm câu hỏi tương tự
export const findSimilarQuestion = (question) => {
    const data = getLearningData();
    const normalizedQuestion = question.toLowerCase().trim();
    return data.find(item =>
        item.question.toLowerCase().includes(normalizedQuestion) ||
        normalizedQuestion.includes(item.question.toLowerCase())
    );
};

// Format dữ liệu học để thêm vào context
export const formatLearningDataForContext = () => {
    const data = getLearningData();
    if (data.length === 0) {
        console.log('No learning data to add to context');
        return '';
    }

    let contextText = '\n\n## Kiến thức đã học từ người dùng (được admin dạy):\n';
    data.forEach(item => {
        contextText += `\nCâu hỏi: ${item.question}\nTrả lời: ${item.answer}\n`;
    });

    console.log('Adding', data.length, 'learned entries to context');
    return contextText;
};
