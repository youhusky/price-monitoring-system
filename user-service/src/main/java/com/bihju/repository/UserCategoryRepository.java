package com.bihju.repository;

import com.bihju.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    UserCategory findUserCategoryByUserIdAndCategoryId(Long userId, Long categoryId);
}
