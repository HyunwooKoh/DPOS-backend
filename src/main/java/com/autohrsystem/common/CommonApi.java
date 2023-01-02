package com.autohrsystem.common;

import java.util.UUID;

public class CommonApi {
    public static String generateTempPath() {
        return UUID.randomUUID().toString();
    }

    public static String getTempDir() {
        return "";
    }
}
