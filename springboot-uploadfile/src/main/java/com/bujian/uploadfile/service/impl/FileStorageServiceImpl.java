package com.bujian.uploadfile.service.impl;

import com.bujian.uploadfile.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 文件操作
 * @author bujian
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path path = Paths.get("fileStorage");

    @Override
    public void init() throws IOException {
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.err.println(e + " (FileStorageServiceImpl.java:33)");
        }
    }

    @Override
    public void save(MultipartFile multipartFile) throws IOException {
        Files.copy(multipartFile.getInputStream(),this.path.resolve(multipartFile.getOriginalFilename()));
    }

    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path file = path.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        Assert.isTrue(resource.exists() || resource.isReadable(),"Could not read the file.");
        return resource;
    }

    @Override
    public Stream<Path> load() throws IOException {
        return Files.walk(this.path,1).filter(path -> !path.equals(this.path)).map(this.path::relativize);
    }

    @Override
    public void clear(String filename) {
        FileSystemUtils.deleteRecursively(path.resolve(filename).toFile());
    }

    @Override
    public void clearAll() {
        FileSystemUtils.deleteRecursively(path.toFile());
    }
}
