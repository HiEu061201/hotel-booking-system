package fu.hbs.repository;

import fu.hbs.entities.CustomerCancellationReasons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCancellationReasonRepository extends JpaRepository<CustomerCancellationReasons, Long> {

}
