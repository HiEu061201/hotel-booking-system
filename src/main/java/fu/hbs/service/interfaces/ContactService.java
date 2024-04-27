package fu.hbs.service.interfaces;

import fu.hbs.dto.SaleManagerController.ViewContactDTO;
import org.springframework.data.domain.Page;


public interface ContactService {

    Page<ViewContactDTO> findAllByTitleAndStatus(String name, Integer status, int page, int size);
}
