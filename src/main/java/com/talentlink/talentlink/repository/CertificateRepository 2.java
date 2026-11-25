package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByProfileIdOrderByIssueDateDescIdDesc(Long profileId);
}
