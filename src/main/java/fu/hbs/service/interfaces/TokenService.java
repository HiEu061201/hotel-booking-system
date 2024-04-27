/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 14/10/2023    1.0        HieuLBM          First Deploy
 *  *
 */
package fu.hbs.service.interfaces;

import fu.hbs.entities.Token;
import fu.hbs.entities.User;

public interface TokenService {

    /**
     * Create a new token for a user.
     *
     * @param user the user for which the token is created
     * @return the newly created token
     */
    Token createToken(User user);

    /**
     * Find a token by its value.
     *
     * @param tokenValue the value of the token to find
     * @return the token associated with the given value
     */
    Token findTokenByValue(String tokenValue);

    /**
     * Delete a token by its identifier.
     *
     * @param id the identifier of the token to delete
     */
    void deleteToken(Long id);
}
