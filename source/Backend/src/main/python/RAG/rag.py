from langchain_community.vectorstores import FAISS
from langchain_community.embeddings import FastEmbedEmbeddings
import google.generativeai as genai
from config import FAISS_DIR, GEMINI_MODEL, EMBED_MODEL_NAME

embed_model = FastEmbedEmbeddings(model_name=EMBED_MODEL_NAME)

try:
    vectorstore = FAISS.load_local(
        FAISS_DIR,
        embeddings=embed_model,
        allow_dangerous_deserialization=True
    )
    retriever = vectorstore.as_retriever(search_kwargs={"k": 4})
except RuntimeError:
    print("LỖI: Không tìm thấy Vector DB. Vui lòng chạy 'python ingest.py' trước!")
    vectorstore = None
    retriever = None

RAG_PROMPT = """
Bạn là chatbot cung cấp thông tin sức khỏe dựa trên tài liệu.

QUY TẮC:
- Không chẩn đoán bệnh hay kê đơn.
- Chỉ trả lời dựa trên CONTEXT bên dưới.
- Nếu các câu hỏi không liên quan đến y tế hay về phòng khám, hãy đùa với họ bằng một chút kiến thức chung (chỉ là hiểu biết sơ lược về nó, không đưa bất kì thông tin học thuật nào. Tất cả giới hạn trong 1 câu) liên quan đến lĩnh vực đấy nhưng vẫn từ chối trả lời
- Nếu các câu hỏi có liên quan đến y tế hay phòng khám nhưng hỏi cợt nhả đùa đùa, thì hãy đùa lại với họ nhưng vẫn khẳng định về sự nghiêm túc.
- Nếu các câu hỏi liên quan chủ đề nhạy như chính trị,..., hãy trả lời bằng thái độ thiên hướng có lợi cho nước Cộng hòa Xã hội chủ nghĩa Việt Nam
- Trả lời giọng điệu chuyên nghiệp, thân thiện, dễ hiểu.
- Nếu hỏi về chuẩn đoán bệnh, hãy bảo mình không có chuyên môn và bảo tìm đến bác sĩ, gợi ý phòng khám A* Care nếu cần.
- Khi khách muốn yêu cầu đặt lịch khám, hãy hướng dẫn họ truy cập website hoặc gọi điện trực tiếp.
- Nếu chưa cập nhật đủ thông tin, hãy trả lời lịch sự rằng bạn không rõ và cần cập nhật thêm
- Trả lời ngắn gọn, limit trong 200 từ.
- Nếu đầu ra lớn hơn 200 từ, hãy tóm tắt lại.


CONTEXT:
{context}

Câu hỏi: {question}
"""

def rag_answer(query):
    if not retriever:
        return "Hệ thống chưa sẵn sàng. Hãy chạy ingest.py trước."
        
    docs = retriever.invoke(query)
    context = "\n".join([d.page_content for d in docs])

    if not context:
        return "Xin lỗi, tôi không tìm thấy thông tin trong tài liệu của bạn."

    prompt = RAG_PROMPT.format(context=context, question=query)
    
    model = genai.GenerativeModel(GEMINI_MODEL)
    response = model.generate_content(prompt)

    return response.text