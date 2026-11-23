# Tài liệu Phân tích & Thiết kế — TalentLink (`docs/`)

Thư mục này lưu trữ tài liệu đặc tả yêu cầu (**User Stories**) và thiết kế giao diện khung (**Wireframes**) cho hệ thống kết nối ứng viên và nhà tuyển dụng **TalentLink** (Task **`T4E7A9A`**).

---

## Danh mục tài liệu

1. **[User Stories & Tiêu chí chấp nhận (`docs/user-stories.md`)](user-stories.md)**
   - Gồm **18 User Stories** theo chuẩn *"Là ..., tôi muốn ..., để ..."* cho **3 vai trò**:
     - **Ứng viên (Candidate):** 6 User Stories (`US-01` -> `US-06`)
     - **Nhà tuyển dụng (Recruiter):** 6 User Stories (`US-07` -> `US-12`)
     - **Quản trị viên (Admin):** 6 User Stories (`US-13` -> `US-18`)
   - Mỗi User Story đều đi kèm **Tiêu chí chấp nhận (Acceptance Criteria)** chi tiết.

2. **[Wireframe 8 Màn hình chính (`docs/wireframes.md`)](wireframes.md)**
   - Bao gồm hình ảnh Wireframe (`.svg`), sơ đồ bố cục (Layout) và đặc tả trường dữ liệu cho 8 màn hình chính:
     1. **Đăng nhập** ([`wireframes/01-login.svg`](wireframes/01-login.svg))
     2. **Đăng ký** ([`wireframes/02-register.svg`](wireframes/02-register.svg))
     3. **Hồ sơ của tôi** ([`wireframes/03-my-profile.svg`](wireframes/03-my-profile.svg))
     4. **Quản trị tài khoản** ([`wireframes/04-account-management.svg`](wireframes/04-account-management.svg))
     5. **Hàng chờ duyệt** ([`wireframes/05-approval-queue.svg`](wireframes/05-approval-queue.svg))
     6. **Tìm ứng viên** ([`wireframes/06-candidate-search.svg`](wireframes/06-candidate-search.svg))
     7. **Chi tiết ứng viên** ([`wireframes/07-candidate-detail.svg`](wireframes/07-candidate-detail.svg))
     8. **Yêu cầu phỏng vấn** ([`wireframes/08-interview-requests.svg`](wireframes/08-interview-requests.svg))

3. **[Bản Wireframe HTML trực quan (`docs/wireframes/index.html`)](wireframes/index.html)**
   - Có thể mở trực tiếp trên trình duyệt để xem và chuyển đổi qua lại giữa 8 màn hình wireframe.

4. **[Thiết kế CSDL & Sơ đồ ERD 13 bảng (`docs/database-design.md`)](database-design.md)** *(Task `T4E7A9B`)*
   - Đặc tả chi tiết 13 bảng: `users`, `profiles`, `educations`, `skills`, `profile_skills` (mức 1–5), `projects`, `certificates`, `companies`, `shortlists`, `interview_requests`, `profile_reviews`, `notifications`, `profile_views`.
   - Sơ đồ ERD trực quan ([`erd/talentlink-erd.svg`](erd/talentlink-erd.svg) & Mermaid `erDiagram`) + Mã nguồn DBML ([`erd/schema.dbml`](erd/schema.dbml)).
   - Giải thích chi tiết toàn bộ 16 Khóa ngoại (Foreign Keys) và 11 Chỉ mục chính (Indexes).
