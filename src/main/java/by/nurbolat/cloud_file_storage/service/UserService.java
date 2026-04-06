package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;

public interface UserService {
    UserReadDto getCurrentUser() throws  UserNotFoundException;
}
