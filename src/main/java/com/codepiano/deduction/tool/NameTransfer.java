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

    public static String transferToKebabCase(String name) {
        return name.replaceAll("_", "-");
    }

    public static String transferToVariableName(String name) {
        var camelCaseName = transferToCamelCase(name);
        return Character.toLowerCase(camelCaseName.charAt(0)) + camelCaseName.substring(1);
    }
}
