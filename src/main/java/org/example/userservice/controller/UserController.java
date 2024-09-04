package org.example.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.request.CheckAuthRequest;
import org.example.userservice.dto.request.RegistrationRequest;
import org.example.userservice.dto.request.LoginRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.DeleteUserResponse;
import org.example.userservice.dto.response.RegistrationResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.dto.response.LoginResponse;
import org.example.userservice.service.UserService;
import org.example.userservice.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.USER_SERVICE_ENDPOINT)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(Constants.REGISTER_ENDPOINT)
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(Constants.LOGIN_ENDPOINT)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping(Constants.CHECK_AUTH_ENDPOINT)
    public ResponseEntity<UserResponse> checkAuth(@Valid @RequestBody CheckAuthRequest checkAuthRequest) {
        UserResponse user = userService.checkAuth(checkAuthRequest);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = userService.updateUserById(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable Long userId) {
        DeleteUserResponse response = userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
