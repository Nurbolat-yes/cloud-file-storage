package by.nurbolat.cloud_file_storage.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UserCreateDtoSchema")
public class UserCreateDto {
    @Email(message = "Incorrect pattern of username")
    @NotBlank(message = "Username must not be empty or null")
    @Size(min = 3,max = 40,message = "Username must be between 3 and 40 character")
    @Schema(example = "example@gmail.com", description = "example of correct username")
    private String username;

    @NotBlank(message = "Password can not be null")
    @Size(min = 3, message = "Password must contain more 3 character")
    @Schema(example = "secret", description = "User secret password")
    private String password;

}
