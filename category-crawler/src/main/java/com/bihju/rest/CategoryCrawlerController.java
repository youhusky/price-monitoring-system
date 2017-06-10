package com.bihju.rest;

import com.bihju.CategoryCrawlerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category-crawler")
public class CategoryCrawlerController {
    private CategoryCrawlerTask categoryCrawlerTask;

    @Autowired
    public CategoryCrawlerController(CategoryCrawlerTask categoryCrawlerTask) {
        this.categoryCrawlerTask = categoryCrawlerTask;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        return "Success";
    }

    @RequestMapping(value = "crawler", method = RequestMethod.GET)
    public String startCrawler() {
        categoryCrawlerTask.startCrawling();
        return "Success";
    }

}
