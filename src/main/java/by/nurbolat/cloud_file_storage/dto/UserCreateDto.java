package by.nurbolat.cloud_file_storage.dto;

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
public class UserCreateDto {
    private String name;

    @Email(message = "Incorrect pattern of email")
    @NotBlank(message = "Email must not be empty or null")
    @Size(min = 3,max = 40,message = "Email must be between 3 and 40 character")
    private String email;

    @NotBlank(message = "Password can not be null")
    @Size(min = 3, message = "Password must contain more 3 character")
    private String password;

    private String role;
}
