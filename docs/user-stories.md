# Danh sách User Stories & Tiêu chí chấp nhận — TalentLink

**Mã Task:** `T4E7A9A`  
**Dự án:** TalentLink — Hệ thống kết nối Ứng viên & Nhà tuyển dụng  
**Tổng số User Stories:** 18 User Stories (chia đều cho 3 vai trò: **Ứng viên**, **Nhà tuyển dụng**, **Quản trị viên**).

---

## Tổng quan 3 Vai trò trong hệ thống

| STT | Vai trò (Role) | Mô tả trách nhiệm chính | Số lượng User Story |
| :-: | :--- | :--- | :-: |
| 1 | **Ứng viên (Candidate)** | Đăng ký, đăng nhập, tạo & cập nhật hồ sơ năng lực, gửi hồ sơ chờ duyệt, nhận và phản hồi yêu cầu phỏng vấn. | 6 (`US-01` – `US-06`) |
| 2 | **Nhà tuyển dụng (Recruiter)** | Đăng ký tài khoản tuyển dụng, tìm kiếm & lọc ứng viên, xem chi tiết hồ sơ ứng viên, gửi và quản lý yêu cầu phỏng vấn. | 6 (`US-07` – `US-12`) |
| 3 | **Quản trị viên (Admin)** | Quản trị tài khoản người dùng (khóa/mở khóa/phân quyền), xét duyệt hồ sơ trong hàng chờ duyệt, giám sát hệ thống. | 6 (`US-13` – `US-18`) |

---

## PHẦN 1: Vai trò Ứng viên (Candidate)

### US-01: Đăng ký tài khoản Ứng viên
- **User Story:**  
  > *"Là **một ứng viên mới**, tôi muốn **đăng ký tài khoản với vai trò Ứng viên bằng họ tên, email và mật khẩu**, để **có thể truy cập hệ thống và tạo hồ sơ năng lực cá nhân**."*
