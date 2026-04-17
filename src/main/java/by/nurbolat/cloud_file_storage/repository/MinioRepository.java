package by.nurbolat.cloud_file_storage.repository;

import by.nurbolat.cloud_file_storage.dto.minio.File;
import by.nurbolat.cloud_file_storage.dto.minio.Folder;
import by.nurbolat.cloud_file_storage.dto.minio.Resource;
import by.nurbolat.cloud_file_storage.dto.minio.ResourceType;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MinioRepository {
    private static final String BUCKETNAME = "user-files";

    private final MinioClient minioClient;

    // Methods for Folder ----------------------------------------------------------

    public List<Resource> getResources(String fullPath) throws MinioException {
        List<Resource> resultFiles = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKETNAME)
                        .prefix(fullPath)
                        .recursive(false)
                        .build()
        );

        for (Result<Item> result : results){
            Item item = result.get();

            if (!item.isDir()) {
                resultFiles.add(File.builder()
                        .path(fullPath)
                        .name(item.objectName().substring(item.objectName().lastIndexOf("/") + 1))
                        .size(item.size())
                        .type(ResourceType.FILE)
                        .build());
            }else {
                String[] parsePath = item.objectName().split("/");
                resultFiles.add(Folder.builder()
                        .path(item.objectName())
                        .name(parsePath[parsePath.length-1])
                        .type(ResourceType.FOLDER)
                        .build());
            }

        }

        return resultFiles;
    }


    public Folder createFolder(String fullPath) throws MinioException, IOException {
        Folder folder = new Folder();

        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(BUCKETNAME)
                        .object(fullPath+".keep")
                        .stream(new ByteArrayInputStream(new byte[]{}),0L,-1L)
                        .build());

        String[] parts = fullPath.split("/");
        folder.setPath(fullPath);
        folder.setName(parts[parts.length-1]);
        folder.setType(ResourceType.FOLDER);
        return folder;
    }

    public File createFile(String fullPath,MultipartFile multipartFile) throws MinioException, IOException {
        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(BUCKETNAME)
                        .object(fullPath+multipartFile.getOriginalFilename())
                        .stream(multipartFile.getInputStream(),multipartFile.getSize(),-1L)
                        .contentType(multipartFile.getContentType())
                        .build());

        return File.builder()
                .path(fullPath)
                .name(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .type(ResourceType.FILE)
                .build();
    }

    public Optional<Resource> getResource(String fullPath) throws MinioException {

        Resource resource = null;

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKETNAME)
                        .prefix(fullPath)
                        .build());

        for (Result<Item> result:results){
            Item item = result.get();
            String[] parsePath = fullPath.split("/");
            if (result.get().isDir()){
                resource = Folder.builder()
                        .path(fullPath)
                        .name(parsePath[parsePath.length-1])
                        .type(ResourceType.FOLDER)
                        .build();
            }else {
                resource = File.builder()
                        .path(fullPath.substring(0,fullPath.lastIndexOf("/")+1))
                        .name(fullPath.substring(fullPath.lastIndexOf("/")+1))
                        .size(item.size())
                        .type(ResourceType.FILE)
                        .build();
            }
        }

        return Optional.ofNullable(resource);
    }
}
