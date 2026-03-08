package com.micro.serviceb.controller;

import com.micro.serviceb.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Map<Long, User> USERS = Map.of(
        1L, new User(1L, "Alice Martin",   "alice@example.com",   "ADMIN"),
        2L, new User(2L, "Bob Dupont",     "bob@example.com",     "USER"),
        3L, new User(3L, "Charlie Lebrun", "charlie@example.com", "MANAGER")
    );

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = USERS.get(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
}
