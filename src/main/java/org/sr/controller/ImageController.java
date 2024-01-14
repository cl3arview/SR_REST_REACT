package org.sr.controller;

import org.sr.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @Operation(summary = "Upload an image for super-resolution processing")
    @ApiResponse(responseCode = "200", description = "Image processed successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @ApiResponse(responseCode = "400", description = "Fatal error")
    @PostMapping(
            value = "/upload",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { MediaType.IMAGE_PNG_VALUE }
    )
    public ResponseEntity<?> uploadImage(
            @RequestPart("image") MultipartFile file
    ) {
        try {
            byte[] enhancedImage = imageService.enhanceImage(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(enhancedImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during image processing: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch an image by name after processing")
    @ApiResponse(responseCode = "200", description = "Image found and returned successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    @GetMapping(value = "/{imageName}", produces = { MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<?> getImage(@PathVariable String imageName) {
        try {
            byte[] image = imageService.getImageByName(imageName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(image);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found: " + e.getMessage());
        }
    }
}
