package com.autohrsystem.common;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class CommonApi {
    @Autowired
    private static Environment env;

    public static String getTempDir(String uuid) {
        String tempDir = env.getProperty("HOME").toString() + uuid + "/";
        return tempDir;
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
