package com.example.xml.controller;

import com.example.xml.config.StorageException;
import com.example.xml.config.StorageFileNotFoundException;
import com.example.xml.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Log4j2
public class FileUploadController {

    private final StorageService storageService;

    private String pathToUri(Path path) {

        return MvcUriComponentsBuilder.fromMethodName(
                        FileUploadController.class, "serveFile", path.getFileName().toString()
                ).build()
                .toUri()
                .toString();
    }

    @GetMapping
    public List<String> listUploadedFiles(Model model) throws IOException {

         return storageService.loadAll()
                 .map(this::pathToUri)
                 .toList();
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        Path path = storageService.store(file);

        return ResponseEntity.ok(pathToUri(path));
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageException(StorageException exc) {
        return ResponseEntity.badRequest().body(exc.getMessage());
    }
}
