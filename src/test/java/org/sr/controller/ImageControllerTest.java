package org.sr.controller;

import org.springframework.web.multipart.MultipartFile;


import org.sr.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void uploadImage_ShouldReturnEnhancedImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        byte[] enhancedImage = "enhanced image content".getBytes();
        given(imageService.enhanceImage(any(MultipartFile.class))).willReturn(enhancedImage);

        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(enhancedImage));

        verify(imageService, times(1)).enhanceImage(any(MultipartFile.class));
    }

    @Test
    void getImage_ShouldReturnImage() throws Exception {
        String imageName = "test.png";
        byte[] imageContent = "image content".getBytes();
        given(imageService.getImageByName(imageName)).willReturn(imageContent);

        mockMvc.perform(get("/api/images/{imageName}", imageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(imageContent));

        verify(imageService, times(1)).getImageByName(imageName);
    }
}
