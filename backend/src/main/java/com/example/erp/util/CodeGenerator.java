package com.example.erp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CodeGenerator {
    public static String generateCode(String prefix) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + time + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
