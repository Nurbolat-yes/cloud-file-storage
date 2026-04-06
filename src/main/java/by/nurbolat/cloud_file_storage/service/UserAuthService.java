package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserAuthService {

    UserReadDto register(UserCreateDto userCreateDto, HttpServletRequest request) throws UserAlreadyExistsException;

    UserReadDto login(UserLoginDto userLoginDto, HttpServletRequest request) throws EmailOrPasswordIncorrect, UserNotFoundException;

    void logout(HttpServletRequest request) ;
}
