package com.codepiano.deduction.exception;

/**
 * @author codepiano
 */

public enum ErrorCode {

    /**
     * 类型不支持
     */
    TYPE_NOT_SUPPORT_ERROR(100, "sql type not support yet!"),
    /**
     * 获取元信息失败
     */
    GET_DB_METADATA_ERROR(101, "sql type not support yet!");

    private int code;
    private String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {

        return description;
    }
}
