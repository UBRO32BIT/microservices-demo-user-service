package org.example.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.request.CheckAuthRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.DeleteUserResponse;
import org.example.userservice.dto.response.RegistrationResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.exceptions.EmailAlreadyExistsException;
import org.example.userservice.exceptions.UserNotFoundException;
import org.example.userservice.dto.request.RegistrationRequest;
import org.example.userservice.dto.request.LoginRequest;
import org.example.userservice.exceptions.UsernameAlreadyExistsException;
import org.example.userservice.dto.response.LoginResponse;
import org.example.userservice.model.User;
import org.example.userservice.model.UserRole;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.UserService;
import org.example.userservice.utils.JwtUtils;
import org.example.userservice.utils.SmtpUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"User"})
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final SmtpUtils smtpUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Create a new user
     * @param request RegistrationRequest request
     * @return Access token of user
     */
    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        boolean isUsernameExists = userRepository.existsByUsername(request.getUsername());
        //If there are already a user then throw error
        if (isUsernameExists) {
            throw new UsernameAlreadyExistsException("Username already exist");
        }

        //If the email is already registered then throw error
        boolean isEmailExists = userRepository.existsByEmail(request.getEmail());
        if (isEmailExists) {
            throw new EmailAlreadyExistsException("Email already exist");
        }

        //Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        //Parse user information
        User user = User.builder()
                .username(request.getUsername())
                .password(encryptedPassword)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(UserRole.USER)
                .profilePicture(request.getProfilePicture())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        //Save to database
        User newUser = userRepository.save(user);
        //Generate token
        String token = jwtUtils.generateToken(newUser);

        //Map user model to user response dto
        UserResponse userResponse = this.toUserResponse(newUser);

        //Map to RegistrationResponse
        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .data(userResponse)
                .token(token)
                .build();

        //Send email to user
        smtpUtils.sendWelcomeEmail(newUser.getEmail());

        return registrationResponse;
    }

    /**
     * Get user information based on credentials
     * @param request LoginRequest DTO
     * @return Access token of user
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        //Call Security provider to authenticate user based on credentials
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        User authenticatedUser = this.getUserModelByUsername(username);
        //Generate access token
        String token = jwtUtils.generateToken(authenticatedUser);

        return new LoginResponse(token);
    }

    /**
     * Get user information based on access token
     * @param request CheckAuthRequest DTO
     * @return User information
     */
    @Override
    public UserResponse checkAuth(CheckAuthRequest request) {
        //Call jwt helper to extract username from token
        String username = jwtUtils.extractUsername(request.getToken());
        return this.getUserByUsername(username);
    }

    /**
     * Get all users
     * @return List of User
     */
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user based on id
     * @param userId Identifier of the user
     * @return UserResponse DTO
     */
    @Override
    @Cacheable(key = "#p0")
    public UserResponse getUserById(Long userId) {
        User user = this.getUserModelById(userId);
        return this.toUserResponse(user);
    }

    /**
     * Get user based on username
     * @param username Username of the user
     * @return UserResponse DTO
     */
    @Override
    public UserResponse getUserByUsername(String username) {
        User user = this.getUserModelByUsername(username);
        return this.toUserResponse(user);
        //return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
    }

    @Override
    @CachePut(key = "#p0")
    public UserResponse updateUserById(Long userId, UpdateUserRequest request) {
        //Get user object from database
        User user = this.getUserModelById(userId);
        //Update fields
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setProfilePicture(request.getProfilePicture());
        //Save changes to database
        User newUser = userRepository.save(user);

        return this.toUserResponse(newUser);
    }

    /**
     * Delete user based on ID
     * @param userId user ID
     * @return Response message
     */
    @Override
    @CacheEvict(key = "#p0")
    public DeleteUserResponse deleteUser(Long userId) {
        User user = this.getUserModelById(userId);
        userRepository.delete(user);
        return new DeleteUserResponse("User deleted");
    }

    /**
     * Return user model from the database based on id
     * @param userId Identifier of the user
     * @return User model object
     */
    private User getUserModelById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Return user model from the database based on username
     * @param username Username of the user
     * @return User model object
     */
    public User getUserModelByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Convert User model to DTO for better handling
     * @param user The user model
     * @return UserResponse DTO
     */
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
