package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO cho Form Sinh viên chọn kỹ năng từ danh mục và tự chấm mức 1–5 (Task T4E7AA6).
 */
public class AddProfileSkillDto {

    @NotNull(message = "Vui lòng chọn một kỹ năng từ danh mục")
    private Long skillId;

    @NotNull(message = "Vui lòng chọn mức độ thành thạo (1 đến 5)")
    @Min(value = 1, message = "Mức độ thành thạo tối thiểu là 1")
    @Max(value = 5, message = "Mức độ thành thạo tối đa là 5")
    private Integer proficiencyLevel = 3;

    public AddProfileSkillDto() {
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(Integer proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }
}
