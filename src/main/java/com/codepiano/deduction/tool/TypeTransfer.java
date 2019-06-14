package com.codepiano.deduction.tool;

import com.codepiano.deduction.constant.GoLangTypes;
import com.codepiano.deduction.exception.AlcedoException;
import com.codepiano.deduction.exception.ErrorCode;
import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TypeDescription;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author codepiano
 */
@Slf4j
@Component
public class TypeTransfer {

    /**
     * postgresql 数组类型格式为 _{type}，eg: bigint 数组的类型为 _bigint
     */
    private static final char POSTGRESQL_ARRAY_TYPE_NAME_FIRST_CHAR = '_';
    /**
     * golang 数组类型声明
     */
    private static final String GOLANG_ARRAY_TYPE_DECLARE = "[]";
    /**
     * 类型关系
     */
    private volatile Map<String, JDBCType> typeRelation;
    /**
     * 存放类型转换的函数
     */
    private Map<Integer, Function<ColumnDescription, String>> typeTranslation = new HashMap<>();
    /**
     * 存放 golang 类型和 import 的关系
     */
    private Map<String, String> golangTypeImport = new HashMap<>();
    /**
     * 存放类型和 pq 数组包装类的对应关系
     */
    private Map<String, String> golangTypeTopqTypeRelation = new HashMap<>();

    @Autowired
    private DatabaseMetaData databaseMetaData;


