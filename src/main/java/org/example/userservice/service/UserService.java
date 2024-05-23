package org.example.userservice.service;

import org.example.userservice.dto.request.CheckAuthRequest;
import org.example.userservice.dto.response.DeleteUserResponse;
import org.example.userservice.dto.request.RegistrationRequest;
import org.example.userservice.dto.request.LoginRequest;
import org.example.userservice.dto.response.LoginResponse;
import org.example.userservice.dto.response.RegistrationResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.model.User;

import java.util.List;

public interface UserService {
     RegistrationResponse register(RegistrationRequest request);
     LoginResponse login(LoginRequest request);
     UserResponse checkAuth(CheckAuthRequest request);
     UserResponse getUserByUsername(String username);
     User getUserModelByUsername(String username);
     List<UserResponse> getAllUsers();
     UserResponse getUserById(Long id);
     DeleteUserResponse deleteUser(Long userId);
}
