package br.com.fiap.garage.application;

import br.com.fiap.commons.config.NativeRuntimeHints;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = "br.com.fiap")
@ImportRuntimeHints(NativeRuntimeHints.class)
public class GarageApplication {

    static void main(String[] args) {
        run(GarageApplication.class, args);
    }
}
