package by.nurbolat.cloud_file_storage.controller;

import by.nurbolat.cloud_file_storage.dto.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser() throws UserNotFoundException {

        UserReadDto user = userService.getCurrentUser();

        return ResponseEntity.ok().body(user);

    }

    @GetMapping("/test")
    public String test(){
        return "you are authenticated";
    }

}
