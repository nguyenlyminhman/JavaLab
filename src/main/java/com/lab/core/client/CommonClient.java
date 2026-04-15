package com.lab.core.client;

import java.util.Map;
import java.util.Optional;

public class CommonClient {
    public static void addParamIfValid(Map<String, Object> paramMap, String key, String value) {
        Optional.ofNullable(value)
                .filter(s -> !s.isBlank())
                .ifPresent(v -> paramMap.put(key, v));
    }

    private void addParamAllowEmpty(Map<String, String> map, String key, String value) {
        Optional.ofNullable(value)
                .ifPresent(v -> map.put(key, v));
        // Không dùng .filter() vì ta chấp nhận cả chuỗi ""
    }

    private void addParamAllowNull(Map<String, String> map, String key, String value) {
        // Không dùng Optional vì Optional.ofNullable().ifPresent() sẽ bỏ qua null
        map.put(key, value);
    }

    private void addParamWithDefault(Map<String, String> map, String key, String value, String defaultValue) {
        String finalValue = Optional.ofNullable(value)
                .filter(s -> !s.isBlank())
                .orElse(defaultValue); // Nếu null/rỗng thì lấy defaultValue

        map.put(key, finalValue);
    }
}
