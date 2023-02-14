package com.example.citations.controller;

import com.example.citations.model.Citation;
import com.example.citations.request.CitationCreateRequest;
import com.example.citations.request.CitationUpdateRequest;
import com.example.citations.service.CitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController("/api/citation")
public class CitationController {
    @Autowired
    private CitationService citationService;

    @Operation(summary = "Method for getting all quotes", description = "Get all citations")
    @ApiResponse(responseCode = "200", description = "List of citations")
    @GetMapping
    public ResponseEntity<List<Citation>> getAllCitations() {
        return ResponseEntity.ok(citationService.getAllCitations());
    }

    @Operation(summary = "Method for getting a quote by ID", description = "Get citation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Citation found"),
            @ApiResponse(responseCode = "404", description = "Citation not found")
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCitation(@PathVariable int id) {
        Optional<Citation> requestResult = citationService.getCitation(id);
        if(requestResult.isPresent()) {
            return ResponseEntity.ok(requestResult.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Метод создания новой цитаты", description = "Create citation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цитатаа создан"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    }
    )
    @PostMapping
    public ResponseEntity<Void> createCitation(@Valid @RequestBody CitationCreateRequest request) {
        if (citationService.createCitation(request)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Citation editing method", description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Citation edited"),
            @ApiResponse(responseCode = "400", description = "Invaid request"),
            @ApiResponse(responseCode = "404", description = "Citation not found")
    }
    )
    @PutMapping
    public ResponseEntity<Void> updateCitation(@Valid @RequestBody CitationUpdateRequest request) {
        if (citationService.getCitation(request.getId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (citationService.updateCitation(request)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Quote removal method by ID", description = "Delete citation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Citation delete"),
            @ApiResponse(responseCode = "404", description = "Citation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitation(@PathVariable int id) {
        if (citationService.deleteCitation(id)) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
