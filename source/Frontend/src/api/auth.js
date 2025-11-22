export async function loginApi({ email, password }) {
  const res = await fetch("http://localhost:8080/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: email, password }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Đăng nhập thất bại");
  return data;
}
