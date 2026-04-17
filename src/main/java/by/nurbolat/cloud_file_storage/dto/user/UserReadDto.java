package by.nurbolat.cloud_file_storage.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "UserReadDtoSchema")
public class UserReadDto {

    @Schema(example = "username", description = "Username")
    private String name;
    @Schema(example = "example@mail.com", description = "user email")
    private String email;
}
