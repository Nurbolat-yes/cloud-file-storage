package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.user.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.user.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.user.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.user.UserNotFoundException;


public interface UserAuthService {

    UserReadDto register(UserCreateDto userCreateDto) throws UserAlreadyExistsException;

    UserReadDto login(UserLoginDto userLoginDto) throws EmailOrPasswordIncorrect, UserNotFoundException;

    void logout() ;
}
