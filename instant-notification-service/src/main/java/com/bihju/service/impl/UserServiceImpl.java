package com.bihju.service.impl;

import com.bihju.domain.User;
import com.bihju.repository.UserRepository;
import com.bihju.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> findUsersByCategoryId(Long categoryId, Double discountPercent) {
        return userRepository.findEmailsByCategoryIdAndNotificationType(categoryId, User.NotificationType.INSTANT, discountPercent);
    }
}
