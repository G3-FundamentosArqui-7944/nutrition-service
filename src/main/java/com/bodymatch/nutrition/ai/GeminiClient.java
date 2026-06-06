package com.bodymatch.nutrition.ai;

public interface GeminiClient {
    String generateText(String model, String prompt);
    String generateFromMultimodal(String model, String prompt, String mimeType, byte[] inlineData);
}
