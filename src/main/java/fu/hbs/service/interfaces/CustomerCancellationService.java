package fu.hbs.service.interfaces;

import fu.hbs.dto.CancellationDTO.ViewCancellationDTO;
import fu.hbs.entities.CustomerCancellation;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface CustomerCancellationService {

    CustomerCancellation saveCustomerCancellation(CustomerCancellation customerCancellation);

    CustomerCancellation findCustomerCancellationNew(Long cancelId);

    Page<ViewCancellationDTO> getAllByStatusConfirm(LocalDate checkIn, Integer status, int page, int size);

    Page<ViewCancellationDTO> getAllByStatusProcess(LocalDate checkIn, Integer status, int page, int size);
}
