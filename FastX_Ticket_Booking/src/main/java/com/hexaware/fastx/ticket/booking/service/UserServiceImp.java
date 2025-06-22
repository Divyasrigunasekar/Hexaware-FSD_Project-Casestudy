/**
 * Provides implementation for user registration, login, and retrieval logic.
 * Handles password encryption and token generation during authentication.
 */


package com.hexaware.fastx.ticket.booking.service;

import com.hexaware.fastx.ticket.booking.dto.AuthResponseDTO;
import com.hexaware.fastx.ticket.booking.dto.LoginRequestDTO;
import com.hexaware.fastx.ticket.booking.dto.UserDTO;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.exceptions.UserNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImp implements IUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    public String registerUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setGender(userDto.getGender());
        user.setContactNumber(userDto.getContactNumber());
        user.setRole(userDto.getRole());

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponseDTO loginUser(LoginRequestDTO loginDto) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Fetch User entity to get userId
            User user = findByEmail(loginDto.getEmail());

            // Pass both email and userId to generateToken
            String token = jwtService.generateToken(user.getEmail(), user.getUserId(),user.getRole());

            return new AuthResponseDTO(token);
        } else {
            throw new RuntimeException("Invalid login");
        }
    }

    @Override
    public User updateUser(int userId, UserDTO userDTO) {
        log.info("Updating user with id: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());

            // Check if password is provided and not empty
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                // Hash the new password before saving
                user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
            }
            // else keep existing passwordHash unchanged

            user.setGender(userDTO.getGender());
            user.setContactNumber(userDTO.getContactNumber());
            user.setRole(userDTO.getRole());
            return userRepository.save(user);
        } else {
            log.warn("User with id {} not found for update", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }


    @Override
    public User getUserById(int userId) {
        log.debug("Fetching user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Deleting user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id {} not found for delete", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

	
}