package com.bihju.service.impl;

import com.bihju.domain.UserCountThreshold;
import com.bihju.repository.UserCountThresholdRepository;
import com.bihju.service.UserCountThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCountThresholdServiceImpl implements UserCountThresholdService {
    private UserCountThresholdRepository userCountThresholdRepository;



    @Autowired
    public UserCountThresholdServiceImpl(UserCountThresholdRepository userCountThresholdRepository) {
        this.userCountThresholdRepository = userCountThresholdRepository;
    }

    @Override
    public UserCountThreshold getUserCountThreshold() {
        return userCountThresholdRepository.findFirstByOrderByIdDesc();
    }

    @Override
    public void setUserCountThreshold(UserCountThreshold userCountThreshold) {
        UserCountThreshold current = getUserCountThreshold();
        if (current == null) {
            userCountThreshold.setCreateTime(System.currentTimeMillis());
            userCountThreshold.setUpdateTime(System.currentTimeMillis());
            userCountThresholdRepository.save(userCountThreshold);
        } else {
            current.setHighPriorityUserCount(userCountThreshold.getHighPriorityUserCount());
            current.setUpdateTime(System.currentTimeMillis());
            userCountThresholdRepository.save(current);
        }
    }
}
