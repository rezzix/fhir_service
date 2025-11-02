package net.rezzix.fhirservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DppserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DppserviceApplication.class, args);
	}

}
