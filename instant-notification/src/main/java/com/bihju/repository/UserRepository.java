package com.bihju.repository;

import com.bihju.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u.email from User u, UserCategory uc " +
            "where u.id = uc.id and uc.categoryId = :categoryId and u.notificationType = :notificationType")
    List<String> findEmailsByCategoryIdAndNotificationType(
            @Param("categoryId") Long categoryId,
            @Param("notificationType") User.NotificationType notificationType);
}
