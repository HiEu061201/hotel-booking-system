package fu.hbs.service.interfaces;

import fu.hbs.entities.CustomerCancellationReasons;

import java.util.List;

public interface CustomerCancellationReasonService {

    List<CustomerCancellationReasons> findAllCancellationReasons();
}
