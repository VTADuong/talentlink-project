## 1. Mô tả thay đổi (Description)
<!-- Mô tả ngắn gọn các thay đổi trong Pull Request này -->
- 

## 2. Thông tin Task & Nhánh (Task & Branch Info)
- **Nhánh hiện tại:** `feature/<tên-task>`
- **Nhánh đích:** `develop` / `main`
- **Issue / Task liên quan:** #

## 3. Loại thay đổi (Type of Change)
- [ ] `feat`: Thêm tính năng mới
- [ ] `fix`: Sửa lỗi (bug fix)
- [ ] `docs`: Cập nhật tài liệu
- [ ] `refactor`: Tái cấu trúc code (không đổi logic nghiệp vụ)
- [ ] `style`: Chỉnh sửa giao diện / format code
- [ ] `test`: Thêm hoặc cập nhật test
- [ ] `chore`: Cập nhật cấu hình, build, dependencies

## 4. Hướng dẫn kiểm thử / Nghiệm thu (How to Test)
1. Checkout nhánh này và cấu hình biến môi trường DB (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
2. Chạy ứng dụng: `./mvnw spring-boot:run`
3. Kiểm tra tại: `http://localhost:8080`
4. Kết quả mong đợi:

## 5. Ảnh chụp màn hình (Screenshots - nếu có thay đổi UI)
<!-- Đính kèm ảnh chụp màn hình trước/sau nếu thay đổi giao diện Thymeleaf -->

## 6. Checklist trước khi Merge
- [ ] Tên nhánh tuân thủ đúng quy ước `feature/<tên-task>` (hoặc `bugfix/<tên-task>`, `hotfix/<tên-task>`).
- [ ] Commit message tuân thủ chuẩn Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, ...).
- [ ] Code tuân thủ đúng cấu trúc phân tầng (`controller` / `service` / `repository` / `entity` / `dto` / `config`).
- [ ] Không hardcode thông tin nhạy cảm (DB password, secret keys) trong code hoặc `application*.properties`.
- [ ] Nếu có thay đổi schema DB, đã tạo file migration Flyway mới `V<version>__<mo_ta>.sql` trong `src/main/resources/db/migration/`.
- [ ] Dự án build và chạy thành công ở local (`./mvnw clean compile`).
- [ ] **Đã có tối thiểu 1 thành viên khác trong nhóm Review & Approve PR này.**
