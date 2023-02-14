package com.example.citations.controller;

import com.example.citations.model.User;
import com.example.citations.request.UserCreateRequest;
import com.example.citations.request.UserUpdateRequest;
import com.example.citations.service.UserService;
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
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Method to get all users", description = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Method for getting user by ID", description = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        Optional<User> requestResult = userService.getUser(id);
        if(requestResult.isPresent()) {
            return ResponseEntity.ok(requestResult.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Method for creating a new user", description = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    }
    )
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateRequest request) {
        if (userService.createUser(request)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "User edit method", description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User edited"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    }
    )
    @PutMapping
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        if (userService.getUser(request.getId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (userService.updateUser(request)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Method for deleting a user by ID", description = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
