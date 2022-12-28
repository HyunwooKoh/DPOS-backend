package com.autohrsystem.common.api;

import java.util.UUID;

public class CommonApi {
    public static String generateTempPath() {
        return UUID.randomUUID().toString();
    }
}
