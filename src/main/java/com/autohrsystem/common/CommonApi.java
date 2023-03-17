package com.autohrsystem.common;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class CommonApi {
    @Autowired
    private static Environment env;

    public static String CreateAndGetTempDir(String uuid) {
        String tempDirPath = env.getProperty("HOME").toString() + uuid + "/";
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            try{
                tempDir.mkdir();
            } catch(Exception e){
              e.getStackTrace();
            }
        }
        return tempDirPath;
    }

    public static String getTempDir(String uuid) {
        String tempDir = env.getProperty("HOME").toString() + uuid + "/";
        return tempDir;
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
