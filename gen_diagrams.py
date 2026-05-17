import urllib.request
import urllib.parse

graphs = {
    "flow1_startup.png": """digraph G {
  node [shape=box, style=filled, fillcolor="#DDF3FF"];
  A [label="Nguoi dung mo App"];
  B [label="AgentInitializer.init()"];
  C [label="Kiem tra JWT Token"];
  D [label="Dang nhap (LoginActivity)"];
  E [label="Goi /api/agents/register"];
  F [label="Goi /api/agents/policy"];
  G [label="Kich hoat HeartbeatWorker"];
  H [label="MainActivity (San sang)"];

  A -> B -> C;
  C -> D [label="Chua co Token"];
  C -> E [label="Da co Token"];
  D -> E [label="Token OK"];
  E -> F -> G -> H;
}""",
    "flow2_dlp.png": """digraph G {
  node [shape=box, style=filled, fillcolor="#FFF5CC"];
  A [label="Bac si bam Xuat PDF"];
  B [label="Trich xuat du lieu benh an"];
  C [label="DlpScanner.scan(text)"];
  D [label="MaskingUtil: Che CCCD, SDT"];
  E [label="AgentEventTracker: Ghi log"];
  F [label="Room Database: Luu Queue Offline"];
  G [label="Xuat file PDF (Da che PII)"];

  A -> B -> C -> D -> E -> F -> G;
}""",
    "flow3_offline.png": """digraph G {
  node [shape=box, style=filled, fillcolor="#D5F5E3"];
  A [label="Mat mang (Offline)"];
  B [label="Log luu tai SQLite (is_synced=0)"];
  C [label="Co mang tro lai"];
  D [label="HDH danh thuc EventSyncWorker"];
  E [label="Gom batch cac Event"];
  F [label="POST /api/agent-events"];
  G [label="Cap nhat is_synced=1"];

  A -> B;
  B -> C [style=dashed];
  C -> D -> E -> F -> G;
}""",
    "flow4_backend.png": """digraph G {
  node [shape=box, style=filled, fillcolor="#FADBD8"];
  A [label="Mobile App goi API"];
  B [label="BehaviorAspect Intercept"];
  C [label="Kiem tra Role & Gio lam viec"];
  D [label="Kiem tra Rate Limit (Redis)"];
  E [label="Tu choi (Throw SecurityException)"];
  F [label="Cho phep chay ham Controller"];
  G [label="Ghi log DlpLog (Backend)"];

  A -> B -> C;
  C -> E [label="Vi pham"];
  C -> D [label="Hop le"];
  D -> E [label="Vuot nguong"];
  D -> F [label="Hop le"];
  E -> G;
}"""
}

for filename, graph in graphs.items():
    encoded_graph = urllib.parse.quote(graph)
    url = f"https://quickchart.io/graphviz?graph={encoded_graph}&format=png"
    print(f"Downloading {filename}...")
    urllib.request.urlretrieve(url, filename)

print("All diagrams downloaded successfully.")
