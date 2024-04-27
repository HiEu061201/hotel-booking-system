package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import fu.hbs.dto.AdminDTO.UpdateUserDTO;
import fu.hbs.dto.AdminDTO.ViewUserDTO;
import fu.hbs.dto.User.UserDto;
import fu.hbs.entities.Role;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.exceptionHandler.UserIvalidException;
import fu.hbs.exceptionHandler.UserNotFoundException;
import fu.hbs.repository.RoleRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRoleRepository userRoleRepository; // Ensure this is properly annotated with @Mock

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	@DisplayName("testSave")
	void testSave() {
		UserDto userDto = new UserDto();
		userDto.setUserEmail("test@example.com");
		userDto.setUserName("Test User");
		userDto.setUserPassword("password123");

		User savedUser = new User();
		savedUser.setUserId(1L);
		savedUser.setEmail(userDto.getUserEmail());
		savedUser.setName(userDto.getUserName());
		savedUser.setPassword("encodedPassword");
		savedUser.setStatus(true);

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		// When
		User result = userService.save(userDto);

		// Then
		assertNotNull(result);
		assertEquals(savedUser.getUserId(), result.getUserId());
		assertEquals(userDto.getUserEmail(), result.getEmail());
		assertEquals(userDto.getUserName(), result.getName());
		assertEquals("encodedPassword", result.getPassword());
		assertTrue(result.isStatus());

	}

	@Test
	@DisplayName("testCheckPasswordUser_CorrectPassword")
	void testCheckPasswordUser_CorrectPassword() {
		// Given
		String email = "test@example.com";
		String password = "correctPassword";

		User user = new User();
		user.setEmail(email);
		user.setPassword(password);

		when(userRepository.findByEmail(email)).thenReturn(user);

		// When
		boolean result = userService.checkPasswordUser(email, password);

		// Then
		assertTrue(result);

		// Verify that userRepository.findByEmail is called
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	@DisplayName("testCheckPasswordUser_IncorrectPassword")
	void testCheckPasswordUser_IncorrectPassword() {
		// Given
		String email = "test@example.com";
		String correctPassword = "correctPassword";
		String incorrectPassword = "incorrectPassword";

		User user = new User();
		user.setEmail(email);
		user.setPassword(correctPassword);

		when(userRepository.findByEmail(email)).thenReturn(user);

		// When
		boolean result = userService.checkPasswordUser(email, incorrectPassword);

		// Then
		assertFalse(result);

		// Verify that userRepository.findByEmail is called
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	@DisplayName("testCheckUserByEmail_UserExists")
	void testCheckUserByEmail_UserExists() {
		// Given
		String existingEmail = "test@example.com";
		User existingUser = new User();
		existingUser.setEmail(existingEmail);

		when(userRepository.findByEmail(existingEmail)).thenReturn(existingUser);

		// When
		boolean result = userService.checkUserbyEmail(existingEmail);

		// Then
		assertTrue(result);

		// Verify that userRepository.findByEmail is called
		verify(userRepository, times(1)).findByEmail(existingEmail);
	}

	@Test
	@DisplayName("testCheckUserByEmail_UserNotExists")
	void testCheckUserByEmail_UserNotExists() {
		// Given
		String nonExistingEmail = "nonexistent@example.com";

		when(userRepository.findByEmail(nonExistingEmail)).thenReturn(null);

		// When
		boolean result = userService.checkUserbyEmail(nonExistingEmail);

		// Then
		assertFalse(result);

		// Verify that userRepository.findByEmail is called
		verify(userRepository, times(1)).findByEmail(nonExistingEmail);
	}

	@Test
	@DisplayName("getUserByEmailUTCIDO1")
	void testGetUserByEmail_UserExists() {
		// Given
		String existingEmail = "test@example.com";
		User existingUser = new User();
		existingUser.setEmail(existingEmail);

		// Stubbing
		when(userRepository.findByEmail(existingEmail)).thenReturn(existingUser);

		// When
		User result = userService.getUserbyEmail(existingEmail);

		// Then
		assertNotNull(result, "User should exist");
		assertEquals(existingEmail, result.getEmail(), "Returned user's email should match");
	}

	@Test
	@DisplayName("getUserByEmailUTCIDO2")
	void testGetUserByEmail_UserNotExists() {
		// Given
		String nonExistingEmail = "nonexistent@example.com";

		// When
		User result = userService.getUserbyEmail(nonExistingEmail);

		// Then
		assertNull(result, "Không tìm thấy user");
	}

	@Test
	@DisplayName("testUpdate_UserPhoneIsNull")
	void testUpdate_UserPhoneIsNull() throws UserIvalidException {
		// Given
		User user = new User();
		user.setUserId(1L);
		user.setPhone(null);

		when(userRepository.save(user)).thenReturn(user);

		// When
		User result = userService.update(user);

		// Then
		assertNotNull(result);
		assertNull(result.getPhone());

		// Verify that userRepository.save is called
		verify(userRepository, times(1)).save(user);
		verify(userRepository, times(0)).findByPhone(anyString());
	}

	@Test
	@DisplayName("testUpdate_UserPhoneExistsAndBelongsToCurrentUser")
	void testUpdate_UserPhoneExistsAndBelongsToCurrentUser() throws UserIvalidException {
		// Given
		User user = new User();
		user.setUserId(1L);
		user.setPhone("123456789");

		when(userRepository.findByPhone(user.getPhone())).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		// When
		User result = userService.update(user);

		// Then
		assertNotNull(result);
		assertEquals(user.getPhone(), result.getPhone());

		// Verify that userRepository.findByPhone and userRepository.save are called
		verify(userRepository, times(1)).findByPhone(user.getPhone());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	@DisplayName("testUpdate_UserPhoneExistsAndDoesNotBelongToCurrentUser")
	void testUpdate_UserPhoneExistsAndDoesNotBelongToCurrentUser() {
		// Given
		User user = new User();
		user.setUserId(1L);
		user.setPhone("123456789");

		User existingUser = new User();
		existingUser.setUserId(2L);

		when(userRepository.findByPhone(user.getPhone())).thenReturn(Optional.of(existingUser));

		// When and Then
		assertThrows(UserIvalidException.class, () -> userService.update(user));

		// Verify that userRepository.findByPhone is called
		verify(userRepository, times(1)).findByPhone(user.getPhone());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	@DisplayName("testFindByIdUTCID01")
	void testFindById_UserExists() throws UserNotFoundException {
		// Given
		Long userId = 1L;
		User existingUser = new User();
		existingUser.setUserId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		// When
		User result = userService.findById(userId);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());

	}

	@Test
	@DisplayName("testFindByIdUTCID02")
	void testFindById_UserNotExists() {
		// Given
		Long nonExistingUserId = 2L;

		when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

		// When and Then
		assertThrows(UserNotFoundException.class, () -> userService.findById(nonExistingUserId));

	}

	@Test
	@DisplayName("testFindByPhone_UserExists")
	void testFindByPhone_UserExists() throws UserNotFoundException {
		// Given
		String existingPhone = "123456789";
		User existingUser = new User();
		existingUser.setPhone(existingPhone);

		when(userRepository.findByPhone(existingPhone)).thenReturn(Optional.of(existingUser));

		// When
		boolean result = userService.findByPhone(existingPhone);

		// Then
		assertTrue(result);

		// Verify that userRepository.findByPhone is called
		verify(userRepository, times(1)).findByPhone(existingPhone);
	}

	@Test
	@DisplayName("testFindByPhone_UserNotExists")
	void testFindByPhone_UserNotExists() throws UserNotFoundException {
		// Given
		String nonExistingPhone = "987654321";

		when(userRepository.findByPhone(nonExistingPhone)).thenReturn(null);

		// When
		boolean result = userService.findByPhone(nonExistingPhone);

		// Then
		assertFalse(result);

		// Verify that userRepository.findByPhone is called
		verify(userRepository, times(1)).findByPhone(nonExistingPhone);
	}

	@Test
	@DisplayName("testSave_UserSuccessfullySaved")
	void testSave_UserSuccessfullySaved() {
		// Given
		User userToSave = new User();
		User savedUser = new User();
		savedUser.setUserId(1L);

		when(userRepository.save(userToSave)).thenReturn(savedUser);

		// When
		User result = userService.save(userToSave);

		// Then
		assertNotNull(result);
		assertEquals(savedUser.getUserId(), result.getUserId());

		// Verify that userRepository.save is called
		verify(userRepository, times(1)).save(userToSave);
	}

	@Test
	@DisplayName("testGetUserById_UserExists")
	void testGetUserById_UserExists() {
		// Given
		Long userId = 1L;
		User existingUser = new User();
		existingUser.setUserId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		// When
		User result = userService.getUserById(userId);

		// Then
		assertNotNull(result);
		assertEquals(userId, result.getUserId());

		// Verify that userRepository.findById is called
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("testGetUserById_UserNotExists")
	void testGetUserById_UserNotExists() {
		// Given
		Long nonExistingUserId = 2L;

		when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

		// When
		User result = userService.getUserById(nonExistingUserId);

		// Then
		assertNull(result);

		// Verify that userRepository.findById is called
		verify(userRepository, times(1)).findById(nonExistingUserId);
	}

	@Test
	@DisplayName("testSearchByNameAndStatusUTCID04")
	void testSearchByNameAndStatus_RoleIdIsNull() {
		// Given
		String name = "John Doe";
		Boolean status = true;
		Long roleId = null;
		int page = 0;
		int size = 10;

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<User> userPage = mock(Page.class);

		when(userRepository.searchByNameAndStatus(name, status, pageRequest)).thenReturn(userPage);

		// When
		Page<ViewUserDTO> result = userService.searchByNameAndStatus(name, status, roleId, page, size);

		// Then
		assertNull(result);
	}

	@Test
	@DisplayName("testSearchByNameAndStatusUTCID01")
	void testSearchByNameAndStatus_RoleIdIsNotNull() {
		// Arrange
		String name = "John Doe";
		Boolean status = true;
		Long roleId = 1L;
		int page = 0;
		int size = 10;

		User user = new User();
		user.setUserId(1L);

		UserRole userRole = new UserRole();
		userRole.setRoleId(1L);

		Role role = new Role();
		role.setRoleId(1L);

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

		when(userRepository.searchByNameAndStatusAndRoleId(name, status, roleId, pageRequest)).thenReturn(userPage);
		when(userRoleRepository.findByUserId(user.getUserId())).thenReturn(userRole);
		when(roleRepository.findByRoleId(userRole.getRoleId())).thenReturn(role);

		// Act
		Page<ViewUserDTO> result = userService.searchByNameAndStatus(name, status, roleId, page, size);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(user, result.getContent().get(0).getUser());
		assertEquals(userRole, result.getContent().get(0).getUserRole());
		assertEquals(role, result.getContent().get(0).getRole());

	}

	@Test
	@DisplayName("testUpdateUserStatusUTCID01")
	void testUpdateUserStatus() {
		// Given
		UpdateUserDTO updateUserDTO = new UpdateUserDTO();
		updateUserDTO.setUserId(1L);
		updateUserDTO.setStatus(true);

		// When
		userService.updateUserStatus(updateUserDTO);

		verify(userRepository, times(1)).updateUserStatus(updateUserDTO.getStatus(), updateUserDTO.getUserId());
	}

	@Test
	@DisplayName("testUpdateUserRoleByUserIdUTCID01")
	void testUpdateUserRoleByUserId() {
		// Given
		UpdateUserDTO updateUserDTO = new UpdateUserDTO();
		updateUserDTO.setUserId(1L);
		updateUserDTO.setRoleId(2L); // Set the desired role ID

		// When
		userService.updateUserRoleByUserId(updateUserDTO);

		// Then
		// Verify that userRoleRepository.updateUserRoleByUserId is called with the
		// expected parameters
		verify(userRoleRepository, times(1)).updateUserRoleByUserId(updateUserDTO.getRoleId(),
				updateUserDTO.getUserId());
	}

	@Test
	@DisplayName("testSaveStaffUTCID01")
	void testSaveStaff() {
		// Given
		User staff = new User();
		// Set properties for the staff object as needed

		Long staffRole = 2L; // Set the desired staff role ID

		// Mock the behavior of userRepository.save to return a staff with a generated
		// userId
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User staffWithId = invocation.getArgument(0);
			staffWithId.setUserId(1L); // Set the desired user ID
			return staffWithId;
		});

		// When
		userService.saveStaff(staff, staffRole);

		// Then
		// Verify that userRepository.save is called with the staff object
		verify(userRepository, times(1)).save(staff);

		// Verify that userRoleRepository.save is called with the expected UserRole
		UserRole expectedUserRole = new UserRole();
		expectedUserRole.setUserId(1L); // The user ID generated during save
		expectedUserRole.setRoleId(staffRole);
	}

	@Test
	@DisplayName("saveStaffRole")
	void testSaveStaffRole_VerifySaveMethodCalled() {
		// Given
		UserRole userRole = new UserRole();
		userRole.setId(1L);
		// When
		userService.saveStaffRole(userRole);

		verify(userRoleRepository, times(1)).save(userRole);
	}
}
