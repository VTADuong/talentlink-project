package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProfileIdOrderByIdDesc(Long profileId);

    long countByProfileId(Long profileId);
}
