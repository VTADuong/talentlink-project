package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.ProfileSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {

    long countBySkillId(Long skillId);

    boolean existsBySkillId(Long skillId);

    List<ProfileSkill> findByProfileId(Long profileId);
}
