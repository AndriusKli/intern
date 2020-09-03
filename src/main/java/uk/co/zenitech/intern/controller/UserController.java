package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.User;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "api/users")
@Api("api/users")
public class UserController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return Arrays.asList(new User(0L, "string"));
    }

    @GetMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable Long userId ) {
        return new User(0L, "string");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody User user) {

    }

    @PutMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@RequestBody User user, @PathVariable Long userId) {

    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {

    }
}
