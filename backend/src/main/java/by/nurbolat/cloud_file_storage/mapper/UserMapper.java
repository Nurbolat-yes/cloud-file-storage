package by.nurbolat.cloud_file_storage.mapper;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.user.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateDto userCreateDto);

    UserCreateDto toCreateDto(User user);

    UserReadDto toReadDto(User user);

    UserLoginDto toLoginDto(User user);

    List<UserCreateDto> toCreateDtoList(List<User> users);

}
