package uk.co.zenitech.intern.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.zenitech.intern.entity.User;
import uk.co.zenitech.intern.service.user.UserService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "api/users")
@Api("api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId ) {
        return ResponseEntity.ok(userService.findUser(userId));
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) throws URISyntaxException {
        userService.createUser(user);
        return ResponseEntity.created(new URI(user.getUserId().toString())).build();
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable Long userId) {
        userService.updateUser(userId, user);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}
