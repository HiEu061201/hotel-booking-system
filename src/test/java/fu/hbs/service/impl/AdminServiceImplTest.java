package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fu.hbs.dto.AdminDTO.ViewUserDTO;
import fu.hbs.entities.Role;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.repository.RoleRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

	@InjectMocks
	private AdminServiceImpl adminService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Test
	void testGetUserDetails() {
		// Set up
		Long userId = 1L;

		// User found
		User mockUser = new User();
		mockUser.setUserId(userId);
		when(userRepository.findByUserId(userId)).thenReturn(mockUser);

		// User role found
		UserRole mockUserRole = new UserRole();
		mockUserRole.setRoleId(1L);
		mockUserRole.setUserId(1L);
		when(userRoleRepository.findByUserId(userId)).thenReturn(mockUserRole);

		// Role found
		Role mockRole = new Role();
		mockRole.setRoleId(1L);
		when(roleRepository.findByRoleId(mockUserRole.getRoleId())).thenReturn(mockRole);

		// Run service method
		ViewUserDTO result = adminService.getUserDetails(userId);

		// Assert
		assertEquals(mockUser, result.getUser());
		assertEquals(mockUserRole, result.getUserRole());
		assertEquals(mockRole, result.getRole());

	}

	@Test
	void testGetAll() {
		// Set up
		Role mockRole1 = new Role();
		mockRole1.setRoleId(1L);

		Role mockRole2 = new Role();
		mockRole2.setRoleId(2L);

		// Define behavior of the roleRepository
		when(roleRepository.findAll()).thenReturn(Arrays.asList(mockRole1, mockRole2));

		// Run service method
		List<Role> result = adminService.getAll();

		// Assert
		assertEquals(2, result.size());
		assertEquals(mockRole1, result.get(0));
		assertEquals(mockRole2, result.get(1));

		// Verify repository method call
		verify(roleRepository, times(1)).findAll();
	}
}