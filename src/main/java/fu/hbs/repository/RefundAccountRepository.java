package fu.hbs.repository;

import fu.hbs.entities.RefundAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefundAccountRepository extends JpaRepository<RefundAccount, Long> {
    RefundAccount save(RefundAccount refundAccount);

    @Query(value = "SELECT ra.*\n" +
            "FROM refund_account ra\n" +
            "WHERE user_id = ?1 \n" +
            "ORDER BY account_id DESC\n" +
            "LIMIT 1", nativeQuery = true)
    RefundAccount findAccountIdNew(Long userId);


    RefundAccount findByAccountId(Long accountId);
}
