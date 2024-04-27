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
package fu.hbs.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fu.hbs.entities.Role;
import fu.hbs.repository.RoleRepository;
import fu.hbs.repository.UserRepository;

@Service("customizeUserDetailsService")
public class CustomizeUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public CustomizeUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Load user details by email for authentication.
     *
     * @param email the email address of the user
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the email is not found
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, LockedException {
        fu.hbs.entities.User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Mật khẩu hoặc tài khoản không đúng!");
        }

        if (!user.isStatus()) {
            throw new LockedException("Tài khoản đã bị khóa!");
        }

        List<Role> roles = roleRepository.findRole(user.getUserId());
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities
        );
    }


}
