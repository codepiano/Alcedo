package com.codepiano.deduction.tool;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class NameTransfer {

    public static String transferToCamelCase(String name) {
        return Arrays.stream(name.split("_")).reduce("", (x, y) -> {
            if (!StringUtils.isEmpty(y)) {
                return x + Character.toUpperCase(y.charAt(0)) + y.substring(1);
            }
            return x;
        });
    }
}
