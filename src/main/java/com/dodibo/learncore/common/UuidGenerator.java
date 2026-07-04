package com.dodibo.learncore.common;

import java.util.UUID;

public class UuidGenerator {

    public static String generate(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}