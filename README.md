# TalentLink

Dự án **TalentLink** xây dựng trên nền tảng **Spring Boot (Java 17)** theo kiến trúc phân tầng, sử dụng **Thymeleaf**, **Spring Security**, **Spring Data JPA**, **Bean Validation**, **MySQL** và **Flyway Migration**.

---

## 1. Công nghệ sử dụng (Tech Stack)

- **Ngôn ngữ:** Java 17
- **Framework:** Spring Boot (Web MVC, Thymeleaf, Security, Data JPA, Validation)
- **Cơ sở dữ liệu:** MySQL
- **Quản lý Migration DB:** Flyway (`src/main/resources/db/migration`)
- **Build Tool:** Maven (`./mvnw`)

---

## 2. Cấu trúc Package theo tầng

Mã nguồn được tổ chức trong package gốc `com.talentlink.talentlink` theo 6 tầng chuẩn:

```text
src/
├── main/
│   ├── java/com/talentlink/talentlink/
│   │   ├── config/          # Cấu hình hệ thống (SecurityConfig, WebConfig, ...)
│   │   ├── controller/      # Tầng điều khiển (MVC Controllers, xử lý request/view)
│   │   ├── dto/             # Data Transfer Objects & Bean Validation (@NotBlank, @Email, ...)
│   │   ├── entity/          # JPA Entities ánh xạ bảng trong MySQL
│   │   ├── repository/      # Spring Data JPA Repositories
│   │   ├── service/         # Tầng xử lý nghiệp vụ (Business Logic)
│   │   └── TalentlinkApplication.java
│   └── resources/
│       ├── db/migration/    # Các file SQL migration của Flyway (V1__init.sql, ...)
│       ├── static/          # Tài nguyên tĩnh (CSS, JS, Images)
│       ├── templates/       # Giao diện Thymeleaf (index.html, ...)
│       ├── application.properties
│       └── application-dev.properties
└── test/
```

---

## 3. Cấu hình môi trường & Chạy dự án (Nghiệm thu)

### Yêu cầu cài đặt
- **JDK 17+**
- **MySQL 8.0+** đang chạy tại local

### Bước 1: Clone repository
```bash
git clone https://github.com/VTADuong/talentlink-project.git
cd talentlink-project
```

### Bước 2: Thiết lập biến môi trường cho profile `dev`
Dự án sử dụng profile mặc định là `dev` ([application-dev.properties](src/main/resources/application-dev.properties)) và đọc cấu hình kết nối MySQL thông qua các biến môi trường (có thể tham khảo tại [.env.example](.env.example)):

| Biến môi trường | Mô tả | Giá trị mặc định (khi không truyền) |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Profile kích hoạt | `dev` |
| `SERVER_PORT` | Cổng chạy web server | `8080` |
| `DB_HOST` | Địa chỉ MySQL host | `localhost` |
| `DB_PORT` | Cổng MySQL | `3306` |
| `DB_NAME` | Tên cơ sở dữ liệu | `talentlink_db` |
| `DB_URL` | Chuỗi JDBC URL đầy đủ (ghi đè host/port/name nếu đặt) | `jdbc:mysql://localhost:3306/talentlink_db?createDatabaseIfNotExist=true...` |
| `DB_USERNAME` | Tài khoản MySQL | `root` |
| `DB_PASSWORD` | Mật khẩu MySQL | *(trống)* |

**Cách truyền biến môi trường trên macOS / Linux:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=mat_khau_cua_ban
export DB_NAME=talentlink_db
```

**Cách truyền trên Windows (PowerShell):**
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="mat_khau_cua_ban"
$env:DB_NAME="talentlink_db"
```

*(Hoặc cấu hình trực tiếp trong **Run/Debug Configurations -> Environment variables** của IntelliJ IDEA: `DB_USERNAME=root;DB_PASSWORD=your_password;DB_NAME=talentlink_db`)*.

### Bước 3: Chạy ứng dụng & Nghiệm thu trang chủ trống
```bash
./mvnw spring-boot:run
```
Sau khi ứng dụng khởi động thành công, truy cập trình duyệt tại:
- **URL:** `http://localhost:8080/`
- **Kết quả nghiệm thu:**
  - Khi chạy trên một database MySQL trống (`talentlink_db`), Flyway tự động thực thi:
    - [`V1__init.sql`](src/main/resources/db/migration/V1__init.sql): Tự động tạo đầy đủ **13 bảng**, khóa ngoại và chỉ mục theo thiết kế ERD.
    - [`V2__seed_sample_data.sql`](src/main/resources/db/migration/V2__seed_sample_data.sql): Tự động nạp dữ liệu mẫu giả lập gồm **1 Admin**, **3 Đối tác (kèm 3 Công ty)**, **15 Hồ sơ sinh viên giả** (đủ Học vấn, Kỹ năng mức 1–5, Dự án, Chứng chỉ) và **30 Kỹ năng**.
  - Hiển thị trang chủ trống (`index.html`), không bị chặn bởi màn hình đăng nhập mặc định của Spring Security.

