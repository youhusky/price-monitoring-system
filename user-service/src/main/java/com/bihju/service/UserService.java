package com.bihju.service;

import com.bihju.domain.User;

public interface UserService {
    User createUser(User user);
    void subscribeCategory(Long userId, Long categoryId);
}
