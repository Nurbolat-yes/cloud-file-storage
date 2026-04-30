package by.nurbolat.cloud_file_storage.service.impl;

import by.nurbolat.cloud_file_storage.dto.minio.File;
import by.nurbolat.cloud_file_storage.dto.minio.Folder;
import by.nurbolat.cloud_file_storage.dto.minio.Resource;
import by.nurbolat.cloud_file_storage.exception.custom.user.UserNotFoundException;
import by.nurbolat.cloud_file_storage.repository.MinioRepository;
import by.nurbolat.cloud_file_storage.service.MiniService;
import by.nurbolat.cloud_file_storage.service.UserService;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MiniService {
    private final UserService userService;
    private final MinioRepository minioRepository;

    @Override
    public Folder uploadFolder(String path) throws MinioException, UserNotFoundException {

        String fullPath = getFullPath(path);

        return minioRepository.createFolder(fullPath);

    }

    @Override
    public List<File> uploadFile(String path, List<MultipartFile> multipartFiles) throws UserNotFoundException, MinioException, IOException {
        List<File> results = new ArrayList<>();

        String fullPath = getFullPath(path);

        for (MultipartFile multipartFile:multipartFiles){
            var file = minioRepository.createFile(fullPath,multipartFile);
            results.add(file);
        }

        return results;
    }

    @Override
    public List<Resource> getFolderResources(String path) throws UserNotFoundException, MinioException {

        String fullPath = getFullPath(path);

        return minioRepository.getResources(fullPath);

    }

    @Override
    public Resource getResourceInformation(String path) throws UserNotFoundException {

        String fullPath = getFullPath(path);

        var maybeResource = minioRepository.getResource(fullPath);

        return maybeResource.get();
    }

    @Override
    public void deleteResource(String path) throws MinioException, UserNotFoundException {

        minioRepository.removeResource(path);
    }

    @Override
    public StreamingResponseBody downloadResource(String path) throws  UserNotFoundException {
        return minioRepository.download(path);
    }

    @Override
    public Optional<Resource> moveResource(String from, String to) throws UserNotFoundException,  MinioException {

        System.out.println("PATH FROM : "+from);
        System.out.println("PATH TO : "+ to);

        if (!to.startsWith("user-" + userService.getCurrentUserId() + "-files/")){
            to = getFullPath(to);
        }

        return minioRepository.moveResource(from,to);
    }

    @Override
    public List<Resource> searchByQuery(String query) throws UserNotFoundException, MinioException {

        return minioRepository.search(getFullPath("/"),query);
    }

    private String getFullPath(String path) throws UserNotFoundException {
        if (path.equals("/")){
            return "user-" + userService.getCurrentUserId() + "-files/";
        }
        return "user-" + userService.getCurrentUserId() + "-files/" +path;
    }
}
