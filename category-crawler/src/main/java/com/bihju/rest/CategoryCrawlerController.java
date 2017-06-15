package com.bihju.rest;

import com.bihju.CategoryCrawlerTask;
import com.bihju.domain.UserCountThreshold;
import com.bihju.service.UserCountThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category-crawlers")
public class CategoryCrawlerController {
    private CategoryCrawlerTask categoryCrawlerTask;
    private UserCountThresholdService userCountThresholdService;

    @Autowired
    public CategoryCrawlerController(CategoryCrawlerTask categoryCrawlerTask, UserCountThresholdService userCountThresholdService) {
        this.categoryCrawlerTask = categoryCrawlerTask;
        this.userCountThresholdService = userCountThresholdService;
    }

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public String startCrawler() {
        categoryCrawlerTask.startCrawling();
        return "Success";
    }

    @RequestMapping(value = "priorities", method = RequestMethod.GET)
    public String updatePriority() {
        categoryCrawlerTask.updateCategoryPriorities();
        return "Success";
    }

    @RequestMapping(value = "user-count-threshold", method = RequestMethod.GET)
    public UserCountThreshold getUserCountThreshold() {
        return userCountThresholdService.getUserCountThreshold();
    }

    @RequestMapping(value = "user-count-threshold", method = RequestMethod.PUT)
    public String updateUserCountThreshold(@RequestBody UserCountThreshold userCountThreshold) {
        userCountThresholdService.setUserCountThreshold(userCountThreshold);
        return "Success";
    }
}
