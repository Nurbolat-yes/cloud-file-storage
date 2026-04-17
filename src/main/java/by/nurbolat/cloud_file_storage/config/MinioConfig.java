package by.nurbolat.cloud_file_storage.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(@Value("${spring.minio.url}") String url,
                                   @Value("${spring.minio.user}") String accessKey,
                                   @Value("${spring.minio.password}") String secretKey){
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey,secretKey)
                .build();
    }
}
