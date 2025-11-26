import os
from langchain_community.embeddings import FastEmbedEmbeddings # Dùng wrapper chuẩn
from langchain_community.vectorstores import FAISS
from langchain_text_splitters import RecursiveCharacterTextSplitter
from docx import Document as DocxDocument
from pypdf import PdfReader
from config import DOCS_DIR, FAISS_DIR, EMBED_MODEL_NAME

embed_model = FastEmbedEmbeddings(model_name=EMBED_MODEL_NAME)

def load_txt(path):
    with open(path, "r", encoding="utf-8") as f:
        return f.read()

def load_pdf(path):
    reader = PdfReader(path)
    text = ""
    for page in reader.pages:
        if page.extract_text():
            text += page.extract_text() + "\n"
    return text

def load_docx(path):
    doc = DocxDocument(path)
    return "\n".join(p.text for p in doc.paragraphs)

def load_documents():
    texts = []
    if not os.path.exists(DOCS_DIR):
        os.makedirs(DOCS_DIR)
        print(f"Đã tạo thư mục {DOCS_DIR}. Hãy bỏ file vào đó!")
        return []
        
    for f in os.listdir(DOCS_DIR):
        path = os.path.join(DOCS_DIR, f)
        try:
            if f.endswith(".txt"):
                texts.append(load_txt(path))
            elif f.endswith(".pdf"):
                texts.append(load_pdf(path))
            elif f.endswith(".docx"):
                texts.append(load_docx(path))
            print(f"Đã load: {f}")
        except Exception as e:
            print(f"Lỗi đọc file {f}: {e}")
    return texts

def chunk_texts(texts):
    splitter = RecursiveCharacterTextSplitter(chunk_size=800, chunk_overlap=150)
    chunks = []
    for t in texts:
        chunks.extend(splitter.split_text(t))
    return chunks

def build_faiss(chunks):
    if not chunks:
        print("Không có dữ liệu để tạo Vector DB!")
        return

    print("Đang tạo Vector Database... (Có thể mất vài giây)")
    vectorstore = FAISS.from_texts(
        texts=chunks,
        embedding=embed_model
    )

    vectorstore.save_local(FAISS_DIR)
    print(f"FAISS DB đã lưu thành công tại: {FAISS_DIR}")

if __name__ == "__main__":
    print("Bắt đầu Ingest dữ liệu...")
    docs = load_documents()
    chunks = chunk_texts(docs)
    build_faiss(chunks)