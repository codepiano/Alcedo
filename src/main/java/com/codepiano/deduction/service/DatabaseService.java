package com.codepiano.deduction.service;

import com.codepiano.deduction.exception.AlcedoException;
import com.codepiano.deduction.exception.ErrorCode;
import com.codepiano.deduction.models.DatabaseDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author codepiano
 */
@Service
@Slf4j
public class DatabaseService {

    @Autowired
    private DatabaseMetaData databaseMetaData;

    public DatabaseDescription getDatabaseDescription() {
        DatabaseDescription databaseDescription = new DatabaseDescription();
        try {
            databaseDescription.setDatabaseProductName(databaseMetaData.getDatabaseProductName());
            databaseDescription.setDatabaseProductVersion(databaseMetaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("get database info error!", e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return databaseDescription;
    }
}
