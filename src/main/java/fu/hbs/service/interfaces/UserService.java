/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 04/10/2023    1.0        HieuLBM          First Deploy
 *
 *
 */
package fu.hbs.service.interfaces;

import fu.hbs.dto.AdminDTO.UpdateUserDTO;
import fu.hbs.dto.AdminDTO.ViewUserDTO;
import fu.hbs.dto.User.UserDto;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.exceptionHandler.UserIvalidException;
import fu.hbs.exceptionHandler.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface UserService {
    /**
     * Save a new user with the provided user data.
     *
     * @param userDto the user data to create a new user
     * @return the newly created user
     */
    User save(UserDto userDto);

    ResponseEntity<?> getListUser();

    /**
     * Check if the provided email and password match a user's credentials.
     *
     * @param email    the email of the user
     * @param password the password to check
     * @return true if the credentials are valid, false otherwise
     */
    Boolean checkPasswordUser(String email, String password);

    /**
     * Check if a user with the provided email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    Boolean checkUserbyEmail(String email);

    /**
     * Get a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return the user associated with the provided email
     */
    User getUserbyEmail(String email);

    /**
     * Find a user by their identifier.
     *
     * @param id the identifier of the user to find
     * @return the user with the specified identifier
     * @throws UserNotFoundException if the user is not found
     */
    User findById(Long id) throws UserNotFoundException;

    /**
     * Update the information of a user.
     *
     * @param user the user data to update
     * @return the updated user
     * @throws UserIvalidException if the user data is invalid
     */
    User update(User user) throws UserIvalidException;

    /**
     * Check if a user with the provided phone number exists.
     *
     * @param phone the phone number to check
     * @return true if a user with the phone number exists, false otherwise
     * @throws UserNotFoundException if the phone number is invalid
     */
    boolean findByPhone(String phone) throws UserNotFoundException;

    /**
     * Save a user with the provided data.
     *
     * @param user the user data to save
     * @return the saved user
     */
    User save(User user);

    User getUserById(Long userId);


    Page<ViewUserDTO> searchByNameAndStatus(String name, Boolean status, Long roleId, int page, int size);

    void updateUserStatus(UpdateUserDTO updateUserDTO);

    void updateUserRoleByUserId(UpdateUserDTO updateUserDTO);

    void saveStaff(User staff, Long staffRole);


    void saveStaffRole(UserRole userRole);
}
