package by.nurbolat.cloud_file_storage.repository;

import by.nurbolat.cloud_file_storage.dto.minio.File;
import by.nurbolat.cloud_file_storage.dto.minio.Folder;
import by.nurbolat.cloud_file_storage.dto.minio.Resource;
import by.nurbolat.cloud_file_storage.dto.minio.ResourceType;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private static final String BUCKETNAME = "user-files";
    private final MinioClient minioClient;

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
                        .path(fullPath)
                        .name(parsePath[parsePath.length-1] +"/")
                        .type(ResourceType.DIRECTORY)
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
        folder.setType(ResourceType.DIRECTORY);
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
        System.out.println("Get PATH = [" + fullPath + "]");

        boolean isDir = fullPath.endsWith("/");

        if (isDir) {

            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKETNAME)
                            .prefix(fullPath)
                            .recursive(false)
                            .build()
            );

            if (!items.iterator().hasNext()) {
                return Optional.empty();
            }

            String trimmed = fullPath.substring(0, fullPath.length() - 1);
            int lastSlash = trimmed.lastIndexOf("/");

            String parentPath = trimmed.substring(0, lastSlash + 1);
            String folderName = trimmed.substring(lastSlash + 1) + "/";

            return Optional.of(
                    Folder.builder()
                            .path(parentPath)
                            .name(folderName)
                            .type(ResourceType.DIRECTORY)
                            .build()
            );

        } else {

            try {
                StatObjectResponse result = minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(BUCKETNAME)
                                .object(fullPath)
                                .build()
                );

                return Optional.of(
                        File.builder()
                                .path(fullPath.substring(0, fullPath.lastIndexOf("/") + 1))
                                .name(fullPath.substring(fullPath.lastIndexOf("/") + 1))
                                .size(result.size())
                                .type(ResourceType.FILE)
                                .build()
                );

            } catch (Exception e) {
                return Optional.empty();
            }

        }
    }

    public void removeResource(String fullPath) throws MinioException {
        System.out.println("DELETE PATH = [" + fullPath + "]");

        if (!fullPath.endsWith("/")){
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKETNAME)
                            .object(fullPath)
                            .build()
            );
        }
        else {
             Iterable<Result<Item>> objects = minioClient.listObjects(
                     ListObjectsArgs.builder()
                             .bucket(BUCKETNAME)
                             .prefix(fullPath)
                             .recursive(true)
                             .build()
             );

             for (Result<Item> result : objects){
                 Item item = result.get();

                 minioClient.removeObject(
                         RemoveObjectArgs.builder()
                                 .bucket(BUCKETNAME)
                                 .object(item.objectName())
                                 .build()
                 );
             }
        }
    }

    public StreamingResponseBody download(String fullPath) {
        System.out.println("DOWNLOAD PATH = [" + fullPath + "]");

        StreamingResponseBody stream = outputStream -> {
            try {

                if (!fullPath.endsWith("/")) {

                    try (InputStream is = minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(BUCKETNAME)
                                    .object(fullPath)
                                    .build())) {

                        byte[] buffer = new byte[8192];
                        int len;

                        while ((len = is.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }

                        outputStream.flush();
                    }

                    return;
                }

                try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {

                    Iterable<Result<Item>> results = minioClient.listObjects(
                            ListObjectsArgs.builder()
                                    .bucket(BUCKETNAME)
                                    .prefix(fullPath)
                                    .recursive(true)
                                    .build()
                    );

                    for (Result<Item> result : results) {
                        Item item = result.get();

                        if (item.isDir()) continue;

                        try (InputStream is = minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(BUCKETNAME)
                                        .object(item.objectName())
                                        .build())) {

                            String relativeName =
                                    item.objectName().substring(fullPath.length());

                            ZipEntry entry = new ZipEntry(relativeName);
                            zipOut.putNextEntry(entry);

                            byte[] buffer = new byte[8192];
                            int len;

                            while ((len = is.read(buffer)) != -1) {
                                zipOut.write(buffer, 0, len);
                            }

                            zipOut.closeEntry();
                        }
                    }

                    zipOut.finish();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return stream;
    }

    public Optional<Resource> moveResource(String fromFullPath, String toFullPath) throws MinioException {
        System.out.println("FROM PATH = [" + fromFullPath + "]");
        System.out.println("TO PATH = [" + toFullPath + "]");

        if (getResource(toFullPath).isPresent()) {
            throw new IllegalArgumentException(
                    "The resource lying on the path " + toFullPath + " already exists"
            );
        }

        try {
            if (fromFullPath.endsWith("/")) {

                Iterable<Result<Item>> objectsFromOldFolder = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(BUCKETNAME)
                                .prefix(fromFullPath)
                                .recursive(true)
                                .build()
                );

                for (Result<Item> result : objectsFromOldFolder) {
                    Item item = result.get();

                    if (item.isDir()) continue;

                    String oldObject = item.objectName();

                    String newObject =
                            toFullPath + oldObject.substring(fromFullPath.length());

                    System.out.println("COPY: " + oldObject + " -> " + newObject);

                    minioClient.copyObject(
                            CopyObjectArgs.builder()
                                    .bucket(BUCKETNAME)
                                    .object(newObject)
                                    .source(
                                            SourceObject.builder()
                                                    .bucket(BUCKETNAME)
                                                    .object(oldObject)
                                                    .build()
                                    )
                                    .build()
                    );

                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(BUCKETNAME)
                                    .object(oldObject)
                                    .build()
                    );
                }
            }
            else {

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(BUCKETNAME)
                                .object(toFullPath)
                                .source(
                                        SourceObject.builder()
                                                .bucket(BUCKETNAME)
                                                .object(fromFullPath)
                                                .build()
                                )
                                .build()
                );
                System.out.println("COPIED OK");

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(BUCKETNAME)
                                .object(fromFullPath)
                                .build()
                );
                System.out.println("DELETED OK");
            }

            return getResource(toFullPath);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Resource> search(String fullPath,String query) throws MinioException {
        List<Resource> resources = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKETNAME)
                        .prefix(fullPath)
                        .recursive(true)
                        .build()
        );

        String search = query.toLowerCase().trim();

        for (Result<Item> result :results){
            Item item = result.get();

            if (item.isDir()){
                continue;
            }

            String objectName = item.objectName();
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);


            if (fileName.toLowerCase().contains(search)){

                String parentPath = "";
                int lastSlash = objectName.lastIndexOf("/");

                if (lastSlash != -1) {
                    parentPath = objectName.substring(0, lastSlash);
                }

                resources.add(
                        File.builder()
                                .path(parentPath)
                                .name(fileName)
                                .size(item.size())
                                .type(ResourceType.FILE)
                                .build()
                );
            }
        }
        return resources;
    }
}
