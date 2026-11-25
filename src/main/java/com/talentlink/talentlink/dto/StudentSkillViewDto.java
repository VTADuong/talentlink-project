package com.talentlink.talentlink.dto;

/**
 * DTO hiển thị thông tin Kỹ năng của sinh viên kèm Nhóm kỹ năng và Mô tả mức độ 1–5 trên trang hồ sơ.
 */
public class StudentSkillViewDto {

    private Long profileSkillId;
    private Long skillId;
    private String skillName;
    private String category;
    private Integer proficiencyLevel;
    private String levelTitle;
    private String levelDescription;

    public StudentSkillViewDto() {
    }

    public StudentSkillViewDto(
            Long profileSkillId,
            Long skillId,
            String skillName,
            String category,
            Integer proficiencyLevel,
            String levelTitle,
            String levelDescription) {
        this.profileSkillId = profileSkillId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
        this.levelTitle = levelTitle;
        this.levelDescription = levelDescription;
    }

    public Long getProfileSkillId() {
        return profileSkillId;
    }

    public void setProfileSkillId(Long profileSkillId) {
        this.profileSkillId = profileSkillId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(Integer proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }
}
