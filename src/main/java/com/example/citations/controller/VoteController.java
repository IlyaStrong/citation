package com.example.citations.controller;

import com.example.citations.model.Citation;
import com.example.citations.model.CitationWithScore;
import com.example.citations.model.User;
import com.example.citations.request.VoteRequest;
import com.example.citations.service.CitationService;
import com.example.citations.service.UserService;
import com.example.citations.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
    @Autowired
    private UserService userService;

    @Autowired
    private CitationService citationService;

    @Autowired
    private VoteService voteService;

    @Operation(summary = "Method of quotation (voting) of a quotation by the user", description = "Vote for citation by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voice counted"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Citation or user not found")
    }
    )
    @PostMapping
    public ResponseEntity<Void> vote(@Valid @RequestBody VoteRequest request) {
        Optional<User> opt1 = userService.getUser(request.getUserId());
        if (opt1.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Citation> opt2 = citationService.getCitation(request.getCitationId());
        if (opt2.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (voteService.vote(request)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Method for obtaining top 10 citation", description = "Get top-10 citations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top-10 citations"),
    }
    )
    @GetMapping("/top")
    public ResponseEntity<List<Citation>> getTop() {
        return ResponseEntity.ok().body(voteService.getTopCitations(10));

    }

    @Operation(summary = "Method for obtaining the graph of votes", description = "Get top-10 citations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "graph"),
            @ApiResponse(responseCode = "404", description = "Citation not found")
    }
    )
    @GetMapping("/id")
    public ResponseEntity<CitationWithScore> getGraphic(@PathVariable int id) {
        Optional<Citation> requestResult = citationService.getCitation(id);
        if (requestResult.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(voteService.getGraph(id));

    }
}
