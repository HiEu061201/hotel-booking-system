package fu.hbs.utils;

import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import fu.hbs.exceptionHandler.MailExceptionHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Component
public class EmailUtil {
    private final JavaMailSender javaMailSender;

    public EmailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendBookingEmail(String recipientEmail, String subject, String emailContent)
            throws MailExceptionHandler {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("your-email@example.com", "Khách sạn 3HKT");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true); // Sử dụng HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailExceptionHandler("Lỗi gửi mail");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}