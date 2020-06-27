package io.pivotal.sfdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.FunctionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.pivotal.sfdc.service.AccountService;
import io.pivotal.sfdc.service.AuthService;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Spring configuration and entry point for the application.
 * 
 * @author Jignesh Sheth
 *
 */

@SpringBootApplication(exclude = FunctionConfiguration.class)
@EnableSwagger2
@Controller
public class AccountServiceApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceApplication.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	AuthService authService;

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	public Docket newsApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("io.pivotal.sfdc.controller")).paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("SFDC Account Service API sample")
				.description("SFDC Account Service API demo using Spring Cloud Services and SFDC Api")
				.termsOfServiceUrl("http://pivotal.io/").contact(new Contact("Jignesh Sheth", null, null))
				.license("Apache License Version 2.0").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
				.version("2.0").build();
	}

	@RequestMapping("/")
	public String home() {
		return "redirect:/swagger-ui.html";
	}

	@Override
	public void run(String... arg0) throws Exception {
		LOGGER.debug("preloading data");
		try {
			accountService.preload();
		} catch (Exception e) {
			LOGGER.error("Can not preload data. " + e.getMessage());
			if (e.getMessage().contains("INVALID_SESSION_ID")) {
				authService.invalidateSession();
				try {
					accountService.preload();
				} catch (Exception ex) {
					LOGGER.error("Can not preload data, even after invalidating session. " + e.getMessage());
				}
			}
		}
	}
}
