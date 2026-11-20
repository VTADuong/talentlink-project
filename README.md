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
git clone https://github.com/VTADuong/TalentLink.git
cd TalentLink
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
- **Kết quả nghiệm thu:** Hiển thị trang chủ trống (`index.html`), không bị chặn bởi màn hình đăng nhập mặc định của Spring Security và Flyway tự động chạy migration `V1__init.sql`.

---

## 4. Quy ước làm việc nhóm

Chi tiết quy trình làm việc nhóm được quy định tại **[CONTRIBUTING.md](CONTRIBUTING.md)**:

1. **Quy ước nhánh:** `feature/<tên-task>` (ví dụ: `feature/user-authentication`), `bugfix/<tên-task>`, `hotfix/<tên-task>`.
2. **Quy ước Commit:** Theo chuẩn Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `style:`, `test:`, `chore:`).
3. **Mẫu Pull Request:** Tự động áp dụng từ [`.github/pull_request_template.md`](.github/pull_request_template.md).
4. **Quy định Review:** **Bắt buộc tối thiểu 1 review (Approve)** từ thành viên khác trước khi merge vào nhánh chính.
