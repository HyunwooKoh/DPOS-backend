package com.autohrsystem.structure;

import java.util.Map;

public interface OcrSubTaskArguments {
    void update(ArgumentValues values);
    Map<String, Object> getValueMap();
    OcrSubTaskArguments set(String key, Object value);
    default String get(String key) {
        return get(key, String.class);
    }
    default String get(String key, String defaultValue) {
        return get(key, defaultValue, String.class);
    }
    <T> T get(String key, Class<T> type);
    <T> T get(String key, T defaultValue, Class<T> type);

    String getType();
}
