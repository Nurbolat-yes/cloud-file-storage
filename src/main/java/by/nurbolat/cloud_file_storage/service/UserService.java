package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;

public interface UserService {
    UserReadDto getCurrentUser() throws  UserNotFoundException;
    Long getCurrentUserId() throws UserNotFoundException;
}
