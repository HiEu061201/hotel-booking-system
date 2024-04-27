package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fu.hbs.entities.Token;
import fu.hbs.entities.User;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.repository.TokenRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.service.interfaces.TokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@ContextConfiguration(classes = {RestPasswordServiceImpl.class})
@ExtendWith(SpringExtension.class)
class RestPasswordImplTest {
    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestPasswordServiceImpl restPasswordImpl;

    @MockBean
    private TemplateEngine templateEngine;

    @MockBean(name = "tokenRepository")
    private TokenRepository tokenRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean(name = "userRepository")
    private UserRepository userRepository;

    @InjectMocks
    private RestPasswordServiceImpl restPasswordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        restPasswordService = new RestPasswordServiceImpl(
                userRepository, tokenService, javaMailSender, tokenRepository, passwordEncoder, templateEngine);
    }

    /**
     * Method under test: {@link RestPasswordServiceImpl#resetPasswordRequest(String)}
     */
    @Test
    @DisplayName("resetPasswordRequestUTCID01")
    void resetPasswordRequestUTCID01() throws ResetExceptionHandler, MailException, MessagingException {
        // Given
        String userEmail = "jane.doe@example.org";
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword("ok");
        user.setUserId(1L);

        Token token = new Token();
        token.setId(1L);
        token.setToken("ABC123");
        token.setUserId(1L);

        when(userRepository.findByEmailAndStatus(userEmail, true)).thenReturn(user);
        when(tokenService.createToken(user)).thenReturn(token);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("Process");
        doNothing().when(javaMailSender).send(Mockito.<MimeMessage>any());

        // When
        restPasswordService.resetPasswordRequest(userEmail);

        // Then
        verify(userRepository).findByEmailAndStatus(userEmail, true);
        verify(tokenService).createToken(user);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("forgotPassword/resetPasswordTemplate"), contextCaptor.capture());

        Context capturedContext = contextCaptor.getValue();
        assertEquals("http://localhost:8080/hbs/reset-password?token=" + token.getToken(), capturedContext.getVariable("resetLink"));
        assertNotNull(capturedContext.getVariable("expirationDate"));


    }

    @Test
    @DisplayName("resetPasswordRequestUTCID02")
    void resetPasswordRequestUTCID02() {
        // Given
        String userEmail = "truong12";
        when(userRepository.findByEmailAndStatus(userEmail, true)).thenReturn(null);

        // When/Then
        assertThrows(MailExceptionHandler.class, () -> {
            restPasswordService.resetPasswordRequest(userEmail);
        });

        // Verify that sendResetPasswordEmail is not called
        verify(javaMailSender, never()).send(Mockito.<MimeMessage>any());
    }

    @Test
    @DisplayName("resetPasswordRequestUTCID03")
    void resetPasswordRequestUTCID03() {
        // Given
        String userEmail = "";
        when(userRepository.findByEmailAndStatus(userEmail, true)).thenReturn(null);

        // When/Then
        assertThrows(MailExceptionHandler.class, () -> {
            restPasswordService.resetPasswordRequest(userEmail);
        });

        // Verify that sendResetPasswordEmail is not called
        verify(javaMailSender, never()).send(Mockito.<MimeMessage>any());
    }

    @Test
    @DisplayName("resetPasswordRequestUTCID04")
    void resetPasswordRequestUTCID04() {
        // Given
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByEmailAndStatus(userEmail, true)).thenReturn(null);

        // When/Then
        assertThrows(MailExceptionHandler.class, () -> {
            restPasswordService.resetPasswordRequest(userEmail);
        });

        // Verify that sendResetPasswordEmail is not called
        verify(javaMailSender, never()).send(Mockito.<MimeMessage>any());
    }

    /**
     * Method under test: {@link RestPasswordServiceImpl#resetPassword(Token, String)}
     */
    @Test
    @DisplayName("resetPasswordUTCID01")
    void resetPasswordUTCID01() throws ResetExceptionHandler {
        // Given
        Token token = new Token();
        token.setId(1L);
        token.setToken("User@111");
        token.setUserId(1L);

        User user = new User();
        user.setUserId(1L);
        user.setPassword("User@111");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");


        // When
        boolean result = restPasswordService.resetPassword(token, "newPassword");

        // Then
        assertTrue(result, "Cập nhật thành công");

    }

    /**
     * Method under test: {@link RestPasswordServiceImpl#resetPassword(Token, String)}
     */
    @Test
    @DisplayName("resetPasswordUTCID02")
    void resetPasswordUTCID02() {
        // Given
        Token token = new Token();
        token.setId(1L);
        token.setToken("ABC123");
        token.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        ResetExceptionHandler exception = assertThrows(ResetExceptionHandler.class, () -> {
            restPasswordService.resetPassword(token, "User@111");
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Cập nhật thất bại", exception.getMessage());

    }

    @Test
    @DisplayName("resetPasswordUTCID03")
    void resetPasswordUTCID03() {
        // Given
        Token token = new Token();
        token.setId(1L);
        token.setToken("1245678");
        token.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        ResetExceptionHandler exception = assertThrows(ResetExceptionHandler.class, () -> {
            restPasswordService.resetPassword(token, "1245678");
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Cập nhật thất bại", exception.getMessage());

    }

    @Test
    @DisplayName("resetPasswordUTCID04")
    void resetPasswordUTCID04() {
        // Given
        Token token = new Token();
        token.setId(1L);
        token.setToken("123");
        token.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        ResetExceptionHandler exception = assertThrows(ResetExceptionHandler.class, () -> {
            restPasswordService.resetPassword(token, "");
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Cập nhật thất bại", exception.getMessage());

    }

    @Test
    @DisplayName("resetPasswordUTCID05")
    void resetPasswordUTCID05() {
        // Given
        Token token = new Token();
        token.setId(1L);
        token.setToken("sa");
        token.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        ResetExceptionHandler exception = assertThrows(ResetExceptionHandler.class, () -> {
            restPasswordService.resetPassword(token, null);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Cập nhật thất bại", exception.getMessage());

    }


    /**
     * Method under test: {@link RestPasswordServiceImpl#sendResetPasswordEmail(String, String, String)}
     */
    @Test
    @DisplayName("testSendResetPasswordEmailUTCID01")
    void testSendResetPasswordEmail2() throws MailExceptionHandler, MailException {
        doThrow(new MailExceptionHandler("An error occurred")).when(javaMailSender).send(Mockito.<MimeMessage>any());
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        assertThrows(MailExceptionHandler.class, () -> restPasswordImpl.sendResetPasswordEmail("truongmq123@gmail.com",
                "Hello from the Dreaming Spires", "jane.doe@example.org"));
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(Mockito.<MimeMessage>any());
    }


}



