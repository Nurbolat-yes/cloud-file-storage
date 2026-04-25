package by.nurbolat.cloud_file_storage.controller;

import by.nurbolat.cloud_file_storage.dto.minio.File;
import by.nurbolat.cloud_file_storage.dto.minio.Folder;
import by.nurbolat.cloud_file_storage.dto.minio.Resource;
import by.nurbolat.cloud_file_storage.exception.custom.UserNotFoundException;
import by.nurbolat.cloud_file_storage.service.MiniService;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.codec.ResourceEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(
        origins = "http://localhost",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MinioController {

    private final MiniService miniService;

    //---------------Resource Controllers----------------------------------------

    @PostMapping(value = "/resource",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<File>> addFiles(@RequestParam("path") String path,
                                               @RequestParam(name = "object") List<MultipartFile> files) throws MinioException, IOException, UserNotFoundException {

            List<File> result = miniService.uploadFile(path,files);
            return new ResponseEntity<>(result,HttpStatus.CREATED);

    }

    @GetMapping(value = "/resource")
    public ResponseEntity<Resource> getResource(@RequestParam("path") String path) throws UserNotFoundException, MinioException {
        Optional<Resource> resource = miniService.getResourceInformation(path);

        if (resource.isPresent()){
            return new ResponseEntity<>(resource.get(),HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/resource/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(@RequestParam(value = "path") String path) throws UserNotFoundException {

        StreamingResponseBody stream = miniService.downloadResource(path);

        return ResponseEntity.ok()
                .header("Content-Disposition","attachment; filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }

    @DeleteMapping(value = "resource")
    public ResponseEntity<Void> deleteResource(@RequestParam("path") String path) throws UserNotFoundException, MinioException {

        miniService.deleteResource(path);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "resource/move")
    public ResponseEntity<Resource> moveResource(@RequestParam("from") String from,
                                             @RequestParam("to") String to) throws UserNotFoundException, MinioException {

        System.out.println(from);
        System.out.println(to);

        Optional<Resource> resource = miniService.moveResource(from,to);

        return new ResponseEntity<>(resource.get(),HttpStatus.OK);
    }

    @GetMapping(value = "/resource/search")
    public ResponseEntity<List<Resource>> searchResource(@RequestParam("query") String query) throws UserNotFoundException, MinioException {

        List<Resource> resources = miniService.serchByQuery(query);

        return new ResponseEntity<>(resources,HttpStatus.OK);
    }


    //---------------Directory Controllers----------------------------------------

    @PostMapping(value = "/directory")
    public ResponseEntity<Folder> addFolder(@RequestParam("path") String path) throws UserNotFoundException, MinioException, IOException {
        Folder folder = miniService.uploadFolder(path);

        return new ResponseEntity<>(folder,HttpStatus.CREATED);
    }

    @GetMapping(value = "/directory")
    public ResponseEntity<List<Resource>> getResources(@RequestParam("path") String path) throws UserNotFoundException, MinioException {
        List<Resource> resources = miniService.getFolderResources(path);

        return new ResponseEntity<>(resources,HttpStatus.OK);
    }

}
