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
package fu.hbs.service.interfaces;

import fu.hbs.entities.Token;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.ResetExceptionHandler;

public interface ResetService {
    /**
     * Send a password reset request to the provided email address.
     *
     * @param email the email address for which the password reset is requested
     * @throws ResetExceptionHandler if there is an issue with the password
     */
    void resetPasswordRequest(String email) throws ResetExceptionHandler;

    /**
     * Reset a user's password using a token and a new password.
     *
     * @param token       the token used for password reset
     * @param newPassword the new password to set for the user
     * @return true if the password reset was successful, false otherwise
     * @throws ResetExceptionHandler if there is an issue with the password reset
     */
    boolean resetPassword(Token token, String newPassword) throws ResetExceptionHandler;

    /**
     * Send a password reset email to the recipient with the provided email content
     * and subject.
     *
     * @param recipientEmail the recipient's email address
     * @param subject        the subject of the email
     * @param emailContent   the content of the email
     * @throws MailExceptionHandler if there is an issue with sending the password
     */
    void sendResetPasswordEmail(String recipientEmail, String subject, String emailContent) throws MailExceptionHandler;
}
