package fu.hbs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fu.hbs.dto.AdminDTO.ViewUserDTO;
import fu.hbs.entities.Role;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.repository.RoleRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.repository.UserRoleRepository;
import fu.hbs.service.interfaces.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public ViewUserDTO getUserDetails(Long userId) {
        ViewUserDTO viewUserDTO = new ViewUserDTO();
        User user = userRepository.findByUserId(userId);
        UserRole userRole = userRoleRepository.findByUserId(user.getUserId());
        Role role = roleRepository.findByRoleId(userRole.getRoleId());
        viewUserDTO.setUserRole(userRole);
        viewUserDTO.setRole(role);
        viewUserDTO.setUser(user);
        return viewUserDTO;

    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

}
