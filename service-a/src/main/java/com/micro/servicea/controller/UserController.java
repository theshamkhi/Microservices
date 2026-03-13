package com.micro.servicea.controller;

import com.micro.servicea.client.UserFeignClient;
import com.micro.servicea.model.User;
import com.micro.servicea.service.RestTemplateService;
import com.micro.servicea.service.UserFeignService;
import com.micro.servicea.service.WebClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    private final RestTemplateService restTemplateService;
    private final WebClientService   webClientService;
    private final UserFeignService   userFeignService;

    public UserController(RestTemplateService restTemplateService,
                          WebClientService webClientService,
                          UserFeignService userFeignService) {
        this.restTemplateService = restTemplateService;
        this.webClientService    = webClientService;
        this.userFeignService = userFeignService;
    }

    // --- 1. RestTemplate ---
    @GetMapping("/rest-template/{id}")
    public ResponseEntity<User> getViaRestTemplate(@PathVariable Long id) {
        User user = restTemplateService.getUserById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // --- 2. WebClient (reactive) ---
    @GetMapping("/web-client/{id}")
    public Mono<ResponseEntity<User>> getViaWebClient(@PathVariable Long id) {
        return webClientService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // --- 3. Feign ---
    @GetMapping("/feign/{id}")
    public ResponseEntity<User> getViaFeign(@PathVariable Long id) {
        User user = userFeignService.getUserById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
}
