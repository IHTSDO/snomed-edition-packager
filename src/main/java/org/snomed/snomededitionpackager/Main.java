package org.snomed.snomededitionpackager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
	static {
		System.setProperty("aws.region", "us-east-1");
	}
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
