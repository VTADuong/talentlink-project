package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO cho Form Thêm / Sửa Dự án cá nhân trên trang hồ sơ (Task T4E7AA7):
 * - Tên dự án, vai trò, công nghệ dùng, link GitHub (URL hợp lệ), link demo (tùy chọn, URL hợp lệ), mô tả.
 */
public class ProjectDto {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên dự án")
    @Size(min = 2, max = 150, message = "Tên dự án phải từ 2 đến 150 ký tự")
    private String projectName;

    @NotBlank(message = "Vui lòng nhập vai trò của bạn trong dự án")
    @Size(min = 2, max = 100, message = "Vai trò trong dự án phải từ 2 đến 100 ký tự")
    private String roleInProject;

    @NotBlank(message = "Vui lòng nhập các công nghệ sử dụng")
    @Size(min = 2, max = 255, message = "Công nghệ sử dụng phải từ 2 đến 255 ký tự")
    private String techStack;

    @NotBlank(message = "Vui lòng nhập link GitHub của dự án")
    @Size(max = 255, message = "Link GitHub tối đa 255 ký tự")
    @Pattern(
            regexp = "^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$",
            message = "Link GitHub phải là một URL hợp lệ (bắt đầu bằng http:// hoặc https://)"
    )
    private String githubUrl;

    @Size(max = 255, message = "Link Demo tối đa 255 ký tự")
    @Pattern(
            regexp = "^$|^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$",
            message = "Link Demo phải là một URL hợp lệ (bắt đầu bằng http:// hoặc https://)"
    )
    private String demoUrl;

    @NotBlank(message = "Vui lòng nhập mô tả dự án")
    @Size(min = 5, max = 2000, message = "Mô tả dự án phải từ 5 đến 2000 ký tự")
    private String description;

    public ProjectDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(String roleInProject) {
        this.roleInProject = roleInProject;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
