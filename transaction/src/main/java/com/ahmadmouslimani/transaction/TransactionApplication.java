package com.ahmadmouslimani.transaction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(
		info = @Info(
				title = "Transaction Service REST API Documentation",
				description = "Transaction microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Ahmad Mouslimani",
						email = "amd.moslmani@gmail.com"
				)
		)
)
public class TransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

}