---

## 4. Danh sách Tài khoản mẫu (Sample Accounts — Seed Data `V2`)

> **Lưu ý:** Toàn bộ tài khoản và hồ sơ dưới đây là **dữ liệu giả lập (`@talentlink.demo`)** phục vụ kiểm thử và nghiệm thu, tuyệt đối không sử dụng dữ liệu cá nhân của người thật.

- **Mật khẩu chung cho tất cả tài khoản mẫu:** `123456aA@`

### 4.1. Tài khoản Quản trị viên (1 Admin)
| STT | Email đăng nhập | Mật khẩu | Vai trò (`role`) | Họ tên hiển thị |
| :-: | :--- | :--- | :--- | :--- |
| 1 | `admin@talentlink.demo` | `123456aA@` | `ADMIN` | Quản Trị Viên Demo |

### 4.2. Tài khoản Đối tác / Nhà tuyển dụng (3 Đối tác kèm 3 Công ty)
| STT | Email đăng nhập | Mật khẩu | Vai trò (`role`) | Công ty gắn kèm (`companies`) |
| :-: | :--- | :--- | :--- | :--- |
| 1 | `partner1@talentlink.demo` | `123456aA@` | `RECRUITER` | Công ty TNHH Công Nghệ Sao Việt Demo |
| 2 | `partner2@talentlink.demo` | `123456aA@` | `RECRUITER` | Tập Đoàn Phần Mềm Ánh Dương Demo |
| 3 | `partner3@talentlink.demo` | `123456aA@` | `RECRUITER` | Công ty Cổ Phần Giải Pháp Số Phương Đông |

### 4.3. Tài khoản Sinh viên / Ứng viên giả lập (15 Hồ sơ sinh viên đủ các phần)
Mỗi hồ sơ đều có đầy đủ dữ liệu ở các bảng `profiles`, `educations`, `profile_skills` (mức 1–5), `projects`, `certificates`:

| STT | Email đăng nhập | Mật khẩu | Họ tên giả lập | Vị trí chuyên môn (`headline`) | Trạng thái hồ sơ |
| :-: | :--- | :--- | :--- | :--- | :--- |
| 1 | `student01@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 01 | Java Backend Intern / Fresher | `APPROVED` |
| 2 | `student02@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 02 | Fullstack Java & React Developer | `APPROVED` |
| 3 | `student03@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 03 | Spring Boot & Microservices Fresher | `APPROVED` |
| 4 | `student04@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 04 | Frontend React & TypeScript Fresher | `APPROVED` |
| 5 | `student05@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 05 | Backend Developer (Java / PostgreSQL) | `APPROVED` |
| 6 | `student06@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 06 | Node.js & Vue.js Web Developer | `APPROVED` |
| 7 | `student07@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 07 | DevOps & Cloud Intern | `APPROVED` |
| 8 | `student08@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 08 | Java Web MVC & Thymeleaf Developer | `APPROVED` |
| 9 | `student09@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 09 | Python Backend & Data Intern | `APPROVED` |
| 10 | `student10@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 10 | Mobile Flutter & REST API Developer | `APPROVED` |
| 11 | `student11@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 11 | .NET Core & SQL Developer Fresher | `APPROVED` |
| 12 | `student12@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 12 | Golang & Distributed Systems Intern | `PENDING` |
| 13 | `student13@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 13 | QA Automation & JUnit Tester | `PENDING` |
| 14 | `student14@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 14 | Java Enterprise Intern | `REJECTED` |
| 15 | `student15@talentlink.demo` | `123456aA@` | Sinh Viên Giả Lập 15 | Junior Fullstack Software Engineer | `APPROVED` |

---

## 5. Quy ước làm việc nhóm

Chi tiết quy trình làm việc nhóm được quy định tại **[CONTRIBUTING.md](CONTRIBUTING.md)**:

1. **Quy ước nhánh:** `feature/<tên-task>` (ví dụ: `feature/user-authentication`), `bugfix/<tên-task>`, `hotfix/<tên-task>`.
2. **Quy ước Commit:** Theo chuẩn Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `style:`, `test:`, `chore:`).
3. **Mẫu Pull Request:** Tự động áp dụng từ [`.github/pull_request_template.md`](.github/pull_request_template.md).
4. **Quy định Review:** **Bắt buộc tối thiểu 1 review (Approve)** từ thành viên khác trước khi merge vào nhánh chính.
