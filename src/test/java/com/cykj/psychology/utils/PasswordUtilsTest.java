package com.cykj.psychology.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilsTest {
    private final PasswordUtils passwordUtils = new PasswordUtils();

    @Test
    void encodesAndVerifiesBcryptPasswords() {
        String encoded = passwordUtils.encode("StrongPassword123!");

        assertNotEquals("StrongPassword123!", encoded);
        assertTrue(passwordUtils.matches("StrongPassword123!", encoded));
        assertFalse(passwordUtils.matches("wrong-password", encoded));
        assertFalse(passwordUtils.needsUpgrade(encoded));
    }

    @Test
    void acceptsLegacyMd5OnlyForLoginMigration() {
        String legacyHash = "5f4dcc3b5aa765d61d8327deb882cf99";

        assertTrue(passwordUtils.matches("password", legacyHash));
        assertTrue(passwordUtils.needsUpgrade(legacyHash));
    }

    @Test
    void generatesNonDefaultTemporaryPasswords() {
        String first = passwordUtils.generateTemporaryPassword();
        String second = passwordUtils.generateTemporaryPassword();

        assertTrue(first.length() >= 12);
        assertNotEquals(first, second);
    }
}
