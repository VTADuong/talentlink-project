package com.talentlink.talentlink.config;

import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.UserRepository;
import com.talentlink.talentlink.service.CustomUserDetailsService;
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
    public CommandLineRunner seedDefaultAuthUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
                student.setRole("STUDENT");
                student.setLocked(false);
                student.setCreatedAt(now);
                student.setUpdatedAt(now);

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
                userRepository.save(student);
                userRepository.save(lockedUser);
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
