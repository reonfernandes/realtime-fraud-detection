package com.reon.titan_backend.common;

import java.util.UUID;

public class UniqueIdGenerator {
    private UniqueIdGenerator(){}

    public static String uniqueIdGenerator() {
        return UUID.randomUUID().toString();
    }
}
