package io.pivotal.sfdc;

import io.pivotal.sfdc.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

/**
 * The Spring configuration and entry point for
 * the application.
 *
 * @author Jignesh Sheth
 *
 */

@EnableTask
@SpringBootApplication
public class DataLoaderApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(DataLoaderApplication.class);

    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("preloading data");
        try {
            accountService.preload();
        } catch (Exception e) {
            logger.error("Can not preload data. "+e.getMessage());
        }
    }
}
