package by.nurbolat.cloud_file_storage.controller;

import by.nurbolat.cloud_file_storage.dto.user.UserCreateDto;
import by.nurbolat.cloud_file_storage.dto.user.UserLoginDto;
import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.EmailOrPasswordIncorrect;
import by.nurbolat.cloud_file_storage.exception.custom.UserAlreadyExistsException;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication",description = "API for authentication, registration and logout")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/sign-up")
    @Operation(summary = "Registering a new user", description = "Creating and Authenticating a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserReadDto.class))
            }),
            @ApiResponse(responseCode = "400",description = "Validation error", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                      "password": "Password must contain more 3 character",
                                      "email": "Incorrect pattern of email"
                                    }
                                    """)
                    })
            }),
            @ApiResponse(responseCode = "409", description = "Email already exists error", content = {
               @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,examples = {
                       @ExampleObject(value = """
                                   {
                                        "status": 409,
                                        "message": "email already exists",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                   }
                                   """)
               })
            }),
            @ApiResponse(responseCode = "500",description = "Unknown error", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 500,
                                        "message": "Internal server error",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                    }
                                    """)
                    })
            })
    })
    public ResponseEntity<?> createNewUser(@RequestBody @Validated UserCreateDto userCreateDto, HttpServletRequest request) throws UserAlreadyExistsException {

        UserReadDto userReadDto = userAuthService.register(userCreateDto);
        openSession(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userReadDto);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Login user", description = "Login the user into system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User success login", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserReadDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                      "password": "Password must contain more 3 character",
                                      "email": "Incorrect pattern of email"
                                    }
                                    """)
                    })
            }),
            @ApiResponse(responseCode = "401", description = "Email or password incorrect", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 401,
                                        "message": "email or password incorrect",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                    }
                                    """)
                    })
            }),
            @ApiResponse(responseCode = "500",description = "Unknown error", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 500,
                                        "message": "Internal server error",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                    }
                                    """)
                    })
            })
    })
    public ResponseEntity<?> loginUser(@RequestBody @Validated UserLoginDto userLoginDto,HttpServletRequest request) throws EmailOrPasswordIncorrect, UserNotFoundException {

        UserReadDto userReadDto = userAuthService.login(userLoginDto);
        openSession(request);

        return ResponseEntity.status(HttpStatus.OK).body(userReadDto);
    }

    @PostMapping("/sign-out")
    @Operation(summary = "User logout", description = "Logout the user from system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized user", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 401,
                                        "message": "unauthorized user",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                    }
                                    """)
                    })
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 500,
                                        "message": "Internal server error",
                                        "timestamp": "2026-04-11T06:53:26.987Z"
                                    }
                                    """)
                    })
            })
    })
    public ResponseEntity<?> logout(HttpServletRequest request) {

        userAuthService.logout();
        closeSession(request);

        return ResponseEntity.noContent().build();
    }

    private void openSession(HttpServletRequest request){
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }

    private void closeSession(HttpServletRequest request){
        request.getSession(false).invalidate();


    }

}
