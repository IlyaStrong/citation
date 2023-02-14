package com.example.citations;

import com.example.citations.controller.UserController;
import com.example.citations.controller.VoteController;
import com.example.citations.model.Citation;
import com.example.citations.model.User;
import com.example.citations.model.Vote;
import com.example.citations.request.CitationCreateRequest;
import com.example.citations.request.UserCreateRequest;
import com.example.citations.request.UserUpdateRequest;
import com.example.citations.request.VoteRequest;
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
class VoteControllerTests {
    private static final int UNKNOWN_ID = 100000;

    @Autowired
    private VoteController voteController;

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
    void voteTest() {
        userService.createUser(new UserCreateRequest("john", "pass"));
        userService.createUser(new UserCreateRequest("jane", "pass"));
        List<User> users = userService.getAllUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);

        citationService.createCitation(new CitationCreateRequest("New citation!", user1.getId()));
        citationService.createCitation(new CitationCreateRequest("One more citation!", user2.getId()));
        List<Citation> citations = citationService.getAllCitations();
        Citation citation1 = citations.get(0);
        Citation citation2 = citations.get(1);

        ResponseEntity<?> responseEntity1 = voteController.vote(new VoteRequest(citation1.getId(), user1.getId(), 100));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity1.getStatusCode());

        ResponseEntity<?> responseEntity2 = voteController.vote(new VoteRequest(citation1.getId(), UNKNOWN_ID, 1));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());

        ResponseEntity<?> responseEntity3 = voteController.vote(new VoteRequest(UNKNOWN_ID, user1.getId(), 1));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity3.getStatusCode());

        ResponseEntity<?> responseEntity4 = voteController.vote(new VoteRequest(citation1.getId(), user1.getId(), 1));
        Assertions.assertEquals(HttpStatus.OK, responseEntity4.getStatusCode());
        Citation citation = citationService.getCitation(citation1.getId()).orElse(null);
        Assertions.assertNotNull(citation);
        Assertions.assertEquals(1, citation.getVotes().size());
        Vote vote = citation.getVotes().iterator().next();
        Assertions.assertEquals(1, vote.getScore());
        Assertions.assertEquals(citation1.getId(), vote.getCitation().getId());
        Assertions.assertEquals(user1.getId(), vote.getUser().getId());

        ResponseEntity<?> responseEntity5 = voteController.vote(new VoteRequest(citation1.getId(), user1.getId(),  1));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity5.getStatusCode());
    }

    @Test
    @Order(2)
    void topTest() {
        List<User> users = userService.getAllUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);

        List<Citation> citations = citationService.getAllCitations();
        Citation citation1 = citations.get(0);
        Citation citation2 = citations.get(1);

        voteService.vote(new VoteRequest(citation2.getId(), user1.getId(), 1));
        voteService.vote(new VoteRequest(citation1.getId(), user2.getId(),  -1));
        voteService.vote(new VoteRequest(citation2.getId(), user2.getId(), 1));

        ResponseEntity<List<Citation>> responseEntity = voteController.getTop();
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Citation> topCitations = responseEntity.getBody();
        Assertions.assertNotNull(topCitations);
        Assertions.assertEquals(2, topCitations.size());
        int score1 = topCitations.get(0).getVotes().stream().mapToInt(Vote::getScore).sum();
        int score2 = topCitations.get(1).getVotes().stream().mapToInt(Vote::getScore).sum();
        Assertions.assertTrue(score1 > score2);
    }
}