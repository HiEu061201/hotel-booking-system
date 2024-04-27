package fu.hbs.repository;

import fu.hbs.entities.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    PaymentType findByPaymentId(Long paymentId);
}
