package com.codepiano.deduction;

import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import com.codepiano.deduction.service.ColumnService;
import com.codepiano.deduction.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.List;

/**
 * @author codepiano
 */
@SpringBootApplication
@Slf4j
public class DeductionApplication {

    @Autowired
    private TableService tableService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TemplateEngine templateEngine;

    public static void main(String[] args) {
        SpringApplication.run(DeductionApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            List<TableDescription> tables = tableService.getAllTablesInCatalog("sonarqube");
            List<ColumnDescription> columns = columnService.getAllColumnsInfoFromTable(tables.get(0));
            Context c = new Context();
            c.setVariable("columns", columns);
            c.setVariable("a", "test");
            String s = templateEngine.process("[(${a})], [# th:each=\"column : ${columns}\"]\n" +
                    "   - [(${column.getTableName()})]\n" +
                    "[/]", c);
            System.out.println(s);
        };
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        return templateEngine;
    }

    @Bean
    public StringTemplateResolver thymeleafTemplateResolver() {
        StringTemplateResolver templateResolver
                = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        return templateResolver;
    }
}
