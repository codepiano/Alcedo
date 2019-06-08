package com.codepiano.deduction;

import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import com.codepiano.deduction.service.ColumnService;
import com.codepiano.deduction.service.TableService;
import com.codepiano.deduction.template.BaseControllerTemplate;
import com.codepiano.deduction.template.BaseDaoTemplate;
import com.codepiano.deduction.template.BaseServiceTemplate;
import com.codepiano.deduction.template.BeanTemplate;
import com.codepiano.deduction.template.ConfigTemplate;
import com.codepiano.deduction.template.ConstantTemplate;
import com.codepiano.deduction.template.ControllerTemplate;
import com.codepiano.deduction.template.DaoTemplate;
import com.codepiano.deduction.template.ErrorTemplate;
import com.codepiano.deduction.template.GoModTemplate;
import com.codepiano.deduction.template.MainTemplate;
import com.codepiano.deduction.template.ResponseModelTemplate;
import com.codepiano.deduction.template.ServiceTemplate;
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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author codepiano
 * todo 工程结构和配置结构对应
 * todo 惟一索引查询
 */
@SpringBootApplication
@Slf4j
public class DeductionApplication {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${output.api.dir.base}")
    private String baseDir;

    @Value("${output.api.dir.bean.business}")
    private String businessBeanPath;

    @Value("${output.api.dir.bean.http}")
    private String httpBeanPath;

    @Value("${output.api.dir.dao}")
    private String daoPath;

    @Value("${output.api.dir.service}")
    private String servicePath;

    @Value("${output.api.dir.controller}")
    private String controllerPath;

    @Value("${output.api.dir.error}")
    private String errorPath;

    @Value("${output.api.dir.constant}")
    private String constantPath;

    @Value("${output.api.package.base}")
    private String basePackage;

    @Value("${output.api.package.bean.business}")
    private String businessBeanPackage;

    @Value("${output.api.package.bean.http}")
    private String httpBeanPackage;

    @Value("${output.api.package.dao}")
    private String daoPackage;

    @Value("${output.api.package.service}")
    private String servicePackage;

    @Value("${output.api.package.controller}")
    private String controllerPackage;

    @Value("${output.api.package.error}")
    private String errorPackage;

    @Value("${output.api.package.constant}")
    private String constantPackage;

    @Value("${output.cmd.dir.base}")
    private String cmdBaseDir;

    @Value("${output.cmd.dir.main}")
    private String cmdMainPath;

    @Value("${output.cmd.package.base}")
    private String cmdBasePackage;

