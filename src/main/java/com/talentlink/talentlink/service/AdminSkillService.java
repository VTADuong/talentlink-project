package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.SkillDto;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.ProfileSkillRepository;
import com.talentlink.talentlink.repository.SkillRepository;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ Quản trị danh mục kỹ năng tại /admin/skills (Task T4E7AA5):
 * - 5 nhóm kỹ năng chuẩn: Ngôn ngữ, Framework, CSDL, Công cụ, Kỹ năng mềm.
 * - Kiểm tra tên không trùng, không phân biệt hoa thường.
 * - Kỹ năng đã có sinh viên dùng thì chỉ ẩn (hidden = true), không xóa.
 */
@Service
public class AdminSkillService {

    public static final List<String> SKILL_CATEGORIES = List.of(
            "Ngôn ngữ",
            "Framework",
            "CSDL",
            "Công cụ",
            "Kỹ năng mềm"
    );

    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;

    public AdminSkillService(
            SkillRepository skillRepository,
            ProfileSkillRepository profileSkillRepository) {
        this.skillRepository = skillRepository;
        this.profileSkillRepository = profileSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<Skill> listSkills(String keyword, String categoryFilter, String statusFilter) {
        String cleanKeyword = keyword != null ? keyword.trim().toLowerCase(Locale.ROOT) : "";
        String cleanCategory = categoryFilter != null ? categoryFilter.trim() : "ALL";
        String cleanStatus = statusFilter != null ? statusFilter.trim().toUpperCase(Locale.ROOT) : "ALL";

        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .filter(s -> cleanKeyword.isEmpty()
                        || (s.getName() != null && s.getName().toLowerCase(Locale.ROOT).contains(cleanKeyword)))
                .filter(s -> "ALL".equalsIgnoreCase(cleanCategory)
                        || cleanCategory.isEmpty()
                        || (s.getCategory() != null && s.getCategory().equalsIgnoreCase(cleanCategory)))
                .filter(s -> {
                    if ("VISIBLE".equals(cleanStatus)) {
                        return !s.isHidden();
                    }
                    if ("HIDDEN".equals(cleanStatus)) {
                        return s.isHidden();
                    }
                    return true;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getSkillUsageCounts(List<Skill> skills) {
        Map<Long, Long> counts = new HashMap<>();
        for (Skill skill : skills) {
            counts.put(skill.getId(), profileSkillRepository.countBySkillId(skill.getId()));
        }
        return counts;
    }

    /**
     * Kiểm tra tên kỹ năng có bị trùng (không phân biệt hoa thường) hay không.
     *
     * @param name      Tên kỹ năng cần kiểm tra
     * @param excludeId ID kỹ năng đang sửa (hoặc null khi thêm mới)
     */
    @Transactional(readOnly = true)
    public boolean isDuplicateSkillName(String name, Long excludeId) {
        if (name == null || name.isBlank()) {
            return false;
        }
        Optional<Skill> existingOpt = skillRepository.findByNameIgnoreCase(name.trim());
        if (existingOpt.isEmpty()) {
            return false;
        }
        if (excludeId == null) {
            return true;
        }
        return !existingOpt.get().getId().equals(excludeId);
    }

    @Transactional(readOnly = true)
    public SkillDto getSkillDtoById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", id));

        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setCategory(skill.getCategory());
        dto.setHidden(skill.isHidden());
        return dto;
    }

    @Transactional
    public Skill createSkill(SkillDto dto) {
        String cleanName = dto.getName().trim();
        Skill skill = new Skill();
        skill.setName(cleanName);
        skill.setSlug(generateUniqueSlug(cleanName));
        skill.setCategory(dto.getCategory().trim());
        skill.setHidden(dto.isHidden());
        skill.setCreatedAt(LocalDateTime.now());
        return skillRepository.save(skill);
    }

    @Transactional
    public Skill updateSkill(Long id, SkillDto dto) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", id));

        String cleanName = dto.getName().trim();
        if (!skill.getName().equalsIgnoreCase(cleanName)) {
            skill.setSlug(generateUniqueSlug(cleanName));
        }
        skill.setName(cleanName);
        skill.setCategory(dto.getCategory().trim());
        skill.setHidden(dto.isHidden());
        return skillRepository.save(skill);
    }

    @Transactional
    public Skill toggleHidden(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", id));
        skill.setHidden(!skill.isHidden());
        return skillRepository.save(skill);
    }

    /**
     * Xóa hoặc Ẩn kỹ năng:
     * - Nếu kỹ năng ĐÃ CÓ sinh viên dùng (usageCount > 0): Chỉ chuyển sang trạng thái ẨN (hidden = true), KHÔNG xóa!
     * - Nếu kỹ năng CHƯA CÓ sinh viên nào dùng: Xóa khỏi DB.
     *
     * @return true nếu đã xóa cứng, false nếu chỉ chuyển sang ẩn do đã có sinh viên dùng.
     */
    @Transactional
    public boolean deleteOrHideIfUsed(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", id));

        long usageCount = profileSkillRepository.countBySkillId(id);
        if (usageCount > 0) {
            skill.setHidden(true);
            skillRepository.save(skill);
            return false;
        }

        skillRepository.delete(skill);
        return true;
    }

    private String generateUniqueSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (normalized.isEmpty()) {
            normalized = "skill";
        }
        String candidate = normalized;
        int suffix = 1;
        while (skillRepository.existsBySlugIgnoreCase(candidate)) {
            candidate = normalized + "-" + suffix;
            suffix++;
        }
        return candidate;
    }
}
