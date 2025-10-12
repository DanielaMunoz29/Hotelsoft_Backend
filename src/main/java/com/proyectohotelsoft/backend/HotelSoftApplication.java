package com.proyectohotelsoft.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.proyectohotelsoft.backend")
public class HotelSoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelSoftApplication.class, args);
	}

}
