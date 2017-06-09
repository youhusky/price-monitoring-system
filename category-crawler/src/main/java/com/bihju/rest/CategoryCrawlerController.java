package com.bihju.rest;

import com.bihju.CategoryCrawlerApp;
import com.bihju.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category-crawler")
public class CategoryCrawlerController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryCrawlerApp categoryCrawler;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        return "Success";
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public void crawlCategories() {
        try {
            categoryCrawler.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
