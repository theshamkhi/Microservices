package com.micro.servicea.service;

import com.micro.servicea.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateService {

    private final RestTemplate restTemplate;
    private static final String SERVICE_B_URL = "http://localhost:8082/users/{id}";

    public RestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User getUserById(Long id) {
        return restTemplate.getForObject(SERVICE_B_URL, User.class, id);
    }
}
