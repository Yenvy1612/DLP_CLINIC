import { useState, useRef, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FiMessageCircle, FiX, FiSend, FiUser, FiEdit3, FiCheck } from "react-icons/fi";
import { sendMessageToGemini } from "../api/chatbot";
import { addLearningData } from "../api/learningData";
import { getUserRole } from "../utils/authUtils";
import logo from "../assets/images/logo/clinic.png";

function Chatbot() {
    const [isOpen, setIsOpen] = useState(false);
    const [showHint, setShowHint] = useState(true);
    const [messages, setMessages] = useState([
        {
            role: "assistant",
            content: "Xin chào! Tôi là trợ lý ảo của A*Care Clinic. Tôi có thể giúp bạn về thông tin phòng khám, dịch vụ, đặt lịch khám và các câu hỏi thường gặp. Bạn cần hỗ trợ gì?" + (getUserRole() === "ADMIN" ? "\n\n💡 Mẹo: Bạn có thể dạy tôi bằng cách nhấn nút ✏️ bên cạnh câu trả lời của tôi!" : "")
        }
    ]);
    const [input, setInput] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [editingMessageIndex, setEditingMessageIndex] = useState(null);
    const [editedAnswer, setEditedAnswer] = useState("");
    const messagesEndRef = useRef(null);

    // Kiểm tra user có phải admin không
    const isAdmin = getUserRole() === "ADMIN";

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!input.trim() || isLoading) return;

        const userMessage = input.trim();
        setInput("");

        // Add user message
        setMessages(prev => [...prev, { role: "user", content: userMessage }]);
        setIsLoading(true);

        try {
            const response = await sendMessageToGemini(userMessage);
            setMessages(prev => [...prev, { 
                role: "assistant", 
                content: response,
                userQuestion: userMessage // Lưu câu hỏi gốc để có thể dạy lại
            }]);
        } 
        catch (error) {
            console.error("Chat error:", error);
            let errorMessage = "Xin lỗi, tôi đang gặp sự cố kỹ thuật. ";

            if (error.message.includes("API error: 400")) {
                errorMessage += "Có vấn đề với yêu cầu. Vui lòng thử lại với câu hỏi khác.";
            } 
            else if (error.message.includes("API error: 401") || error.message.includes("API error: 403")) {
                errorMessage += "API key không hợp lệ. Vui lòng kiểm tra cấu hình.";
            } 
            else if (error.message.includes("API error: 429")) {
                errorMessage = "Chatbot đang bảo trì. Vui lòng:\n\n" +
                    "• Thử lại sau 1-2 phút\n" +
                    "• Hoặc liên hệ trực tiếp:\n" +
                    "  Email: hung.clinic@ptit.edu.vn\n" +
                    "  Hotline: 037 933 0721";
            } 
            else if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
                errorMessage += "Không thể kết nối đến server. Vui lòng kiểm tra internet.";
            } 
            else {
                errorMessage += "Vui lòng thử lại sau hoặc liên hệ: hung.clinic@ptit.edu.vn";
            }

            setMessages(prev => [...prev, {
                role: "assistant",
                content: errorMessage
            }]);
        } 
        finally {
            setIsLoading(false);
        }
    };

    // Bắt đầu chỉnh sửa câu trả lời
    const handleStartEdit = (index, currentAnswer) => {
        setEditingMessageIndex(index);
        setEditedAnswer(currentAnswer);
    };

    // Lưu câu trả lời đã chỉnh sửa và dạy chatbot
    const handleSaveEdit = (index) => {
        const message = messages[index];
        if (message.userQuestion && editedAnswer.trim()) {
            // Lưu vào learning data
            const saved = addLearningData(message.userQuestion, editedAnswer.trim());
            
            if (saved) {
                // Cập nhật message trong UI
                setMessages(prev => {
                    const newMessages = [...prev];
                    newMessages[index] = {
                        ...newMessages[index],
                        content: editedAnswer.trim(),
                        isLearned: true // Đánh dấu đã học
                    };
                    return newMessages;
                });
                
                // Hiện thông báo
                setMessages(prev => [...prev, {
                    role: "assistant",
                    content: "Cảm ơn bạn! Tôi đã học câu trả lời mới này và sẽ sử dụng nó cho những câu hỏi tương tự."
                }]);
            }
        }
        
        setEditingMessageIndex(null);
        setEditedAnswer("");
    };

    // Hủy chỉnh sửa
    const handleCancelEdit = () => {
        setEditingMessageIndex(null);
        setEditedAnswer("");
    };

    return (
        <>
            {/* Hint Message Bubble */}
            <AnimatePresence>
                {showHint && !isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: 20, scale: 0.8 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 20, scale: 0.8 }}
                        transition={{ duration: 0.3 }}
                        className="fixed bottom-40 right-6 z-40"
                    >
                        <div className="bg-[#00278D] rounded-2xl shadow-2xl p-4 max-w-xs flex items-start gap-3 relative mb-3">
                            <div className="flex-1">
                                <p className="text-sm text-white font-medium">
                                    Bạn cần hỗ trợ gì không ạ? 
                                </p>
                            </div>
                            <button
                                onClick={() => setShowHint(false)}
                                className="flex-shrink-0 text-white hover:text-slate-600 transition-colors"
                                aria-label="Close hint"
                            >
                                <FiX className="text-lg" />
                            </button>
                            <div className="absolute -bottom-2 right-6 w-4 h-4 bg-[#00278D] rotate-45 shadow-lg"></div>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* Chatbot Button - Above the scroll-to-top button */}
            <motion.button
                onClick={() => setIsOpen(!isOpen)}
                className="fixed bottom-24 right-6 z-50 h-14 w-14 rounded-full bg-[#00278D] hover:from-[#003bb5] hover:to-sky-400 text-white shadow-xl shadow-blue-900/30 flex items-center justify-center transition-all duration-300 p-2"
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                aria-label="Open chatbot"
            >
                {isOpen ? (
                    <FiX className="text-2xl" />
                ) : (
                    <img src={logo} alt="chatbot" className="w-full h-full rounded-full" />
                )}
            </motion.button>

            {/* Chatbot Window */}
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: 20, scale: 0.95 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 20, scale: 0.95 }}
                        transition={{ duration: 0.2 }}
                        className="fixed bottom-40 right-6 z-40 w-96 h-[500px] bg-white rounded-4xl shadow-2xl flex flex-col overflow-hidden"
                    >
                        {/* Header */}
                        <div className="bg-[#00278D] text-white p-4 flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center p-1">
                                <img src={logo} alt="clinic logo" className="w-full h-full object-contain rounded-full" />
                            </div>
                            <div className="flex-1">
                                <h3 className="font-bold text-lg">A<sup>*</sup>ssistant</h3>
                                <p className="text-xs text-white/80 flex items-center gap-1">
                                    <span className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></span>
                                    Trợ lý ảo hỗ trợ 24/7
                                </p>
                            </div>
                        </div>

                        {/* Messages */}
                        <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50">
                            {messages.map((msg, index) => (
                                <motion.div
                                    key={index}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
                                >
                                    <div className={`flex gap-2 max-w-[80%] ${msg.role === "user" ? "flex-row-reverse" : "flex-row"}`}>
                                        {/* Avatar */}
                                        <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${msg.role === "user" ? "bg-sky-500" : "bg-gradient-to-br from-[#00278D] to-sky-500"}`}>
                                            {msg.role === "user" ? (
                                                <FiUser className="text-white text-sm" />
                                            ) : (
                                                <img src={logo} alt="bot" className="w-6 h-6 object-contain rounded-full" />
                                            )}
                                        </div>
                                        
                                        <div className="flex flex-col gap-1">
                                            {/* Message bubble */}
                                            <div className={`rounded-2xl px-4 py-2 ${msg.role === "user" ? "bg-sky-500 text-white" : "bg-white text-slate-800 shadow-sm"}`}>
                                                {editingMessageIndex === index ? (
                                                    <textarea
                                                        value={editedAnswer}
                                                        onChange={(e) => setEditedAnswer(e.target.value)}
                                                        className="w-full text-sm leading-relaxed border border-sky-300 rounded p-2 min-h-[100px] focus:outline-none focus:ring-2 focus:ring-sky-500"
                                                        placeholder="Nhập câu trả lời đúng..."
                                                    />
                                                ) : (
                                                    <p className="text-sm leading-relaxed whitespace-pre-wrap">
                                                        {msg.content}
                                                        {msg.isLearned && <span className="ml-2 text-green-500">✓ Đã học</span>}
                                                    </p>
                                                )}
                                            </div>
                                            
                                            {/* Edit buttons - Chỉ hiện cho ADMIN và assistant messages có userQuestion */}
                                            {isAdmin && msg.role === "assistant" && msg.userQuestion && (
                                                <div className="flex gap-1 ml-1">
                                                    {editingMessageIndex === index ? (
                                                        <>
                                                            <button
                                                                onClick={() => handleSaveEdit(index)}
                                                                className="text-xs px-2 py-1 bg-green-500 text-white rounded hover:bg-green-600 flex items-center gap-1"
                                                                title="Lưu và dạy chatbot"
                                                            >
                                                                <FiCheck className="text-xs" />
                                                                Lưu
                                                            </button>
                                                            <button
                                                                onClick={handleCancelEdit}
                                                                className="text-xs px-2 py-1 bg-slate-300 text-slate-700 rounded hover:bg-slate-400"
                                                            >
                                                                Hủy
                                                            </button>
                                                        </>
                                                    ) : (
                                                        <button
                                                            onClick={() => handleStartEdit(index, msg.content)}
                                                            className="text-xs px-2 py-1 bg-sky-100 text-sky-600 rounded hover:bg-sky-200 flex items-center gap-1"
                                                            title="Sửa câu trả lời và dạy chatbot"
                                                        >
                                                            <FiEdit3 className="text-xs" />
                                                            Dạy lại
                                                        </button>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </motion.div>
                            ))}
                            {isLoading && (
                                <motion.div
                                    initial={{ opacity: 0 }}
                                    animate={{ opacity: 1 }}
                                    className="flex justify-start"
                                >
                                    <div className="flex gap-2 items-end">
                                        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#00278D] to-sky-500 flex items-center justify-center">
                                            <img src={logo} alt="bot" className="w-6 h-6 object-contain rounded-full" />
                                        </div>
                                        <div className="bg-white rounded-2xl px-4 py-3 shadow-sm">
                                            <div className="flex gap-1">
                                                <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: "0ms" }}></span>
                                                <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: "150ms" }}></span>
                                                <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: "300ms" }}></span>
                                            </div>
                                        </div>
                                    </div>
                                </motion.div>
                            )}
                            <div ref={messagesEndRef} />
                        </div>

                        {/* Input */}
                        <form onSubmit={handleSubmit} className="p-4 bg-white border-t border-slate-200">
                            <div className="flex gap-2">
                                <input
                                    type="text"
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    placeholder="Nhập câu hỏi của bạn..."
                                    className="flex-1 text-[#00278D] px-4 py-2 rounded-full border border-slate-300 focus:outline-none focus:ring-2 focus:ring-sky-500 text-sm"
                                    disabled={isLoading}
                                />
                                <button
                                    type="submit"
                                    disabled={isLoading || !input.trim()}
                                    className="w-10 h-10 rounded-full bg-gradient-to-br from-[#00278D] to-sky-500 hover:from-[#003bb5] hover:to-sky-400 disabled:opacity-50 disabled:cursor-not-allowed text-white flex items-center justify-center transition-all"
                                >
                                    <FiSend className="text-lg" />
                                </button>
                            </div>
                        </form>
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    );
}

export default Chatbot;
