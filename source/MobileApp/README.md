# 📱 ACare Clinic — Android App

## Tổng quan

App Android native (Kotlin) dành cho bệnh nhân của phòng khám ACare. Toàn bộ dữ liệu được lấy từ **Backend API** — app chỉ là giao diện mobile.

---

## Cấu trúc thư mục

```
MobileApp/
├── app/
│   ├── build.gradle              ← Dependencies (Retrofit, OkHttp, Material3...)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/acare/clinic/
│           ├── App.kt            ← Application class (khởi tạo NetworkClient)
│           ├── data/
│           │   ├── model/
│           │   │   └── Models.kt ← Tất cả data classes (Appointment, User...)
│           │   └── network/
│           │       ├── ApiService.kt    ← Retrofit interface
│           │       └── NetworkClient.kt ← OkHttp + CookieJar + Retrofit singleton
│           ├── ui/
│           │   ├── splash/SplashActivity.kt
│           │   ├── auth/LoginActivity.kt
│           │   ├── main/MainActivity.kt  ← Shell + BottomNavigation
│           │   ├── home/HomeFragment.kt
│           │   ├── appointment/AppointmentFragment.kt  (TODO)
│           │   ├── record/MedicalRecordFragment.kt     (TODO)
│           │   └── profile/ProfileFragment.kt          (TODO)
│           └── utils/
│               └── SessionManager.kt   ← EncryptedSharedPreferences
└── res/
    ├── layout/   ← XML layouts
    ├── menu/     ← bottom_nav_menu.xml
    ├── navigation/nav_graph.xml
    └── values/   ← colors, themes, strings
```

---

## Luồng xác thực

```
App khởi động
    ↓
SplashActivity → kiểm tra SessionManager.isLoggedIn()
    ├─ Có session → MainActivity (BottomNav)
    └─ Chưa login → LoginActivity
                        ↓
              POST /api/auth/login
              OkHttp CookieJar lưu access_token, refresh_token cookie
              SessionManager lưu userId, name, role
                        ↓
              MainActivity
```

---

## Cài đặt URL Backend

Mở `app/build.gradle`, sửa `BASE_URL`:

```groovy
// Emulator Android (localhost của máy host)
buildConfigField "String", "BASE_URL", '"http://10.0.2.2:8080/"'

// Thiết bị thật (cùng WiFi)
buildConfigField "String", "BASE_URL", '"http://192.168.x.x:8080/"'

// Production
buildConfigField "String", "BASE_URL", '"https://your-domain.com/"'
```

---

## Tech Stack

| Thành phần | Thư viện |
|---|---|
| Ngôn ngữ | Kotlin |
| UI | Material Design 3 |
| Networking | Retrofit 2 + OkHttp 4 |
| Cookie auth | OkHttp CookieJar |
| Navigation | Navigation Component |
| Async | Kotlin Coroutines |
| Secure storage | EncryptedSharedPreferences |
| Image loading | Coil |

---

## Màn hình

| Màn hình | Mô tả |
|---|---|
| Splash | Logo animation, auto redirect |
| Login | Đăng nhập với email/mật khẩu |
| Register | Đăng ký tài khoản bệnh nhân |
| Home | Chào hỏi theo giờ, quick actions, 3 lịch hẹn gần nhất |
| Appointments | Danh sách lịch hẹn pending / lịch sử |
| Medical Records | Hồ sơ bệnh án |
| Profile | Thông tin cá nhân, đổi mật khẩu, đăng xuất |

---

## Tích hợp Agent SDK (sắp có)

Khi có Agent SDK, thêm vào `app/build.gradle`:

```groovy
// Bỏ comment dòng này:
// implementation 'com.your.agent:agent-sdk:1.0.0'
```

Và khởi tạo trong `App.kt`:

```kotlin
// Bỏ comment:
// AgentSDK.init(this, BuildConfig.BASE_URL)
```

---

## Build APK

```bash
cd MobileApp
./gradlew assembleDebug
# APK tại: app/build/outputs/apk/debug/app-debug.apk
```

> Yêu cầu: Android Studio hoặc Android SDK Command Line Tools
