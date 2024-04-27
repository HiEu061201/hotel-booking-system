package fu.hbs.repository;

import fu.hbs.entities.EarlyCheckoutRefunds;
import fu.hbs.entities.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarlyCheckoutRefundsRepository extends JpaRepository<EarlyCheckoutRefunds, Long> {
}
