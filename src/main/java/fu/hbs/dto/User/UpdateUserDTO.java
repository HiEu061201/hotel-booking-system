package fu.hbs.dto.User;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserDTO {
	private Long userId;
	private String name;
	private Date dob;
	private String email;
	private String address;
	private String phone;
	private String gender;
	private byte[] image;

}
