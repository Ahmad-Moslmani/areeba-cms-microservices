package com.ahmadmouslimani.fraud;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Fraud Service REST API Documentation",
				description = "Fraud microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Ahmad Mouslimani",
						email = "amd.moslmani@gmail.com"
				)
		)
)
public class FraudApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudApplication.class, args);
	}

}
