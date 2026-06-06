package com.bodymatch.nutrition.storage;

public record StoredObject(String storageKey, String url, long sizeBytes, String contentType) {
}
