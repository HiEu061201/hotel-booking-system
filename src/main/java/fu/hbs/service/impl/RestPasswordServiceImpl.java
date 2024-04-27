/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 14/10/2023    1.0        HieuLBM          First Deploy
 * 30/10/2023    2.0        HieuLBM          Change Link
 */
package fu.hbs.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import fu.hbs.entities.Token;
import fu.hbs.entities.User;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.repository.TokenRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.service.interfaces.ResetService;
import fu.hbs.service.interfaces.TokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class RestPasswordServiceImpl implements ResetService {

    private UserRepository userRepository;

    private TokenRepository tokenRepository;
    private TokenService tokenService;

    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    public RestPasswordServiceImpl(UserRepository userRepository,
                                   TokenService tokenService,
                                   JavaMailSender javaMailSender,
                                   TokenRepository tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   TemplateEngine templateEngine) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.javaMailSender = javaMailSender;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.templateEngine = templateEngine;
    }

    /**
     * Constructor for RestPasswordImpl.
     *
     * @param email the repository for user data
     */

    public void resetPasswordRequest(String email) throws ResetExceptionHandler {
        User user = userRepository.findByEmailAndStatus(email, true);
        if (user != null) {
            Token token = tokenService.createToken(user);

            // Tạo nội dung email với mã token và thời gian hết hạn
            Context context = new Context();
            context.setVariable("resetLink", "/hbs/reset-password?token=" + token.getToken());
            context.setVariable("expirationDate", token.getExpirationDate());

            String emailContent = templateEngine.process("forgotPassword/resetPasswordTemplate", context);

            // Gửi email
            sendResetPasswordEmail(user.getEmail(), "Quên Mật Khẩu", emailContent);
        } else {
            throw new MailExceptionHandler("Không tìm thấy mail");
        }

    }

    /**
     * Request a password reset for a user based on their email.
     *
     * @param newPassword the email address of the user
     * @throws ResetExceptionHandler if there is an issue with the reset request
     */

    public boolean resetPassword(Token token, String newPassword) throws ResetExceptionHandler {
        String encodedPassword = passwordEncoder.encode(newPassword);
        Optional<User> userOptional = userRepository.findById(token.getUserId());
        // Đặt lại mật khẩu cho người dùng
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(encodedPassword);
            userRepository.save(user);
            tokenRepository.deleteById(token.getId());
            return true;
        } else {
            throw new ResetExceptionHandler("Cập nhật thất bại");
        }

    }

    /**
     * Send a reset password email to a recipient.
     *
     * @param recipientEmail the email address of the recipient
     * @param subject        the subject of the email
     * @param emailContent   the content of the email
     * @throws MailExceptionHandler if there is an issue with sending the email
     */
    public void sendResetPasswordEmail(String recipientEmail, String subject, String emailContent)
            throws MailExceptionHandler {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("3HKT@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true); // Sử dụng HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailExceptionHandler("Lỗi gửi mail");
        }
    }
}
