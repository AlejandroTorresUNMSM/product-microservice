package com.atorres.nttdata.productomicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
public class ProductoMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductoMicroserviceApplication.class, args);
	}

}
