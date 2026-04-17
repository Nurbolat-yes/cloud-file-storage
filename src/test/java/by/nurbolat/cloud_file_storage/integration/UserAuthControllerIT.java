package by.nurbolat.cloud_file_storage.integration;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.user.UserLoginDto;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.repository.UserRepository;
import by.nurbolat.cloud_file_storage.service.UserAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class UserAuthControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthService userAuthService;

    @Test
    void createNewUser() throws Exception{
        UserCreateDto createDto = UserCreateDto
                .builder()
                .name("Nurbolat")
                .email("nur@32")
                .password("secret")
                .build();

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nurbolat"))
                .andExpect(jsonPath("$.email").value("nur@32"))
                .andExpect(authenticated());

        var maybeUser = userRepository.findUserByEmail("nur@32");

        assertTrue(maybeUser.isPresent());
        assertEquals(maybeUser.get().getName(),createDto.getName());
    }

    @Test
    void createUserWithExistingEmail() throws Exception {
        UserCreateDto original = UserCreateDto.builder()
                .name("example")
                .email("example@gmail")
                .password("123")
                .build();

        UserCreateDto duplicate = UserCreateDto.builder()
                .name("example1")
                .email("example@gmail")
                .password("12345")
                .build();

        userAuthService.register(original);
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with email: "+duplicate.getEmail()+" already exists!"))
                .andExpect(jsonPath("$.status").value("409"))
                .andExpect(unauthenticated());

        assertThrows(UserAlreadyExistsException.class,()->{
            userAuthService.register(duplicate);
        });
    }

    @Test
    void createUserWithIncorrectFields() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder()
                .name("someName")
                .email("incorrectEmail")
                .password("1")
                .build();

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must contain more 3 character"))
                .andExpect(jsonPath("$.email").value("Incorrect pattern of email"))
                .andExpect(unauthenticated());

        var maybeUser = userRepository.findUserByEmail(createDto.getEmail());

        assertThat(maybeUser).isEmpty();
    }

    @Test
    void successUserLogin() throws Exception {

        userAuthService.register(new UserCreateDto(
                "test",
                "test@gmail.com",
                "verySecret"
        ));

        UserLoginDto loginDto = UserLoginDto.builder()
                .email("test@gmail.com")
                .password("verySecret")
                .build();

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("test"),
                        jsonPath("$.email").value("test@gmail.com"),
                        authenticated()
                );
    }

    @Test
    void loginWithIncorrectCredentials() throws Exception{
        userAuthService.register(new UserCreateDto(
                "test",
                "test@gmail.com",
                "verySecret"
        ));

        SecurityContextHolder.clearContext();

        UserLoginDto loginDto = UserLoginDto.builder()
                .email("test1@gmail.com")
                .password("verySecret1")
                .build();

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.status").value("401"),
                        jsonPath("$.message").value("Email or Password incorrect"),
                        unauthenticated()
                );
    }

    @Test
    void successLogout() throws Exception {
        userAuthService.register(new UserCreateDto("somename","example@1","secret"));

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        mockMvc.perform(post("/api/auth/sign-out"))
                .andExpectAll(
                        status().isNoContent()
                );

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test
    void logoutWithUnauthorizedUser() throws Exception{
        mockMvc.perform(post("/api/auth/sign-out"))
                .andExpect(status().isUnauthorized());
    }

}
