package com.codepiano.deduction.database;

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

@Service
@Slf4j
public class TableService {

    @Autowired
    private DataSource dataSource;

    public List<TableDescription> getAllTablesInCatalog(String catalog) {
        final List<TableDescription> result = new ArrayList<>();
        BeanPropertyRowMapper<TableDescription> rowMapper = BeanPropertyRowMapper.newInstance(TableDescription.class);
        try {
            JdbcUtils.extractDatabaseMetaData(dataSource, dbmd -> {
                ResultSet rs = dbmd.getTables(catalog, null, null, new String[]{"TABLE"});
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs, rs.getRow()));
                }
                return result;
            });
        } catch (MetaDataAccessException e) {
            log.error("get tables of catalog: %s error!", catalog, e);
        }
        return result;
    }
}
