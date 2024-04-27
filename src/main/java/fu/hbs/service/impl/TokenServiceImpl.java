/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 14/10/2023    1.0        HieuLBM          First Deploy
 * 18/10/2023	 2.0		HieuLBM			 edit deleteToken
 * 18/10/2023	 2.0		HieuLBM			 edit findByUserId
 */
package fu.hbs.service.impl;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fu.hbs.entities.Token;
import fu.hbs.entities.User;
import fu.hbs.repository.TokenRepository;
import fu.hbs.service.interfaces.TokenService;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    private TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Create a new token for a user or update an existing token if one already
     * exists.
     *
     * @param user the user for whom the token is created or updated
     * @return the created or updated token
     */
    @Override
    public Token createToken(User user) {
        Token existingToken = tokenRepository.findByUserId(user.getUserId());

        // Nếu đã tồn tại mã token cho người dùng, hãy cập nhật mã token cũ
        if (existingToken != null) {
            existingToken.setToken(generateToken());
            // Cập nhật thời gian hết hạn, ví dụ: 1 giờ sau thời điểm cập nhật
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 1);
            existingToken.setExpirationDate(calendar.getTime());
            return tokenRepository.save(existingToken);
        }

        Token newToken = new Token();
        newToken.setUserId(user.getUserId());
        newToken.setToken(generateToken());

        // Thiết lập thời gian hết hạn cho token (ví dụ: 1 giờ sau thời điểm tạo)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        newToken.setExpirationDate(calendar.getTime());

        return tokenRepository.save(newToken);
    }

    /**
     * Find a token by its value (token string) and check if it's valid and not
     * expired.
     *
     * @param tokenValue the token value to search for
     * @return the token if it's valid and not expired, or null if the token is
     * invalid or expired
     */
    @Override
    public Token findTokenByValue(String tokenValue) {
        Token token = tokenRepository.findByToken(tokenValue);
        // Kiểm tra xem token có tồn tại và chưa hết hạn không
        if (token != null && !token.isExpired()) {
            return token;
        }

        return null; // Token không hợp lệ hoặc đã hết hạn
    }

    /**
     * Generate a new unique token using UUID.
     *
     * @return a new unique token string
     */
    private String generateToken() {
        // Tạo mã token duy nhất
        // (Ví dụ: sử dụng UUID)
        return UUID.randomUUID().toString();
    }

    /**
     * Delete a token by its ID.
     *
     * @param id the ID of the token to be deleted
     */
    @Override
    public void deleteToken(Long id) {
        tokenRepository.deleteById(id);
    }
}
