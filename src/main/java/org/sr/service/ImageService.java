package org.sr.service;

import org.sr.service.SuperResolution;
import org.sr.service.SuperResolutionTranslator;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

@Service
public class ImageService {

    private final Path storagePath = Paths.get("build/output/super-res/"); // Define your storage path

    public ImageService() throws IOException {
        // Ensure the storage directory exists
        Files.createDirectories(storagePath);
    }

    public byte[] enhanceImage(MultipartFile file) throws IOException, ModelException, TranslateException {
        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        Image djlImage = ImageFactory.getInstance().fromImage(inputImage);

        Criteria<Image, Image> criteria = Criteria.builder()
                .setTypes(Image.class, Image.class)
                .optModelUrls("https://storage.googleapis.com/tfhub-modules/captain-pool/esrgan-tf2/1.tar.gz")
                .optTranslator(new SuperResolutionTranslator())
                .build();

        try (ZooModel<Image, Image> model = criteria.loadModel();
             Predictor<Image, Image> enhancer = model.newPredictor()) {
            Image enhancedImage = enhancer.predict(djlImage);
            BufferedImage enhancedBufferedImage = (BufferedImage) enhancedImage.getWrappedImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(enhancedBufferedImage, "png", baos);
            baos.flush();
            return baos.toByteArray();
        }
    }

    public byte[] getImageByName(String imageName) throws IOException {
        Path imagePath = storagePath.resolve(imageName);

        if (!Files.exists(imagePath)) {
            throw new IOException("Image not found with name: " + imageName);
        }

        return Files.readAllBytes(imagePath);
    }

    // Utility method to save images
    public void saveEnhancedImage(byte[] imageData, String imageName) throws IOException {
        Path outputPath = storagePath.resolve(imageName);
        Files.write(outputPath, imageData);
    }
}