    @Value("${output.cmd.package.main}")
    private String cmdMainPackage;

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
            var uri = URI.create(datasourceUrl.substring(5));
            String catalog = uri.getPath().substring(1);
            var businessModelDir = baseDir + File.separator + businessBeanPath;
            FileUtils.forceMkdir(new File(businessModelDir));
            var httpModelDir = baseDir + File.separator + httpBeanPath;
            FileUtils.forceMkdir(new File(httpModelDir));
            var daoDir = baseDir + File.separator + daoPath;
            FileUtils.forceMkdir(new File(daoDir));
            var serviceDir = baseDir + File.separator + servicePath;
            FileUtils.forceMkdir(new File(serviceDir));
            var controllerDir = baseDir + File.separator + controllerPath;
            FileUtils.forceMkdir(new File(controllerDir));
            var errorDir = baseDir + File.separator + errorPath;
            FileUtils.forceMkdir(new File(errorDir));
            var cmdMainDir = cmdBaseDir + File.separator + cmdMainPath;
            FileUtils.forceMkdir(new File(cmdMainDir));
            var constantDir = baseDir + File.separator + constantPath;
            FileUtils.forceMkdir(new File(constantDir));
            List<TableDescription> tables = tableService.getAllTablesInCatalog(catalog);
            var packagePath = packagePath();
            tables.forEach(tableDescription -> {
                String modelName = NameTransfer.transferToCamelCase(tableDescription.getTableName());
                String variableName = NameTransfer.transferToVariableName(modelName);
                // 生成 bean 代码
                List<ColumnDescription> columns = columnService.getAllColumnsInfoFromTable(tableDescription);
                String model = BeanTemplate.template(tableDescription, columns, typeTransfer, businessBeanPackage)
                        .render()
                        .toString();
                writeToGoFile(businessModelDir, tableDescription.getTableName(), model);
                // 生成 model 层代码
                String dao = DaoTemplate.template(basePackage + businessBeanPath, modelName, variableName, daoPackage)
                        .render()
                        .toString();
                writeToGoFile(daoDir, tableDescription.getTableName() + "_dao", dao);
                // 生成 service 代码
                String service = ServiceTemplate.template(packagePath, servicePackage, modelName, variableName, businessBeanPackage, errorPackage)
                        .render()
                        .toString();
                writeToGoFile(serviceDir, tableDescription.getTableName() + "_service", service);
                // 生成 controller 代码
                String controller = ControllerTemplate.template(packagePath, controllerPackage, modelName, variableName, businessBeanPackage, errorPackage, httpBeanPackage)
                        .render()
                        .toString();
                writeToGoFile(controllerDir, tableDescription.getTableName() + "_controller", controller);
            });
            // 生成响应体
            String response = ResponseModelTemplate.template(httpModelDir)
                    .render()
                    .toString();
            writeToGoFile(httpModelDir, "response", response);
            // 生成 dao 对象初始化代码，注入数据库连接
            String db = BaseDaoTemplate.template(tables, daoPackage)
                    .render()
                    .toString();
            writeToGoFile(daoDir, "db_access", db);
            // 生成 service 对象代码
            String baseService = BaseServiceTemplate.template(servicePackage, basePackage + File.separator + daoPath, daoPackage)
                    .render()
                    .toString();
            writeToGoFile(serviceDir, "service", baseService);
            // 生成 controller 对象代码
            String baseController = BaseControllerTemplate.template(servicePackage, basePackage + File.separator + servicePackage, servicePackage)
                    .render()
                    .toString();
            writeToGoFile(controllerDir, "controller", baseController);
            // 生成 error 代码
            String error = ErrorTemplate.template(errorPackage)
                    .render()
                    .toString();
            writeToGoFile(errorDir, "error", error);
            // 生成常量定义
            String constant = ConstantTemplate.template(constantPackage)
                    .render()
                    .toString();
            System.out.println(constant);
            writeToGoFile(errorDir, "error", error);
            // 生成 main 代码
            String main = MainTemplate.template(packagePath, daoPackage, servicePackage, controllerPackage, tables)
                    .render()
                    .toString();
            writeToGoFile(cmdMainDir, "main", main);
            // 生成 go.mod
            String goMod = GoModTemplate.template(basePackage)
                    .render()
                    .toString();
            writeToFile(baseDir, "go.mod", goMod);
            // 生成 config.yml
            String config = ConfigTemplate.template(uri.getHost(), uri.getPort(), datasourceUsername, datasourcePassword, uri.getPath())
                    .render()
                    .toString();
            writeToFile(baseDir, "config.yml", config);
        };
    }

    private Map<String, String> packagePath() {
        Map<String, String> packages = new HashMap<>();
        packages.put("bean", basePackage + File.separator + businessBeanPath);
        packages.put("dao", basePackage + File.separator + daoPath);
        packages.put("service", basePackage + File.separator + servicePath);
        packages.put("controller", basePackage + File.separator + controllerPath);
        packages.put("error", basePackage + File.separator + errorPath);
        packages.put("http", basePackage + File.separator + httpBeanPath);
        packages.put("constant", basePackage + File.separator + constantPath);
        return packages;
    }

    private void writeToGoFile(String baseDir, String fileName, String content) {
        this.writeToFile(baseDir, fileName + ".go", content);
    }

    private void writeToFile(String baseDir, String fileName, String content) {
        File modelFile = new File(baseDir + File.separator + fileName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(modelFile))) {
            IOUtils.write(content, bos, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("file {} not found!", modelFile.getAbsolutePath());
        }
    }

    @Bean
    public DatabaseMetaData databaseMetaData(DataSource dataSource) throws MetaDataAccessException, SQLException {
        return (DatabaseMetaData) JdbcUtils.extractDatabaseMetaData(dataSource, databaseMetaData -> databaseMetaData);
    }
}
