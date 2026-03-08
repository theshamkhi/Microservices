package com.micro.servicea.client;

import com.micro.servicea.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-b", url = "http://localhost:8082")
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") Long id);
}
