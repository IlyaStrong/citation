package com.example.citations;

import com.example.citations.controller.UserController;
import com.example.citations.model.User;
import com.example.citations.request.UserCreateRequest;
import com.example.citations.request.UserUpdateRequest;
import com.example.citations.service.CitationService;
import com.example.citations.service.UserService;
import com.example.citations.service.VoteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTests {
    private static final int UNKNOWN_ID = 100000;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private CitationService citationService;

    @Autowired
    private VoteService voteService;

    @Test
    @Order(0)
    public void init() {
        voteService.clear();
        citationService.clear();
        userService.clear();
    }

    @Test
    @Order(1)
    void getAllUsersTests() {
        ResponseEntity<List<User>> responseEntity1 = userController.getAllUsers();
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        List<User> users1 = responseEntity1.getBody();
        Assertions.assertNotNull(users1);
        Assertions.assertEquals(0, users1.size());

        userService.createUser(new UserCreateRequest("test", "pass"));
        ResponseEntity<List<User>> responseEntity2 = userController.getAllUsers();
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        List<User> users2 = responseEntity2.getBody();
        Assertions.assertNotNull(users2);
        Assertions.assertEquals(1, users2.size());

        userService.createUser(new UserCreateRequest("test2", "pass"));
        ResponseEntity<List<User>> responseEntity3 = userController.getAllUsers();
        Assertions.assertEquals(HttpStatus.OK, responseEntity3.getStatusCode());
        List<User> users3 = responseEntity3.getBody();
        Assertions.assertNotNull(users3);
        Assertions.assertEquals(2, users3.size());
    }

    @Test
    @Order(2)
    void getUserByIdTests() {
        List<User> users = userService.getAllUsers();
        ResponseEntity<?> responseEntity1 = userController.getUser(users.get(0).getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        User user1 = (User)responseEntity1.getBody();
        Assertions.assertNotNull(user1);
        Assertions.assertEquals(users.get(0).getUsername(), user1.getUsername());

        ResponseEntity<?> responseEntity2 = userController.getUser(users.get(1).getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        User user2 = (User)responseEntity2.getBody();
        Assertions.assertNotNull(user2);
        Assertions.assertEquals(users.get(1).getUsername(), user2.getUsername());

        ResponseEntity<?> responseEntity3 = userController.getUser(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity3.getStatusCode());
    }

    @Test
    @Order(3)
    void updateUserByIdTests() {
        List<User> users = userService.getAllUsers();
        ResponseEntity<?> responseEntity1 = userController.updateUser(new UserUpdateRequest(users.get(0).getId(), "passes"));
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Optional<User> opt1 = userService.getUser(users.get(0).getId());
        Assertions.assertTrue(opt1.isPresent());

        ResponseEntity<?> responseEntity2 = userController.getUser(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());
    }

    @Test
    @Order(4)
    void createUserTests() {
        List<User> users = userService.getAllUsers();
        ResponseEntity<?> responseEntity1 = userController.createUser(new UserCreateRequest("test3", "pass"));
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        List<User> updatedUsers = userService.getAllUsers();
        updatedUsers.removeIf(u -> users.stream().anyMatch(u2 -> u2.getId() == u.getId()));
        Assertions.assertEquals(1, updatedUsers.size());
        Optional<User> opt1 = userService.getUser(updatedUsers.iterator().next().getId());
        Assertions.assertTrue(opt1.isPresent());
        User user1 = opt1.get();
        Assertions.assertEquals("test3", user1.getUsername());
        Assertions.assertTrue(passwordEncoder.matches("pass", user1.getPassword()));

        ResponseEntity<?> responseEntity2 = userController.createUser(new UserCreateRequest("test3", "pass"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity2.getStatusCode());
    }

    @Test
    @Order(5)
    void deleteUserByIdTests() {
        ResponseEntity<?> responseEntity1 = userController.deleteUser(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity1.getStatusCode());

        List<User> users = userService.getAllUsers();
        ResponseEntity<?> responseEntity2 = userController.deleteUser(users.iterator().next().getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Assertions.assertEquals(2, userService.getAllUsers().size());
    }

}
