package com.assignment.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@ComponentScan(basePackages = "com.assignment")
@EnableJpaRepositories(basePackages = "com.assignment.dao")
@EntityScan(basePackages = "com.assignment.entity")
public class AssignmentJava6Application {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentJava6Application.class, args);
	}

}
