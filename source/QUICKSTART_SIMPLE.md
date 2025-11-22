# 🚀 Hướng dẫn chạy A*Care Clinic

> **Bạn đã có file ZIP source/** → Giải nén và làm theo 4 bước

---

## Bước 1: Cài phần mềm

Tải và cài đặt theo thứ tự:

1. **Java JDK 21**: https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe
2. **Node.js**: https://nodejs.org/en/download/
3. **MySQL Installer**: https://dev.mysql.com/downloads/installer/
   - Chọn "MySQL Installer Community" → Tải
   - Chạy file → Chọn **"Developer Default"**
   - Đặt **Root Password** (ví dụ: `123456`) → Nhớ password này!
   - Next → Finish

---

## Bước 2: Import Database

1. **Mở MySQL Workbench** (đã cài ở bước 1)

2. **Kết nối MySQL:**
   - Click vào **Local instance MySQL** → Nhập root password

3. **Tạo database:**
   - Menu: **Database** → **Create Schema**
   - Schema Name: `acare_clinic`
   - Charset: `utf8mb4`, Collation: `utf8mb4_unicode_ci`
   - Click **Apply** → **Apply** → **Finish**

4. **Import dữ liệu:**
   - Menu: **Server** → **Data Import**
   - Chọn **Import from Self-Contained File**
   - Click **...** → Chọn file `file_query.sql` (trong thư mục source vừa giải nén)
   - **Default Target Schema**: chọn `acare_clinic`
   - Click **Start Import**
   - Đợi "Import Completed" → **Xong!**

---

## Bước 3: Chạy Backend

1. **Sửa password MySQL:**
   - Mở file: `Backend/src/main/resources/application.properties`
   - Tìm dòng: `spring.datasource.password=123456`
   - Đổi `123456` → **root password MySQL của bạn**
   - Lưu (Ctrl+S)

2. **Mở Terminal trong thư mục Backend:**
   - Cách 1: Shift + Chuột phải trong thư mục Backend → "Open PowerShell window here"
   - Cách 2: Mở CMD/PowerShell → gõ `cd đường\dẫn\đến\Backend`

3. **Chạy lệnh:**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

4. **Đợi thấy:** `Started BackendApplication in X.XXX seconds`

**⚠️ Giữ cửa sổ này mở! Không tắt!**

---

## Bước 4: Chạy Frontend

1. **Mở Terminal MỚI trong thư mục Frontend**

2. **Cài packages:**
   ```bash
   npm install
   ```
   _(Đợi 1-2 phút)_

3. **Tạo file `.env`:**
   - Trong thư mục Frontend, tạo file mới tên **`.env`**
   - Mở file, gõ:
     ```
     VITE_GEMINI_API_KEY=AIzaSyDummy
     ```
   - Lưu
   
   *(Lấy API key thật tại https://aistudio.google.com/apikey nếu muốn dùng chatbot)*

4. **Chạy lệnh:**
   ```bash
   npm run dev
   ```

5. **Đợi thấy:** `Local: http://localhost:5173/`

6. **Mở trình duyệt → vào: http://localhost:5173**

---

## Hoàn thành!

Bây giờ có **2 cửa sổ Terminal** đang chạy:
- **Backend** (port 8080)
- **Frontend** (port 5173)

**Để tắt:** Nhấn `Ctrl+C` trong mỗi cửa sổ

