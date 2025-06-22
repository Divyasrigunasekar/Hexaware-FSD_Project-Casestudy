package com.hexaware.fastx.ticket.booking.service;
import com.hexaware.fastx.ticket.booking.dto.LoginRequestDTO;
import com.hexaware.fastx.ticket.booking.dto.UserDTO;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Transactional  // Ensures test data rolls back after each test
class UserServiceImpTest {

    @Autowired
    private IUserService userService;

    @Test
    void testRegisterUser() {
        UserDTO dto = getSampleUser("test1@example.com");
        String response = userService.registerUser(dto);
        assertEquals("User registered successfully", response);
    }

    @Test
    void testGetUserById() {
        UserDTO dto = getSampleUser("test2@example.com");
        userService.registerUser(dto);
        User user = userService.findByEmail(dto.getEmail());

        User found = userService.getUserById(user.getUserId());
        assertNotNull(found);
        assertEquals(dto.getName(), found.getName());
    }

    @Test
    void testUpdateUser() {
        UserDTO dto = getSampleUser("test3@example.com");
        userService.registerUser(dto);
        User existing = userService.findByEmail(dto.getEmail());

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setName("Updated Name");
        updatedDTO.setEmail("updated@example.com");
        updatedDTO.setPassword("updated123");
        updatedDTO.setGender("Other");
        updatedDTO.setContactNumber("9999999999");
        updatedDTO.setRole("ADMIN");

        User updatedUser = userService.updateUser(existing.getUserId(), updatedDTO);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("ADMIN", updatedUser.getRole());
    }

    @Test
    void testDeleteUser() {
        UserDTO dto = getSampleUser("test4@example.com");
        userService.registerUser(dto);
        User user = userService.findByEmail(dto.getEmail());

        userService.deleteUser(user.getUserId());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getUserId()));
    }

    @Test
    void testGetAllUsers() {
        UserDTO dto1 = getSampleUser("test5a@example.com");
        UserDTO dto2 = getSampleUser("test5b@example.com");

        userService.registerUser(dto1);
        userService.registerUser(dto2);

        List<User> users = userService.getAllUsers();
        assertTrue(users.size() >= 2); // Depending on preloaded data
    }

    @Test
    void testLoginUser() {
        UserDTO dto = getSampleUser("test6@example.com");
        userService.registerUser(dto);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail(dto.getEmail());
        loginDTO.setPassword(dto.getPassword());

        assertDoesNotThrow(() -> userService.loginUser(loginDTO));
    }

    // Utility method to avoid repetition
    private UserDTO getSampleUser(String email) {
        UserDTO dto = new UserDTO();
        dto.setName("Test User");
        dto.setEmail(email);
        dto.setPassword("password123");
        dto.setGender("Other");
        dto.setContactNumber("1234567890");
        dto.setRole("USER");
        return dto;
    }
}