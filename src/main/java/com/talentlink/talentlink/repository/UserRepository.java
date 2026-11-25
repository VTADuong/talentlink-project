package com.talentlink.talentlink.repository;

import com.talentlink.talentlink.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
            SELECT u FROM User u
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:filterByRole = false OR UPPER(u.role) IN :roles)
              AND (:locked IS NULL OR u.locked = :locked)
            """)
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("filterByRole") boolean filterByRole,
            @Param("roles") List<String> roles,
            @Param("locked") Boolean locked,
            Pageable pageable);
}