    @PostConstruct
    private void init() {
        // 初始化类型转换关系
        typeTranslation.put(Types.BIT, columnDescription -> GoLangTypes.BOOL);
        typeTranslation.put(Types.TINYINT, columnDescription -> GoLangTypes.INT);
        typeTranslation.put(Types.SMALLINT, columnDescription -> GoLangTypes.INT);
        typeTranslation.put(Types.INTEGER, columnDescription -> GoLangTypes.INT);
        typeTranslation.put(Types.BIGINT, columnDescription -> GoLangTypes.INT64);
        typeTranslation.put(Types.FLOAT, columnDescription -> GoLangTypes.FLOAT64);
        typeTranslation.put(Types.REAL, columnDescription -> GoLangTypes.FLOAT64);
        typeTranslation.put(Types.DOUBLE, columnDescription -> GoLangTypes.FLOAT64);
        typeTranslation.put(Types.NUMERIC, columnDescription -> GoLangTypes.FLOAT64);
        typeTranslation.put(Types.DECIMAL, columnDescription -> GoLangTypes.FLOAT64);
        typeTranslation.put(Types.CHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.VARCHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.LONGVARCHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.DATE, columnDescription -> GoLangTypes.TIME);
        typeTranslation.put(Types.TIME, columnDescription -> GoLangTypes.TIME);
        typeTranslation.put(Types.TIMESTAMP, columnDescription -> GoLangTypes.TIME);
        typeTranslation.put(Types.BINARY, columnDescription -> GoLangTypes.BYTES);
        typeTranslation.put(Types.VARBINARY, columnDescription -> GoLangTypes.BYTES);
        typeTranslation.put(Types.LONGVARBINARY, columnDescription -> GoLangTypes.BYTES);
        typeTranslation.put(Types.NULL, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.NULL);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.OTHER, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.OTHER);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.JAVA_OBJECT, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.JAVA_OBJECT);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.DISTINCT, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.DISTINCT);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.STRUCT, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.STRUCT);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.ARRAY, columnDescription -> {
            // 返回数组元素的类型
            var typeName = columnDescription.getTypeName();
            if (typeName.charAt(0) == POSTGRESQL_ARRAY_TYPE_NAME_FIRST_CHAR) {
                return typeName.substring(1);
            } else {
                log.error("array type not start with {}", POSTGRESQL_ARRAY_TYPE_NAME_FIRST_CHAR);
                throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
            }
        });
        typeTranslation.put(Types.BLOB, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.CLOB, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.REF, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.REF);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.DATALINK, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.DATALINK);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.BOOLEAN, columnDescription -> GoLangTypes.BOOL);
        typeTranslation.put(Types.ROWID, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.ROWID);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.NCHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.NVARCHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.LONGNVARCHAR, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.NCLOB, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.SQLXML, columnDescription -> GoLangTypes.STRING);
        typeTranslation.put(Types.REF_CURSOR, columnDescription -> {
            log.error("unsupport type java_object: {}", Types.REF_CURSOR);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        });
        typeTranslation.put(Types.TIME_WITH_TIMEZONE, columnDescription -> GoLangTypes.TIME);
        typeTranslation.put(Types.TIMESTAMP_WITH_TIMEZONE, columnDescription -> GoLangTypes.TIME);
        // 初始化数据库类型和 jdbc 类型的对应关系
        this.initRelationMap();
        // 初始化 golang 类型和 import 的对应关系
        golangTypeImport.put("time.Time", "time");
        golangTypeImport.put(GOLANG_ARRAY_TYPE_DECLARE, "github.com/lib/pq");
        // 初始化 golang 类型和 pq array 包装类的关系
        golangTypeTopqTypeRelation.put("int", "pq.Int64Array");
        golangTypeTopqTypeRelation.put("int64", "pq.Int64Array");
        golangTypeTopqTypeRelation.put("string", "pq.StringArray");
        golangTypeTopqTypeRelation.put("float64", "pq.Float64Array");
        golangTypeTopqTypeRelation.put("bool", "pq.BoolArray");
        golangTypeTopqTypeRelation.put("[]char", "pq.ByteaArray");
    }

    /**
     * 数据库类型和 jdbc 类型的对应关系
     */
    private void initRelationMap() {
        // 用临时数组，防止初始化过程中被返回
        Map<String, JDBCType> tempMap = new HashMap<>();
        getAllTypesInfoDatabaseProvide().forEach(typeDescription -> tempMap.put(typeDescription.getTypeName(), JDBCType.valueOf(typeDescription.getDataType())));
        // 最后赋值单例变量，防止初始化一半的 map 被返回
        typeRelation = tempMap;
    }


    /**
     * 获取数据库提供的所有类型
     *
     * @return
     */
    private List<TypeDescription> getAllTypesInfoDatabaseProvide() {
        final List<TypeDescription> result = new ArrayList<>();
        BeanPropertyRowMapper<TypeDescription> rowMapper = BeanPropertyRowMapper.newInstance(TypeDescription.class);
        try {
            ResultSet rs = databaseMetaData.getTypeInfo();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs, rs.getRow()));
            }
        } catch (Exception e) {
            log.error("get types info of database error!", e);
            throw new AlcedoException(ErrorCode.GET_DB_METADATA_ERROR);
        }
        return result;
    }

    /**
     * 转换数据库类型到 golang 类型
     *
     * @param columnDescription
     * @return
     */
    public String transferToGoLangType(ColumnDescription columnDescription) {
        var dataType = columnDescription.getDataType();
        String result = getFunctor(dataType).apply(columnDescription);
        // postgresql 的数组类型为 _{type}，需要进行二次转换
        if (dataType == Types.ARRAY) {
            // 通过类型名称构造 jdbc type
            JDBCType jdbcType = typeRelation.get(result);
            if (jdbcType == null) {
                throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
            }
            var functor = getFunctor(jdbcType.getVendorTypeNumber());
            return golangTypeTopqTypeRelation.get(functor.apply(columnDescription));
        } else {
            return result;
        }
    }

    private Function<ColumnDescription, String> getFunctor(Integer sqlTypeCode) {
        var functor = typeTranslation.get(sqlTypeCode);
        if (functor == null) {
            log.error("sql type {} do not have correspond transfer functor!", sqlTypeCode);
            throw new AlcedoException(ErrorCode.TYPE_NOT_SUPPORT_ERROR);
        }
        return functor;
    }

    public List<String> getImportTypes(List<ColumnDescription> columns) {
        return columns.stream()
                .map(this::transferToGoLangType)
                .map(type -> {
                    // 需要额外引用的非基本类型
                    if (golangTypeImport.containsKey(type)) {
                        return golangTypeImport.get(type);
                    }
                    // 需要额外处理的数组类型
                    if (type.endsWith("Array")) {
                        return golangTypeImport.get(GOLANG_ARRAY_TYPE_DECLARE);
                    }
                    return "";
                })
                .filter(StringUtils::isNoneEmpty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
