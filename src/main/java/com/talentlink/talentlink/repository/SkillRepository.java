package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.Skill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    List<Skill> findAllByOrderByCategoryAscNameAsc();
}
