package com.codepiano.deduction.service;

import com.codepiano.deduction.exception.AlcedoException;
import com.codepiano.deduction.exception.ErrorCode;
import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.IndexDescription;
import com.codepiano.deduction.models.PrimaryKeyDescription;
import com.codepiano.deduction.models.TableDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author codepiano
 */
@Service
@Slf4j
public class TableService {

    @Autowired
    private DatabaseMetaData databaseMetaData;

    /**
     * 获取 catalog 中的所有 TABLE 类型的表
     *
     * @param catalog
     * @return
     */
    public List<TableDescription> getAllTablesInCatalog(String catalog) {

        final List<TableDescription> result = new ArrayList<>();
        BeanPropertyRowMapper<TableDescription> rowMapper = BeanPropertyRowMapper.newInstance(TableDescription.class);
        try {
            ResultSet rs = databaseMetaData.getTables(catalog, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                TableDescription tableDescription = rowMapper.mapRow(rs, rs.getRow());
                if (tableDescription == null) {
                    log.error("get table description error! table description is null!");
                    throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
                }
                tableDescription.setPrimaryKeys(getPrimaryKeys(catalog, tableDescription.getTableName()));
                tableDescription.setIndexes(getIndex(catalog, tableDescription.getTableName()));
                result.add(tableDescription);
            }
        } catch (Exception e) {
            log.error("get tables of catalog: {} error!", catalog, e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return result;
    }

    /**
     * 获取 table 的主键
     *
     * @param catalog
     * @param tableName
     * @return
     */
    private Map<String, PrimaryKeyDescription> getPrimaryKeys(String catalog, String tableName) {
        final Map<String, PrimaryKeyDescription> result = new HashMap<>();
        BeanPropertyRowMapper<PrimaryKeyDescription> rowMapper = BeanPropertyRowMapper.newInstance(PrimaryKeyDescription.class);
        try {
            ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, null, tableName);
            while (rs.next()) {
                PrimaryKeyDescription primaryKeyDescription = rowMapper.mapRow(rs, rs.getRow());
                // 有可能没有主键
                if (primaryKeyDescription != null) {
                    result.put(primaryKeyDescription.getColumnName(), primaryKeyDescription);
                }
            }
        } catch (Exception e) {
            log.error("get tables of catalog: {} error!", catalog, e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return result;
    }

    /**
     * 获取 table 的索引
     *
     * @param catalog
     * @param tableName
     * @return
     */
    private Map<String, List<IndexDescription>> getIndex(String catalog, String tableName) {
        final Map<String, List<IndexDescription>> result = new HashMap<>();
        BeanPropertyRowMapper<IndexDescription> rowMapper = BeanPropertyRowMapper.newInstance(IndexDescription.class);
        try {
            ResultSet rs = databaseMetaData.getIndexInfo(catalog, null, tableName, false, true);
            while (rs.next()) {
                IndexDescription indexDescription = rowMapper.mapRow(rs, rs.getRow());
                // 有可能没有主键
                if (indexDescription != null) {
                    if (result.containsKey(indexDescription.getIndexName())) {
                        result.get(indexDescription.getIndexName()).add(indexDescription);
                    } else {
                        List<IndexDescription> index = new ArrayList<>();
                        index.add(indexDescription);
                        result.put(indexDescription.getIndexName(), index);
                    }
                }
            }
            // 按索引中的序号排序
            result.values().forEach(indexes -> indexes.sort(Comparator.comparingInt(IndexDescription::getOrdinalPosition)));
        } catch (Exception e) {
            log.error("get tables of catalog: {} error!", catalog, e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return result;
    }
}
