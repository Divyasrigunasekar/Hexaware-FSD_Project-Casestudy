package com.hexaware.fastx.ticket.booking.controller;
import com.hexaware.fastx.ticket.booking.config.UserInfoUserDetails;
import com.hexaware.fastx.ticket.booking.dto.AuthResponseDTO;
import com.hexaware.fastx.ticket.booking.dto.LoginRequestDTO;
import com.hexaware.fastx.ticket.booking.dto.UserDTO;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.repository.UserRepository;
import com.hexaware.fastx.ticket.booking.service.IUserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private IUserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserDTO userDTO) {
        String message = userService.registerUser(userDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        return ResponseEntity.ok(userService.loginUser(loginDTO));
    }

    // Admin can get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Admin or user himself can get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId, Authentication authentication) {
        UserInfoUserDetails principal = (UserInfoUserDetails) authentication.getPrincipal();

        // allow if admin or same user
        if (!principal.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")) && principal.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't access other user accounts.");
        }

        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    

    // User can update own profile, Admin can update any user
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<User> updateUser(@PathVariable int userId, @RequestBody UserDTO userDTO) {
        log.info("Updating user with id: {}", userId);
        User updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable int userId, Authentication authentication) {
        String loggedInUserEmail = authentication.getName();
        User loggedInUser = userService.findByEmail(loggedInUserEmail);

        Map<String, String> response = new HashMap<>();

        // Allow ADMIN to delete any user
        if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            userService.deleteUser(userId);
            response.put("message", "User deleted successfully by admin");
            return ResponseEntity.ok(response);
        }

        // Allow USER to delete only their own account
        if ("USER".equalsIgnoreCase(loggedInUser.getRole())) {
            if (loggedInUser.getUserId() != userId) {
                response.put("message", "You can delete only your own account");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            userService.deleteUser(userId);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }

        // Deny OPERATOR or any other role
        response.put("message", "You are not authorized to delete users");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        UserInfoUserDetails principal = (UserInfoUserDetails) authentication.getPrincipal();
        User user = userService.getUserById(principal.getUserId());

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId()); // manually expose
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        
        dto.setGender(user.getGender());
        dto.setContactNumber(user.getContactNumber());
        dto.setRole(user.getRole());
        // no password in response

        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        Map<String, String> response = new HashMap<>();
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            response.put("message", "Password reset successful.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



}
