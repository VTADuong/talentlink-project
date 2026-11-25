package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.ProjectDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.Project;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.ProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ Quản lý Dự án cá nhân của sinh viên (Task T4E7AA7):
 * - Thêm / Sửa / Xóa dự án: tên, vai trò, công nghệ dùng, link GitHub, link demo (tùy chọn), mô tả.
 * - Giới hạn tối đa 20 dự án cho mỗi hồ sơ.
 * - Kiểm tra quyền sở hữu (chặn 403 Forbidden nếu sửa/xóa dự án của người khác).
 */
@Service
public class ProjectService {

    public static final int MAX_PROJECTS_PER_PROFILE = 20;

    private final ProjectRepository projectRepository;
    private final MyProfileService myProfileService;

    public ProjectService(ProjectRepository projectRepository, MyProfileService myProfileService) {
        this.projectRepository = projectRepository;
        this.myProfileService = myProfileService;
    }

    @Transactional
    public List<Project> listMyProjects(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return projectRepository.findByProfileIdOrderByIdDesc(myProfile.getId());
    }

    @Transactional
    public long countMyProjects(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return projectRepository.countByProfileId(myProfile.getId());
    }

    @Transactional
    public boolean hasReachedMaxProjects(String currentEmail) {
        return countMyProjects(currentEmail) >= MAX_PROJECTS_PER_PROFILE;
    }

    @Transactional
    public ProjectDto getMyProjectDtoForEdit(Long projectId, String currentEmail) {
        Project project = getOwnedProjectOrThrow403(projectId, currentEmail);

        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setRoleInProject(project.getRoleInProject());
        dto.setTechStack(project.getTechStack());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setDemoUrl(project.getDemoUrl());
        dto.setDescription(project.getDescription());
        return dto;
    }

    @Transactional
    public Project createProject(String currentEmail, ProjectDto dto) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        long currentCount = projectRepository.countByProfileId(myProfile.getId());
        if (currentCount >= MAX_PROJECTS_PER_PROFILE) {
            throw new BusinessException(
                    "Mỗi hồ sơ chỉ được phép thêm tối đa " + MAX_PROJECTS_PER_PROFILE + " dự án cá nhân!",
                    "/me/profile");
        }

        Project project = new Project();
        project.setProfileId(myProfile.getId());
        applyDtoToEntity(dto, project);
        project.setCreatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long projectId, String currentEmail, ProjectDto dto) {
        Project project = getOwnedProjectOrThrow403(projectId, currentEmail);
        applyDtoToEntity(dto, project);
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long projectId, String currentEmail) {
        Project project = getOwnedProjectOrThrow403(projectId, currentEmail);
        projectRepository.delete(project);
    }

    private Project getOwnedProjectOrThrow403(Long projectId, String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Dự án cá nhân", projectId));

        if (!project.getProfileId().equals(myProfile.getId())) {
            throw new ForbiddenOperationException(
                    "Bạn không có quyền chỉnh sửa hoặc xóa dự án cá nhân của người khác (Mã dự án #" + projectId + ")!");
        }
        return project;
    }

    private void applyDtoToEntity(ProjectDto dto, Project project) {
        project.setProjectName(dto.getProjectName().trim());
        project.setRoleInProject(dto.getRoleInProject().trim());
        project.setTechStack(dto.getTechStack().trim());
        project.setGithubUrl(dto.getGithubUrl().trim());
        project.setDemoUrl(dto.getDemoUrl() != null && !dto.getDemoUrl().isBlank()
                ? dto.getDemoUrl().trim() : null);
        project.setDescription(dto.getDescription().trim());
    }
}
