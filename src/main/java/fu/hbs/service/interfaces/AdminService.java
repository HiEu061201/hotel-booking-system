package fu.hbs.service.interfaces;

import java.util.List;

import fu.hbs.dto.AdminDTO.ViewUserDTO;
import org.springframework.stereotype.Service;

import fu.hbs.entities.Role;

@Service
public interface AdminService {

    ViewUserDTO getUserDetails(Long userId);

    List<Role> getAll();


}
 