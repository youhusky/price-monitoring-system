package com.bihju.rest;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.domain.User;
import com.bihju.service.CategoryService;
import com.bihju.service.ProductService;
import com.bihju.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Log4j
public class UserServiceApi {
    private UserService userService;
    private CategoryService categoryService;
    private ProductService productService;

    @Autowired
    public UserServiceApi(UserService userService, CategoryService categoryService, ProductService productService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @RequestMapping("test")
    public String test() {
        return "Success";
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public List<Category> listCategories() {
        return categoryService.getAllCategories();
    }

    @RequestMapping(value="users/{userId}/categories/{categoryId}", method= RequestMethod.POST)
    public String subscribe(@PathVariable Long userId, @PathVariable Long categoryId) {
        try {
            userService.subscribeCategory(userId, categoryId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return e.getMessage();
        }

        return "Success";
    }

    @RequestMapping(value="users/{userId}/categories/{categoryId}", method= RequestMethod.DELETE)
    public String unsubscribe(@PathVariable Long userId, @PathVariable Long categoryId) {
        try {
            userService.unSubscribeCategory(userId, categoryId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return e.getMessage();
        }

        return "Success";
    }

    @RequestMapping(value="users", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value="users/{userId}", method= RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @RequestMapping(value="users/{userId}", method= RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
        } catch(RuntimeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return e.getMessage();
        }

        return "Success";
    }

    @RequestMapping(value = "deals/{categoryId}", method = RequestMethod.GET)
    public List<Product> search(@PathVariable long categoryId) {
        return productService.findProduct(categoryId);
    }
}
