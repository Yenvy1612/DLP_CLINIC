source/
├── 📄 .gitignore
├── 📄 README.md
├── 📄 PROJECT_STRUCTURE.md
├── 📄 file_query.sql
│
├── 📂 Backend/
│   ├── 📄 .gitattributes
│   ├── 📄 .gitignore
│   ├── 📄 pom.xml
│   ├── 📄 mvnw
│   ├── 📄 mvnw.cmd
│   ├── 📂 .mvn/
│   └── 📂 src/
│       ├── 📂 main/
│       │   ├── 📂 java/com/acare/backend/
│       │   │   ├── 📄 BackendApplication.java
│       │   │   │
│       │   │   ├── 📂 config/
│       │   │   │   └── CorsConfig
│       │   │   │
│       │   │   ├── 📂 controller/
│       │   │   │   ├── UserController
│       │   │   │   ├── AppointmentController
│       │   │   │   ├── ServiceController
│       │   │   │   ├── RoomController
│       │   │   │   └── ActivityController
│       │   │   │   └── AuthController
│       │   │   │   └── CustomErrorController
│       │   │   │
│       │   │   ├── 📂 dto/
│       │   │   │   └── ResponseDTO
│       │   │   │
│       │   │   ├── 📂 entity/
│       │   │   │   ├── User.java
│       │   │   │   ├── Appointment.java
│       │   │   │   ├── Service.java
│       │   │   │   ├── Room.java
│       │   │   │   └── ActivityLog.java
│       │   │   │
│       │   │   ├── 📂 repository/
│       │   │   │   ├── UserRepository
│       │   │   │   ├── AppointmentRepository
│       │   │   │   ├── ServiceRepository
│       │   │   │   ├── RoomRepository
│       │   │   │   └── ActivityLogRepository
│       │   │   │
│       │   │   └── 📂 service/
│       │   │       ├── UserService
│       │   │       ├── AppointmentService
│       │   │       ├── ServiceService
│       │   │       └── ActivityLogService
│       │   │
│       │   └── 📂 resources/
│       │       └── 📄 application.properties
│       │
│       └── 📂 test/
│           └── 📂 java/com/acare/backend/
│               └── BackendApplicationTests.java
│
└── 📂 Frontend/
    ├── 📄 .gitignore
    ├── 📄 package.json
    ├── 📄 package-lock.json
    ├── 📄 vite.config.js
    ├── 📄 index.html
    ├── 📂 public/
    │
    └── 📂 src/
        ├── 📄 main.jsx
        ├── 📄 App.jsx
        │
        ├── 📂 api/
        │   ├── 📄 auth.js
        │   ├── 📄 chatbot.js
        │   ├── 📄 getAnswerFromGemini.js
        │   ├── 📄 learningData.js
        │   │
        │   ├── 📂 activity/
        │   │   └── getRecentActivities.js
        │   │
        │   ├── 📂 appointment/
        │   │   ├── getAppointments.js
        │   │   ├── getAppointmentById.js
        │   │   ├── getAppointmentsByDoctorId.js
        │   │   ├── getTodayAppointments.js
        │   │   ├── filterAppointments.js
        │   │   ├── addAppoinment.js
        │   │   ├── 📂 delete/
        │   │   ├── 📂 done/
        │   │   ├── 📂 pending/
        │   │   └── 📂 update/
        │   │
        │   ├── 📂 room/
        │   │   ├── getRoom.js
        │   │   ├── getRoomById.js
        │   │   ├── addRoom.js
        │   │   ├── updateRoom.js
        │   │   ├── deleteRoom.js
        │   │   └── searchRooms.js
        │   │
        │   ├── 📂 service/
        │   │   ├── getServices.js
        │   │   ├── getServiceById.js
        │   │   ├── getServiceByName.js
        │   │   ├── addService.js
        │   │   ├── updateService.js
        │   │   ├── deleteService.js
        │   │   └── searchServices.js
        │   │
        │   └── 📂 user/
        │       ├── getUsers.js
        │       ├── getUser.js
        │       ├── getDoctors.js
        │       ├── getPatients.js
        │       ├── addUser.js
        │       ├── updateUser.js
        │       ├── deleteUser.js
        │       └── searchUsers.js
        │
        ├── 📂 assets/
        │   ├── 📂 fonts/
        │   ├── 📂 images/
        │   │   ├── 📂 auth/
        │   │   ├── 📂 booking/
        │   │   ├── 📂 cart/
        │   │   ├── 📂 doctor/
        │   │   ├── 📂 logo/
        │   │   └── 📂 profile/
        │   └── 📂 styles/
        │       └── 📄 index.css
        │
        ├── 📂 components/
        │   ├── 📄 Header.jsx
        │   ├── 📄 Footer.jsx
        │   └── 📄 SideBar.jsx
        │   └── 📄 Chatbot.jsx
        │
        ├── 📂 contexts/
        │   └── 📄 SideBarContext.jsx
        │
        ├── 📂 data/
        │   ├── 📄 baseContextData.js
        │   ├── 📄 faq.js
        │   ├── 📄 footerList.js
        │   └── 📄 headerList.js
        │
        ├── 📂 layouts/
        │   └── 📄 MainLayout.jsx
        │
        ├── 📂 pages/
        │   │
        │   ├── 📄 About.jsx
        │   ├── 📄 Contact.jsx
        │   ├── 📄 Services.jsx
        │   ├── 📄 FAQ.jsx
        │   ├── 📄 Profile.jsx
        │   ├── 📄 EditProfile.jsx
        │   ├── 📄 Instruction.jsx
        │   ├── 📄 NotFound.jsx
        │   │
        │   ├── 📂 admin/
        │   │   │
        │   │   ├── 📂 dashboard/
        │   │   │   ├── Dashboard.jsx
        │   │   │   └── DashboardReportTable.jsx
        │   │   │
        │   │   ├── 📂 user-management/
        │   │   │   ├── Users.jsx
        │   │   │   ├── AddUser.jsx
        │   │   │   ├── UpdateUser.jsx
        │   │   │   └── ShowUser.jsx
        │   │   │
        │   │   ├── 📂 service-management/
        │   │   │   ├── Services.jsx
        │   │   │   ├── AddService.jsx
        │   │   │   ├── UpdateService.jsx
        │   │   │   └── ShowService.jsx
        │   │   │
        │   │   ├── 📂 room-management/
        │   │   │   ├── Rooms.jsx
        │   │   │   ├── AddRoom.jsx
        │   │   │   └── UpdateRoom.jsx
        │   │   │
        │   │   └── 📂 appointment-management/
        │   │       ├── Appointments.jsx
        │   │       └── AppointmentLine.jsx
        │   │
        │   ├── 📂 auth/
        │   │   ├── Login.jsx
        │   │   └── Register.jsx
        │   │
        │   ├── 📂 doctor/
        │   ├── 📂 patient/
        │   └── 📂 home/
        │
        ├── 📂 routes/ 
        │   └── 📄 index.jsx
        │
        └── 📂 utils/
            └── 📄 authUtils.js

