package io.pivotal.sfdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.pivotal.sfdc.service.AccountService;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class DataloadServiceApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(DataloadServiceApplication.class);
	
	@Autowired
	private AccountService ac;

	public static void main(String[] args) {
		SpringApplication.run(DataloadServiceApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		logger.debug("preloading data");
		ac.preload();
		
	}
}
