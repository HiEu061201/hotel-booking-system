package fu.hbs.dto.SaleManagerController;

import fu.hbs.entities.Feedback;
import fu.hbs.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewFeedBackDTO {
    private Feedback feedback;
    private User user;
}
