package br.com.fiap.garage.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = "br.com.fiap")
public class GarageApplication {

    static void main(String[] args) {
        run(GarageApplication.class, args);
    }
}
