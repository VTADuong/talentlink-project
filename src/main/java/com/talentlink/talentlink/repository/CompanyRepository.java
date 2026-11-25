package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.Company;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserId(Long userId);

    List<Company> findByUserIdIn(Collection<Long> userIds);
}
