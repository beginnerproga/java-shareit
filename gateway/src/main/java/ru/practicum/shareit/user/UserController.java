package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoGateWay;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get users - gateway request");
        return userClient.getUsers();

    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user by id = " + userId + " - gateway request");
        return userClient.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addUser(@Validated(Create.class) @RequestBody UserDtoGateWay userDtoGateWay) {
        log.info("Add user by id - gateway request");
        return userClient.addUser(userDtoGateWay);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated({Update.class}) @RequestBody UserDtoGateWay userDtoGateWay, @PathVariable long userId) {
        log.info("Update user by id = " + userId + " - gateway request");
        return userClient.updateUser(userDtoGateWay, userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Delete user by id = " + userId + " - gateway request");
        return userClient.deleteUser(userId);

    }

}
