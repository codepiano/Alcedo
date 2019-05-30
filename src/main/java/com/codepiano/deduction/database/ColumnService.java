package com.codepiano.deduction.database;

import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author codepiano
 */
@Service
@Slf4j
public class ColumnService {

    @Autowired
    private DataSource dataSource;

    public List<ColumnDescription> getAllColumnsInfoFromTable(TableDescription tableDescription) {
        final List<ColumnDescription> result = new ArrayList<>();
        BeanPropertyRowMapper<ColumnDescription> rowMapper = BeanPropertyRowMapper.newInstance(ColumnDescription.class);
        try {
            JdbcUtils.extractDatabaseMetaData(dataSource, dbmd -> {
                ResultSet rs = dbmd.getColumns(tableDescription.getTableCat(), null, tableDescription.getTableName(), null);
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs, rs.getRow()));
                }
                return result;
            });
        } catch (MetaDataAccessException e) {
            log.error("get columns from table %s error: %s!", tableDescription.getTableName(), e);
        }
        return result;
    }
}
