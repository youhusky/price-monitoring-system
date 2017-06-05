package com.bihju.service;

import java.util.List;

public interface UserService {
    List<String> findUsersByCategoryId(Long categoryId);
}
