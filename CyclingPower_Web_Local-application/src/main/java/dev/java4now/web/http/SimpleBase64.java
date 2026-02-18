package dev.java4now.web.http;

public class SimpleBase64 {
    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static String encode(String input) {
        if (input == null) return null;
        byte[] bytes = input.getBytes();
        StringBuilder encoded = new StringBuilder();
        int buffer = 0;
        int bufferBits = 0;

        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 0xFF);
            bufferBits += 8;
            while (bufferBits >= 6) {
                bufferBits -= 6;
                encoded.append(BASE64_CHARS.charAt((buffer >>> bufferBits) & 0x3F));
            }
        }

        // Handle remaining bits
        if (bufferBits > 0) {
            buffer <<= (6 - bufferBits); // Align remaining bits
            encoded.append(BASE64_CHARS.charAt(buffer & 0x3F));
            // Add padding
            int paddingCount = (3 - (bytes.length % 3)) % 3;
            for (int i = 0; i < paddingCount; i++) {
                encoded.append('=');
            }
        }

        return encoded.toString();
    }
}