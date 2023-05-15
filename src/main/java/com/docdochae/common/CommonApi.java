package com.docdochae.common;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class CommonApi {
    @Autowired
    private static Environment env;

    public static String CreateAndGetTempDir(String uuid) {
        String tempDirPath = normalizePath(System.getenv("HOME") + "/" + uuid + "/");
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
        return normalizePath(System.getenv("HOME") + "/" + uuid + "/");
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String getDirectoryOfFile(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf('/'));
    }

    public static String normalizePath(String path) {
        return path.replaceAll("\\\\", "/").replace("//", "/");
    }
}
