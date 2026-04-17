package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.user.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;


public interface UserAuthService {

    UserReadDto register(UserCreateDto userCreateDto) throws UserAlreadyExistsException;

    UserReadDto login(UserLoginDto userLoginDto) throws EmailOrPasswordIncorrect, UserNotFoundException;

    void logout() ;
}
