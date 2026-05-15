package com.example.secondhand.controller;

import com.example.secondhand.dto.ApiResponse;
import com.example.secondhand.dto.LoginRequest;
import com.example.secondhand.dto.LoginResponse;
import com.example.secondhand.dto.RegisterRequest;
import com.example.secondhand.dto.RegisterResponse;
import com.example.secondhand.entity.User;
import com.example.secondhand.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (!isValidUsername(username)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名必须是3-20位字母或数字"));
        }

        if (!isValidPassword(password)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "密码必须是6-20位"));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名已存在"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(request.getNickname());

        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse(savedUser.getId(), savedUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpSession session) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名不能为空"));
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "密码不能为空"));
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "账号或密码错误"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "账号或密码错误"));
        }

        session.setAttribute("userId", user.getId());

        LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getNickname());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]{3,20}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
    }
}