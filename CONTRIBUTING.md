# Quy ước làm việc nhóm (Contributing Guidelines) — TalentLink

Tài liệu này thống nhất quy trình quản lý mã nguồn, đặt tên nhánh, viết commit, tạo Pull Request (PR) và review code cho dự án **TalentLink**.

---

## 1. Quy ước nhánh (Branching Convention)

Nhóm sử dụng mô hình phân nhánh chuẩn:

- **`main`**: Nhánh chính chứa code ổn định, đã qua nghiệm thu (Production-ready). **Cấm push trực tiếp.**
- **`develop`** *(tùy chọn theo sprint)*: Nhánh tích hợp các tính năng trong sprint trước khi合 nhất vào `main`. **Cấm push trực tiếp.**
- **`feature/<tên-task>`**: Nhánh phát triển tính năng mới hoặc task được giao.
- **`bugfix/<tên-task>`**: Nhánh sửa lỗi phát sinh trong quá trình phát triển.
- **`hotfix/<tên-task>`**: Nhánh sửa lỗi khẩn cấp trên nhánh `main`.

### Quy tắc đặt `<tên-task>`:
- Sử dụng chữ thường (`lowercase`), không dấu, các từ nối nhau bằng dấu gạch ngang (`kebab-case`).
- Ngắn gọn, mô tả đúng chức năng của task.

**Ví dụ hợp lệ:**
- `feature/init-project-structure`
- `feature/auth-login-register`
- `feature/job-posting-crud`
- `bugfix/fix-login-redirect`

**Ví dụ KHÔNG hợp lệ:**
- `duong-branch`, `test`, `Feature/Login_Page`, `task1`

---

## 2. Quy ước Commit Message (Conventional Commits)

Mọi commit phải tuân theo định dạng:

```text
<type>(<scope tùy chọn>): <mô tả ngắn gọn>
```

### Danh sách `<type>` chuẩn:
| Type | Ý nghĩa | Ví dụ |
| :--- | :--- | :--- |
| `feat` | Thêm mới một tính năng | `feat(auth): thêm trang đăng nhập bằng Thymeleaf` |
| `fix` | Sửa lỗi (bug) | `fix(security): mở quyền truy cập trang chủ cho guest` |
| `docs` | Thêm/cập nhật tài liệu (`README`, `CONTRIBUTING`...) | `docs: cập nhật quy ước nhánh và mẫu PR` |
| `refactor` | Tái cấu trúc code nhưng không làm thay đổi hành vi | `refactor(service): tách xử lý validate ra DTO` |
| `style` | Chỉnh sửa UI/CSS hoặc format code, khoảng trắng | `style(ui): căn chỉnh layout header trang chủ` |
| `test` | Thêm hoặc sửa unit test / integration test | `test(service): thêm unit test cho UserService` |
| `chore` | Cấu hình build, `pom.xml`, Flyway, `.gitignore`... | `chore(config): cấu hình profile dev đọc biến môi trường DB` |

### Lưu ý khi commit:
- Mô tả viết ngắn gọn, rõ ràng (không quá 72 ký tự ở dòng tiêu đề).
- Không gộp nhiều việc không liên quan vào cùng 1 commit.
- Không commit các file cấu hình cá nhân (`.env`, `.idea/workspace.xml`, `target/`).

---

## 3. Quy trình tạo Pull Request (PR) & Code Review

### Bước 1: Tạo nhánh làm việc từ nhánh chính
```bash
git checkout main
git pull origin main
git checkout -b feature/<tên-task>
```

### Bước 2: Code & Commit theo quy ước
- Tuân thủ kiến trúc phân tầng trong `com.talentlink.talentlink`:
  - `controller`: Nhận HTTP request, điều hướng view Thymeleaf hoặc trả response.
  - `service`: Xử lý logic nghiệp vụ.
  - `repository`: Giao tiếp DB thông qua Spring Data JPA.
  - `entity`: Các lớp ánh xạ bảng CSDL (`@Entity`).
  - `dto`: Đối tượng truyền dữ liệu giữa Controller và Service kèm annotation Validation (`@Valid`, `@NotBlank`, ...).
  - `config`: Cấu hình hệ thống (`SecurityConfig`, WebConfig...).
- Kiểm tra ứng dụng chạy không lỗi trước khi push:
```bash
./mvnw clean spring-boot:run
```

### Bước 3: Push nhánh và mở Pull Request
```bash
git push -u origin feature/<tên-task>
```
- Lên GitHub tạo **Pull Request**.
- Điền đầy đủ thông tin theo mẫu có sẵn trong [`.github/pull_request_template.md`](.github/pull_request_template.md).
- Gán (**Assign**) cho mình và chọn ít nhất **1 Reviewer** trong nhóm.

### Bước 4: Review & Merge (Bắt buộc tối thiểu 1 Review)
- **Quy định bắt buộc:** Mọi PR **phải có ít nhất 1 thành viên khác trong nhóm Review và chọn `Approve`** trước khi được phép Merge.
- Không tự merge PR của chính mình khi chưa có `Approve`.
- Sau khi được `Approve` và không có xung đột (conflict), thực hiện **Squash and merge** hoặc **Merge pull request** và xóa nhánh `feature/<tên-task>` trên remote.

---

## 4. Hướng dẫn thiết lập Branch Protection trên GitHub (Dành cho Repo Admin)

Để hệ thống GitHub tự động chặn merge khi chưa đủ 1 review:

1. Vào repository trên GitHub -> **Settings** -> **Branches** (hoặc **Rules** -> **Rulesets**).
2. Nhấn **Add classic branch protection rule** (hoặc **New branch ruleset**).
3. Tại **Branch name pattern**, nhập `main` (lặp lại tương tự cho `develop` nếu dùng).
4. Tích chọn các mục sau:
   - [x] **Require a pull request before merging**
   - [x] **Require approvals** -> Chọn số lượng: **`1`**
   - [x] **Dismiss stale pull request approvals when new commits are pushed** (Hủy approve cũ nếu tác giả push thêm code mới)
   - [x] **Do not allow bypassing the above settings** (Áp dụng cho cả Admin nhóm)
5. Nhấn **Create** / **Save changes**.
