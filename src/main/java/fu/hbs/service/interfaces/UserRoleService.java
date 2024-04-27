package fu.hbs.service.interfaces;

import fu.hbs.entities.UserRole;

public interface UserRoleService {
    UserRole findByUserIdAndAndRoleId(Long userId, Long roleId);
}
