package fu.hbs.dto.AdminDTO;

import fu.hbs.entities.Role;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewUserDTO {
    private User user;
    private Role role;
    private UserRole userRole;
}
