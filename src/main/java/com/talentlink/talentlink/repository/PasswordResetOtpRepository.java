package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.PasswordResetOtp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findFirstByEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    List<PasswordResetOtp> findByEmailIgnoreCaseAndUsedFalseAndInvalidatedFalse(String email);
}
