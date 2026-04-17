package by.nurbolat.cloud_file_storage.controller;

import by.nurbolat.cloud_file_storage.dto.user.UserReadDto;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Rest Controller",description = "API for getting current user")
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/me")
    @Operation(summary = "Get current user", description = "Getting the current (authenticated) user in system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully received", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserReadDto.class))
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
    public ResponseEntity<?> getCurrentUser() throws UserNotFoundException {

        UserReadDto user = userService.getCurrentUser();

        return ResponseEntity.ok().body(user);

    }

}
