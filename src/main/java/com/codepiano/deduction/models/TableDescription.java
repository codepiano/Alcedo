package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class TableDescription {

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
     * table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     */
    private String tableType;
    /**
     * explanatory comment on the table (may be null)
     */
    private String remarks;
    /**
     * the types catalog (may be null)
     */
    private String typeCat;
    /**
     * the types schema (may be null)
     */
    private String typeSchem;
    /**
     * type name (may be null)
     */
    private String typeName;
    /**
     * name of the designated "identifier" column of a typed table (may be null)
     */
    private String selfReferencingColName;
    /**
     * specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
     */
    private String refGeneration;
    /**
     * primary key descriptions
     */
    private Map<String, PrimaryKeyDescription> primaryKeys;
    /**
     * index descriptions
     */
    private Map<String, List<IndexDescription>> indexes;
    /**
     * columns
     */
    private List<ColumnDescription> columns;
}
