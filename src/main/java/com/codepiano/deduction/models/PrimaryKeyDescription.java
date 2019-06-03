package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class PrimaryKeyDescription {

    /**
     * table catalog (may be null)
     */
    private String tableCat;
    /**
     * table schema (may be null)
     */
    private String tableSchem;
    /**
     * table name
     */
    private String tableName;
    /**
     * column name
     */
    private String columnName;
    /**
     * sequence number within primary key( a value of 1 represents the first column of the primary key, a value of 2 would represent the second column within the primary key).
     */
    private Integer keySeq;
    /**
     * primary key name (may be null)
     */
    private String pkName;
}
