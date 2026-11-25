package com.talentlink.talentlink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.talentlink.talentlink.config.CustomAuthenticationFailureHandler;
import com.talentlink.talentlink.config.RoleBasedAuthenticationSuccessHandler;
import com.talentlink.talentlink.config.SecurityConfig;
import com.talentlink.talentlink.dto.AddProfileSkillDto;
import com.talentlink.talentlink.dto.CertificateDto;
import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.dto.ProjectDto;
import com.talentlink.talentlink.dto.StudentRegisterDto;
import com.talentlink.talentlink.dto.StudentRegistrationValidator;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.EducationRepository;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.ProfileSkillRepository;
import com.talentlink.talentlink.repository.SkillRepository;
import com.talentlink.talentlink.repository.UserRepository;
import com.talentlink.talentlink.service.AvatarService;
import com.talentlink.talentlink.service.CertificateService;
import com.talentlink.talentlink.service.CustomUserDetailsService;
import com.talentlink.talentlink.service.CustomUserPrincipal;
import com.talentlink.talentlink.service.EducationService;
import com.talentlink.talentlink.service.MyProfileService;
import com.talentlink.talentlink.service.MySkillService;
import com.talentlink.talentlink.service.ProjectService;
import com.talentlink.talentlink.service.StudentRegistrationService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

/**
 * Kiểm thử Controller & Bảo mật bằng MockMvc (Task T4E7AAA):
 * - Đăng nhập (thành công theo vai trò, thất bại do sai mật khẩu hoặc tài khoản bị khóa).
 * - Phân quyền URL (/admin/** chỉ ADMIN, /partner/** chỉ PARTNER, /me/** chỉ STUDENT).
 * - Form lỗi validate (Đăng ký sinh viên, Hồ sơ cá nhân, Học vấn ngày bắt đầu > ngày kết thúc, thiếu CSRF).
 */
