package fu.hbs.service.impl;

import fu.hbs.entities.Role;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.repository.RoleRepository;
import fu.hbs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.LockedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomizeUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CustomizeUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("authenticateUserUTCIDO1")
    void authenticateUserUTCIDO1() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("@User123");
        mockUser.setStatus(true);

        Role mockRole = new Role();
        mockRole.setRoleName("ROLE_USER");

        List<Role> mockRoles = new ArrayList<>();
        mockRoles.add(mockRole);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(roleRepository.findRole(mockUser.getUserId())).thenReturn(mockRoles);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails, "Đăng nhập thành công");

    }

    @Test
    @DisplayName("authenticateUserUTCIDO2")
    void authenticateUserUTCIDO2() {
        // Given
        String email = "truong12";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("@User123");
        mockUser.setStatus(true);


        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());


    }

    @Test
    @DisplayName("authenticateUserUTCIDO3")
    void authenticateUserUTCIDO3() {
        // Given
        String email = "";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("@User123");
        mockUser.setStatus(true);


        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());


    }

    @Test
    @DisplayName("authenticateUserUTCIDO4")
    void authenticateUserUTCIDO4() {
        // Given
        String email = null;
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("@User123");
        mockUser.setStatus(true);


        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());


    }


    @Test
    @DisplayName("authenticateUserUTCIDO5")
    void authenticateUserUTCIDO5() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("eqslogin");
        mockUser.setStatus(true);

        Role mockRole = new Role();
        mockRole.setRoleName("ROLE_USER");

        List<Role> mockRoles = new ArrayList<>();
        mockRoles.add(mockRole);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());
    }

    @Test
    @DisplayName("authenticateUserUTCIDO6")
    void authenticateUserUTCIDO6() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("");
        mockUser.setStatus(true);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());
    }

    @Test
    @DisplayName("authenticateUserUTCIDO7")
    void authenticateUserUTCIDO7() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("1234567");
        mockUser.setStatus(true);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());
    }

    @Test
    @DisplayName("authenticateUserUTCIDO8")
    void authenticateUserUTCIDO8() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("User1233");
        mockUser.setStatus(true);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());
    }

    @Test
    @DisplayName("authenticateUserUTCIDO9")
    void authenticateUserUTCIDO9() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword(null);
        mockUser.setStatus(true);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Mật khẩu hoặc tài khoản không đúng!", exception.getMessage());
    }


    @Test
    @DisplayName("authenticateUserUTCIDO10")
    void authenticateUserUTCIDO10() {
        // Given
        String email = "truong12@gmail.com";
        fu.hbs.entities.User mockUser = new fu.hbs.entities.User();
        mockUser.setEmail(email);
        mockUser.setPassword("@User123");
        mockUser.setStatus(false);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        // When/Then
        LockedException lockedException = assertThrows(LockedException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });


        // Kiểm tra xem thông báo của ngoại lệ có đúng không
        assertEquals("Tài khoản đã bị khóa!", lockedException.getMessage());
    }
}
