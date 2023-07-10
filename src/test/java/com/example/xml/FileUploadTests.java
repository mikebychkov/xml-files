package com.example.xml;

import com.example.xml.config.StorageFileNotFoundException;
import com.example.xml.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
public class FileUploadTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
    public void shouldListAllFiles() throws Exception {

        given(this.storageService.loadAll())
                .willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")));

        this.mvc.perform(get("/files"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\"http://localhost/files/first.txt\", \"http://localhost/files/second.txt\"]"));
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        when(this.storageService.store(multipartFile))
                .thenReturn(Paths.get("test.txt"));

        this.mvc.perform(multipart("/files").file(multipartFile))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/files/test.txt"));
    }

    @Test
    public void should404WhenMissingFile() throws Exception {

        given(this.storageService.loadAsResource("test.txt"))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/files/test.txt"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldOK() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        when(this.storageService.loadAsResource("test.txt"))
            .thenReturn(multipartFile.getResource());

        this.mvc.perform(get("/files/test.txt"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}