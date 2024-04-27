package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fu.hbs.entities.Token;
import fu.hbs.entities.User;
import fu.hbs.repository.TokenRepository;

@ContextConfiguration(classes = {TokenServiceImpl.class})
@ExtendWith(SpringExtension.class)
class TokenServiceImplTest {
    @MockBean(name = "tokenRepository")
    private TokenRepository tokenRepository;

    @Autowired
    private TokenServiceImpl tokenServiceImpl;

    /**
     * Method under test: {@link TokenServiceImpl#createToken(User)} Test case này
     * đang test hàm createToken của tokenService khi người dùng đã có token cũ tồn
     * tại trong csdl.
     */
    @Test
    @DisplayName("createTokenUTCID01")
    void testCreateTokenWhenTokenDoesNotExistThenReturnNewToken() {
        // Given
        User user = new User();
        user.setUserId(1L);

        when(tokenRepository.findByUserId(user.getUserId())).thenReturn(null);
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Token result = tokenServiceImpl.createToken(user);

        // Then
        assertNotNull(result, "Tạo mới token thành công");

    }

    @Test
    @DisplayName("createTokenUTCID02")
    void testCreateTokenWhenTokenExistsThenReturnUpdatedToken() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Token existingToken = new Token();
        existingToken.setUserId(user.getUserId());
        existingToken.setToken("oldToken");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        existingToken.setExpirationDate(calendar.getTime());

        when(tokenRepository.findByUserId(user.getUserId())).thenReturn(existingToken);
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Token result = tokenServiceImpl.createToken(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        assertTrue(result.getExpirationDate().after(Calendar.getInstance().getTime()));
        assertNotEquals("oldToken", result.getToken());
    }

    /**
     * Method under test: {@link TokenServiceImpl#findTokenByValue(String)}
     */
    @Test
    @DisplayName("findTokenByValueUTCID01")
    void testFindTokenByValue() {
        Token token = mock(Token.class);
        when(token.isExpired()).thenReturn(false);
        doNothing().when(token).setExpirationDate(Mockito.<Date>any());
        doNothing().when(token).setId(Mockito.<Long>any());
        doNothing().when(token).setToken(Mockito.<String>any());
        doNothing().when(token).setUserId(Mockito.<Long>any());
        token.setExpirationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        token.setId(1L);
        token.setToken("ABC123");
        token.setUserId(1L);
        when(tokenRepository.findByToken(Mockito.<String>any())).thenReturn(token);
        Token token1 = tokenServiceImpl.findTokenByValue("ABC123");
        assertNotNull(token1, "Token hợp lệ");

    }

    /**
     * Method under test: {@link TokenServiceImpl#findTokenByValue(String)} Test
     * case này giả lập trường hợp khi được truyền vào một token đã hết hạn thì hàm
     * findTokenByValue sẽ trả về null.
     */

    @Test
    @DisplayName("findTokenByValueUTCID02")
    public void testFindTokenByValueWhenExpiredTokenThenReturnNull() {
        User user = new User();
        user.setUserId(1L);
        Token token = new Token();
        token.setUserId(user.getUserId());
        token.setToken("valid-token");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        token.setExpirationDate(calendar.getTime());
        // token hết hạn
        when(tokenRepository.findByToken("expired-token")).thenReturn(token);

        // Gọi phương thức findTokenByValue của tokenService, truyền vào token đã hết

        Token result = tokenServiceImpl.findTokenByValue("expired-token");

        // Kiểm tra kết quả trả về là null, để đảm bảo rằng khi token hết hạn thì hàm sẽ

        assertNull(result, "Token hết hạn");
    }

    @Test
    public void testFindTokenByValueWhenInvalidTokenThenReturnNull() {
        when(tokenRepository.findByToken("invalid-token")).thenReturn(null);

        Token result = tokenServiceImpl.findTokenByValue("invalid-token");

        assertNull(result);
    }

    /**
     * Method under test: {@link TokenServiceImpl#deleteToken(Long)}
     */
    @Test
    void testDeleteToken() {
        doNothing().when(tokenRepository).deleteById(Mockito.<Long>any());
        tokenServiceImpl.deleteToken(1L);
        verify(tokenRepository).deleteById(Mockito.<Long>any());
    }
}
