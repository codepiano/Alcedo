package com.codepiano.deduction;

import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import com.codepiano.deduction.service.ColumnService;
import com.codepiano.deduction.service.TableService;
import com.codepiano.deduction.template.backend.BaseControllerTemplate;
import com.codepiano.deduction.template.backend.BaseDaoTemplate;
import com.codepiano.deduction.template.backend.BaseServiceTemplate;
import com.codepiano.deduction.template.backend.BeanTemplate;
import com.codepiano.deduction.template.backend.ConfigTemplate;
import com.codepiano.deduction.template.backend.ConstantTemplate;
import com.codepiano.deduction.template.backend.ControllerTemplate;
import com.codepiano.deduction.template.backend.DaoTemplate;
import com.codepiano.deduction.template.backend.ErrorTemplate;
import com.codepiano.deduction.template.backend.GoModTemplate;
import com.codepiano.deduction.template.backend.MainTemplate;
import com.codepiano.deduction.template.backend.ResponseModelTemplate;
import com.codepiano.deduction.template.backend.ServiceTemplate;
import com.codepiano.deduction.template.frontend.APITemplate;
import com.codepiano.deduction.template.frontend.AppTemplate;
import com.codepiano.deduction.template.frontend.MainJsTemplate;
import com.codepiano.deduction.template.frontend.RouterIndexTemplate;
import com.codepiano.deduction.template.frontend.RouterTemplate;
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

    @Value("${backend.api.dir.base}")
    private String apiBasePath;

    @Value("${backend.api.dir.bean.business}")
    private String businessBeanPath;

    @Value("${backend.api.dir.bean.http}")
    private String httpBeanPath;

    @Value("${backend.api.dir.dao}")
    private String daoPath;

    @Value("${backend.api.dir.service}")
    private String servicePath;

    @Value("${backend.api.dir.controller}")
    private String controllerPath;

    @Value("${backend.api.dir.error}")
    private String errorPath;

    @Value("${backend.api.dir.constant}")
    private String constantPath;

    @Value("${backend.api.package.base}")
    private String basePackage;

    @Value("${backend.api.package.bean.business}")
    private String businessBeanPackage;

    @Value("${backend.api.package.bean.http}")
    private String httpBeanPackage;

    @Value("${backend.api.package.dao}")
    private String daoPackage;

    @Value("${backend.api.package.service}")
    private String servicePackage;

    @Value("${backend.api.package.controller}")
    private String controllerPackage;

    @Value("${backend.api.package.error}")
    private String errorPackage;

    @Value("${backend.api.package.constant}")
    private String constantPackage;

    @Value("${backend.cmd.dir.base}")
    private String cmdBasePath;

    @Value("${backend.cmd.dir.main}")
    private String cmdMainPath;

    @Value("${backend.cmd.package.base}")
    private String cmdBasePackage;

    @Value("${backend.cmd.package.main}")
    private String cmdMainPackage;

    @Value("${frontend.dir.base}")
    private String frontendBasePath;

    @Value("${frontend.dir.store}")
    private String frontendStorePath;

    @Value("${frontend.dir.config}")
    private String frontendConfigPath;

    @Value("${frontend.dir.router}")
    private String frontendRouterPath;

    @Value("${frontend.dir.view}")
    private String frontendViewPath;

    @Value("${frontend.dir.api}")
    private String frontendAPIPath;

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
            // backend
            var businessModelDir = apiBasePath + File.separator + businessBeanPath;
            FileUtils.forceMkdir(new File(businessModelDir));
            var httpModelDir = apiBasePath + File.separator + httpBeanPath;
            FileUtils.forceMkdir(new File(httpModelDir));
            var daoDir = apiBasePath + File.separator + daoPath;
            FileUtils.forceMkdir(new File(daoDir));
            var serviceDir = apiBasePath + File.separator + servicePath;
            FileUtils.forceMkdir(new File(serviceDir));
            var controllerDir = apiBasePath + File.separator + controllerPath;
            FileUtils.forceMkdir(new File(controllerDir));
            var errorDir = apiBasePath + File.separator + errorPath;
            FileUtils.forceMkdir(new File(errorDir));
            var cmdMainDir = cmdBasePath + File.separator + cmdMainPath;
            FileUtils.forceMkdir(new File(cmdMainDir));
            var constantDir = apiBasePath + File.separator + constantPath;
            FileUtils.forceMkdir(new File(constantDir));
            // backend begin
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
            writeToFile(apiBasePath, "go.mod", goMod);
            // 生成 config.yml
            String config = ConfigTemplate.template(uri.getHost(), uri.getPort(), datasourceUsername, datasourcePassword, uri.getPath())
                    .render()
                    .toString();
            writeToFile(apiBasePath, "config.yml", config);
            // frontend
            var frontendStoreDir = frontendBasePath + File.separator + this.frontendStorePath;
            FileUtils.forceMkdir(new File(frontendStoreDir));
            var frontendSrcDir = frontendBasePath + File.separator + "/src";
            FileUtils.forceMkdir(new File(frontendSrcDir));
            var frontendConfigDir = frontendBasePath + File.separator + this.frontendConfigPath;
            FileUtils.forceMkdir(new File(frontendConfigDir));
            var frontendRouterDir = frontendBasePath + File.separator + this.frontendRouterPath;
            FileUtils.forceMkdir(new File(frontendRouterDir));
            var frontendViewDir = frontendBasePath + File.separator + this.frontendRouterPath;
            FileUtils.forceMkdir(new File(frontendViewDir));
            var frontendAPIDir = frontendBasePath + File.separator + this.frontendAPIPath;
            FileUtils.forceMkdir(new File(frontendAPIDir));
            // frontend begin
            tables.forEach(tableDescription -> {
                String router = RouterTemplate.template(tableDescription, this.frontendRouterPath, this.frontendViewPath)
                        .render()
                        .toString();
                writeToJsFile(frontendRouterDir, NameTransfer.transferToKebabCase(tableDescription.getTableName()), router);
                String api = APITemplate.template(tableDescription)
                        .render()
                        .toString();
                writeToJsFile(frontendAPIDir, NameTransfer.transferToKebabCase(tableDescription.getTableName()), api);
                System.out.println(api);
            });
            String mainJs = MainJsTemplate.template(this.frontendRouterPath, this.frontendStorePath, this.frontendConfigPath)
                    .render()
                    .toString();
            writeToJsFile(frontendSrcDir, "main.js", mainJs);
            String appVue = AppTemplate.template()
                    .render()
                    .toString();
            writeToVueFile(frontendSrcDir, "app", appVue);
            String routerIndex = RouterIndexTemplate.template(tables, this.frontendRouterPath)
                    .render()
                    .toString();
            writeToJsFile(frontendRouterDir, "index", appVue);
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

    private void writeToJsFile(String baseDir, String fileName, String content) {
        this.writeToFile(baseDir, fileName + ".js", content);
    }

    private void writeToVueFile(String baseDir, String fileName, String content) {
        this.writeToFile(baseDir, fileName + ".vue", content);
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
    public DatabaseMetaData databaseMetaData(DataSource dataSource) throws MetaDataAccessException {
        return (DatabaseMetaData) JdbcUtils.extractDatabaseMetaData(dataSource, databaseMetaData -> databaseMetaData);
    }
}
