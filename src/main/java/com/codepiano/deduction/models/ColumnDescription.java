package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class ColumnDescription {


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
     * SQL type from java.sql.Types
     */
    private Integer dataType;
    /**
     * Data source dependent type name, for a UDT the type name is fully qualified
     */
    private String typeName;
    /**
     * column size.
     */
    private Integer columnSize;
    /**
     * not used.
     */
    private Integer bufferLength;
    /**
     * the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
     */
    private Integer decimalDigits;
    /**
     * Radix (typically either 10 or 2)
     */
    private Integer numPrecRadix;
    /**
     * is NULL allowed.
     * <p>
     * 1. columnNoNulls - might not allow NULL values
     * 2. columnNullable - definitely allows NULL values
     * 3. columnNullableUnknown - nullability unknown
     */
    private Integer nullable;
    /**
     * comment describing column (may be null)
     */
    private String remarks;
    /**
     * default value for the column, which should be interpreted as a String  when the value is enclosed in single quotes (may be null)
     */
    private String columnDef;
    /**
     * unused
     */
    private Integer sqlDataType;
    /**
     * unused
     */
    private Integer sqlDatetimeSub;
    /**
     * for char types the maximum number of bytes in the column
     */
    private Integer charOctetLength;
    /**
     * index of column in table (starting at 1)
     */
    private Integer ordinalPosition;
    /**
     * ISO rules are used to determine the nullability for a column.
     * <p>
     * 1. YES --- if the column can include NULLs
     * 2. NO --- if the column cannot include NULLs
     * 3. empty String  --- if the nullability for the column is unknown
     */
    private String isNullable;
    /**
     * catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
     */
    private String scopeCatalog;
    /**
     * schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
     */
    private String scopeSchema;
    /**
     * table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
     */
    private String scopeTable;
    /**
     * source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
     */
    private String sourceDataType;
    /**
     * Indicates whether this column is auto incremented
     * <p>
     * 1. YES ---if the column is auto incremented
     * 2. NO ---if the column is not auto incremented
     * 3. empty String  ---if it cannot be determined whether the column is auto incremented
     */
    private String isAutoincrement;
    /**
     * Indicates whether this is a generated column
     * <p>
     * 1. YES ---if this a generated column
     * 2. NO ---if this not a generated column
     * 3. empty String  ---if it cannot be determined whether this is a generated column
     */
    private String isGeneratedcolumn;

}
