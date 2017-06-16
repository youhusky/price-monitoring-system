package com.bihju.rest;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.domain.User;
import com.bihju.service.CategoryService;
import com.bihju.service.ProductService;
import com.bihju.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
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

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public List<Category> listCategories() {
        return categoryService.getAllCategories();
    }

    @RequestMapping(value="{userId}/categories/{categoryId}", method= RequestMethod.POST)
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

    @RequestMapping(value="{userId}/categories/{categoryId}", method= RequestMethod.DELETE)
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

    @RequestMapping(value="", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value="{userId}", method= RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @RequestMapping(value="{userId}", method= RequestMethod.DELETE)
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

    @RequestMapping(value = "deals", method = RequestMethod.GET)
    public Page<Product> searchDeals(
            @RequestParam(value = "categoryIdString", required = false) Long categoryId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
            @RequestParam(value = "sortBy", required = false, defaultValue = "updateTime") String sortBy
            ) throws Exception {

        Assert.isTrue(page >= 0, "Page index must be >= 0");
        Assert.isTrue(direction.equalsIgnoreCase(Sort.Direction.ASC.toString())
                || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()),
                "Direction should be ASC or DESC");

        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.fromString(direction), sortBy);
        return productService.searchDeals(categoryId, pageRequest);
    }
}
