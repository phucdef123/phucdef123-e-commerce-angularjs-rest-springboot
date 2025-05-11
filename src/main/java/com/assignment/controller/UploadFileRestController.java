package com.assignment.controller;

import com.assignment.service.FileManagerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin("*")
@RestController
public class UploadFileRestController {
    @Autowired
    private FileManagerService fileManagerService;

    @PostMapping("/admin/upload/{folder}")
    public ResponseEntity<JsonNode> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("folder") String folder) {
        File saveFile = fileManagerService.save(folder, file);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("name", saveFile.getName());
        root.put("size", saveFile.length());
        String imageUrl = "/uploads/" + folder + "/" + saveFile.getName();
        root.put("url", imageUrl);
        return ResponseEntity.ok(root);
    }

    @GetMapping("/uploads/{folder}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/" + folder + "/" + filename);
        Resource resource = new FileSystemResource(filePath.toFile());
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
