package com.codepiano.deduction.tool;

import com.codepiano.deduction.models.IndexDescription;
import com.codepiano.deduction.models.TableDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static String transferToName(List<IndexDescription> indexes) {
        return indexes.stream().
                map(IndexDescription::getColumnName).
                map(NameTransfer::transferToCamelCase).
                collect(Collectors.joining("And"));
    }

    public static String transferToComment(List<IndexDescription> indexes) {
        return indexes.stream().
                map(IndexDescription::getColumnName).
                collect(Collectors.joining(" 和 "));
    }

    public static String transferToQuery(List<IndexDescription> indexes) {
        return indexes.stream().
                map(IndexDescription::getColumnName).
                map(x -> x + " = ?").
                collect(Collectors.joining(" and "));
    }

    public static String transferToParam(List<IndexDescription> indexes) {
        return indexes.stream().
                map(IndexDescription::getColumnName).
                map(NameTransfer::transferToVariableName).
                collect(Collectors.joining(" , "));
    }

    public static String transferToFunctionParam(TableDescription table, List<IndexDescription> indexes, TypeTransfer typeTransfer) {
        return indexes.stream().
                map(indexDescription -> {
                    var name = transferToVariableName(indexDescription.getColumnName());
                    var columnDescription = table.getColumnsMap().get(name);
                    var type = typeTransfer.transferToGoLangType(columnDescription);
                    return name + " " + type;
                }).
                collect(Collectors.joining(", "));
    }

}
