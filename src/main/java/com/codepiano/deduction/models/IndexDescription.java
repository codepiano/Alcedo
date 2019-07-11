package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class IndexDescription {
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
     * Can index values be non-unique. false when TYPE is tableIndexStatistic
     */
    private Boolean nonUnique;
    /**
     * index catalog (may be null); null when TYPE is tableIndexStatistic
     */
    private String indexQualifier;
    /**
     * index name; null when TYPE is tableIndexStatistic
     */
    private String indexName;
    /**
     * index type:
     * <ul>
     * <li> tableIndexStatistic - this identifies table statistics that are returned in conjunction with a table's index descriptions
     * <li> tableIndexClustered - this is a clustered index
     * <li> tableIndexHashed - this is a hashed index
     * <li> tableIndexOther - this is some other style of index
     * </ul>
     */
    private short type;
    /**
     * column sequence number within index; zero when TYPE is tableIndexStatistic
     */
    private short ordinalPosition;
    /**
     * column name; null when TYPE is tableIndexStatistic
     */
    private String columnName;
    /**
     * column sort sequence, "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
     */
    private String ascOrDesc;
    /**
     * When TYPE is tableIndexStatistic, then this is the number of rows in the table; otherwise, it is the number of unique values in the index.
     */
    private long ardinality;
    /**
     * When TYPE is tableIndexStatistic then this is the number of pages used for the table, otherwise it is the number of pages used for the current index.
     */
    private long Ages;
    /**
     * Filter condition, if any. (may be null)
     */
    private String filterCondition;

    public String translateTypeToString(int type) {
        switch (type) {
            case 0:
                return "statistic";
            case 1:
                return "clustered";
            case 2:
                return "hashed";
            case 3:
                return "other";
            default:
                return "unknown";
        }
    }

    public String getAscOrDescString() {
        switch (getAscOrDesc()) {
            case "A":
                return "asc";
            case "D":
                return "desc";
            default:
                return "unknown";
        }
    }
}