@WebMvcTest(controllers = {
        AuthController.class,
        RoleHomeController.class,
        MyProfileController.class
})
@Import({
        SecurityConfig.class,
        RoleBasedAuthenticationSuccessHandler.class,
        CustomAuthenticationFailureHandler.class,
        GlobalExceptionHandler.class
})
class Sprint1ControllerMockMvcTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private StudentRegistrationService registrationService;

    @MockitoBean
    private StudentRegistrationValidator registrationValidator;

    @MockitoBean
    private MyProfileService myProfileService;

    @MockitoBean
    private EducationService educationService;

    @MockitoBean
    private MySkillService mySkillService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private CertificateService certificateService;

    @MockitoBean
    private AvatarService avatarService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProfileRepository profileRepository;

    @MockitoBean
    private EducationRepository educationRepository;

    @MockitoBean
    private SkillRepository skillRepository;

    @MockitoBean
    private ProfileSkillRepository profileSkillRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("student01@talentlink.demo");
        mockUser.setFullName("Sinh Viên Demo 01");
        mockUser.setRole("STUDENT");

        Profile mockProfile = new Profile();
        mockProfile.setId(1L);
        mockProfile.setUserId(1L);
        mockProfile.setVisibility("PRIVATE");
        mockProfile.setOpenToWork(false);

        when(myProfileService.getCurrentUser(anyString())).thenReturn(mockUser);
        when(myProfileService.getOrCreateMyProfile(anyString())).thenReturn(mockProfile);
        when(myProfileService.buildFormDto(anyString())).thenReturn(new MyProfileUpdateDto());
        when(educationService.listMyEducations(anyString())).thenReturn(Collections.emptyList());
        when(mySkillService.getMySkillsGroupedByCategory(anyString())).thenReturn(Collections.emptyMap());
        when(mySkillService.listAvailableCatalogSkills()).thenReturn(Collections.emptyList());
        when(mySkillService.getMySelectedSkillIds(anyString())).thenReturn(Collections.emptySet());
        when(projectService.listMyProjects(anyString())).thenReturn(Collections.emptyList());
        when(certificateService.listMyCertificates(anyString())).thenReturn(Collections.emptyList());
        when(avatarService.extractInitialFromName(any())).thenReturn("D");
    }

    @Test
    @DisplayName("17. Đăng nhập thành công với tài khoản STUDENT -> Chuyển hướng về /me/dashboard")
    void login_ValidStudentCredentials_RedirectsToStudentDashboard() throws Exception {
        User student = new User();
        student.setId(1L);
        student.setEmail("student01@talentlink.demo");
        student.setFullName("Sinh Viên 01");
        student.setPasswordHash(passwordEncoder.encode("123456aA@"));
        student.setRole("STUDENT");
        student.setLocked(false);

        when(userDetailsService.loadUserByUsername("student01@talentlink.demo"))
                .thenReturn(new CustomUserPrincipal(student));

        mockMvc.perform(formLogin("/login")
                        .user("email", "student01@talentlink.demo")
                        .password("password", "123456aA@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/me/dashboard"));
    }

    @Test
    @DisplayName("18. Đăng nhập thất bại (Trường hợp lỗi): Tài khoản bị khóa không đăng nhập được và báo lỗi")
    void login_LockedAccount_RedirectsToLoginError() throws Exception {
        User lockedStudent = new User();
        lockedStudent.setId(5L);
        lockedStudent.setEmail("locked@talentlink.demo");
        lockedStudent.setFullName("Sinh Viên Bị Khóa");
        lockedStudent.setPasswordHash(passwordEncoder.encode("123456aA@"));
        lockedStudent.setRole("STUDENT");
        lockedStudent.setLocked(true);
        lockedStudent.setLockedReason("Vi phạm quy định hệ thống");

        when(userDetailsService.loadUserByUsername("locked@talentlink.demo"))
                .thenReturn(new CustomUserPrincipal(lockedStudent));

        mockMvc.perform(formLogin("/login")
                        .user("email", "locked@talentlink.demo")
                        .password("password", "123456aA@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?locked"));
    }

    @Test
    @DisplayName("19. Đăng nhập thất bại (Trường hợp lỗi): Sai mật khẩu chuyển hướng về /login?error")
    void login_WrongPassword_RedirectsToLoginError() throws Exception {
        when(userDetailsService.loadUserByUsername("unknown@talentlink.demo"))
                .thenThrow(new UsernameNotFoundException("Không tìm thấy tài khoản"));

        mockMvc.perform(formLogin("/login")
                        .user("email", "unknown@talentlink.demo")
                        .password("password", "WrongPass!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("20. Phân quyền URL: Chưa đăng nhập truy cập /me/profile bị chuyển hướng về trang /login")
    void urlAuthorization_AnonymousUserAccessingStudentProfile_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/me/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("21. Phân quyền URL (Trường hợp lỗi 403): STUDENT truy cập /admin/dashboard hoặc /partner/dashboard bị chặn 403")
    void urlAuthorization_StudentAccessingAdminOrPartnerUrl_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .with(user("student01@talentlink.demo").roles("STUDENT")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));

        mockMvc.perform(get("/partner/dashboard")
                        .with(user("student01@talentlink.demo").roles("STUDENT")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    @DisplayName("22. Phân quyền URL: ADMIN truy cập /admin/dashboard thành công (200) nhưng vào /me/profile bị chặn 403")
    void urlAuthorization_AdminRoleBoundaries_AllowsAdminAndBlocksStudentUrl() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .with(user("admin@talentlink.demo").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/admin-home"));

        mockMvc.perform(get("/me/profile")
                        .with(user("admin@talentlink.demo").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    @DisplayName("23. Form lỗi Validate (POST /register): Để trống họ tên, email sai định dạng, mật khẩu ngắn và không khớp")
    void registerFormValidation_InvalidInput_ReturnsRegisterViewWithFieldErrors() throws Exception {
        doAnswer(invocation -> {
            BindingResult br = invocation.getArgument(1);
            br.rejectValue("confirmPassword", "register.confirmPassword.mismatch",
                    "Mật khẩu nhập lại không khớp với mật khẩu đã nhập");
            return null;
        }).when(registrationValidator).validate(any(StudentRegisterDto.class), any(BindingResult.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "")
                        .param("email", "not-an-email")
                        .param("password", "123")
                        .param("confirmPassword", "456"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "registerDto", "fullName", "email", "password", "confirmPassword"));

        verify(registrationService, never()).registerStudent(any());
    }

    @Test
    @DisplayName("24. Form lỗi Validate (POST /me/educations): Ngày bắt đầu sau ngày kết thúc và GPA > 4.0 bị báo lỗi trên form")
    void educationFormValidation_StartDateAfterEndDateAndInvalidGpa_ReturnsProfileViewWithFieldErrors() throws Exception {
        mockMvc.perform(post("/me/educations")
                        .with(user("student01@talentlink.demo").roles("STUDENT"))
                        .with(csrf())
                        .param("schoolName", "Đại học Quốc Gia")
                        .param("major", "Khoa học Máy tính")
                        .param("startDate", "2026-09-01")
                        .param("endDate", "2022-06-01")
                        .param("gpa", "4.50"))
                .andExpect(status().isOk())
                .andExpect(view().name("me/profile"))
                .andExpect(model().attributeHasFieldErrors("educationDto", "endDate", "gpa"));

        verify(educationService, never()).createEducation(anyString(), any(EducationDto.class));
    }

    @Test
    @DisplayName("25. Bảo mật CSRF (Trường hợp lỗi 403): Gửi form POST không kèm CSRF token bị chặn 403 Forbidden")
    void csrfProtection_PostRegisterWithoutCsrfToken_Returns403Forbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Sinh Viên Test")
                        .param("email", "test@talentlink.demo")
                        .param("password", "123456aA@")
                        .param("confirmPassword", "123456aA@"))
                .andExpect(status().isForbidden());
    }
}
