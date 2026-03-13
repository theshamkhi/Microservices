package com.micro.servicea.service;

import com.micro.servicea.client.UserFeignClient;
import com.micro.servicea.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserFeignService {
    private final UserFeignClient userFeignClient;

    public UserFeignService(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    public User getUserById(Long id) {
        return userFeignClient.getUserById(id);
    }
}
