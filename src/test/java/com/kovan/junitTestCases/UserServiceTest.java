package com.kovan.junitTestCases;

import com.kovan.entities.User;
import com.kovan.repository.UserRepository;
import com.kovan.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final User user = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .passwordHash("hashed password")
                .dateOfBirth("1990-01-01")
                .role("USER")
                .build();

    @Test
    void testCreateUser() {

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserById_Success() {

        when(userRepository.findById(user.getUserId())).thenReturn(of(user));

        User result = userService.getUserById(user.getUserId());

        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        verify(userRepository, times(1)).findById(user.getUserId());
    }

    @Test
    void testGetUserById_UserNotFound() {

        when(userRepository.findById(2L)).thenReturn(empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(2L);
        });

        assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void testGetAllUsers() {

        User user2 = User.builder()
                .userId(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("janesmith@example.com")
                .passwordHash("hashed password2")
                .dateOfBirth("1995-05-05")
                .role("USER")
                .build();

        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user.getUserId(), result.getFirst().getUserId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testDeleteUser_Success() {

        Long userId = user.getUserId();
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}

