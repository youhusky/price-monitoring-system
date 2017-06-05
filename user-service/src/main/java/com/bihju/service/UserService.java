package com.bihju.service;

import com.bihju.domain.User;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);
    void subscribeCategory(Long userId, Long categoryId);
}
