package fu.hbs.dto.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserDTO {
    private Long userId;
    private Long roleId;
    private Boolean status;
}
