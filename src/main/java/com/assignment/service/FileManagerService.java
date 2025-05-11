package com.assignment.service;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileManagerService {
    @Autowired
    ServletContext servletContext;

    private final Path root = Paths.get(System.getProperty("user.dir") + "/uploads/");

    private Path getPath(String folder, String filename) {
        Path dir = root.resolve(folder);

        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tạo thư mục: " + dir, e);
            }
        }
        return dir.resolve(filename);
    }

    public byte[] read(String folder, String file){
        Path path = getPath(folder,file);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File save(String folder, MultipartFile file){
        String name = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filename = Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
        Path path = getPath(folder, filename);
        try{
            file.transferTo(path.toFile());
            return path.toFile();
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu file", e);
        }
    }

    public List<String> list(String folder){
        try{
            List<String> filenames = new ArrayList<>();
            File dir = Paths.get(servletContext.getRealPath("/files"), folder).toFile();
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        filenames.add(file.getName());
                    }
                }
            }
            return filenames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String folder, String file){
        try{
            Path path = getPath(folder,file);
            path.toFile().delete();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
