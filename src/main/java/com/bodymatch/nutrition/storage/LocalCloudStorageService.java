package com.bodymatch.nutrition.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class LocalCloudStorageService implements CloudStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCloudStorageService.class);

    @Value("${cloud-storage.local-path:./storage}")
    private String localPath;

    @Value("${cloud-storage.base-url:http://localhost:8084/storage}")
    private String baseUrl;

    @Value("${cloud-storage.bucket:bodymatch-nutrition}")
    private String bucket;

    @Override
    public StoredObject upload(String folder, String originalFilename, String contentType, byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Content must not be empty");
        }
        var safeName = sanitize(originalFilename != null ? originalFilename : "file.bin");
        var key = String.format("%s/%s/%s-%s", bucket, folder, UUID.randomUUID(), safeName);
        try {
            var fullPath = Paths.get(localPath, key);
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, content,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            var url = String.format("%s/%s", baseUrl, key);
            LOGGER.info("Stored object {} ({} bytes) at {}", key, content.length, fullPath);
            return new StoredObject(key, url, content.length, contentType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store object: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            var path = Paths.get(localPath, storageKey);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOGGER.warn("Failed to delete {}: {}", storageKey, e.getMessage());
        }
    }

    @Override
    public byte[] download(String storageKey) {
        try {
            return Files.readAllBytes(localPath(storageKey));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download object: " + e.getMessage(), e);
        }
    }

    private Path localPath(String storageKey) {
        return Paths.get(localPath, storageKey);
    }

    private String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
