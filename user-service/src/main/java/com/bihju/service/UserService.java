package com.bihju.service;

import com.bihju.domain.User;
import com.bihju.domain.UserCategory;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);
    void subscribeCategory(UserCategory userCategory);
    void unSubscribeCategory(Long userId, Long categoryId);
    void updateMinDiscountPercent(UserCategory userCategory);
}
