package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.AddProfileSkillDto;
import com.talentlink.talentlink.dto.StudentSkillViewDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.ProfileSkill;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.ProfileSkillRepository;
import com.talentlink.talentlink.repository.SkillRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ Quản lý "Kỹ năng của tôi" cho sinh viên (Task T4E7AA6):
 * - Chọn kỹ năng từ danh mục (hỗ trợ ô tìm nhanh), tự chấm mức 1–5 kèm mô tả từng mức.
 * - Không cho phép chọn trùng kỹ năng đã có trong hồ sơ.
 * - Bỏ kỹ năng khỏi hồ sơ.
 * - Hiển thị kỹ năng theo từng nhóm (Ngôn ngữ, Framework, CSDL, Công cụ, Kỹ năng mềm) trên trang hồ sơ.
 */
@Service
public class MySkillService {

    public static final Map<Integer, String> LEVEL_TITLES = Map.of(
            1, "Mức 1 — Cơ bản (Beginner)",
            2, "Mức 2 — Sơ cấp (Elementary)",
            3, "Mức 3 — Trung cấp (Intermediate)",
            4, "Mức 4 — Thành thạo (Advanced)",
            5, "Mức 5 — Chuyên gia (Expert)"
    );

    public static final Map<Integer, String> LEVEL_DESCRIPTIONS;

    static {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "Mức 1 (Cơ bản): Đã nắm lý thuyết, cú pháp cơ bản và làm các bài tập thực hành nhỏ.");
        map.put(2, "Mức 2 (Sơ cấp): Đã áp dụng được vào bài tập lớn hoặc đồ án môn học khi có hướng dẫn.");
        map.put(3, "Mức 3 (Trung cấp): Tự chủ xây dựng và hoàn thiện tính năng trong dự án thực tế.");
        map.put(4, "Mức 4 (Thành thạo): Nắm sâu kiến trúc, tối ưu hiệu năng và xử lý được các bài toán phức tạp.");
        map.put(5, "Mức 5 (Chuyên gia): Làm chủ công nghệ, thiết kế kiến trúc hệ thống và hướng dẫn người khác.");
        LEVEL_DESCRIPTIONS = Map.copyOf(map);
    }

    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final MyProfileService myProfileService;

    public MySkillService(
            SkillRepository skillRepository,
            ProfileSkillRepository profileSkillRepository,
            MyProfileService myProfileService) {
        this.skillRepository = skillRepository;
        this.profileSkillRepository = profileSkillRepository;
        this.myProfileService = myProfileService;
    }

    @Transactional(readOnly = true)
    public List<Skill> listAvailableCatalogSkills() {
        return skillRepository.findByHiddenFalseOrderByCategoryAscNameAsc();
    }

    @Transactional
    public Set<Long> getMySelectedSkillIds(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return profileSkillRepository.findByProfileId(myProfile.getId()).stream()
                .map(ProfileSkill::getSkillId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public boolean isDuplicateSkillInProfile(String currentEmail, Long skillId) {
        if (skillId == null) {
            return false;
        }
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return profileSkillRepository.existsByProfileIdAndSkillId(myProfile.getId(), skillId);
    }

    /**
     * Lấy danh sách kỹ năng của sinh viên được gom nhóm theo Nhóm kỹ năng (category)
     * để hiển thị trực quan trên trang hồ sơ.
     */
    @Transactional
    public Map<String, List<StudentSkillViewDto>> getMySkillsGroupedByCategory(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        List<ProfileSkill> profileSkills = profileSkillRepository.findByProfileId(myProfile.getId());
        if (profileSkills.isEmpty()) {
            return new LinkedHashMap<>();
        }

        List<Long> skillIds = profileSkills.stream().map(ProfileSkill::getSkillId).toList();
        Map<Long, Skill> skillMap = skillRepository.findAllById(skillIds).stream()
                .collect(Collectors.toMap(Skill::getId, Function.identity()));

        List<StudentSkillViewDto> views = new ArrayList<>();
        for (ProfileSkill ps : profileSkills) {
            Skill skill = skillMap.get(ps.getSkillId());
            if (skill != null) {
                int level = ps.getProficiencyLevel() != null ? ps.getProficiencyLevel() : 3;
                String category = (skill.getCategory() != null && !skill.getCategory().isBlank())
                        ? skill.getCategory()
                        : "Khác";
                views.add(new StudentSkillViewDto(
                        ps.getId(),
                        skill.getId(),
                        skill.getName(),
                        category,
                        level,
                        LEVEL_TITLES.getOrDefault(level, "Mức " + level),
                        LEVEL_DESCRIPTIONS.getOrDefault(level, "")
                ));
            }
        }

        views.sort(Comparator.comparing(StudentSkillViewDto::getProficiencyLevel).reversed()
                .thenComparing(StudentSkillViewDto::getSkillName));

        Map<String, List<StudentSkillViewDto>> grouped = new LinkedHashMap<>();
        // Sắp xếp ưu tiên theo 5 nhóm kỹ năng chuẩn
        for (String standardCategory : AdminSkillService.SKILL_CATEGORIES) {
            List<StudentSkillViewDto> inCat = views.stream()
                    .filter(v -> v.getCategory().equalsIgnoreCase(standardCategory))
                    .toList();
            if (!inCat.isEmpty()) {
                grouped.put(standardCategory, inCat);
            }
        }
        // Các nhóm khác (nếu có)
        for (StudentSkillViewDto v : views) {
            boolean matchedStandard = AdminSkillService.SKILL_CATEGORIES.stream()
                    .anyMatch(c -> c.equalsIgnoreCase(v.getCategory()));
            if (!matchedStandard) {
                grouped.computeIfAbsent(v.getCategory(), k -> new ArrayList<>()).add(v);
            }
        }
        return grouped;
    }

    /**
     * Thêm kỹ năng vào hồ sơ sinh viên kèm mức tự chấm 1–5.
     * Chặn chọn trùng kỹ năng đã có trong hồ sơ.
     */
    @Transactional
    public Skill addSkillToMyProfile(String currentEmail, AddProfileSkillDto dto) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", dto.getSkillId()));

        if (profileSkillRepository.existsByProfileIdAndSkillId(myProfile.getId(), skill.getId())) {
            throw new BusinessException(
                    "Kỹ năng [" + skill.getName() + "] đã có trong hồ sơ của bạn. Không được chọn trùng kỹ năng!",
                    "/me/profile");
        }

        ProfileSkill ps = new ProfileSkill();
        ps.setProfileId(myProfile.getId());
        ps.setSkillId(skill.getId());
        ps.setProficiencyLevel(dto.getProficiencyLevel());
        ps.setYearsUsed(BigDecimal.ONE);
        ps.setCreatedAt(LocalDateTime.now());
        profileSkillRepository.save(ps);

        return skill;
    }

    /**
     * Bỏ (gỡ) kỹ năng khỏi hồ sơ của sinh viên.
     * Kiểm tra quyền sở hữu: không cho phép xóa kỹ năng của hồ sơ người khác (403 Forbidden).
     */
    @Transactional
    public void removeSkillFromMyProfile(Long profileSkillId, String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        ProfileSkill ps = profileSkillRepository.findById(profileSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng hồ sơ", profileSkillId));

        if (!ps.getProfileId().equals(myProfile.getId())) {
            throw new ForbiddenOperationException(
                    "Bạn không có quyền gỡ kỹ năng khỏi hồ sơ của người khác!");
        }

        profileSkillRepository.delete(ps);
    }
}
