package com.acare.backend.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateKey {
    public static void main(String... args) throws Exception {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        Path path = Paths.get("jwt_secret.txt");
        Files.writeString(path, base64Key);
    }
}

