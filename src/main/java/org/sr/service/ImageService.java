package org.sr.service;

import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImageService {

    // The method to handle the image enhancement logic
    public byte[] enhanceImage(MultipartFile file) throws IOException, TranslateException, ModelException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Convert MultipartFile to DJL's Image
        Image image = ImageFactory.getInstance().fromInputStream(file.getInputStream());

        // Call SuperResolution to enhance the image
        List<Image> enhancedImages = SuperResolution.enhance(List.of(image));

        // Assuming we always get one image back and it's the first
        Image enhanced = enhancedImages.get(0);

        // Convert enhanced Image to byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            enhanced.save(baos, "png");
            return baos.toByteArray();
        }
    }

    // This method should be implemented based on how you're storing and retrieving the images
    public byte[] getImageByName(String imageName) {
        // Placeholder for actual implementation
        throw new UnsupportedOperationException("getImageByName not implemented yet.");
    }

    // Other service methods...
}
