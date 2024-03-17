package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.NotUniqueFieldException;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void initialize() {
        user = User.builder()
                .id(null)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now())
                .build();
    }

    @Test
    void giverUser_whenSave_returnSavedUser() {
        User expected = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(user.getLastLoginDate())
                .build();

        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmail("test@mail.com",  -1L))
                .thenReturn(Optional.empty());

        when(userRepository.save(user)).thenAnswer(invocation -> {
           User u = invocation.getArgument(0, User.class);
           u.setId(1L);
           return u;
        });

        User result = userService.save(user);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).save(same(user));
        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
    }

    @Test
    void givenUser_whenSave_throwsNotUniqueFiled_usernameNotUnique() {
        User existingUser = User.builder()
                .id(2L)
                .email("test2@mail.com")
                .username("TestUser")
                .password("987654321")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findUserByEmail("test@mail.com",  -1L))
                .thenReturn(Optional.empty());

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.save(user));

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");
        String expectedExceptionMessage = "Not unique fields";

        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());

        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
    }

    @Test
    void givenUser_whenSave_throwsNotUniqueFiled_emailNotUnique() {
        User existingUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("TestUser2")
                .password("987654321")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findUserByUsername(user.getUsername(), -1L))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmail(user.getEmail(),  -1L))
                .thenReturn(Optional.of(existingUser));

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.save(user));

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use");
        String expectedExceptionMessage = "Not unique fields";

        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());

        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
    }

    @Test
    void givenUser_whenSave_throwsNotUniqueFiled_usernameAndEmailNotUnique() {
        User existingUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("TestUser")
                .password("987654321")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findUserByUsername(user.getUsername(), -1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findUserByEmail(user.getEmail(),  -1L))
                .thenReturn(Optional.of(existingUser));

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.save(user));

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use",
                        ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");
        String expectedExceptionMessage = "Not unique fields";

        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());

        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
    }

    @Test
    void giverUser_whenSave_throwsIdConflict_idNotNull() {
        user.setId(1L);
        IdConflictException e = assertThrows(IdConflictException.class, () -> userService.save(user));
        String expectedExceptionMessage = "User ID must be null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenID_whenFindById_thenReturnedUser() {
        User expected = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expected));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void givenID_whenFindById_thenThrowsDatabaseEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> userService.findById(1L));
        String expectedExceptionMessage = "User not found";

        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void givenUserWithNewEmail_whenUpdate_returnUser() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email("old_Test@mail.com")
                .username(user.getUsername())
                .password(user.getPassword())
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User expected = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.empty());

        when(userRepository.save(same(existingUser))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0, User.class);
            User updated = User.builder().id(u.getId()).build();

            updated.setEmail(u.getEmail());
            updated.setUsername(u.getUsername());
            updated.setPassword(u.getPassword());
            updated.setLastLoginDate(u.getLastLoginDate());
            updated.setBudgets(u.getBudgets());
            return updated;
        });

        User result = userService.update(user);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
        verify(userRepository, times(1)).save(same(existingUser));
    }

    @Test
    void givenUserWithNewUserName_whenUpdate_thenReturnUpdatedUser() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email(user.getEmail())
                .username("old_TestUser")
                .password(user.getPassword())
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User expected = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.empty());

        when(userRepository.save(same(existingUser))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0, User.class);
            existingUser.setEmail(u.getEmail());
            existingUser.setUsername(u.getUsername());
            existingUser.setPassword(u.getPassword());
            existingUser.setLastLoginDate(u.getLastLoginDate());
            existingUser.setBudgets(u.getBudgets());
            return existingUser;
        });

        User result = userService.update(user);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
        verify(userRepository, times(1)).save(same(existingUser));
    }

    @Test
    void givenUserWithNewUserNameAndNewEmail_whenUpdate_thenReturnUpdatedUser() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email("old_test@mail.com")
                .username("old_TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User expected = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.empty());

        when(userRepository.save(same(existingUser))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0, User.class);
            existingUser.setEmail(u.getEmail());
            existingUser.setUsername(u.getUsername());
            existingUser.setPassword(u.getPassword());
            existingUser.setLastLoginDate(u.getLastLoginDate());
            existingUser.setBudgets(u.getBudgets());
            return existingUser;
        });

        User result = userService.update(user);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
        verify(userRepository, times(1)).save(same(existingUser));
    }

    @Test
    void givenUserWithoutId_whenUpdate_throwsIdConflict_userIdIsNull() {
        IdConflictException e = assertThrows(IdConflictException.class, () -> userService.update(user));
        String expectedExceptionMessage = "User ID must be not null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserWithNewUserName_whenUpdate_throwsNotUniqueField_usernameNotUnique() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("old_TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User anotherUser =  User.builder()
                .id(2L)
                .email("another_test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.of(anotherUser));

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");
        String expectedExceptionMessage = "Not unique fields";

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.update(user));
        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithNewEmail_whenUpdate_throwsNotUniqueField_emailNotUnique() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email("old_test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User anotherUser =  User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("another_TestUser")
                .password("123456789")
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.of(anotherUser));
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.empty());

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use");
        String expectedExceptionMessage = "Not unique fields";

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.update(user));
        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithNewUserNameAndEmail_whenUpdate_throwsNotUniqueField_emailAndUsernameNotUnique() {
        user.setId(1L);

        User existingUser = User.builder()
                .id(1L)
                .email("old_test@mail.com")
                .username("old_TestUser")
                .password("123456789")
                .lastLoginDate(user.getLastLoginDate())
                .budgets(user.getBudgets())
                .build();

        User anotherUser =  User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .budgets(Collections.emptyList())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail("test@mail.com", 1L)).thenReturn(Optional.of(anotherUser));
        when(userRepository.findUserByUsername("TestUser", 1L)).thenReturn(Optional.of(anotherUser));

        Map<ErrorCode, String> expectedExceptionErrors =
                Map.of(ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use",
                        ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");
        String expectedExceptionMessage = "Not unique fields";

        NotUniqueFieldException e = assertThrows(NotUniqueFieldException.class, () -> userService.update(user));
        assertEquals(expectedExceptionErrors, e.getErrors());
        assertEquals(expectedExceptionMessage, e.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUser_whenUpdate_throwsDatabaseEntityNotFoundException() {
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        String expectedExceptionMessage = "User not found";

        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> userService.update(user));
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void givenID_whenDeleteById_thenReturnedNothing() {
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository,times(1)).findById(1L);
    }

    @Test
    void givenID_whenDeleteById_throwsEntityNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        String expectedExceptionMessage = "User not found";

        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> userService.deleteById(1L));
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void givenUserWithID_whenValidateUniqueFields_returnEmptyMap()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        user.setId(1L);

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", 1L))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L))
                .thenReturn(Optional.empty());

        Map<ErrorCode, String> expectedErrors = new HashMap<>();

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithID_whenValidateUniqueFields_returnUsernameBusyErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        user.setId(1L);

        User anotherUser = User.builder()
                .id(2L)
                .email("another_test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", 1L))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", 1L))
                .thenReturn(Optional.of(anotherUser));

        Map<ErrorCode, String> expectedErrors = Map.of(ErrorCode.USERNAME_BUSY,
                "Username TestUser is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithID_whenValidateUniqueFields_returnEmailBusyErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        user.setId(1L);

        User anotherUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("another_TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", 1L))
                .thenReturn(Optional.of(anotherUser));
        when(userRepository.findUserByUsername("TestUser", 1L))
                .thenReturn(Optional.empty());

        Map<ErrorCode, String> expectedErrors = Map.of(ErrorCode.EMAIL_BUSY,
                "Email test@mail.com is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithID_whenValidateUniqueFields_returnEmailBusyAndUsernameErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        user.setId(1L);

        User anotherUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", 1L))
                .thenReturn(Optional.of(anotherUser));
        when(userRepository.findUserByUsername("TestUser", 1L))
                .thenReturn(Optional.of(anotherUser));

        Map<ErrorCode, String> expectedErrors = Map.of(
                ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use",
                ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", 1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", 1L);
    }

    @Test
    void givenUserWithoutID_whenValidateUniqueFields_returnEmptyMap()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", -1L))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.empty());

        Map<ErrorCode, String> expectedErrors = new HashMap<>();

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
    }

    @Test
    void givenUserWithoutID_whenValidateUniqueFields_returnUsernameBusyErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User anotherUser = User.builder()
                .id(2L)
                .email("another_test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", -1L))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.of(anotherUser));

        Map<ErrorCode, String> expectedErrors = Map.of(ErrorCode.USERNAME_BUSY,
                "Username TestUser is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
    }

    @Test
    void givenUserWithoutID_whenValidateUniqueFields_returnEmailBusyErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User anotherUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("another_TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", -1L))
                .thenReturn(Optional.of(anotherUser));
        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.empty());

        Map<ErrorCode, String> expectedErrors = Map.of(ErrorCode.EMAIL_BUSY,
                "Email test@mail.com is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
    }

    @Test
    void givenUserWithoutID_whenValidateUniqueFields_returnEmailBusyAndUsernameBusyErrorCodeAndMessage()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User existingUser = User.builder()
                .id(2L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now().minusDays(1))
                .build();

        Method method = userService.getClass().getDeclaredMethod("validateUniqueFields", User.class);
        method.setAccessible(true);

        when(userRepository.findUserByEmail("test@mail.com", -1L))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByUsername("TestUser", -1L))
                .thenReturn(Optional.of(existingUser));

        Map<ErrorCode, String> expectedErrors = Map.of(
                ErrorCode.EMAIL_BUSY, "Email test@mail.com is already in use",
                ErrorCode.USERNAME_BUSY, "Username TestUser is already in use");

        assertEquals(expectedErrors, method.invoke(userService, user));
        verify(userRepository, times(1)).findUserByEmail("test@mail.com", -1L);
        verify(userRepository, times(1)).findUserByUsername("TestUser", -1L);
    }
}