package com.mytripmychoice.backend.service;

import com.mytripmychoice.backend.model.User;
import com.mytripmychoice.backend.repository.UserRepository;
import com.mytripmychoice.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> signup(Map<String, String> request) {
        String email = request.get("email");
        String name = request.get("name");
        String password = request.get("password");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists!"));
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCredits(2);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Account created successfully!"));
    }

    public ResponseEntity<?> login(Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found!"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid password!"));
        }

        String token = jwtUtil.generateToken(email, user.getId());

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("credits", user.getCredits());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getProfile(String token) {
        String cleanToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(cleanToken);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found!"));
        }

        User user = userOpt.get();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("credits", user.getCredits());

        return ResponseEntity.ok(userData);
    }
}