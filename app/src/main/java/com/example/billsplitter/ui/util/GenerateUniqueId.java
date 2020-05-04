package com.example.billsplitter.ui.util;

import java.util.UUID;

public class GenerateUniqueId {
    public static String getUniqueId() {
        return UUID.randomUUID().toString();
    }
}