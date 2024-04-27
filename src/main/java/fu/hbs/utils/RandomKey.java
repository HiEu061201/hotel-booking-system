package fu.hbs.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RandomKey {
	public static String generateRandomKey() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
		String key = encoder.encode("hieu");

		return key;
	}

}
