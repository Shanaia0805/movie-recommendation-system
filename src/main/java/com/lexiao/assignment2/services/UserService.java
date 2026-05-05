package com.lexiao.assignment2.services;

import com.lexiao.assignment2.entities.User;
import com.lexiao.assignment2.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean registerUser(String email, String rawPassword) {
        // Check if the user already exists by email
        if (userMapper.findByEmail(email) != null) {
            return false;  // Return false if the user already exists
        }

        // Create a new user and set the email and encoded password
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        // Insert the new user into the database
        userMapper.insertUser(newUser);

        return true;  // Return true if the registration is successful
    }

    public User authenticate(String email, String rawPassword) {
        // Find the user by email
        User user = userMapper.findByEmail(email);

        // If the user doesn't exist or password doesn't match, return null
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        return user;  // Return the authenticated user
    }
}
