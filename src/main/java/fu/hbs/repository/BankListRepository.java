package fu.hbs.repository;

import fu.hbs.entities.BankList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankListRepository extends JpaRepository<BankList, Long> {

    BankList findByBankId(Long bankId);
}
