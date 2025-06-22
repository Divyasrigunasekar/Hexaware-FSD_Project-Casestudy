package com.hexaware.fastx.ticket.booking.service;


import com.hexaware.fastx.ticket.booking.dto.AuthResponseDTO;
import com.hexaware.fastx.ticket.booking.dto.LoginRequestDTO;
import com.hexaware.fastx.ticket.booking.dto.UserDTO;
import com.hexaware.fastx.ticket.booking.entity.User;

import jakarta.validation.Valid;

import java.util.List;

public interface IUserService {
    
    User updateUser(int userId, UserDTO userDTO);
    User getUserById(int userId);
    List<User> getAllUsers();
    void deleteUser(int userId);
    User findByEmail(String email);
	String registerUser(@Valid UserDTO userDTO);
	AuthResponseDTO loginUser(LoginRequestDTO loginDTO);
}