- **Màn hình liên quan:** Đăng ký (`/register`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Form đăng ký hiển thị đầy đủ các trường: Họ và tên, Email, Số điện thoại, Vai trò (`Ứng viên` / `Nhà tuyển dụng`), Mật khẩu, Xác nhận mật khẩu.
  - [x] Kiểm tra hợp lệ (Validation): Email đúng định dạng và chưa tồn tại trong CSDL; mật khẩu tối thiểu 8 ký tự; xác nhận mật khẩu phải trùng khớp.
  - [x] Mật khẩu được mã hóa bằng `BCryptPasswordEncoder` trước khi lưu vào MySQL.
  - [x] Sau khi đăng ký thành công, hệ thống hiển thị thông báo thành công và chuyển hướng sang màn hình **Đăng nhập** (`/login`).

---

### US-02: Đăng nhập vào hệ thống
- **User Story:**  
  > *"Là **một ứng viên đã có tài khoản**, tôi muốn **đăng nhập bằng email và mật khẩu**, để **xác thực danh tính và quản lý hồ sơ cũng như lời mời phỏng vấn của mình**."*
- **Màn hình liên quan:** Đăng nhập (`/login`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Cho phép nhập Email, Mật khẩu và tùy chọn "Ghi nhớ đăng nhập" (Remember me).
  - [x] Khi nhập đúng thông tin và tài khoản đang ở trạng thái `ACTIVE`, hệ thống tạo phiên đăng nhập (Spring Security Session) và chuyển hướng tới trang chủ hoặc trang **Hồ sơ của tôi**.
  - [x] Khi nhập sai email hoặc mật khẩu, hiển thị thông báo lỗi *"Email hoặc mật khẩu không chính xác"* (không tiết lộ trường nào sai).
  - [x] Nếu tài khoản bị Admin khóa (`LOCKED`), hiển thị thông báo *"Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Quản trị viên"*.

---

### US-03: Tạo và cập nhật "Hồ sơ của tôi"
- **User Story:**  
  > *"Là **một ứng viên**, tôi muốn **cập nhật thông tin hồ sơ cá nhân (chức danh, giới thiệu bản thân, kỹ năng, số năm kinh nghiệm, học vấn, dự án và link CV)**, để **nhà tuyển dụng có thể đánh giá đúng năng lực của tôi**."*
- **Màn hình liên quan:** Hồ sơ của tôi (`/candidate/profile`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Hiển thị form thông tin hồ sơ hiện tại của ứng viên gồm: Ảnh đại diện, Họ tên, Vị trí ứng tuyển (Title), Số năm kinh nghiệm, Địa điểm làm việc mong muốn, Mức lương kỳ vọng, Danh sách kỹ năng (Skills tags), Kinh nghiệm làm việc, Học vấn và file/link CV.
  - [x] Bắt buộc nhập các trường quan trọng: Họ tên, Vị trí chuyên môn, ít nhất 1 Kỹ năng, Số năm kinh nghiệm (>= 0).
  - [x] Khi nhấn **"Lưu nháp"**, thông tin được cập nhật vào DB với trạng thái `DRAFT` và hiển thị thông báo *"Lưu hồ sơ thành công"*.

---

### US-04: Gửi hồ sơ vào Hàng chờ duyệt
- **User Story:**  
  > *"Là **một ứng viên**, tôi muốn **gửi hồ sơ đã hoàn thiện của mình để Quản trị viên xét duyệt**, để **hồ sơ của tôi được công khai trong danh sách Tìm ứng viên cho các nhà tuyển dụng**."*
- **Màn hình liên quan:** Hồ sơ của tôi (`/candidate/profile`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Chỉ cho phép bấm nút **"Gửi duyệt hồ sơ"** khi các trường bắt buộc trong hồ sơ đã được điền đầy đủ.
  - [x] Sau khi gửi duyệt, trạng thái hồ sơ chuyển từ `DRAFT` (hoặc `REJECTED`) sang `PENDING_APPROVAL` (Chờ duyệt).
  - [x] Ứng viên nhìn thấy nhãn trạng thái hiện tại của hồ sơ trên màn hình **Hồ sơ của tôi** (`Nháp`, `Đang chờ duyệt`, `Đã duyệt`, hoặc `Từ chối` kèm lý do từ chối từ Admin).

---

### US-05: Xem và phản hồi Yêu cầu phỏng vấn
- **User Story:**  
  > *"Là **một ứng viên**, tôi muốn **xem danh sách các yêu cầu phỏng vấn gửi tới mình và chọn Chấp nhận hoặc Từ chối**, để **chủ động sắp xếp lịch phỏng vấn với nhà tuyển dụng phù hợp**."*
- **Màn hình liên quan:** Yêu cầu phỏng vấn (`/interviews`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Hiển thị danh sách yêu cầu phỏng vấn nhận được gồm: Tên công ty/Nhà tuyển dụng, Vị trí tuyển dụng, Thời gian phỏng vấn đề xuất, Hình thức (`Online` / `Offline`), Địa điểm/Link họp, Ghi chú và Trạng thái (`PENDING`, `ACCEPTED`, `DECLINED`).
  - [x] Với các yêu cầu đang ở trạng thái `PENDING`, ứng viên có thể bấm **"Chấp nhận"** hoặc **"Từ chối"** (kèm ghi chú phản hồi).
  - [x] Sau khi phản hồi, trạng thái yêu cầu được cập nhật ngay lập tức và hiển thị cho cả Ứng viên lẫn Nhà tuyển dụng.

---

### US-06: Bật/Tắt trạng thái "Đang tìm việc"
- **User Story:**  
  > *"Là **một ứng viên**, tôi muốn **bật hoặc tắt trạng thái 'Đang sẵn sàng nhận việc' trên hồ sơ của tôi**, để **chỉ nhận lời mời phỏng vấn khi tôi thực sự có nhu cầu tìm việc mới**."*
- **Màn hình liên quan:** Hồ sơ của tôi (`/candidate/profile`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Có nút gạt (toggle) trạng thái **"Đang tìm việc (Open to Work)"** trên màn hình **Hồ sơ của tôi**.
  - [x] Khi tắt trạng thái này, hồ sơ ứng viên sẽ tạm ẩn khỏi kết quả **Tìm ứng viên** của Nhà tuyển dụng dù hồ sơ đã được duyệt (`APPROVED`).

---

## PHẦN 2: Vai trò Nhà tuyển dụng (Recruiter)

### US-07: Đăng ký tài khoản Nhà tuyển dụng
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **đăng ký tài khoản với vai trò Nhà tuyển dụng kèm thông tin công ty**, để **có thể tìm kiếm ứng viên và gửi lời mời phỏng vấn trên TalentLink**."*
- **Màn hình liên quan:** Đăng ký (`/register`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Khi chọn vai trò **`Nhà tuyển dụng`** trên form Đăng ký, hệ thống ghi nhận role `ROLE_RECRUITER` (và thông tin Tên công ty / Website công ty).
  - [x] Kiểm tra email doanh nghiệp/cá nhân hợp lệ, không trùng lặp và mật khẩu đạt chuẩn bảo mật.
  - [x] Sau khi đăng ký và đăng nhập thành công, thanh điều hướng hiển thị menu dành cho Nhà tuyển dụng (**Tìm ứng viên**, **Yêu cầu phỏng vấn**).

---

### US-08: Tìm kiếm và lọc ứng viên theo tiêu chí
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **tìm kiếm và lọc danh sách ứng viên theo từ khóa, kỹ năng, số năm kinh nghiệm và địa điểm**, để **nhanh chóng tìm được ứng viên phù hợp với vị trí đang tuyển**."*
- **Màn hình liên quan:** Tìm ứng viên (`/candidates`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Chỉ hiển thị các hồ sơ ứng viên đã được Admin phê duyệt (`status = APPROVED`) và đang bật trạng thái tìm việc.
  - [x] Hỗ trợ bộ lọc kết hợp: Từ khóa (tên/chức danh), Kỹ năng (Java, Spring Boot, React, SQL...), Số năm kinh nghiệm tối thiểu, Địa điểm (Hà Nội, TP.HCM, Đà Nẵng, Remote).
  - [x] Kết quả hiển thị dạng lưới thẻ (Card) có phân trang, mỗi thẻ tóm tắt: Họ tên, Chức danh, Số năm kinh nghiệm, Địa điểm, Top kỹ năng và nút **"Xem chi tiết"**.

---

### US-09: Xem Chi tiết hồ sơ ứng viên
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **xem trang chi tiết hồ sơ của một ứng viên**, để **đánh giá toàn diện kinh nghiệm làm việc, học vấn, dự án và CV trước khi mời phỏng vấn**."*
- **Màn hình liên quan:** Chi tiết ứng viên (`/candidates/{id}`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Hiển thị đầy đủ các mục trong hồ sơ ứng viên: Thông tin tổng quan, Giới thiệu bản thân, Danh sách kỹ năng, Quá trình làm việc, Học vấn, Dự án nổi bật và nút xem/tải CV.
  - [x] Trên trang Chi tiết ứng viên có nút nổi bật **"Gửi yêu cầu phỏng vấn"** để mở form đặt lịch phỏng vấn trực tiếp cho ứng viên đó.

---

### US-10: Gửi Yêu cầu phỏng vấn cho ứng viên
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **gửi yêu cầu phỏng vấn kèm vị trí tuyển dụng, ngày giờ, hình thức phỏng vấn và lời nhắn cho ứng viên**, để **mời ứng viên tiềm năng tham gia buổi phỏng vấn của công ty**."*
- **Màn hình liên quan:** Chi tiết ứng viên (`/candidates/{id}`) & Yêu cầu phỏng vấn (`/interviews`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Form gửi yêu cầu phỏng vấn bao gồm: Vị trí tuyển dụng, Thời gian phỏng vấn (phải là thời điểm trong tương lai), Hình thức (`Online` / `Trực tiếp`), Địa điểm hoặc Link phòng họp, Nội dung lời nhắn.
  - [x] Kiểm tra không cho phép gửi trùng yêu cầu phỏng vấn đang ở trạng thái `PENDING` cho cùng một ứng viên ở cùng một vị trí.
  - [x] Sau khi gửi thành công, bản ghi mới được tạo với trạng thái `PENDING` và xuất hiện trong màn hình **Yêu cầu phỏng vấn** của cả Nhà tuyển dụng và Ứng viên.

---

### US-11: Quản lý và theo dõi các Yêu cầu phỏng vấn đã gửi
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **theo dõi danh sách các yêu cầu phỏng vấn đã gửi và lọc theo trạng thái (Chờ phản hồi, Đã chấp nhận, Đã từ chối)**, để **nắm bắt lịch phỏng vấn và chủ động liên hệ ứng viên**."*
- **Màn hình liên quan:** Yêu cầu phỏng vấn (`/interviews`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Hiển thị bảng/danh sách các yêu cầu phỏng vấn mà Nhà tuyển dụng đã gửi, sắp xếp theo thời gian mới nhất.
  - [x] Có tab hoặc dropdown lọc theo trạng thái: `Tất cả`, `Chờ phản hồi (PENDING)`, `Đã chấp nhận (ACCEPTED)`, `Đã từ chối (DECLINED)`, `Đã hủy (CANCELLED)`.
  - [x] Cho phép Nhà tuyển dụng hủy (`Cancel`) yêu cầu phỏng vấn nếu yêu cầu đó vẫn đang ở trạng thái `PENDING`.

---

### US-12: Cập nhật thông tin Công ty của Nhà tuyển dụng
- **User Story:**  
  > *"Là **một nhà tuyển dụng**, tôi muốn **cập nhật thông tin hồ sơ công ty (tên công ty, quy mô, địa chỉ, giới thiệu)**, để **ứng viên tin tưởng hơn khi nhận được yêu cầu phỏng vấn từ tôi**."*
- **Màn hình liên quan:** Hồ sơ của tôi (`/recruiter/profile`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Cho phép chỉnh sửa Tên công ty, Logo, Quy mô nhân sự, Website, Địa chỉ văn phòng và Mô tả công ty.
  - [x] Thông tin công ty hiển thị kèm trong mỗi Yêu cầu phỏng vấn gửi tới ứng viên.

---

## PHẦN 3: Vai trò Quản trị viên (Admin)

### US-13: Xem danh sách và tìm kiếm trong Quản trị tài khoản
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **xem danh sách toàn bộ tài khoản trong hệ thống và tìm kiếm, lọc theo vai trò hoặc trạng thái**, để **kiểm soát người dùng tham gia nền tảng TalentLink**."*
- **Màn hình liên quan:** Quản trị tài khoản (`/admin/accounts`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Chỉ người dùng có quyền `ROLE_ADMIN` mới truy cập được đường dẫn `/admin/**` (Spring Security chặn `403 Forbidden` với các vai trò khác).
  - [x] Hiển thị bảng danh sách tài khoản có phân trang gồm: ID, Họ tên, Email, Vai trò (`CANDIDATE`, `RECRUITER`, `ADMIN`), Ngày tạo, Trạng thái (`ACTIVE`, `LOCKED`).
  - [x] Cho phép tìm kiếm theo từ khóa (tên/email) và lọc theo Vai trò hoặc Trạng thái tài khoản.

---

### US-14: Khóa và Mở khóa tài khoản người dùng
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **khóa hoặc mở khóa tài khoản của ứng viên/nhà tuyển dụng vi phạm quy định**, để **đảm bảo an toàn và chất lượng cho cộng đồng người dùng trên hệ thống**."*
- **Màn hình liên quan:** Quản trị tài khoản (`/admin/accounts`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Trên mỗi dòng tài khoản (trừ tài khoản Admin đang đăng nhập), có nút thao tác **"Khóa"** (nếu đang `ACTIVE`) hoặc **"Mở khóa"** (nếu đang `LOCKED`) kèm hộp thoại xác nhận.
  - [x] Khi tài khoản bị chuyển sang `LOCKED`, người dùng đó không thể đăng nhập và hồ sơ của họ tạm thời không hiển thị trên trang tìm kiếm.
  - [x] Không cho phép Admin tự khóa chính tài khoản của mình.

---

### US-15: Phân quyền và tạo tài khoản từ trang Quản trị
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **thay đổi vai trò hoặc thêm mới tài khoản người dùng từ trang quản trị**, để **hỗ trợ cấp quyền nhanh cho nhân sự vận hành hoặc đối tác tuyển dụng**."*
- **Màn hình liên quan:** Quản trị tài khoản (`/admin/accounts`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Cho phép Admin cập nhật vai trò của tài khoản hoặc thêm tài khoản mới với đầy đủ kiểm tra hợp lệ dữ liệu.
  - [x] Mọi thay đổi được lưu vào MySQL và ghi nhận thời gian cập nhật (`updated_at`).

---

### US-16: Xem danh sách hồ sơ trong Hàng chờ duyệt
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **xem danh sách các hồ sơ ứng viên đang nằm trong Hàng chờ duyệt theo thứ tự thời gian gửi**, để **kịp thời kiểm duyệt nội dung trước khi công khai trên hệ thống**."*
- **Màn hình liên quan:** Hàng chờ duyệt (`/admin/approvals`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Hiển thị danh sách tất cả hồ sơ có trạng thái `PENDING_APPROVAL` gồm: Mã hồ sơ, Tên ứng viên, Email, Vị trí chuyên môn, Kỹ năng chính, Thời gian gửi duyệt.
  - [x] Cho phép xem nhanh (Preview) hoặc mở xem toàn bộ nội dung chi tiết của hồ sơ ứng viên trước khi ra quyết định duyệt.

---

### US-17: Phê duyệt hồ sơ ứng viên trong Hàng chờ duyệt
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **phê duyệt các hồ sơ ứng viên hợp lệ trong Hàng chờ duyệt**, để **hồ sơ đó chính thức xuất hiện trên trang Tìm ứng viên cho nhà tuyển dụng**."*
- **Màn hình liên quan:** Hàng chờ duyệt (`/admin/approvals`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Khi Admin bấm nút **"Phê duyệt (Approve)"** trên một hồ sơ chờ duyệt, trạng thái hồ sơ chuyển thành `APPROVED`, lưu lại người duyệt và thời điểm duyệt.
  - [x] Hồ sơ sau khi duyệt biến mất khỏi danh sách chờ duyệt và ngay lập tức có thể tìm thấy ở màn hình **Tìm ứng viên**.

---

### US-18: Từ chối hồ sơ không đạt yêu cầu kèm lý do
- **User Story:**  
  > *"Là **một quản trị viên**, tôi muốn **từ chối hồ sơ ứng viên thiếu thông tin hoặc vi phạm quy chuẩn kèm theo lý do cụ thể**, để **ứng viên biết cần chỉnh sửa những gì và gửi duyệt lại**."*
- **Màn hình liên quan:** Hàng chờ duyệt (`/admin/approvals`)
- **Tiêu chí chấp nhận (Acceptance Criteria):**
  - [x] Khi Admin bấm nút **"Từ chối (Reject)"**, hệ thống bắt buộc nhập **Lý do từ chối** (không được để trống).
  - [x] Trạng thái hồ sơ chuyển thành `REJECTED` và lưu lý do từ chối vào CSDL.
  - [x] Ứng viên khi vào màn hình **Hồ sơ của tôi** sẽ thấy trạng thái `Bị từ chối` cùng nội dung lý do để chỉnh sửa và gửi duyệt lại.

---

## Ma trận truy vết User Story — Màn hình Wireframe

| Màn hình chính | Đường dẫn dự kiến | Vai trò sử dụng | User Story liên quan |
| :--- | :--- | :--- | :--- |
| **1. Đăng nhập** | `/login` | Tất cả vai trò | `US-02` |
| **2. Đăng ký** | `/register` | Ứng viên, Nhà tuyển dụng | `US-01`, `US-07` |
| **3. Hồ sơ của tôi** | `/candidate/profile` | Ứng viên (và Nhà tuyển dụng) | `US-03`, `US-04`, `US-06`, `US-12` |
| **4. Quản trị tài khoản** | `/admin/accounts` | Quản trị viên | `US-13`, `US-14`, `US-15` |
| **5. Hàng chờ duyệt** | `/admin/approvals` | Quản trị viên | `US-16`, `US-17`, `US-18` |
| **6. Tìm ứng viên** | `/candidates` | Nhà tuyển dụng | `US-08` |
| **7. Chi tiết ứng viên** | `/candidates/{id}` | Nhà tuyển dụng, Quản trị viên | `US-09`, `US-10` |
| **8. Yêu cầu phỏng vấn** | `/interviews` | Ứng viên, Nhà tuyển dụng | `US-05`, `US-10`, `US-11` |
