package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO cho Form Thêm / Sửa Kỹ năng tại /admin/skills (Task T4E7AA5):
 * - Tên kỹ năng (không trùng, không phân biệt hoa thường).
 * - Nhóm kỹ năng: Ngôn ngữ, Framework, CSDL, Công cụ, Kỹ năng mềm.
 */
public class SkillDto {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên kỹ năng")
    @Size(min = 1, max = 100, message = "Tên kỹ năng không được vượt quá 100 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn nhóm kỹ năng")
    @Size(max = 50, message = "Nhóm kỹ năng tối đa 50 ký tự")
    private String category;

    private boolean hidden = false;

    public SkillDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
