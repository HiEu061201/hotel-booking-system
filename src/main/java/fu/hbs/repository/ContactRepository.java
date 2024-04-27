package fu.hbs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import fu.hbs.entities.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Contact findByEmail(String email);

    Long findUserIdByEmail(String email);

    @Query(value = "SELECT * FROM hotelsystem.contact s WHERE \n" +
            "    (?1 IS NULL OR LOWER(s.title) LIKE CONCAT('%', LOWER(?1), '%')) \n" +
            "    AND (?2 IS NULL OR s.status = ?2) \n" +
            "    OR (?1 IS NULL AND ?2 IS NULL) ORDER BY contact_id DESC", nativeQuery = true)
    Page<Contact> findAllByTitleAndStatus(String title, Integer status, PageRequest pageRequest);

    @Modifying
    @Transactional
    @Query("UPDATE Contact c SET c.status = :status WHERE c.contactId = :contactId")
    void updateStatus(@Param("status") Boolean status, @Param("contactId") Long contactId);

//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE contact\n" +
//            "SET status = 1 \n" +  // Set status to 1
//            "WHERE contact_id = ?1", nativeQuery = true)
//    void updateContactStatus(Long contactId);
}
