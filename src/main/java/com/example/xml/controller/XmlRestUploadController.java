package com.example.xml.controller;

import com.example.xml.config.InvalidXMLException;
import com.example.xml.dto.XmlData;
import com.example.xml.service.XMLValidationService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/xml")
@Log4j2
public class XmlRestUploadController {

    @Autowired
    private XMLValidationService xmlValidationService;

    @PostMapping(path = "/upload", consumes = "application/xml")
    public ResponseEntity<?> upload(HttpServletRequest request) throws IOException {

        if (request.getContentLengthLong() > 10_000_000L) {
            throw new InvalidXMLException("File size is to big");
        }

        String contentDisposition = request.getHeader("Content-Disposition");
        String fileName = contentDisposition.replace("attachment; filename=", "");
        fileName = fileName.replace("\"", "");

        log.info("FILE: {}", fileName);

        Path destinationFile = Paths.get("./files/data/" + fileName);

        Files.copy(request.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/epaper", consumes = "application/xml")
    public ResponseEntity<?> uploadAndValidate(HttpServletRequest request) throws IOException {

//        xmlValidationService.validateXMLSchema("./files/schema/request.xsd", request.getInputStream());

        if (request.getContentLengthLong() > 10_000_000L) {
            throw new InvalidXMLException("File size is to big");
        }

        byte[] file = request.getInputStream().readAllBytes();

        xmlValidationService.validateXMLSchema("./files/schema/request.xsd", new ByteArrayInputStream(file));

        //

        XmlMapper mapper = new XmlMapper();

        XmlData xml = mapper.readValue(new ByteArrayInputStream(file), XmlData.class);

        log.info("XML: {}", xml);

        var screenInfo = xml.getDeviceInfo().getScreenInfo();
        log.info("WIDTH: {}; HEIGHT: {}; DPI: {}", screenInfo.getWidth(), screenInfo.getHeight(), screenInfo.getDpi());

        log.info("NEWSPAPER NAME: {}", xml.getDeviceInfo().getAppInfo().getNewspaperName());

        //

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(InvalidXMLException.class)
    public ResponseEntity<?> handleStorageException(InvalidXMLException exc) {
        return ResponseEntity.badRequest().body("XML validation error: " + exc.getMessage());
    }
}
