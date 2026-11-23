-- =====================================================================
-- V1__init.sql
-- Tạo toàn bộ 13 bảng theo thiết kế ERD (Task T4E7A9B & T4E7A9C)
-- Hệ quản trị CSDL: MySQL 8.0+ (InnoDB, utf8mb4_unicode_ci)
-- =====================================================================

-- 1. Bảng users: Tài khoản người dùng (Admin, Đối tác/NTD, Sinh viên/Ứng viên)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NULL,
    role VARCHAR(20) NOT NULL COMMENT 'CANDIDATE | RECRUITER | ADMIN',
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    locked_reason VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('CANDIDATE', 'RECRUITER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_role_locked ON users (role, is_locked);

-- 2. Bảng profiles: Hồ sơ năng lực sinh viên / ứng viên
CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    headline VARCHAR(150) NOT NULL,
    bio TEXT NULL,
    years_of_experience INT NOT NULL DEFAULT 0,
    location VARCHAR(100) NULL,
    expected_salary DECIMAL(12, 2) NULL,
    cv_url VARCHAR(255) NULL,
    avatar_url VARCHAR(255) NULL,
    is_open_to_work BOOLEAN NOT NULL DEFAULT TRUE,
    approval_status VARCHAR(25) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT | PENDING | APPROVED | REJECTED',
    submitted_at DATETIME NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_profiles_exp CHECK (years_of_experience >= 0),
    CONSTRAINT chk_profiles_status CHECK (approval_status IN ('DRAFT', 'PENDING', 'APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_profiles_search ON profiles (approval_status, is_open_to_work, location, years_of_experience);
CREATE INDEX idx_profiles_approval_queue ON profiles (approval_status, submitted_at);

-- 3. Bảng educations: Quá trình học vấn của ứng viên
CREATE TABLE IF NOT EXISTS educations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    school_name VARCHAR(150) NOT NULL,
    degree VARCHAR(100) NULL,
    major VARCHAR(150) NULL,
    gpa DECIMAL(3, 2) NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    description TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_educations_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_educations_profile ON educations (profile_id, start_date);

-- 4. Bảng skills: Danh mục từ điển kỹ năng chuẩn
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    category VARCHAR(50) NULL COMMENT 'BACKEND | FRONTEND | DATABASE | DEVOPS | MOBILE | OTHER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_skills_name UNIQUE (name),
    CONSTRAINT uk_skills_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng profile_skills: Kỹ năng gắn với hồ sơ (mức thành thạo 1-5)
CREATE TABLE IF NOT EXISTS profile_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency_level TINYINT NOT NULL COMMENT 'Mức thành thạo từ 1 đến 5',
    years_used DECIMAL(3, 1) NOT NULL DEFAULT 0.0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_profile_skills_pair UNIQUE (profile_id, skill_id),
    CONSTRAINT fk_profile_skills_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE RESTRICT,
    CONSTRAINT chk_profile_skills_level CHECK (proficiency_level BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_profile_skills_filter ON profile_skills (skill_id, proficiency_level);

-- 6. Bảng projects: Dự án thực tế của ứng viên
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    project_name VARCHAR(150) NOT NULL,
    role_in_project VARCHAR(100) NULL,
    tech_stack VARCHAR(255) NULL,
    project_url VARCHAR(255) NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    description TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_projects_profile ON projects (profile_id);

-- 7. Bảng certificates: Chứng chỉ chuyên môn / ngoại ngữ
CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    certificate_name VARCHAR(150) NOT NULL,
    issuing_organization VARCHAR(150) NOT NULL,
    issue_date DATE NULL,
    expiry_date DATE NULL,
    credential_url VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_certificates_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_certificates_profile ON certificates (profile_id);

-- 8. Bảng companies: Thông tin công ty của Đối tác / Nhà tuyển dụng
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    tax_code VARCHAR(50) NULL,
    website VARCHAR(255) NULL,
    logo_url VARCHAR(255) NULL,
    company_size VARCHAR(50) NULL,
    address VARCHAR(255) NULL,
    description TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_companies_user UNIQUE (user_id),
    CONSTRAINT fk_companies_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_companies_name ON companies (company_name);

-- 9. Bảng shortlists: Danh sách hồ sơ ứng viên được Nhà tuyển dụng lưu
CREATE TABLE IF NOT EXISTS shortlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    note VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_shortlists_recruiter_profile UNIQUE (recruiter_id, profile_id),
    CONSTRAINT fk_shortlists_recruiter FOREIGN KEY (recruiter_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_shortlists_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_shortlists_profile ON shortlists (profile_id);

-- 10. Bảng interview_requests: Yêu cầu phỏng vấn
CREATE TABLE IF NOT EXISTS interview_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    scheduled_at DATETIME NOT NULL,
    mode VARCHAR(20) NOT NULL COMMENT 'ONLINE | OFFLINE',
    meeting_link_or_location VARCHAR(255) NOT NULL,
    message TEXT NULL,
    candidate_response_note TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | ACCEPTED | DECLINED | CANCELLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interviews_recruiter FOREIGN KEY (recruiter_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_interviews_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE RESTRICT,
    CONSTRAINT fk_interviews_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT chk_interviews_mode CHECK (mode IN ('ONLINE', 'OFFLINE')),
    CONSTRAINT chk_interviews_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_interviews_profile_status ON interview_requests (profile_id, status, scheduled_at);
CREATE INDEX idx_interviews_recruiter_status ON interview_requests (recruiter_id, status, scheduled_at);

-- 11. Bảng profile_reviews: Lịch sử xét duyệt hồ sơ của Admin
CREATE TABLE IF NOT EXISTS profile_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    decision VARCHAR(20) NOT NULL COMMENT 'APPROVED | REJECTED',
    rejection_reason TEXT NULL,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_reviews_decision CHECK (decision IN ('APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_profile_reviews_history ON profile_reviews (profile_id, reviewed_at);
CREATE INDEX idx_profile_reviews_reviewer ON profile_reviews (reviewer_id, reviewed_at);

-- 12. Bảng notifications: Thông báo hệ thống
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    reference_url VARCHAR(255) NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notifications_recipient_unread ON notifications (recipient_id, is_read, created_at);

-- 13. Bảng profile_views: Thống kê lượt xem hồ sơ
CREATE TABLE IF NOT EXISTS profile_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    viewer_id BIGINT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,
    viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_views_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_views_viewer FOREIGN KEY (viewer_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_profile_views_profile_time ON profile_views (profile_id, viewed_at);
CREATE INDEX idx_profile_views_viewer ON profile_views (viewer_id, viewed_at);
