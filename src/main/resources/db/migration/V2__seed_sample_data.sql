-- =====================================================================
-- V2__seed_sample_data.sql
-- Dữ liệu mẫu phục vụ phát triển & nghiệm thu (Task T4E7A9C)
-- LƯU Ý: Toàn bộ dữ liệu là giả lập (@talentlink.demo), KHÔNG dùng dữ liệu người thật.
-- Mật khẩu chung của tất cả tài khoản mẫu: 123456aA@
-- BCrypt Hash: $2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. SEED 30 KỸ NĂNG (Bảng skills: id 1 -> 30)
-- ---------------------------------------------------------------------
INSERT INTO skills (id, name, slug, category, created_at) VALUES
(1,  'Java',                'java',                'BACKEND',  '2026-11-01 08:00:00'),
(2,  'Spring Boot',         'spring-boot',         'BACKEND',  '2026-11-01 08:00:00'),
(3,  'Spring Security',     'spring-security',     'BACKEND',  '2026-11-01 08:00:00'),
(4,  'Spring Data JPA',     'spring-data-jpa',     'BACKEND',  '2026-11-01 08:00:00'),
(5,  'Hibernate',           'hibernate',           'BACKEND',  '2026-11-01 08:00:00'),
(6,  'Thymeleaf',           'thymeleaf',           'FRONTEND', '2026-11-01 08:00:00'),
(7,  'RESTful API',         'restful-api',         'BACKEND',  '2026-11-01 08:00:00'),
(8,  'Microservices',       'microservices',       'BACKEND',  '2026-11-01 08:00:00'),
(9,  'Node.js',             'nodejs',              'BACKEND',  '2026-11-01 08:00:00'),
(10, 'Python',              'python',              'BACKEND',  '2026-11-01 08:00:00'),
(11, 'C# / .NET Core',      'dotnet-core',         'BACKEND',  '2026-11-01 08:00:00'),
(12, 'Go (Golang)',         'golang',              'BACKEND',  '2026-11-01 08:00:00'),
(13, 'HTML5 & CSS3',        'html5-css3',          'FRONTEND', '2026-11-01 08:00:00'),
(14, 'JavaScript (ES6+)',   'javascript',          'FRONTEND', '2026-11-01 08:00:00'),
(15, 'TypeScript',          'typescript',          'FRONTEND', '2026-11-01 08:00:00'),
(16, 'ReactJS',             'reactjs',             'FRONTEND', '2026-11-01 08:00:00'),
(17, 'Vue.js',              'vuejs',               'FRONTEND', '2026-11-01 08:00:00'),
(18, 'Bootstrap / Tailwind','bootstrap-tailwind',  'FRONTEND', '2026-11-01 08:00:00'),
(19, 'MySQL',               'mysql',               'DATABASE', '2026-11-01 08:00:00'),
(20, 'PostgreSQL',          'postgresql',          'DATABASE', '2026-11-01 08:00:00'),
(21, 'Redis',               'redis',               'DATABASE', '2026-11-01 08:00:00'),
(22, 'MongoDB',             'mongodb',             'DATABASE', '2026-11-01 08:00:00'),
(23, 'Flyway Migration',    'flyway',              'DATABASE', '2026-11-01 08:00:00'),
(24, 'Git & GitHub',        'git-github',          'DEVOPS',   '2026-11-01 08:00:00'),
(25, 'Docker',              'docker',              'DEVOPS',   '2026-11-01 08:00:00'),
(26, 'Linux / Bash',        'linux-bash',          'DEVOPS',   '2026-11-01 08:00:00'),
(27, 'CI/CD (GitHub Actions)', 'ci-cd',            'DEVOPS',   '2026-11-01 08:00:00'),
(28, 'JUnit & Mockito',     'junit-mockito',       'OTHER',    '2026-11-01 08:00:00'),
(29, 'Flutter / Android',   'flutter-android',     'MOBILE',   '2026-11-01 08:00:00'),
(30, 'Agile / Scrum',       'agile-scrum',         'OTHER',    '2026-11-01 08:00:00');

-- ---------------------------------------------------------------------
-- 2. SEED TÀI KHOẢN NGƯỜI DÙNG (Bảng users: 1 Admin + 3 Đối tác + 15 Sinh viên giả)
-- ---------------------------------------------------------------------
INSERT INTO users (id, email, password_hash, full_name, phone, role, is_locked, locked_reason, created_at, updated_at) VALUES
-- 1 Admin (id = 1)
(1,  'admin@talentlink.demo',    '$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Quản Trị Viên Demo',         '0900000001', 'ADMIN',     FALSE, NULL, '2026-11-01 08:00:00', '2026-11-01 08:00:00'),

-- 3 Đối tác / Nhà tuyển dụng (id = 2..4)
(2,  'partner1@talentlink.demo', '$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Đại Diện Tuyển Dụng Sao Việt', '0900000002', 'RECRUITER', FALSE, NULL, '2026-11-02 08:00:00', '2026-11-02 08:00:00'),
(3,  'partner2@talentlink.demo', '$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Đại Diện Tuyển Dụng Ánh Dương','0900000003', 'RECRUITER', FALSE, NULL, '2026-11-02 09:00:00', '2026-11-02 09:00:00'),
(4,  'partner3@talentlink.demo', '$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Đại Diện Tuyển Dụng Phương Đông','0900000004','RECRUITER', FALSE, NULL, '2026-11-02 10:00:00', '2026-11-02 10:00:00'),

