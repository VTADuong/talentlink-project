package com.talentlink.talentlink.config;

import com.talentlink.talentlink.entity.Education;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.EducationRepository;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.UserRepository;
import com.talentlink.talentlink.service.CustomUserDetailsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình Spring Security (Task T4E7A9F):
 * - Đăng nhập form bằng email + mật khẩu (session), remember-me, đăng xuất.
 * - Bật CSRF cho mọi form.
 * - Phân quyền URL: /admin/** chỉ ADMIN, /partner/** chỉ PARTNER, /me/** chỉ STUDENT.
 * - Tài khoản bị khóa không đăng nhập được và báo rõ lý do.
 * - Sau đăng nhập chuyển về trang chủ đúng vai trò.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final RoleBasedAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Value("${app.security.remember-me-key:TalentLinkRememberMeSecretKey2026}")
    private String rememberMeKey;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            RoleBasedAuthenticationSuccessHandler successHandler,
            CustomAuthenticationFailureHandler failureHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Khởi tạo sẵn tài khoản mẫu cho 3 vai trò (ADMIN, PARTNER, STUDENT) và 1 tài khoản bị khóa
     * trong trường hợp bảng users chưa có dữ liệu khi chạy nhánh độc lập.
     */
    @Bean
    public CommandLineRunner seedDefaultAuthUsers(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            EducationRepository educationRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                String defaultHash = passwordEncoder.encode("123456aA@");
                LocalDateTime now = LocalDateTime.now();

                User admin = new User();
                admin.setEmail("admin@talentlink.demo");
                admin.setPasswordHash(defaultHash);
                admin.setFullName("Quản Trị Viên Demo");
                admin.setRole("ADMIN");
                admin.setLocked(false);
                admin.setCreatedAt(now);
                admin.setUpdatedAt(now);

                User partner = new User();
                partner.setEmail("partner1@talentlink.demo");
                partner.setPasswordHash(defaultHash);
                partner.setFullName("Đối Tác Tuyển Dụng Demo");
                partner.setRole("PARTNER");
                partner.setLocked(false);
                partner.setCreatedAt(now);
                partner.setUpdatedAt(now);

                User student = new User();
                student.setEmail("student01@talentlink.demo");
                student.setPasswordHash(defaultHash);
                student.setFullName("Sinh Viên Demo 01");
                student.setPhone("0901234501");
                student.setRole("STUDENT");
                student.setLocked(false);
                student.setCreatedAt(now);
                student.setUpdatedAt(now);

                User student2 = new User();
                student2.setEmail("student02@talentlink.demo");
                student2.setPasswordHash(defaultHash);
                student2.setFullName("Sinh Viên Demo 02");
                student2.setPhone("0901234502");
                student2.setRole("STUDENT");
                student2.setLocked(false);
                student2.setCreatedAt(now);
                student2.setUpdatedAt(now);

                User lockedUser = new User();
                lockedUser.setEmail("locked@talentlink.demo");
                lockedUser.setPasswordHash(defaultHash);
                lockedUser.setFullName("Tài Khoản Bị Khóa Demo");
                lockedUser.setRole("STUDENT");
                lockedUser.setLocked(true);
                lockedUser.setLockedReason("Vi phạm quy định cộng đồng TalentLink (Tài khoản mẫu kiểm thử khóa).");
                lockedUser.setCreatedAt(now);
                lockedUser.setUpdatedAt(now);

                userRepository.save(admin);
                userRepository.save(partner);
                User savedStudent1 = userRepository.save(student);
                User savedStudent2 = userRepository.save(student2);
                userRepository.save(lockedUser);

                // Tạo sẵn hồ sơ và học vấn mẫu cho student01 (id=1) và student02 (id=2) để test chặn 403 khi đổi id trên URL
                Profile p1 = new Profile();
                p1.setUserId(savedStudent1.getId());
                p1.setHeadline("Java Backend Fresher");
                p1.setLocation("Hà Nội");
                p1.setBio("Sinh viên năm cuối chuyên ngành Kỹ thuật Phần mềm.");
                p1.setDateOfBirth(LocalDate.of(2004, 5, 15));
                p1.setOpenToWork(false);
                p1.setVisibility("PRIVATE");
                p1.setApprovalStatus("DRAFT");
                p1.setCreatedAt(now);
                p1.setUpdatedAt(now);
                Profile savedP1 = profileRepository.save(p1);

                Education edu1 = new Education();
                edu1.setProfileId(savedP1.getId());
                edu1.setSchoolName("Đại học Công Nghệ Giả Lập Hà Nội");
                edu1.setMajor("Kỹ thuật Phần mềm");
                edu1.setDegree("Kỹ sư");
                edu1.setStartDate(LocalDate.of(2022, 9, 1));
                edu1.setEndDate(LocalDate.of(2026, 6, 30));
                edu1.setGpa(new BigDecimal("3.45"));
                edu1.setDescription("Đạt học bổng khuyến khích học tập.");
                edu1.setCreatedAt(now);
                educationRepository.save(edu1);

                Profile p2 = new Profile();
                p2.setUserId(savedStudent2.getId());
                p2.setHeadline("Frontend React Developer");
                p2.setLocation("TP. Hồ Chí Minh");
                p2.setBio("Sinh viên chuyên ngành Khoa học Máy tính.");
                p2.setDateOfBirth(LocalDate.of(2004, 8, 20));
                p2.setOpenToWork(false);
                p2.setVisibility("PRIVATE");
                p2.setApprovalStatus("DRAFT");
                p2.setCreatedAt(now);
                p2.setUpdatedAt(now);
                Profile savedP2 = profileRepository.save(p2);

                Education edu2 = new Education();
                edu2.setProfileId(savedP2.getId());
                edu2.setSchoolName("Đại học Kỹ Thuật Số TP.HCM (Demo)");
                edu2.setMajor("Khoa học Máy tính");
                edu2.setDegree("Cử nhân");
                edu2.setStartDate(LocalDate.of(2022, 9, 1));
                edu2.setEndDate(LocalDate.of(2026, 6, 30));
                edu2.setGpa(new BigDecimal("3.60"));
                edu2.setDescription("Học vấn của Sinh viên 02 (dùng để kiểm thử chặn 403 khi Sinh viên 01 đổi ID trên URL).");
                edu2.setCreatedAt(now);
                educationRepository.save(edu2);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Bật CSRF cho mọi form
                .csrf(Customizer.withDefaults())

                // 2. Phân quyền URL theo đúng yêu cầu
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password", "/flash/**", "/error/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/partner/**").hasRole("PARTNER")
                        .requestMatchers("/me/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )

                // 3. Trang đăng nhập riêng bằng email + mật khẩu
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )

                // 4. Quản lý phiên đăng nhập bằng HTTP Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                )

                // 5. Cấu hình Remember-Me (duy trì đăng nhập 7 ngày)
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .rememberMeParameter("remember-me")
                        .rememberMeCookieName("talentlink-remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService)
                )

                // 6. Cấu hình Đăng xuất (POST /logout có CSRF, xóa session & cookie)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "talentlink-remember-me")
                        .permitAll()
                )

                // 7. Trang báo lỗi 403 khi truy cập sai vai trò
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                );

        return http.build();
    }
}
