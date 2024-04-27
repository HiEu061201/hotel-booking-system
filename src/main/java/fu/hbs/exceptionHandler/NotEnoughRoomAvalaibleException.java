/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 12/11/2023    1.0        HieuDT74          First Deploy
 */
package fu.hbs.exceptionHandler;

public class NotEnoughRoomAvalaibleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotEnoughRoomAvalaibleException(String message) {
        super(message);
    }
}