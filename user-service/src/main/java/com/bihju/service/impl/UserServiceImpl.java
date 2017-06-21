package com.bihju.service.impl;

import com.bihju.domain.Category;
import com.bihju.domain.User;
import com.bihju.domain.UserCategory;
import com.bihju.repository.CategoryRepository;
import com.bihju.repository.UserCategoryRepository;
import com.bihju.repository.UserRepository;
import com.bihju.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private UserCategoryRepository userCategoryRepository;

    public UserServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository,
                           UserCategoryRepository userCategoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
    }

    @Override
    public User createUser(User user) {
        User existingUser = userRepository.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            return existingUser;
        }

        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = findUserById(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setUpdateTime(System.currentTimeMillis());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User existingUser = findUserById(userId);
        userRepository.delete(existingUser);
    }

    @Override
    public void subscribeCategory(UserCategory userCategory) throws RuntimeException {
        long userId = userCategory.getUserId();
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user id: " + userCategory.getUserId());
        }

        long categoryId = userCategory.getCategoryId();
        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Invalid category id: " + userCategory.getCategoryId());
        }

        if (userCategoryRepository.findUserCategoryByUserIdAndCategoryId(userId, categoryId) != null) {
            throw new RuntimeException("User already subscribed the category, userId: " + userId + ", categoryId" + categoryId);
        }

        userCategory.setCreateTime(System.currentTimeMillis());
        userCategory.setUpdateTime(System.currentTimeMillis());
        userCategoryRepository.save(userCategory);
    }

    @Override
    public void unSubscribeCategory(Long userId, Long categoryId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user id: " + userId);
        }

        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Invalid category id: " + categoryId);
        }

        UserCategory existingUserCategory = userCategoryRepository.findUserCategoryByUserIdAndCategoryId(userId, categoryId);
        if (existingUserCategory == null) {
            throw new RuntimeException("User did not subscribe the category, userId: " + userId + ", categoryId" + categoryId);
        }

        userCategoryRepository.delete(existingUserCategory);
    }

    @Override
    public void updateMinDiscountPercent(UserCategory userCategory) throws RuntimeException {
        long userId = userCategory.getUserId();
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user id: " + userId);
        }

        long categoryId = userCategory.getCategoryId();
        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Invalid category id: " + categoryId);
        }

        UserCategory existingUserCategory = userCategoryRepository.findUserCategoryByUserIdAndCategoryId(userId, categoryId);
        if (existingUserCategory == null) {
            throw new RuntimeException("User did not subscribe the category, userId: " + userId + ", categoryId" + categoryId);
        }

        existingUserCategory.setMinDiscountPercent(userCategory.getMinDiscountPercent());
        existingUserCategory.setUpdateTime(System.currentTimeMillis());
        userCategoryRepository.save(existingUserCategory);
    }

    private User findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user, userId = " + userId);
        }

        return user;
    }
}
