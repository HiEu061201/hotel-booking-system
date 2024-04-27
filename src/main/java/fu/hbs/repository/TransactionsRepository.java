package fu.hbs.repository;

import fu.hbs.entities.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {


    @Query(value = "SELECT * FROM transactions WHERE created_date LIKE  CONCAT( ?1, '%')  \n" +
            " AND (payment_id = ?2 OR ( ?2 IS NULL AND payment_id IN (1, 2))) ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findTransactionsCreatedDateAndPaymentId(LocalDate createDate, Long paymentId, Pageable pageable);

    @Query(value = "SELECT * FROM transactions " +
            "WHERE created_date LIKE CONCAT(?1, '%') " +
            "AND (payment_id = ?2 OR (?2 IS NULL AND payment_id IN (1, 2))) " +
            "AND (?3 IS NULL OR transactions_type_id = ?3) ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findTransactionsCreatedDateAndPaymentIdAndTypeId(LocalDate createDate, Long paymentId, Long typeId, Pageable pageable);

    @Query(value = "SELECT * FROM transactions WHERE payment_id = ?1 ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findTransactionsByPaymentId(Long paymentId, Pageable pageable);


    @Query(value = "SELECT * FROM transactions WHERE transactions_type_id = ?1 ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findTransactionsByTransactionsTypeId(Long transactionsTypeId, Pageable pageable);


    @Query(value = "SELECT * FROM transactions WHERE transactions_type_id = ?1 AND payment_id = ?2 ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findTransactionsByTransactionsTypeIdAndPaymentId(Long transactionsTypeId, Long paymentId, Pageable pageable);

    @Query(value = "SELECT * FROM transactions  ORDER BY transaction_id DESC", nativeQuery = true)
    Page<Transactions> findAll(Pageable pageable);


    @Query(value = "SELECT SUM(amount) " +
            "FROM hotelsystem.transactions " +
            "WHERE YEAR(created_date) = ?1 AND " +
            "MONTH(created_date) = ?2", nativeQuery = true)
    Float getRevenueByYearAndMonth(String year, String month);

    @Query(value = "SELECT * \n" +
            "FROM hotelsystem.transactions \n" +
            "WHERE hotel_booking_id = ?1 \n" +
            "ORDER BY created_date DESC\n" +
            "LIMIT 1;", nativeQuery = true)
    Transactions findByHotelBookingIdNew(Long hotelBookingId);

    List<Transactions> findByHotelBookingId(Long hotelBookingId);

    Optional<Transactions> findByHotelBookingIdAndTransactionsTypeId(Long hotelBookingId, Long transactionTypeId);
}
