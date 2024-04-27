package fu.hbs.dto.SaleManagerController;

import fu.hbs.entities.New;
import fu.hbs.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewNewsDTO {
    private New aNew;
    private User user;
}
