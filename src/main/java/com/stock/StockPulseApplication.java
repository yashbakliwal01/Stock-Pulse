package com.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.stock")
public class StockPulseApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockPulseApplication.class, args);
	}

}
