package fu.hbs.repository;

import fu.hbs.entities.TransactionsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsTyPeRepository extends JpaRepository<TransactionsType, Long> {
    TransactionsType findByTransactionsTypeId(Long transactionId);
}
