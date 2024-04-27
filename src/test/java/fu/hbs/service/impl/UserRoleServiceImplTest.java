package fu.hbs.service.impl;

import fu.hbs.entities.UserRole;
import fu.hbs.repository.UserRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @Test
    @DisplayName("findByUserIdAndRoleIdUTCID01")
    void testFindByUserIdAndRoleId_UserRoleFound() {
        // Given
        Long userId = 1L;
        Long roleId = 2L;

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);

        when(userRoleRepository.findByUserIdAndAndRoleId(userId, roleId)).thenReturn(userRole);

        // When
        UserRole result = userRoleService.findByUserIdAndAndRoleId(userId, roleId);

        // Then
        assertNotNull(result);


    }

    @Test
    @DisplayName("findByUserIdAndRoleIdUTCID02")
    void testFindByUserIdAndRoleId_UserRoleNotFound() {
        // Given
        Long userId = 1L;
        Long roleId = 2L;

        when(userRoleRepository.findByUserIdAndAndRoleId(userId, roleId)).thenReturn(null);

        // When
        UserRole result = userRoleService.findByUserIdAndAndRoleId(userId, roleId);

        // Then
        assertNull(result);
    }
}
