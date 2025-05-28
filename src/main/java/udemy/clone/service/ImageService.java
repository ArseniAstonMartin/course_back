package udemy.clone.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.exception.UploadImageException;
import udemy.clone.properties.MinioProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private static final int SQUARE_SIZE = 680;
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile image) {
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            throw new UploadImageException("Failed to load image, image must not be empty.");
        }
        String fileExtension = image.getOriginalFilename()
                .substring(
                        image.getOriginalFilename().lastIndexOf('.') + 1
                ).toLowerCase();
        String fileName = UUID.randomUUID() + "." + fileExtension;
        try {
            InputStream uploadStream;
            if (IMAGE_EXTENSIONS.contains(fileExtension)) {
                log.info("Uploading image file: {}", image.getOriginalFilename());
                uploadStream = resizeImage(image.getInputStream(), fileExtension);
            } else {
                log.info("Uploading non-image file (likely video): {}", image.getOriginalFilename());
                uploadStream = image.getInputStream();
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(uploadStream, uploadStream.available(), -1)
                            .bucket(minioProperties.getBucket())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Exception when uploading image {}", e.getMessage());
            throw new UploadImageException("Failed to load image, exception putting to file storage . " + e.getMessage());
        }
        return fileName;
    }

    public void deleteImage(String filename) {
        try {
            minioClient.removeObject(
                io.minio.RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(filename)
                    .build()
            );
        } catch (Exception e) {
            throw new UploadImageException("Failed to delete image, exception removing from file storage. " + e.getMessage());
        }
    }

    private InputStream resizeImage(InputStream originalImage, String fileExtension) throws IOException {
        BufferedImage image = ImageIO.read(originalImage);

        int width = image.getWidth();
        int height = image.getHeight();

        int squareSize = Math.min(width, height);
        int leftBorder = (width - squareSize) / 2;
        int bottomBorder = (height - squareSize) / 2;
        BufferedImage croppedImage = image.getSubimage(leftBorder, bottomBorder, squareSize, squareSize);

        BufferedImage resizedImage = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(croppedImage, 0, 0, SQUARE_SIZE, SQUARE_SIZE, null);
        g.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, fileExtension, os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
