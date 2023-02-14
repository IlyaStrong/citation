package com.example.citations;

import com.example.citations.controller.CitationController;
import com.example.citations.controller.UserController;
import com.example.citations.model.Citation;
import com.example.citations.model.User;
import com.example.citations.request.CitationCreateRequest;
import com.example.citations.request.CitationUpdateRequest;
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
class CitationControllerTests {
    private static final int UNKNOWN_ID = 100000;
    @Autowired
    private CitationController citationController;

    @Autowired
    private CitationService citationService;
    @Autowired
    private UserService userService;
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
    void getAllCitations() {
        userService.createUser(new UserCreateRequest("john", "pass"));
        userService.createUser(new UserCreateRequest("jane", "pass"));
        List<User> users = userService.getAllUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);


        ResponseEntity<List<Citation>> responseEntity1 = citationController.getAllCitations();
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        List<Citation> citations1 = responseEntity1.getBody();
        Assertions.assertNotNull(citations1);
        Assertions.assertEquals(0, citations1.size());

        citationService.createCitation(new CitationCreateRequest("New citation", user1.getId()));
        ResponseEntity<List<Citation>> responseEntity2 = citationController.getAllCitations();
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        List<Citation> citations2 = responseEntity2.getBody();
        Assertions.assertNotNull(citations2);
        Assertions.assertEquals(1, citations2.size());

        citationService.createCitation(new CitationCreateRequest("New citation !!", user2.getId()));
        ResponseEntity<List<Citation>> responseEntity3 = citationController.getAllCitations();
        Assertions.assertEquals(HttpStatus.OK, responseEntity3.getStatusCode());
        List<Citation> citations3 = responseEntity3.getBody();
        Assertions.assertNotNull(citations3);
        Assertions.assertEquals(2, citations3.size());
    }

    @Test
    @Order(2)
    void getCitationByIdTests() {
        List<Citation> citations = citationService.getAllCitations();

        ResponseEntity<?> responseEntity1 = citationController.getCitation(citations.get(0).getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Citation citation1 = (Citation) responseEntity1.getBody();
        Assertions.assertNotNull(citation1);
        Assertions.assertEquals(citations.get(0).getContent(), citation1.getContent());
        Assertions.assertEquals(citations.get(0).getAuthor().getId(), citation1.getAuthor().getId());

        ResponseEntity<?> responseEntity2 = citationController.getCitation(citations.get(1).getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Citation citation2 = (Citation) responseEntity2.getBody();
        Assertions.assertNotNull(citation2);
        Assertions.assertEquals(citations.get(1).getContent(), citation2.getContent());
        Assertions.assertEquals(citations.get(1).getAuthor().getId(), citation2.getAuthor().getId());

        ResponseEntity<?> responseEntity3 = citationController.getCitation(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity3.getStatusCode());
    }

    @Test
    @Order(3)
    void updateCitationByIdTests() {
        List<User> users = userService.getAllUsers();
        User user1 = users.get(0);

        List<Citation> citations = citationService.getAllCitations();

        ResponseEntity<?> responseEntity1 = citationController.updateCitation(new CitationUpdateRequest(citations.get(0).getId(), "EMPTY", user1.getId()));
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Optional<Citation> opt1 = citationService.getCitation(citations.get(0).getId());
        Assertions.assertTrue(opt1.isPresent());
        Citation citation1 = opt1.get();
        Assertions.assertEquals("EMPTY", citation1.getContent());
        Assertions.assertEquals(user1.getId(), citation1.getAuthor().getId());

        ResponseEntity<?> responseEntity2 = citationController.updateCitation(new CitationUpdateRequest(citations.get(1).getId(), "New citation !!", user1.getId()));
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Optional<Citation> opt2 = citationService.getCitation(citations.get(1).getId());
        Assertions.assertTrue(opt2.isPresent());
        Citation citation2 = opt2.get();
        Assertions.assertEquals("New citation !!", citation2.getContent());
        Assertions.assertEquals(user1.getId(), citation2.getAuthor().getId());

        ResponseEntity<?> responseEntity3 = citationController.getCitation(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity3.getStatusCode());
    }

    @Test
    @Order(4)
    void createCitationTests() {
        List<User> users = userService.getAllUsers();
        User user2 = users.get(1);
        List<Citation> citations = citationService.getAllCitations();

        ResponseEntity<?> responseEntity1 = citationController.createCitation(new CitationCreateRequest("One more citation", user2.getId()));
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        List<Citation> updatedCitations = citationService.getAllCitations();
        updatedCitations.removeIf(c -> citations.stream().anyMatch(c2 -> c2.getId() == c.getId()));
        Assertions.assertEquals(1, updatedCitations.size());
        Optional<Citation> opt1 = citationService.getCitation(updatedCitations.iterator().next().getId());
        Assertions.assertTrue(opt1.isPresent());
        Citation citation1 = opt1.get();
        Assertions.assertEquals("One more citation", citation1.getContent());
        Assertions.assertEquals(user2.getId(), citation1.getAuthor().getId());

        ResponseEntity<?> responseEntity2 = citationController.createCitation(new CitationCreateRequest("One more citation", UNKNOWN_ID));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity2.getStatusCode());
    }

    @Test
    @Order(5)
    void deleteUserByIdTests() {
        ResponseEntity<?> responseEntity1 = citationController.deleteCitation(UNKNOWN_ID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity1.getStatusCode());

        List<Citation> citations = citationService.getAllCitations();
        ResponseEntity<?> responseEntity2 = citationController.deleteCitation(citations.iterator().next().getId());
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Assertions.assertEquals(2, citationService.getAllCitations().size());
    }

}
