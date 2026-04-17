package by.nurbolat.cloud_file_storage.dto.minio;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Folder implements Resource {
    private String path;
    private String name;
    private ResourceType type;
}
