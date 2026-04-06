package by.nurbolat.cloud_file_storage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserReadDto {
    private String name;
    private String email;
}
