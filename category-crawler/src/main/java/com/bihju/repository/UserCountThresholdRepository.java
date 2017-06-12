package com.bihju.repository;

import com.bihju.domain.UserCountThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "user_count_threshold", collectionResourceRel = "user_count_threshold")
public interface UserCountThresholdRepository extends JpaRepository<UserCountThreshold, Long> {
    UserCountThreshold findFirstByOrderByIdDesc();
}

