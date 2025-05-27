package udemy.clone.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import udemy.clone.exception.UploadImageException;
import udemy.clone.properties.MinioProperties;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile image) {
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            throw new UploadImageException("Failed to load image, image must not be empty.");
        }
        String fileExtension = image.getOriginalFilename()
                .substring(
                        image.getOriginalFilename().lastIndexOf('.') + 1
                );
        String fileName = UUID.randomUUID() + "." + fileExtension;
        try {
            InputStream binaryImage = image.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(binaryImage, binaryImage.available(), -1)
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
}
