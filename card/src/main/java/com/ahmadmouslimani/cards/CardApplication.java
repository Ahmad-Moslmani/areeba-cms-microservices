package com.ahmadmouslimani.cards;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Card Service REST API Documentation",
				description = "Card microservice REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Ahmad Mouslimani",
						email = "amd.moslmani@gmail.com"
				)
		)
)
public class CardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardApplication.class, args);
	}

}
