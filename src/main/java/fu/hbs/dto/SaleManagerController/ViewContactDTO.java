package fu.hbs.dto.SaleManagerController;

import fu.hbs.entities.Contact;
import fu.hbs.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewContactDTO {
	private Long contactId;
    private Contact contact;
    private User user;

}
