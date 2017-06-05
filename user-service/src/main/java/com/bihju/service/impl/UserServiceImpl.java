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

        user.setLastUpdatedTime(System.currentTimeMillis());
        return userRepository.save(user);
    }

    @Override
    public void subscribeCategory(Long userId, Long categoryId) throws RuntimeException {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user id: " + userId);
        }
        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Invalid category id: " + categoryId);
        }

        UserCategory userCategory = userCategoryRepository.findUserCategoryByUserIdAndCategoryId(userId, categoryId);
        if (userCategory != null) {
            throw new RuntimeException("User already subscribed the category, userId: " + userId + ", categoryId" + categoryId);
        }

        userCategory = new UserCategory(userId, categoryId);
        userCategoryRepository.save(userCategory);
    }
}
