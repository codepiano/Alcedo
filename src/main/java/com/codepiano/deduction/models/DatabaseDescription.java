package com.codepiano.deduction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * see:
 * <p>
 * 1.java.sql.DatabaseMetaData#getDatabaseProductName()
 * 2. java.sql.DatabaseMetaData#getDatabaseProductVersion()
 *
 * @author codepiano
 */
@Getter
@Setter
@ToString
public class DatabaseDescription {
    private String databaseProductName;
    private String databaseProductVersion;
}
