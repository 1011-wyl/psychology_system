package com.cykj.psychology.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class PasswordUtils {
    private static final String TEMPORARY_PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        if (isBcrypt(encodedPassword)) {
            return encoder.matches(rawPassword, encodedPassword);
        }
        return MessageDigest.isEqual(
                legacyMd5(rawPassword).getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    public boolean needsUpgrade(String encodedPassword) {
        return !isBcrypt(encodedPassword);
    }

    public String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(12);
        for (int index = 0; index < 12; index++) {
            password.append(TEMPORARY_PASSWORD_CHARS.charAt(
                    secureRandom.nextInt(TEMPORARY_PASSWORD_CHARS.length())
            ));
        }
        return password.toString();
    }

    private boolean isBcrypt(String encodedPassword) {
        return encodedPassword.startsWith("$2a$")
                || encodedPassword.startsWith("$2b$")
                || encodedPassword.startsWith("$2y$");
    }

    private String legacyMd5(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder value = new StringBuilder();
            for (byte item : bytes) {
                value.append(String.format("%02x", item));
            }
            return value.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 compatibility is unavailable", exception);
        }
    }
}
