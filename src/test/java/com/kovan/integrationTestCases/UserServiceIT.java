package com.kovan.integrationTestCases;

import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void cleanup() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .passwordHash("hashedpassword")
                .dateOfBirth("1990-01-01")
                .role("CUSTOMER")
                .build();

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser.getUserId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
    }

    @Test
    void testGetUserById() {
        User user = userRepository.save(
                User.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .email("jane.doe@example.com")
                        .passwordHash("hashedpassword")
                        .dateOfBirth("1992-05-05")
                        .role("ADMIN")
                        .build()
        );

        User retrievedUser = userService.getUserById(user.getUserId());

        assertEquals(user.getUserId(), retrievedUser.getUserId());
        assertEquals("Jane", retrievedUser.getFirstName());
    }

    @Test
    void testGetAllUsers() {
        User user1 = userRepository.save(
                User.builder()
                        .firstName("Alice")
                        .lastName("Smith")
                        .email("alice.smith@example.com")
                        .passwordHash("hashedpassword1")
                        .dateOfBirth("1985-10-10")
                        .role("CUSTOMER")
                        .build()
        );

        User user2 = userRepository.save(
                User.builder()
                        .firstName("Bob")
                        .lastName("Brown")
                        .email("bob.brown@example.com")
                        .passwordHash("hashedpassword2")
                        .dateOfBirth("1988-08-08")
                        .role("SELLER")
                        .build()
        );

        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void testDeleteUser() {
        User user = userRepository.save(
                User.builder()
                        .firstName("Charlie")
                        .lastName("Johnson")
                        .email("charlie.johnson@example.com")
                        .passwordHash("hashedpassword")
                        .dateOfBirth("1995-12-12")
                        .role("CUSTOMER")
                        .build()
        );

        userService.deleteUser(user.getUserId());

        assertFalse(userRepository.existsById(user.getUserId()));
    }
}
