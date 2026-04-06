package by.nurbolat.cloud_file_storage.controller;

import by.nurbolat.cloud_file_storage.dto.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> createNewUser(@RequestBody @Validated UserCreateDto userCreateDto, HttpServletRequest request) throws UserAlreadyExistsException {

        UserReadDto userReadDto = userAuthService.register(userCreateDto,request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userReadDto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> loginUser(@RequestBody @Validated UserLoginDto userLoginDto,HttpServletRequest request) throws EmailOrPasswordIncorrect, UserNotFoundException {

        UserReadDto userReadDto = userAuthService.login(userLoginDto,request);

        return ResponseEntity.status(HttpStatus.OK).body(userReadDto);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        userAuthService.logout(request);

        return ResponseEntity.noContent().build();
    }

}