-- 15 Sinh viên giả (id = 5..19)
(5,  'student01@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 01', '0900000101', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:00:00', '2026-11-03 08:00:00'),
(6,  'student02@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 02', '0900000102', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:10:00', '2026-11-03 08:10:00'),
(7,  'student03@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 03', '0900000103', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:20:00', '2026-11-03 08:20:00'),
(8,  'student04@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 04', '0900000104', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:30:00', '2026-11-03 08:30:00'),
(9,  'student05@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 05', '0900000105', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:40:00', '2026-11-03 08:40:00'),
(10, 'student06@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 06', '0900000106', 'CANDIDATE', FALSE, NULL, '2026-11-03 08:50:00', '2026-11-03 08:50:00'),
(11, 'student07@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 07', '0900000107', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:00:00', '2026-11-03 09:00:00'),
(12, 'student08@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 08', '0900000108', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:10:00', '2026-11-03 09:10:00'),
(13, 'student09@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 09', '0900000109', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:20:00', '2026-11-03 09:20:00'),
(14, 'student10@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 10', '0900000110', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:30:00', '2026-11-03 09:30:00'),
(15, 'student11@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 11', '0900000111', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:40:00', '2026-11-03 09:40:00'),
(16, 'student12@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 12', '0900000112', 'CANDIDATE', FALSE, NULL, '2026-11-03 09:50:00', '2026-11-03 09:50:00'),
(17, 'student13@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 13', '0900000113', 'CANDIDATE', FALSE, NULL, '2026-11-03 10:00:00', '2026-11-03 10:00:00'),
(18, 'student14@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 14', '0900000114', 'CANDIDATE', FALSE, NULL, '2026-11-03 10:10:00', '2026-11-03 10:10:00'),
(19, 'student15@talentlink.demo','$2a$10$1oUPVoH81mx5NbkrA7RVx.U0RFZ5oJMfgl.sig2TM2pXy/qEPqo6S', 'Sinh Viên Giả Lập 15', '0900000115', 'CANDIDATE', FALSE, NULL, '2026-11-03 10:20:00', '2026-11-03 10:20:00');

-- ---------------------------------------------------------------------
-- 3. SEED 3 CÔNG TY ĐỐI TÁC (Bảng companies: id 1 -> 3 gắn với user_id 2..4)
-- ---------------------------------------------------------------------
INSERT INTO companies (id, user_id, company_name, tax_code, website, logo_url, company_size, address, description, created_at, updated_at) VALUES
(1, 2, 'Công ty TNHH Công Nghệ Sao Việt Demo',     '0100000001', 'https://saoviet-tech.demo',   '/images/companies/saoviet.png',   '200-500',  'Khu Công nghệ cao Hòa Lạc, Hà Nội',        'Đối tác phát triển giải pháp phần mềm doanh nghiệp và nền tảng đám mây.', '2026-11-02 08:30:00', '2026-11-02 08:30:00'),
(2, 3, 'Tập Đoàn Phần Mềm Ánh Dương Demo',         '0100000002', 'https://anhduong-soft.demo',  '/images/companies/anhduong.png',  '500-1000', 'Quận Cầu Giấy, Hà Nội',                    'Chuyên gia công và phát triển hệ thống tài chính ngân hàng trên nền tảng Java Spring Boot.', '2026-11-02 09:30:00', '2026-11-02 09:30:00'),
(3, 4, 'Công ty Cổ Phần Giải Pháp Số Phương Đông', '0100000003', 'https://phuongdong-dig.demo', '/images/companies/phuongdong.png','50-200',   'Khu Đô thị Thủ Thiêm, TP. Hồ Chí Minh',    'Đơn vị tiên phong cung cấp dịch vụ chuyển đổi số, thương mại điện tử và ứng dụng di động.', '2026-11-02 10:30:00', '2026-11-02 10:30:00');

-- ---------------------------------------------------------------------
-- 4. SEED 15 HỒ SƠ SINH VIÊN GIẢ (Bảng profiles: id 1 -> 15 gắn với user_id 5..19)
-- ---------------------------------------------------------------------
INSERT INTO profiles (id, user_id, headline, bio, years_of_experience, location, expected_salary, cv_url, avatar_url, is_open_to_work, approval_status, submitted_at, approved_at, created_at, updated_at) VALUES
(1,  5,  'Java Backend Intern / Fresher',       'Sinh viên năm cuối chuyên ngành Kỹ thuật phần mềm, nắm vững Java 17, Spring Boot 3, JPA và MySQL.',       1, 'Hà Nội',          10000000.00, '/files/cv/student01.pdf', '/images/avatars/s01.png', TRUE,  'APPROVED', '2026-11-04 08:00:00', '2026-11-04 10:00:00', '2026-11-03 11:00:00', '2026-11-04 10:00:00'),
(2,  6,  'Fullstack Java & React Developer',    'Đam mê xây dựng ứng dụng web hoàn chỉnh với Spring Boot ở Backend và ReactJS ở Frontend.',                1, 'Hà Nội',          12000000.00, '/files/cv/student02.pdf', '/images/avatars/s02.png', TRUE,  'APPROVED', '2026-11-04 08:15:00', '2026-11-04 10:15:00', '2026-11-03 11:10:00', '2026-11-04 10:15:00'),
(3,  7,  'Spring Boot & Microservices Fresher', 'Có kinh nghiệm thực tập phát triển RESTful API, bảo mật Spring Security và quản lý DB với Flyway.',       1, 'TP. Hồ Chí Minh', 13000000.00, '/files/cv/student03.pdf', '/images/avatars/s03.png', TRUE,  'APPROVED', '2026-11-04 08:30:00', '2026-11-04 10:30:00', '2026-11-03 11:20:00', '2026-11-04 10:30:00'),
(4,  8,  'Frontend React & TypeScript Fresher', 'Tập trung tối ưu trải nghiệm người dùng, thành thạo HTML5, CSS3, TypeScript và ReactJS.',                 1, 'Đà Nẵng',         10000000.00, '/files/cv/student04.pdf', '/images/avatars/s04.png', TRUE,  'APPROVED', '2026-11-04 08:45:00', '2026-11-04 10:45:00', '2026-11-03 11:30:00', '2026-11-04 10:45:00'),
(5,  9,  'Backend Developer (Java / PostgreSQL)','Yêu thích thiết kế CSDL chuẩn hóa, tối ưu truy vấn SQL và viết Unit Test với JUnit & Mockito.',           2, 'Hà Nội',          15000000.00, '/files/cv/student05.pdf', '/images/avatars/s05.png', TRUE,  'APPROVED', '2026-11-04 09:00:00', '2026-11-04 11:00:00', '2026-11-03 11:40:00', '2026-11-04 11:00:00'),
(6,  10, 'Node.js & Vue.js Web Developer',      'Sinh viên tốt nghiệp loại Giỏi, đã hoàn thành 3 đồ án thực tế về thương mại điện tử và realtime chat.',   1, 'TP. Hồ Chí Minh', 11000000.00, '/files/cv/student06.pdf', '/images/avatars/s06.png', TRUE,  'APPROVED', '2026-11-04 09:15:00', '2026-11-04 11:15:00', '2026-11-03 11:50:00', '2026-11-04 11:15:00'),
(7,  11, 'DevOps & Cloud Intern',               'Quen thuộc với Linux, Docker, thiết lập pipeline CI/CD bằng GitHub Actions và triển khai ứng dụng Java.', 0, 'Hà Nội',          9000000.00,  '/files/cv/student07.pdf', '/images/avatars/s07.png', TRUE,  'APPROVED', '2026-11-04 09:30:00', '2026-11-04 11:30:00', '2026-11-03 12:00:00', '2026-11-04 11:30:00'),
(8,  12, 'Java Web MVC & Thymeleaf Developer',  'Nắm vững mô hình MVC, SSR với Thymeleaf, Bean Validation và tích hợp cơ sở dữ liệu MySQL.',              1, 'Hà Nội',          11000000.00, '/files/cv/student08.pdf', '/images/avatars/s08.png', TRUE,  'APPROVED', '2026-11-04 09:45:00', '2026-11-04 11:45:00', '2026-11-03 12:10:00', '2026-11-04 11:45:00'),
(9,  13, 'Python Backend & Data Intern',        'Có nền tảng thuật toán tốt, sử dụng thành thạo Python, SQL và xây dựng dịch vụ REST API.',               0, 'TP. Hồ Chí Minh', 9500000.00,  '/files/cv/student09.pdf', '/images/avatars/s09.png', TRUE,  'APPROVED', '2026-11-04 10:00:00', '2026-11-04 12:00:00', '2026-11-03 12:20:00', '2026-11-04 12:00:00'),
(10, 14, 'Mobile Flutter & REST API Developer', 'Phát triển ứng dụng di động đa nền tảng bằng Flutter kết nối với hệ thống Backend Spring Boot.',         1, 'Đà Nẵng',         11500000.00, '/files/cv/student10.pdf', '/images/avatars/s10.png', TRUE,  'APPROVED', '2026-11-04 10:15:00', '2026-11-04 12:15:00', '2026-11-03 12:30:00', '2026-11-04 12:15:00'),
(11, 15, '.NET Core & SQL Developer Fresher',   'Có kiến thức vững về lập trình hướng đối tượng, C#, .NET Core và quản trị hệ cơ sở dữ liệu quan hệ.',    1, 'Hà Nội',          11000000.00, '/files/cv/student11.pdf', '/images/avatars/s11.png', TRUE,  'APPROVED', '2026-11-04 10:30:00', '2026-11-04 12:30:00', '2026-11-03 12:40:00', '2026-11-04 12:30:00'),
(12, 16, 'Golang & Distributed Systems Intern', 'Tìm hiểu sâu về hệ thống phân tán, caching với Redis và lập trình dịch vụ hiệu năng cao.',               0, 'TP. Hồ Chí Minh', 10000000.00, '/files/cv/student12.pdf', '/images/avatars/s12.png', TRUE,  'PENDING',  '2026-11-05 08:00:00', NULL,                  '2026-11-03 12:50:00', '2026-11-05 08:00:00'),
(13, 17, 'QA Automation & JUnit Tester',        'Định hướng kiểm thử tự động, nắm vững quy trình Agile/Scrum, viết kịch bản kiểm thử JUnit và Postman.',  1, 'Hà Nội',          10000000.00, '/files/cv/student13.pdf', '/images/avatars/s13.png', TRUE,  'PENDING',  '2026-11-05 09:00:00', NULL,                  '2026-11-03 13:00:00', '2026-11-05 09:00:00'),
(14, 18, 'Java Enterprise Intern',              'Sinh viên năm 4 chuyên ngành Hệ thống thông tin, đang chờ phê duyệt hồ sơ thực tập tốt nghiệp.',         0, 'Hà Nội',          8000000.00,  '/files/cv/student14.pdf', '/images/avatars/s14.png', TRUE,  'REJECTED', '2026-11-04 14:00:00', NULL,                  '2026-11-03 13:10:00', '2026-11-04 16:00:00'),
(15, 19, 'Junior Fullstack Software Engineer',  'Đã hoàn thiện đầy đủ học vấn, dự án, chứng chỉ và kỹ năng chuyên môn trên hệ thống TalentLink.',         2, 'TP. Hồ Chí Minh', 16000000.00, '/files/cv/student15.pdf', '/images/avatars/s15.png', TRUE,  'APPROVED', '2026-11-04 11:00:00', '2026-11-04 13:00:00', '2026-11-03 13:20:00', '2026-11-04 13:00:00');

-- ---------------------------------------------------------------------
-- 5. SEED HỌC VẤN CHO ĐỦ 15 HỒ SƠ (Bảng educations: id 1 -> 15)
-- ---------------------------------------------------------------------
INSERT INTO educations (id, profile_id, school_name, degree, major, gpa, start_date, end_date, description, created_at) VALUES
(1,  1,  'Đại học Công Nghệ Giả Lập Hà Nội',     'Kỹ sư',   'Kỹ thuật Phần mềm',          3.45, '2022-09-01', '2026-06-30', 'Đạt học bổng khuyến khích học tập 3 học kỳ liên tiếp.', '2026-11-03 14:00:00'),
(2,  2,  'Đại học Công Nghệ Giả Lập Hà Nội',     'Kỹ sư',   'Khoa học Máy tính',          3.52, '2022-09-01', '2026-06-30', 'Thành viên CLB Lập trình Web & Thuật toán.',            '2026-11-03 14:05:00'),
(3,  3,  'Đại học Kỹ Thuật Số TP.HCM (Demo)',    'Kỹ sư',   'Kỹ thuật Phần mềm',          3.60, '2022-09-01', '2026-06-30', 'Đồ án tốt nghiệp đạt điểm A+ về kiến trúc Spring Boot.', '2026-11-03 14:10:00'),
(4,  4,  'Đại học Bách Khoa Miền Trung (Demo)',  'Cử nhân', 'Công nghệ Thông tin',        3.30, '2022-09-01', '2026-06-30', 'Giải Nhì cuộc thi Thiết kế Giao diện Web Sinh viên.',   '2026-11-03 14:15:00'),
(5,  5,  'Học viện Kỹ Thuật Phần Mềm Demo',      'Kỹ sư',   'Hệ thống Thông tin',         3.70, '2021-09-01', '2025-06-30', 'Tốt nghiệp loại Giỏi, chuyên sâu cơ sở dữ liệu.',       '2026-11-03 14:20:00'),
(6,  6,  'Đại học Kỹ Thuật Số TP.HCM (Demo)',    'Cử nhân', 'Kỹ thuật Phần mềm',          3.40, '2022-09-01', '2026-06-30', 'Hoàn thành chương trình đào tạo Fullstack Web.',        '2026-11-03 14:25:00'),
(7,  7,  'Đại học Công Nghệ Giả Lập Hà Nội',     'Kỹ sư',   'Mạng máy tính & Truyền thông',3.25,'2022-09-01', '2026-06-30', 'Chuyên đề tốt nghiệp về tự động hóa CI/CD và Docker.',  '2026-11-03 14:30:00'),
(8,  8,  'Học viện Kỹ Thuật Phần Mềm Demo',      'Cử nhân', 'Công nghệ Thông tin',        3.48, '2022-09-01', '2026-06-30', 'Nắm vững lập trình Java Web chuẩn doanh nghiệp.',       '2026-11-03 14:35:00'),
(9,  9,  'Đại học Khoa Học Tự Nhiên Demo',       'Cử nhân', 'Khoa học Dữ liệu',           3.55, '2022-09-01', '2026-06-30', 'Nghiên cứu sinh viên về xử lý dữ liệu với Python.',     '2026-11-03 14:40:00'),
(10, 10, 'Đại học Bách Khoa Miền Trung (Demo)',  'Kỹ sư',   'Kỹ thuật Phần mềm',          3.38, '2022-09-01', '2026-06-30', 'Trưởng nhóm phát triển ứng dụng di động sinh viên.',    '2026-11-03 14:45:00'),
(11, 11, 'Đại học Công Nghệ Giả Lập Hà Nội',     'Kỹ sư',   'Hệ thống Thông tin Quản lý', 3.32, '2022-09-01', '2026-06-30', 'Hoàn thành xuất sắc học phần Phát triển phần mềm.',     '2026-11-03 14:50:00'),
(12, 12, 'Đại học Kỹ Thuật Số TP.HCM (Demo)',    'Kỹ sư',   'Khoa học Máy tính',          3.50, '2022-09-01', '2026-06-30', 'Định hướng nghiên cứu hệ thống phân tán hiệu năng cao.','2026-11-03 14:55:00'),
(13, 13, 'Học viện Kỹ Thuật Phần Mềm Demo',      'Cử nhân', 'Đảm bảo Chất lượng Phần mềm',3.42, '2022-09-01', '2026-06-30', 'Chuyên sâu kiểm thử tự động và quản lý chất lượng.',   '2026-11-03 15:00:00'),
(14, 14, 'Đại học Công Nghệ Giả Lập Hà Nội',     'Cử nhân', 'Hệ thống Thông tin',         3.15, '2022-09-01', '2026-06-30', 'Sinh viên năm cuối chuyên ngành Hệ thống thông tin.',   '2026-11-03 15:05:00'),
(15, 15, 'Đại học Kỹ Thuật Số TP.HCM (Demo)',    'Kỹ sư',   'Kỹ thuật Phần mềm',          3.75, '2021-09-01', '2025-06-30', 'Thủ khoa đầu ra chuyên ngành Kỹ thuật Phần mềm.',       '2026-11-03 15:10:00');

-- ---------------------------------------------------------------------
-- 6. SEED KỸ NĂNG HỒ SƠ MỨC 1-5 CHO ĐỦ 15 HỒ SƠ (Bảng profile_skills: 60 dòng)
-- ---------------------------------------------------------------------
INSERT INTO profile_skills (id, profile_id, skill_id, proficiency_level, years_used, created_at) VALUES
-- Hồ sơ 1
(1,  1, 1,  4, 1.5, '2026-11-03 15:20:00'), (2,  1, 2,  4, 1.0, '2026-11-03 15:20:00'),
(3,  1, 4,  3, 1.0, '2026-11-03 15:20:00'), (4,  1, 19, 4, 1.5, '2026-11-03 15:20:00'),
-- Hồ sơ 2
(5,  2, 1,  4, 1.5, '2026-11-03 15:21:00'), (6,  2, 2,  4, 1.0, '2026-11-03 15:21:00'),
(7,  2, 14, 4, 1.5, '2026-11-03 15:21:00'), (8,  2, 16, 3, 1.0, '2026-11-03 15:21:00'),
-- Hồ sơ 3
(9,  3, 2,  5, 1.5, '2026-11-03 15:22:00'), (10, 3, 3,  4, 1.0, '2026-11-03 15:22:00'),
(11, 3, 7,  4, 1.5, '2026-11-03 15:22:00'), (12, 3, 23, 4, 1.0, '2026-11-03 15:22:00'),
-- Hồ sơ 4
(13, 4, 13, 5, 2.0, '2026-11-03 15:23:00'), (14, 4, 14, 4, 1.5, '2026-11-03 15:23:00'),
(15, 4, 15, 4, 1.0, '2026-11-03 15:23:00'), (16, 4, 16, 4, 1.0, '2026-11-03 15:23:00'),
-- Hồ sơ 5
(17, 5, 1,  5, 2.0, '2026-11-03 15:24:00'), (18, 5, 2,  5, 2.0, '2026-11-03 15:24:00'),
(19, 5, 20, 4, 1.5, '2026-11-03 15:24:00'), (20, 5, 28, 4, 1.5, '2026-11-03 15:24:00'),
-- Hồ sơ 6
(21, 6, 9,  4, 1.5, '2026-11-03 15:25:00'), (22, 6, 17, 4, 1.0, '2026-11-03 15:25:00'),
(23, 6, 22, 3, 1.0, '2026-11-03 15:25:00'), (24, 6, 24, 4, 1.5, '2026-11-03 15:25:00'),
-- Hồ sơ 7
(25, 7, 24, 4, 1.5, '2026-11-03 15:26:00'), (26, 7, 25, 4, 1.0, '2026-11-03 15:26:00'),
(27, 7, 26, 4, 1.0, '2026-11-03 15:26:00'), (28, 7, 27, 3, 0.5, '2026-11-03 15:26:00'),
-- Hồ sơ 8
(29, 8, 1,  4, 1.5, '2026-11-03 15:27:00'), (30, 8, 2,  4, 1.5, '2026-11-03 15:27:00'),
(31, 8, 6,  5, 1.5, '2026-11-03 15:27:00'), (32, 8, 18, 4, 1.5, '2026-11-03 15:27:00'),
-- Hồ sơ 9
(33, 9, 10, 4, 1.5, '2026-11-03 15:28:00'), (34, 9, 19, 3, 1.0, '2026-11-03 15:28:00'),
(35, 9, 20, 3, 1.0, '2026-11-03 15:28:00'), (36, 9, 7,  3, 1.0, '2026-11-03 15:28:00'),
-- Hồ sơ 10
(37, 10, 29, 4, 1.5, '2026-11-03 15:29:00'), (38, 10, 7,  4, 1.0, '2026-11-03 15:29:00'),
(39, 10, 1,  3, 1.0, '2026-11-03 15:29:00'), (40, 10, 24, 4, 1.5, '2026-11-03 15:29:00'),
-- Hồ sơ 11
(41, 11, 11, 4, 1.5, '2026-11-03 15:30:00'), (42, 11, 19, 4, 1.5, '2026-11-03 15:30:00'),
(43, 11, 13, 3, 1.0, '2026-11-03 15:30:00'), (44, 11, 24, 3, 1.0, '2026-11-03 15:30:00'),
-- Hồ sơ 12
(45, 12, 12, 3, 1.0, '2026-11-03 15:31:00'), (46, 12, 21, 3, 0.5, '2026-11-03 15:31:00'),
(47, 12, 25, 3, 1.0, '2026-11-03 15:31:00'), (48, 12, 8,  2, 0.5, '2026-11-03 15:31:00'),
-- Hồ sơ 13
(49, 13, 28, 4, 1.5, '2026-11-03 15:32:00'), (50, 13, 30, 4, 1.5, '2026-11-03 15:32:00'),
(51, 13, 1,  3, 1.0, '2026-11-03 15:32:00'), (52, 13, 19, 3, 1.0, '2026-11-03 15:32:00'),
-- Hồ sơ 14
(53, 14, 1,  2, 0.5, '2026-11-03 15:33:00'), (54, 14, 13, 2, 0.5, '2026-11-03 15:33:00'),
(55, 14, 19, 2, 0.5, '2026-11-03 15:33:00'), (56, 14, 24, 1, 0.5, '2026-11-03 15:33:00'),
-- Hồ sơ 15
(57, 15, 1,  5, 2.0, '2026-11-03 15:34:00'), (58, 15, 2,  5, 2.0, '2026-11-03 15:34:00'),
(59, 15, 5,  4, 2.0, '2026-11-03 15:34:00'), (60, 15, 23, 5, 1.5, '2026-11-03 15:34:00');

-- ---------------------------------------------------------------------
-- 7. SEED DỰ ÁN CHO ĐỦ 15 HỒ SƠ (Bảng projects: id 1 -> 15)
-- ---------------------------------------------------------------------
INSERT INTO projects (id, profile_id, project_name, role_in_project, tech_stack, project_url, start_date, end_date, description, created_at) VALUES
(1,  1,  'Hệ thống Quản lý Thư viện Trực tuyến Demo', 'Backend Developer',   'Java 17, Spring Boot, Spring Data JPA, MySQL',         'https://github.com/demo/library-app',     '2025-09-01', '2025-12-15', 'Thiết kế CSDL, xây dựng chức năng mượn trả sách và phân quyền Spring Security.', '2026-11-03 16:00:00'),
(2,  2,  'Nền tảng Đặt lịch Khám bệnh Trực tuyến',    'Fullstack Developer', 'Spring Boot, ReactJS, MySQL, Docker',                  'https://github.com/demo/clinic-booking',  '2025-10-01', '2026-01-20', 'Phát triển REST API đặt lịch và giao diện quản lý lịch hẹn cho bác sĩ.',         '2026-11-03 16:05:00'),
(3,  3,  'Ví Điện tử Sinh viên Demo',                 'Backend Developer',   'Spring Boot 3, Spring Security, Flyway, MySQL',        'https://github.com/demo/student-wallet',  '2026-01-10', '2026-04-30', 'Xây dựng module xác thực người dùng, quản lý giao dịch và lịch sử biến động.',   '2026-11-03 16:10:00'),
(4,  4,  'Dashboard Quản trị Thương mại Điện tử',     'Frontend Developer',  'ReactJS, TypeScript, Tailwind CSS',                    'https://github.com/demo/ecom-dashboard',  '2025-11-01', '2026-02-15', 'Xây dựng giao diện thống kê doanh thu và quản lý giỏ hàng chuẩn responsive.',    '2026-11-03 16:15:00'),
(5,  5,  'Hệ thống Quản lý Kho Hàng Doanh nghiệp',    'Team Lead / Backend', 'Java 17, Spring Boot, PostgreSQL, JUnit 5',            'https://github.com/demo/wms-enterprise',  '2025-08-01', '2026-01-30', 'Thiết kế ERD 15 bảng, tối ưu composite index và đạt độ phủ Unit Test 80%.',      '2026-11-03 16:20:00'),
(6,  6,  'Ứng dụng Chat Nhóm Học tập Realtime',       'Fullstack Developer', 'Node.js, Vue.js, MongoDB, Socket.io',                  'https://github.com/demo/study-chat',      '2026-02-01', '2026-05-15', 'Xây dựng tính năng nhắn tin trực tuyến, chia sẻ tài liệu học tập theo phòng.',   '2026-11-03 16:25:00'),
(7,  7,  'Hệ thống Tự động hóa Triển khai Micro-App', 'DevOps Intern',       'Docker, GitHub Actions, Linux Bash, Nginx',            'https://github.com/demo/devops-pipeline', '2026-01-15', '2026-04-20', 'Đóng gói container cho ứng dụng Spring Boot và thiết lập pipeline CI/CD tự động.','2026-11-03 16:30:00'),
(8,  8,  'Cổng Thông tin Việc làm Sinh viên MVC',     'Web MVC Developer',   'Spring Boot 3, Thymeleaf, Bootstrap 5, MySQL, Flyway', 'https://github.com/demo/campus-jobs',     '2026-02-10', '2026-05-30', 'Xây dựng giao diện SSR bằng Thymeleaf, phân trang và bộ lọc tìm kiếm đa tiêu chí.','2026-11-03 16:35:00'),
(9,  9,  'Hệ thống Gợi ý Khóa học Lập trình',         'Backend Developer',   'Python, FastAPI, PostgreSQL',                          'https://github.com/demo/course-recommender','2025-10-15','2026-01-25','Xây dựng API phân tích lộ trình học tập và đề xuất khóa học phù hợp.',           '2026-11-03 16:40:00'),
(10, 10, 'Ứng dụng Sổ tay Chi tiêu Cá nhân Mobile',   'Mobile Developer',    'Flutter, Spring Boot REST API, MySQL',                 'https://github.com/demo/expense-flutter', '2025-12-01', '2026-03-20', 'Phát triển app di động đồng bộ dữ liệu chi tiêu với máy chủ qua RESTful API.',   '2026-11-03 16:45:00'),
(11, 11, 'Phần mềm Quản lý Nhân sự & Chấm công',      'Backend Developer',   'C#, .NET Core, MySQL, HTML/CSS',                       'https://github.com/demo/hrm-dotnet',      '2025-09-15', '2025-12-30', 'Xây dựng chức năng quản lý hồ sơ nhân viên, tính công và xuất báo cáo tháng.',   '2026-11-03 16:50:00'),
(12, 12, 'Dịch vụ Rút gọn Link Hiệu năng cao',        'Backend Intern',      'Golang, Redis, Docker, MySQL',                         'https://github.com/demo/url-shortener',   '2026-03-01', '2026-05-20', 'Sử dụng Redis caching giúp giảm thời gian phản hồi redirect xuống dưới 5ms.',    '2026-11-03 16:55:00'),
(13, 13, 'Bộ Kịch bản Kiểm thử Tự động E-Commerce',   'QA Automation',       'Java, JUnit 5, Mockito, Postman',                      'https://github.com/demo/qa-automation',   '2026-01-05', '2026-04-10', 'Xây dựng 120 test case tự động kiểm thử luồng đăng nhập, giỏ hàng và thanh toán.','2026-11-03 17:00:00'),
(14, 14, 'Website Giới thiệu Câu lạc bộ Sinh viên',   'Web Developer',       'Java, Spring Boot, HTML5, CSS3, MySQL',                'https://github.com/demo/club-website',    '2026-02-01', '2026-04-01', 'Xây dựng trang tin tức và form đăng ký thành viên mới cho câu lạc bộ.',          '2026-11-03 17:05:00'),
(15, 15, 'Nền tảng Kết nối Thực tập sinh Doanh nghiệp','Fullstack Lead',     'Java 17, Spring Boot 3, Thymeleaf, Security, Flyway',  'https://github.com/demo/intern-connect',  '2026-01-01', '2026-06-01', 'Thiết kế kiến trúc phân tầng 6 layer, tích hợp bảo mật phân quyền và duyệt hồ sơ.','2026-11-03 17:10:00');

-- ---------------------------------------------------------------------
-- 8. SEED CHỨNG CHỈ CHO ĐỦ 15 HỒ SƠ (Bảng certificates: id 1 -> 15)
-- ---------------------------------------------------------------------
INSERT INTO certificates (id, profile_id, certificate_name, issuing_organization, issue_date, expiry_date, credential_url, created_at) VALUES
(1,  1,  'Chứng chỉ Lập trình Java Web Spring Boot',     'Học viện Công nghệ Demo',     '2025-11-15', NULL,         'https://certs.talentlink.demo/c01', '2026-11-03 17:30:00'),
(2,  2,  'Fullstack Java & React Professional Demo',     'Tổ chức Đào tạo Phần mềm Số', '2025-12-10', NULL,         'https://certs.talentlink.demo/c02', '2026-11-03 17:31:00'),
(3,  3,  'TOEIC Listening & Reading 780 (Giả lập)',      'Trung tâm Ngoại ngữ Demo',    '2025-08-20', '2027-08-20', 'https://certs.talentlink.demo/c03', '2026-11-03 17:32:00'),
(4,  4,  'Modern Frontend Development with TypeScript',  'Học viện Công nghệ Demo',     '2026-01-12', NULL,         'https://certs.talentlink.demo/c04', '2026-11-03 17:33:00'),
(5,  5,  'Relational Database Design & SQL Tuning',      'Tổ chức Đào tạo Phần mềm Số', '2025-10-05', NULL,         'https://certs.talentlink.demo/c05', '2026-11-03 17:34:00'),
(6,  6,  'IELTS Academic 6.5 (Chứng chỉ Mẫu)',           'Trung tâm Ngoại ngữ Demo',    '2025-09-01', '2027-09-01', 'https://certs.talentlink.demo/c06', '2026-11-03 17:35:00'),
(7,  7,  'Docker & Containerization Fundamentals',       'Cloud Academy Demo',          '2026-02-18', NULL,         'https://certs.talentlink.demo/c07', '2026-11-03 17:36:00'),
(8,  8,  'Spring MVC & Thymeleaf Enterprise Web',        'Học viện Công nghệ Demo',     '2026-03-01', NULL,         'https://certs.talentlink.demo/c08', '2026-11-03 17:37:00'),
(9,  9,  'Python Programming & Data Structures',         'Tổ chức Đào tạo Phần mềm Số', '2025-11-25', NULL,         'https://certs.talentlink.demo/c09', '2026-11-03 17:38:00'),
(10, 10, 'Cross-Platform Mobile Development (Flutter)',  'Học viện Công nghệ Demo',     '2026-01-30', NULL,         'https://certs.talentlink.demo/c10', '2026-11-03 17:39:00'),
(11, 11, 'Object-Oriented Software Engineering',         'Tổ chức Đào tạo Phần mềm Số', '2025-12-20', NULL,         'https://certs.talentlink.demo/c11', '2026-11-03 17:40:00'),
(12, 12, 'Backend System Design & Caching Basics',       'Cloud Academy Demo',          '2026-02-28', NULL,         'https://certs.talentlink.demo/c12', '2026-11-03 17:41:00'),
(13, 13, 'ISTQB Foundation Level (Bản Mô phỏng Demo)',   'Hội đồng Kiểm thử Demo',      '2026-01-15', NULL,         'https://certs.talentlink.demo/c13', '2026-11-03 17:42:00'),
(14, 14, 'Git & Agile Teamwork Workflow',                'Học viện Công nghệ Demo',     '2026-02-05', NULL,         'https://certs.talentlink.demo/c14', '2026-11-03 17:43:00'),
(15, 15, 'Oracle Certified Professional Java 17 (Demo)', 'Tổ chức Đào tạo Phần mềm Số', '2026-03-15', NULL,         'https://certs.talentlink.demo/c15', '2026-11-03 17:44:00');

-- ---------------------------------------------------------------------
-- 9. SEED LỊCH SỬ DUYỆT HỒ SƠ, SHORTLIST, PHỎNG VẤN, THÔNG BÁO, LƯỢT XEM
-- ---------------------------------------------------------------------
INSERT INTO profile_reviews (id, profile_id, reviewer_id, decision, rejection_reason, reviewed_at) VALUES
(1, 1,  1, 'APPROVED', NULL, '2026-11-04 10:00:00'),
(2, 2,  1, 'APPROVED', NULL, '2026-11-04 10:15:00'),
(3, 3,  1, 'APPROVED', NULL, '2026-11-04 10:30:00'),
(4, 14, 1, 'REJECTED', 'Vui lòng bổ sung mô tả chi tiết đóng góp trong dự án và cập nhật file CV rõ nét hơn.', '2026-11-04 16:00:00'),
(5, 15, 1, 'APPROVED', NULL, '2026-11-04 13:00:00');

INSERT INTO shortlists (id, recruiter_id, profile_id, note, created_at) VALUES
(1, 2, 1,  'Ứng viên nắm vững nền tảng Java 17 và Spring Boot, phù hợp vị trí Fresher Backend.', '2026-11-06 09:00:00'),
(2, 2, 5,  'Kinh nghiệm thiết kế CSDL và viết Unit Test rất tốt.',                               '2026-11-06 09:30:00'),
(3, 3, 3,  'Phù hợp dự án ngân hàng số sử dụng Spring Security và Flyway.',                      '2026-11-06 10:00:00'),
(4, 4, 15, 'Hồ sơ toàn diện, có thể mời phỏng vấn vị trí Junior Fullstack.',                     '2026-11-06 11:00:00');

INSERT INTO interview_requests (id, recruiter_id, company_id, profile_id, job_title, scheduled_at, mode, meeting_link_or_location, message, candidate_response_note, status, created_at, updated_at) VALUES
(1, 2, 1, 1,  'Fresher Java Backend Developer',   '2026-11-28 14:00:00', 'ONLINE',  'https://meet.talentlink.demo/saoviet-s01',  'Chào bạn, Công ty Sao Việt Demo mời bạn tham gia buổi phỏng vấn chuyên môn Java Spring Boot.', NULL, 'PENDING',  '2026-11-07 09:00:00', '2026-11-07 09:00:00'),
(2, 3, 2, 3,  'Spring Boot Engineer',             '2026-11-29 09:30:00', 'OFFLINE', 'Tầng 12, Tòa nhà Ánh Dương Demo, Cầu Giấy', 'Mời bạn tham gia phỏng vấn trực tiếp cùng Trưởng nhóm Kỹ thuật của Tập đoàn Ánh Dương.',       'Em xin xác nhận tham gia đúng giờ ạ.', 'ACCEPTED', '2026-11-07 10:00:00', '2026-11-07 15:00:00'),
(3, 4, 3, 15, 'Junior Fullstack Java Developer',  '2026-11-30 15:00:00', 'ONLINE',  'https://meet.talentlink.demo/phuongdong-15','Ấn tượng với đồ án của bạn, mời bạn trao đổi thêm về vị trí Junior Fullstack.',                  NULL, 'PENDING',  '2026-11-07 11:00:00', '2026-11-07 11:00:00');

INSERT INTO notifications (id, recipient_id, type, title, content, reference_url, is_read, created_at) VALUES
(1, 5,  'PROFILE_APPROVED',  'Hồ sơ của bạn đã được phê duyệt',      'Quản trị viên đã phê duyệt hồ sơ năng lực của bạn. Nhà tuyển dụng hiện có thể tìm thấy bạn.', '/candidate/profile', FALSE, '2026-11-04 10:00:05'),
(2, 5,  'INTERVIEW_NEW',     'Bạn có lời mời phỏng vấn mới',          'Công ty TNHH Công Nghệ Sao Việt Demo vừa gửi lời mời phỏng vấn vị trí Fresher Java Backend.',  '/interviews',        FALSE, '2026-11-07 09:00:05'),
(3, 18, 'PROFILE_REJECTED',  'Hồ sơ cần bổ sung thêm thông tin',      'Hồ sơ của bạn bị từ chối với lý do: Vui lòng bổ sung mô tả chi tiết đóng góp trong dự án.',   '/candidate/profile', FALSE, '2026-11-04 16:00:05'),
(4, 3,  'INTERVIEW_ACCEPTED','Ứng viên đã chấp nhận phỏng vấn',       'Sinh Viên Giả Lập 03 đã xác nhận tham gia buổi phỏng vấn vị trí Spring Boot Engineer.',       '/interviews',        TRUE,  '2026-11-07 15:00:05');

INSERT INTO profile_views (id, profile_id, viewer_id, ip_address, user_agent, viewed_at) VALUES
(1, 1,  2, '127.0.0.1', 'Mozilla/5.0 (Demo Recruiter Browser)', '2026-11-06 08:55:00'),
(2, 3,  3, '127.0.0.1', 'Mozilla/5.0 (Demo Recruiter Browser)', '2026-11-06 09:50:00'),
(3, 5,  2, '127.0.0.1', 'Mozilla/5.0 (Demo Recruiter Browser)', '2026-11-06 09:20:00'),
(4, 15, 4, '127.0.0.1', 'Mozilla/5.0 (Demo Recruiter Browser)', '2026-11-06 10:50:00');
