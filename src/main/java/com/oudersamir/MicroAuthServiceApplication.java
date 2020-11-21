package com.oudersamir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MicroAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroAuthServiceApplication.class, args);
	}
	@Bean
	public SpringApplicationContext springApplicationContext(){
		return new SpringApplicationContext();
	}
}
