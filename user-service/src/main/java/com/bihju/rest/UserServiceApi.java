package com.bihju.rest;

import com.bihju.domain.User;
import com.bihju.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Log4j
public class UserServiceApi {
    @Autowired
    private UserService userService;

    @RequestMapping("test")
    public String test() {
        return "Success";
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

    @RequestMapping(value="", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value="{userId}", method= RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        try {
            return userService.updateUser(user);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
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
}
