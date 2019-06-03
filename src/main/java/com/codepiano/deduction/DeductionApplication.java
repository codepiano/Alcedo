package com.codepiano.deduction;

import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import com.codepiano.deduction.service.ColumnService;
import com.codepiano.deduction.service.TableService;
import com.codepiano.deduction.template.BeanTemplate;
import com.codepiano.deduction.template.DaoTemplate;
import com.codepiano.deduction.tool.NameTransfer;
import com.codepiano.deduction.tool.TypeTransfer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.util.List;


/**
 * @author codepiano
 */
@SpringBootApplication
@Slf4j
public class DeductionApplication {

    @Value("${output.dir.base}")
    private String basePath;

    @Value("${output.dir.bean}")
    private String modelPath;

    @Value("${output.package.base}")
    private String packageBase;

    @Autowired
    private TableService tableService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TypeTransfer typeTransfer;

    public static void main(String[] args) {
        SpringApplication.run(DeductionApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var modelDir = basePath + modelPath;
            FileUtils.forceMkdir(new File(modelDir));
            List<TableDescription> tables = tableService.getAllTablesInCatalog("test");
            tables.forEach(tableDescription -> {
                // 生成 bean 代码
                List<ColumnDescription> columns = columnService.getAllColumnsInfoFromTable(tableDescription);
                String model = BeanTemplate.template(tableDescription, columns, typeTransfer)
                        .render()
                        .toString();
                System.out.println(model);
                File modelFile = new File(modelDir + tableDescription.getTableName() + ".go");
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(modelFile))) {
                    IOUtils.write(model, bos, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    log.error("file {} not found!", modelFile.getAbsolutePath());
                }
                // 生成 model 层代码
                String dao = DaoTemplate.template(packageBase + modelPath, NameTransfer.transferToCamelCase(tableDescription.getTableName()), typeTransfer)
                        .render()
                        .toString();
                System.out.println(dao);
            });
        };
    }

    @Bean
    public DatabaseMetaData databaseMetaData(DataSource dataSource) throws MetaDataAccessException {
        return (DatabaseMetaData) JdbcUtils.extractDatabaseMetaData(dataSource, databaseMetaData -> databaseMetaData);
    }
}
