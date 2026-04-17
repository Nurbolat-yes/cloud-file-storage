package by.nurbolat.cloud_file_storage.service;

import by.nurbolat.cloud_file_storage.dto.minio.File;
import by.nurbolat.cloud_file_storage.dto.minio.Folder;
import by.nurbolat.cloud_file_storage.dto.minio.Resource;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MiniService {

    Folder uploadFolder(String path) throws MinioException, IOException, UserNotFoundException;

    List<File> uploadFile(String path, List<MultipartFile> multipartFile) throws UserNotFoundException, MinioException, IOException;

    List<Resource> getFolderResources(String path) throws UserNotFoundException, MinioException;

    Optional<Resource> getResourceInformation(String path) throws UserNotFoundException, MinioException;
}
