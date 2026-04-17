package by.nurbolat.cloud_file_storage.unit;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.entity.Roles;
import by.nurbolat.cloud_file_storage.entity.User;
import by.nurbolat.cloud_file_storage.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void convertEntityToUserCreateDto(){
        User user = new User(1L,"Talgat","tagat@32","secret", Roles.USER);

        UserCreateDto createDto = userMapper.toCreateDto(user);

        assertNotNull(createDto);

        assertNotNull(createDto.getName());
        assertNotNull(createDto.getEmail());
        assertNotNull(createDto.getPassword());

        assertEquals(user.getName(),createDto.getName());
        assertEquals(user.getEmail(),createDto.getEmail());
        assertEquals(user.getPassword(),createDto.getPassword());
    }

    @Test
    void convertCreateDtoToEntity(){
        UserCreateDto createDto = new UserCreateDto("Maksat","maks@32","secret");

        User user = userMapper.toEntity(createDto);

        assertNotNull(user);

        assertNull(user.getId());
        assertNotNull(user.getName());
        assertNotNull(user.getEmail());
        assertNotNull(user.getPassword());
        assertNull(user.getRole());

        assertEquals(createDto.getName(),user.getName());
        assertEquals(createDto.getEmail(),user.getEmail());
        assertEquals(createDto.getPassword(),user.getPassword());
    }

    @Test
    void convertEntityListToDtoList(){
        List<User> users = new ArrayList<>();
        users.add(new User(1L,"Talgat","tagat@32","secret", Roles.USER));
        users.add(new User(2L,"Maksat","mask@32","secret1", Roles.ADMIN));
        users.add(new User(3L,"Marat","mara@32","secret2", Roles.ADMIN));
        users.add(new User(4L,"Dastan","doston@32","secret3", Roles.USER));

        List<UserCreateDto> createDtoList = userMapper.toCreateDtoList(users);

        assertNotNull(createDtoList);

        assertNotEquals(createDtoList.size(),0);

        for (int i = 0 ; i < users.size() ; i++){
            User user = users.get(i);
            UserCreateDto createDto = createDtoList.get(i);

            assertNotNull(createDto);

            assertNotNull(createDto.getName());
            assertNotNull(createDto.getEmail());
            assertNotNull(createDto.getPassword());

            assertEquals(user.getName(),createDto.getName());
            assertEquals(user.getEmail(),createDto.getEmail());
            assertEquals(user.getPassword(),createDto.getPassword());
        }
    }
}
