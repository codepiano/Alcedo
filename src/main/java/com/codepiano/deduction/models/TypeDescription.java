package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * see java.sql.DatabaseMetaData#getTypeInfo()
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class TypeDescription {

    /**
     * SQL type from java.sql.Types
     */
    private Integer dataType;
    /**
     * Data source dependent type name, for a UDT the type name is fully qualified
     */
    private String typeName;
    /**
     * maximum precision
     */
    private Integer precision;
    /**
     * prefix used to quote a literal (may be null)
     */
    private String literalPrefix;
    /**
     * suffix used to quote a literal (may be null)
     */
    private String literalSuffix;
    /**
     * parameters used in creating the type (may be null)
     */
    private String createParams;
    /**
     * can you use NULL for this type.
     * <p>
     * 1. typeNoNulls - does not allow NULL values
     * 2. typeNullable - allows NULL values
     * 3. typeNullableUnknown - nullability unknown
     */
    private Integer nullable;
    /**
     * is it case sensitive.
     */
    private Boolean caseSensitive;
    /**
     * can you use "WHERE" based on this type:
     * <p>
     * 1. typePredNone - No support
     * 2. typePredChar - Only supported with WHERE .. LIKE
     * 3. typePredBasic - Supported except for WHERE .. LIKE
     * 4. typeSearchable - Supported for all WHERE ..
     */
    private Integer searchable;
    /**
     * is it unsigned.
     */
    private Boolean unsignedAttribute;
    /**
     * can it be a money value.
     */
    private Boolean fixedPrecScale;
    /**
     * can it be used for an auto-increment value.
     */
    private Boolean autoIncrement;
    /**
     * localized version of type name (may be null)
     */
    private String LOCAL_TYPE_NAME;
    /**
     * minimum scale supported
     */
    private Integer MINIMUM_SCALE;
    /**
     * maximum scale supported
     */
    private Integer maximumScale;
    /**
     * unused
     */
    private Integer sqlDataType;
    /**
     * unused
     */
    private Integer SQL_DATETIME_SUB;
    /**
     * usually 2 or 10
     */
    private Integer numPrecRadix;
}
