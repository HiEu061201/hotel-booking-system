/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 04/10/2023    1.0        HieuLBM          First Deploy
 *
 *
 */
package fu.hbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import fu.hbs.entities.User;
import org.springframework.transaction.annotation.Transactional;

@Repository("userRepository")
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByEmailAndStatus(String email, boolean status);

    User getUserByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    User findByUserId(Long id);


    Page<User> findByStatus(Boolean status, PageRequest pageRequest);


    @Query(value = "SELECT * FROM user s WHERE " +
            "    (?1 IS NULL OR LOWER(s.name) LIKE CONCAT('%', LOWER(?1), '%')) " +
            "    AND (?2 IS NULL OR s.status = TRUE) " +
            "    OR (?1 IS NULL AND ?2 IS NULL)", nativeQuery = true)
    Page<User> searchByNameAndStatus(String name, Boolean status, Pageable pageable);

    @Query(value = "SELECT u.*\n" +
            "FROM user u\n" +
            "LEFT JOIN user_role ur ON u.user_id = ur.user_id \n" +
            "JOIN role r ON ur.role_id = r.role_id \n" +
            "WHERE \n" +
            "    (LOWER(u.name) LIKE CONCAT('%', LOWER( ?1 ), '%') OR ?1 IS NULL)\n" +
            "    AND (u.status = ?2 OR ?2 IS NULL)\n" +
            "    AND (r.role_id = ?3 OR ?3 IS NULL); ", nativeQuery = true)
    Page<User> searchByNameAndStatusAndRoleId(String name, Boolean status, Long roleId, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET status = ?1 WHERE user_id = ?2", nativeQuery = true)
    void updateUserStatus(Boolean status, Long userId);
    
    @Query(nativeQuery = true,
            value = "select u.*\n" +
                    "from user u\n" +
                    "inner join user_role ur on ur.user_id = u.user_id \n" +
                    "inner join role r on ur.role_id = r.role_id\n" +
                    "where ur.role_id  = ?1")
    List<User> findAllUserByRole(Long roleId);


}
