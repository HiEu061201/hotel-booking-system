/*
 * Copyright (C) 2023, FPT University 
 * SEP490 - SEP490_G77
 * HBS 
 * Hotel Booking System 
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023    1.0        HieuLBM          First Deploy	
 */
package fu.hbs.exceptionHandler;

import java.io.IOException;

public class FileNotFoundExceptionHandler extends IOException {
	private static final long serialVersionUID = 1L;

	public FileNotFoundExceptionHandler(String message) {
		super(message);
	}

}
