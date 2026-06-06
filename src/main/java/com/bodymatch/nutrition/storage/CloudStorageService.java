package com.bodymatch.nutrition.storage;

public interface CloudStorageService {
    StoredObject upload(String folder, String originalFilename, String contentType, byte[] content);
    void delete(String storageKey);
    byte[] download(String storageKey);
}
