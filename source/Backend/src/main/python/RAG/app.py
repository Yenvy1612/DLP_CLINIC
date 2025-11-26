from fastapi import FastAPI
from pydantic import BaseModel
from rag import rag_answer
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

origins = [
    "http://localhost:5173"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=['GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
    allow_headers=['*']
)

class Query(BaseModel):
    question: str

@app.post("/ask")
def ask_api(req: Query):
    return {"answer": rag_answer(req.question)}
