package com.example.citations.service;

import com.example.citations.model.User;
import com.example.citations.repository.UserRepository;
import com.example.citations.request.UserCreateRequest;
import com.example.citations.request.UserUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }

    public boolean createUser(UserCreateRequest request) {
        try{
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(Instant.now());
            userRepository.save(user);
            return true;
        }
        catch (Exception e) {
            logger.error("Can not create user: ", e);
            return false;
        }
    }

    public boolean updateUser(UserUpdateRequest request) {
        try {
            User user = new User();
            user.setId(request.getId());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.updateUser(user.getPassword(), user.getId());
            return true;
        } catch (Exception e) {
            logger.error("Can not update user: ", e);
            return false;
        }
    }

    public boolean deleteUser(int id) {
        Optional<User> requestResult = userRepository.findById(id);
        if(requestResult.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }

    public void clear() {
        userRepository.deleteAll();
    }
}
