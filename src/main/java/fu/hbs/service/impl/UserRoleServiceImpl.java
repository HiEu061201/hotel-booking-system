package fu.hbs.service.impl;

import fu.hbs.entities.UserRole;
import fu.hbs.repository.UserRoleRepository;
import fu.hbs.service.interfaces.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserRole findByUserIdAndAndRoleId(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndAndRoleId(userId, roleId);
    }
}
