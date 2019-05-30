package com.codepiano.deduction;

import com.codepiano.deduction.database.ColumnService;
import com.codepiano.deduction.database.TableService;
import com.codepiano.deduction.models.ColumnDescription;
import com.codepiano.deduction.models.TableDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

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

    public static void main(String[] args) {
        SpringApplication.run(DeductionApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            List<TableDescription> tables = tableService.getAllTablesInCatalog("sonarqube");
            List<ColumnDescription> columns = columnService.getAllColumnsInfoFromTable(tables.get(0));
            System.out.println(1);
        };
    }
}
