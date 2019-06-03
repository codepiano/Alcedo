package com.codepiano.deduction.service;

import com.codepiano.deduction.exception.AlcedoException;
import com.codepiano.deduction.exception.ErrorCode;
import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import com.codepiano.deduction.tool.TypeTransfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
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
    private DatabaseMetaData databaseMetaData;

    @Autowired
    private TypeTransfer transfer;

    /**
     * 获取 table 中的所有列信息
     *
     * @param tableDescription
     * @return
     */
    public List<ColumnDescription> getAllColumnsInfoFromTable(TableDescription tableDescription) {
        final List<ColumnDescription> result = new ArrayList<>();
        BeanPropertyRowMapper<ColumnDescription> rowMapper = BeanPropertyRowMapper.newInstance(ColumnDescription.class);
        try {
            ResultSet rs = databaseMetaData.getColumns(tableDescription.getTableCat(), null, tableDescription.getTableName(), null);
            while (rs.next()) {
                ColumnDescription columnDescription = rowMapper.mapRow(rs, rs.getRow());
                if (columnDescription == null) {
                    throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
                }
                // 判断是否是主键，如果是，填充主键信息
                var primaryKeyInfoMap = tableDescription.getPrimaryKeys();
                if (primaryKeyInfoMap != null && !primaryKeyInfoMap.isEmpty()) {
                    var primaryKey = primaryKeyInfoMap.get(columnDescription.getColumnName());
                    if (primaryKey != null) {
                        columnDescription.setPrimaryKey(true);
                        columnDescription.setPrimaryKeyName(primaryKey.getPkName());
                        columnDescription.setPrimaryKeyIndex(primaryKey.getKeySeq());
                    } else {
                        columnDescription.setPrimaryKey(false);
                    }
                }
                result.add(columnDescription);
            }
        } catch (Exception e) {
            log.error("get columns from table {} error!", tableDescription.getTableName(), e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return result;
    }

}
