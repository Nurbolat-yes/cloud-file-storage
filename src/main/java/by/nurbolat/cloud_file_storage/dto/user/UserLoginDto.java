package by.nurbolat.cloud_file_storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UserLoginDto")
public class UserLoginDto {
    @Email(message = "Incorrect pattern of email")
    @NotBlank(message = "Email must not be empty or null")
    @Size(min = 3,max = 40,message = "Email must be between 3 and 40 character")
    @Schema(example = "example@gmail.com", description = "example of correct email")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Schema(example = "secret", description = "example of user password")
    private String password;
}
