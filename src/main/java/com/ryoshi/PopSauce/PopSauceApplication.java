package com.ryoshi.PopSauce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableScheduling
public class PopSauceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopSauceApplication.class, args);
	}

}
